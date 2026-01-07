import argparse
import math
import random
from dataclasses import dataclass
from typing import Dict, Any, Optional, List, Tuple

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


# ---------------------------- Утилиты ----------------------------
def train_test_split(X: np.ndarray, y: np.ndarray, test_size: float = 0.2, seed: int = 42):
    rng = np.random.default_rng(seed)
    n = len(y)
    idx = np.arange(n)
    rng.shuffle(idx)
    n_test = int(round(n * test_size))
    test_idx = idx[:n_test]
    train_idx = idx[n_test:]
    return X[train_idx], X[test_idx], y[train_idx], y[test_idx]


def entropy(counts: Dict[Any, int]) -> float:
    total = sum(counts.values())
    if total == 0:
        return 0.0
    ent = 0.0
    for c in counts.values():
        if c == 0:
            continue
        p = c / total
        ent -= p * math.log2(p)
    return ent


def class_counts(y: np.ndarray) -> Dict[Any, int]:
    vals, cnts = np.unique(y, return_counts=True)
    return {int(v): int(c) for v, c in zip(vals, cnts)}


def majority_prob(y: np.ndarray) -> float:
    """Вероятность класса 1 как доля в выборке узла"""
    if len(y) == 0:
        return 0.5
    return float(np.mean(y))


# ---------------------------- Дерево ----------------------------
@dataclass
class Node:
    is_leaf: bool
    # Лист
    prob_one: Optional[float] = None  # вероятность класса 1 (успех)
    # Внутренний узел
    feature_idx: Optional[int] = None
    children: Optional[Dict[Any, 'Node']] = None


class DecisionTree:
    def __init__(self, max_depth: int = 20, min_samples_split: int = 2, random_state: int = 42):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.random_state = random_state
        self.root: Optional[Node] = None
        self.n_features_: Optional[int] = None
        random.seed(self.random_state)

    def fit(self, X: np.ndarray, y: np.ndarray):
        self.n_features_ = X.shape[1]
        self.root = self._build_tree(X, y, depth=0)

    def _should_stop(self, X: np.ndarray, y: np.ndarray, depth: int) -> bool:
        if depth >= self.max_depth:
            return True
        if len(y) < self.min_samples_split:
            return True
        # все одинакового класса?
        unique = np.unique(y)
        if len(unique) == 1:
            return True
        return False

    def _best_split(self, X: np.ndarray, y: np.ndarray, features: List[int]) -> Tuple[Optional[int], float]:
        """Возвращает (feature_idx, gain). Многоветвевое разбиение по значениям признака."""
        base_counts = class_counts(y)
        base_entropy = entropy(base_counts)
        best_gain = -1.0
        best_feat = None

        for f in features:
            values, inv = np.unique(X[:, f], return_inverse=True)
            # разбиваем y по значениям
            subsets = {val: y[inv == i] for i, val in enumerate(values)}
            # инф. энтропия после сплита
            total = len(y)
            after = 0.0
            for subset in subsets.values():
                w = len(subset) / total
                after += w * entropy(class_counts(subset))
            gain = base_entropy - after
            if gain > best_gain:
                best_gain = gain
                best_feat = f

        return best_feat, best_gain

    def _build_tree(self, X: np.ndarray, y: np.ndarray, depth: int) -> Node:
        if self._should_stop(X, y, depth):
            return Node(is_leaf=True, prob_one=majority_prob(y))

        # случайно выбираем sqrt(n_features) признаков
        k = max(1, int(math.sqrt(self.n_features_)))
        feats = random.sample(range(self.n_features_), k)

        best_feat, best_gain = self._best_split(X, y, feats)

        # если сплит бесполезен -> лист
        if best_feat is None or best_gain <= 1e-12:
            return Node(is_leaf=True, prob_one=majority_prob(y))

        # строим детей по значениям признака
        values, inv = np.unique(X[:, best_feat], return_inverse=True)
        children: Dict[Any, Node] = {}
        for i, val in enumerate(values):
            mask = inv == i
            child = self._build_tree(X[mask], y[mask], depth + 1)
            children[val] = child
        return Node(is_leaf=False, feature_idx=best_feat, children=children)

    def predict_proba(self, X: np.ndarray) -> np.ndarray:
        probs = np.array([self._predict_one_proba(x, self.root) for x in X], dtype=float)
        # возвращаем вероятность класса 1
        return probs

    def _predict_one_proba(self, x: np.ndarray, node: Node) -> float:
        while not node.is_leaf:
            feat = node.feature_idx
            val = x[feat]
            if val in node.children:
                node = node.children[val]
            else:
                # неизвестное значение — вернём усреднённую вероятность по детям
                child_probs = [c.prob_one if c.is_leaf else 0.5 for c in node.children.values()]
                return float(np.mean(child_probs)) if child_probs else 0.5
        return float(node.prob_one)

    def predict(self, X: np.ndarray, threshold: float = 0.5) -> np.ndarray:
        proba = self.predict_proba(X)
        return (proba >= threshold).astype(int)


# ---------------------------- Метрики ----------------------------
def accuracy_score(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    return float(np.mean(y_true == y_pred))


def precision_recall(y_true: np.ndarray, y_pred: np.ndarray) -> Tuple[float, float]:
    tp = int(np.sum((y_true == 1) & (y_pred == 1)))
    fp = int(np.sum((y_true == 0) & (y_pred == 1)))
    fn = int(np.sum((y_true == 1) & (y_pred == 0)))
    precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
    recall = tp / (tp + fn) if (tp + fn) > 0 else 0.0
    return precision, recall


def roc_curve_manual(y_true: np.ndarray, y_score: np.ndarray, num_points: int = 200):
    thresholds = np.linspace(0.0, 1.0, num_points)
    tprs, fprs = [], []
    for t in thresholds:
        y_pred = (y_score >= t).astype(int)
        tp = np.sum((y_true == 1) & (y_pred == 1))
        fp = np.sum((y_true == 0) & (y_pred == 1))
        tn = np.sum((y_true == 0) & (y_pred == 0))
        fn = np.sum((y_true == 1) & (y_pred == 0))
        tpr = tp / (tp + fn) if (tp + fn) > 0 else 0.0
        fpr = fp / (fp + tn) if (fp + tn) > 0 else 0.0
        tprs.append(tpr)
        fprs.append(fpr)
    return np.array(fprs), np.array(tprs), thresholds


def pr_curve_manual(y_true: np.ndarray, y_score: np.ndarray, num_points: int = 200):
    thresholds = np.linspace(0.0, 1.0, num_points)
    precisions, recalls = [], []
    for t in thresholds:
        y_pred = (y_score >= t).astype(int)
        p, r = precision_recall(y_true, y_pred)
        precisions.append(p)
        recalls.append(r)
    return np.array(recalls), np.array(precisions), thresholds


def auc_trapezoid(x: np.ndarray, y: np.ndarray) -> float:
    # сортируем по x
    order = np.argsort(x)
    x_sorted = x[order]
    y_sorted = y[order]
    area = float(np.trapz(y_sorted, x_sorted))
    return area


# ---------------------------- Загрузка и подготовка ----------------------------
def load_dataset(path: str) -> pd.DataFrame:
    df = pd.read_csv(path)
    return df


def build_X_y(df: pd.DataFrame) -> Tuple[np.ndarray, np.ndarray, List[str]]:
    # Удалим идентификаторы, которые не несут прогностической силы
    cols = [c for c in df.columns if c not in ["STUDENT ID", "COURSE ID", "GRADE"]]
    X = df[cols].to_numpy()
    # Бинарная цель: успешен/неуспешен. Порог выберем >= 3 как "успех".
    y = (df["GRADE"].to_numpy() >= 3).astype(int)
    return X, y, cols


# ---------------------------- CLI & main ----------------------------
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", type=str, default="DATA (1).csv", help="Path to CSV")
    parser.add_argument("--max_depth", type=int, default=20)
    parser.add_argument("--min_samples_split", type=int, default=2)
    parser.add_argument("--test_size", type=float, default=0.2)
    parser.add_argument("--seed", type=int, default=42)
    args = parser.parse_args()

    df = load_dataset(args.data)
    X, y, feature_names = build_X_y(df)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=args.test_size, seed=args.seed)

    tree = DecisionTree(max_depth=args.max_depth, min_samples_split=args.min_samples_split, random_state=args.seed)
    tree.fit(X_train, y_train)

    # предсказания и метрики
    y_prob = tree.predict_proba(X_test)
    y_pred = (y_prob >= 0.5).astype(int)
    acc = accuracy_score(y_test, y_pred)
    prec, rec = precision_recall(y_test, y_pred)

    print("=== Evaluation (test) ===")
    print(f"Accuracy:  {acc:.4f}")
    print(f"Precision: {prec:.4f}")
    print(f"Recall:    {rec:.4f}")

    # ROC
    fpr, tpr, thr = roc_curve_manual(y_test, y_prob, num_points=400)
    auc_roc = auc_trapezoid(fpr, tpr)
    plt.figure()
    plt.plot(fpr, tpr, label=f"ROC (AUC={auc_roc:.3f})")
    plt.plot([0, 1], [0, 1], linestyle="--")
    plt.xlabel("False Positive Rate")
    plt.ylabel("True Positive Rate")
    plt.title("ROC curve (from scratch)")
    plt.legend()
    plt.tight_layout()
    plt.savefig("roc_curve.png", dpi=160)

    # PR
    recall_arr, precision_arr, thr2 = pr_curve_manual(y_test, y_prob, num_points=400)
    # AUC-PR вычислим по Recall(x)-Precision(y) и отсортируем по recall
    order = np.argsort(recall_arr)
    auc_pr = float(np.trapz(precision_arr[order], recall_arr[order])) if len(order) > 1 else 0.0
    plt.figure()
    plt.plot(recall_arr, precision_arr, label=f"PR (AUC={auc_pr:.3f})")
    plt.xlabel("Recall")
    plt.ylabel("Precision")
    plt.title("Precision–Recall curve (from scratch)")
    plt.legend()
    plt.tight_layout()
    plt.savefig("pr_curve.png", dpi=160)

    # также сохраним важные параметры запуска
    with open("run_summary.txt", "w", encoding="utf-8") as f:
        f.write(f"Rows: {len(df)}, Features: {len(feature_names)}\n")
        f.write(f"Train size: {len(y_train)}, Test size: {len(y_test)}\n")
        f.write(f"max_depth={args.max_depth}, min_samples_split={args.min_samples_split}, seed={args.seed}\n")
        f.write(f"Accuracy={acc:.4f}, Precision={prec:.4f}, Recall={rec:.4f}\n")
        f.write(f"AUC_ROC={auc_roc:.4f}, AUC_PR={auc_pr:.4f}\n")

    print("Файлы сохранены: roc_curve.png, pr_curve.png, run_summary.txt")


if __name__ == "__main__":
    main()

