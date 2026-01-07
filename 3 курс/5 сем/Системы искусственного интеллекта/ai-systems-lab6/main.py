# lab6_logreg_titanic.py
# Лабораторная 6. Логистическая регрессия "с нуля"
# Используются только numpy, pandas и (опционально) matplotlib для графиков.

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from typing import Tuple, Dict, List


# ---------- 1. Загрузка и предварительный анализ данных ----------

def load_titanic(path: str = "train.csv") -> pd.DataFrame:
    """
    Загружает тренировочный датасет Titanic.
    """
    df = pd.read_csv(path)
    return df


def basic_statistics(df: pd.DataFrame) -> None:
    """
    Печатает базовую статистику и строит несколько простых графиков.
    """
    print("Первые строки датасета:")
    print(df.head(), "\n")

    print("Общая информация:")
    print(df.info(), "\n")

    print("Числовая статистика:")
    print(df.describe(), "\n")

    # Распределение целевой переменной
    df["Survived"].value_counts().plot(kind="bar")
    plt.title("Распределение классов Survived")
    plt.xlabel("Survived")
    plt.ylabel("Count")
    plt.tight_layout()
    plt.show()

    # Пример: зависимость выживаемости от пола
    pd.crosstab(df["Sex"], df["Survived"], normalize="index").plot(kind="bar", stacked=True)
    plt.title("Доля выживших по полу")
    plt.ylabel("Доля")
    plt.tight_layout()
    plt.show()


# ---------- 2. Предобработка данных ----------

def preprocess(df: pd.DataFrame) -> Tuple[np.ndarray, np.ndarray, List[str]]:
    """
    Предобработка данных Titanic:
    - выбор информативных признаков
    - заполнение пропусков
    - кодирование категориальных признаков
    - нормализация числовых признаков
    Возвращает X, y и список имен признаков.
    """
    df = df.copy()

    # Оставим только нужные столбцы
    features = ["Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked"]
    target = "Survived"
    df = df[features + [target]]

    # Заполнение пропусков
    df["Age"] = df["Age"].fillna(df["Age"].median())
    df["Fare"] = df["Fare"].fillna(df["Fare"].median())
    df["Embarked"] = df["Embarked"].fillna(df["Embarked"].mode()[0])

    # Кодирование пола: male -> 0, female -> 1
    df["Sex"] = df["Sex"].map({"male": 0, "female": 1}).astype(int)

    # One-hot кодирование Embarked (C, Q, S)
    embarked_dummies = pd.get_dummies(df["Embarked"], prefix="Embarked")
    df = pd.concat([df.drop(columns=["Embarked"]), embarked_dummies], axis=1)

    # Список признаков после one-hot
    feature_cols = [c for c in df.columns if c != target]

    X = df[feature_cols].values.astype(float)
    y = df[target].values.astype(int)

    # Нормализация (стандартизация) признаков
    mean = X.mean(axis=0)
    std = X.std(axis=0)
    std[std == 0] = 1.0  # защита от деления на 0
    X = (X - mean) / std

    # Добавим единичный столбец для свободного члена (bias)
    ones = np.ones((X.shape[0], 1))
    X = np.hstack([ones, X])
    feature_cols = ["bias"] + feature_cols

    return X, y, feature_cols


# ---------- 3. Разбиение на train / test ----------

def train_test_split(
        X: np.ndarray,
        y: np.ndarray,
        test_size: float = 0.2,
        random_state: int = 42
) -> Tuple[np.ndarray, np.ndarray, np.ndarray, np.ndarray]:
    """
    Простое разбиение на обучающую и тестовую выборки.
    """
    np.random.seed(random_state)
    n_samples = X.shape[0]
    indices = np.arange(n_samples)
    np.random.shuffle(indices)

    test_count = int(n_samples * test_size)
    test_idx = indices[:test_count]
    train_idx = indices[test_count:]

    X_train, X_test = X[train_idx], X[test_idx]
    y_train, y_test = y[train_idx], y[test_idx]
    return X_train, X_test, y_train, y_test


# ---------- 4. Логистическая регрессия "с нуля" ----------

def sigmoid(z: np.ndarray) -> np.ndarray:
    """
    Сигмоидная функция.
    """
    return 1.0 / (1.0 + np.exp(-z))


def log_loss(y_true: np.ndarray, y_pred_proba: np.ndarray) -> float:
    """
    Логистическая функция потерь (binary cross-entropy).
    """
    eps = 1e-15
    y_pred_proba = np.clip(y_pred_proba, eps, 1 - eps)
    loss = -np.mean(y_true * np.log(y_pred_proba) + (1 - y_true) * np.log(1 - y_pred_proba))
    return loss


class LogisticRegressionScratch:
    """
    Логистическая регрессия, реализованная "с нуля".
    Поддерживает два метода оптимизации:
      - gradient_descent
      - newton
    """

    def __init__(self,
                 learning_rate: float = 0.01,
                 n_iter: int = 1000,
                 method: str = "gradient_descent"):
        self.learning_rate = learning_rate
        self.n_iter = n_iter
        self.method = method
        self.weights: np.ndarray | None = None
        self.loss_history: List[float] = []

    def _gradient(self, X: np.ndarray, y: np.ndarray, y_pred: np.ndarray) -> np.ndarray:
        """
        Градиент функции потерь.
        """
        return X.T @ (y_pred - y) / len(y)

    def fit(self, X: np.ndarray, y: np.ndarray) -> "LogisticRegressionScratch":
        """
        Обучение модели на данных X, y.
        """
        n_samples, n_features = X.shape
        self.weights = np.zeros(n_features)

        for i in range(self.n_iter):
            z = X @ self.weights
            y_pred = sigmoid(z)
            loss = log_loss(y, y_pred)
            self.loss_history.append(loss)

            grad = self._gradient(X, y, y_pred)

            if self.method == "gradient_descent":
                # Шаг обычного градиентного спуска
                self.weights -= self.learning_rate * grad

            elif self.method == "newton":
                # Метод Ньютона: w := w - H^{-1} * grad
                # H = X^T * R * X / n, R = diag(p*(1-p))
                p = y_pred
                R = p * (1 - p)
                # Формируем диагональную матрицу косвенно через умножение
                XR = X * R[:, np.newaxis]
                H = X.T @ XR / n_samples
                # Добавим маленькую регуляризацию к диагонали, чтобы матрица была обратима
                reg = 1e-4 * np.eye(H.shape[0])
                H_inv = np.linalg.inv(H + reg)
                step = H_inv @ grad
                self.weights -= step  # обычно шаг = 1 достаточно

            else:
                raise ValueError(f"Неизвестный метод оптимизации: {self.method}")

        return self

    def predict_proba(self, X: np.ndarray) -> np.ndarray:
        """
        Возвращает вероятности принадлежности к классу 1.
        """
        if self.weights is None:
            raise RuntimeError("Модель не обучена")
        return sigmoid(X @ self.weights)

    def predict(self, X: np.ndarray, threshold: float = 0.5) -> np.ndarray:
        """
        Возвращает бинарные предсказания 0/1.
        """
        proba = self.predict_proba(X)
        return (proba >= threshold).astype(int)


# ---------- 5. Метрики качества ----------

def accuracy_score(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    return (y_true == y_pred).mean()


def precision_recall_f1(y_true: np.ndarray, y_pred: np.ndarray) -> Tuple[float, float, float]:
    """
    Рассчитывает precision, recall, F1 для класса "1".
    """
    tp = np.sum((y_true == 1) & (y_pred == 1))
    fp = np.sum((y_true == 0) & (y_pred == 1))
    fn = np.sum((y_true == 1) & (y_pred == 0))

    precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
    recall = tp / (tp + fn) if (tp + fn) > 0 else 0.0
    f1 = 2 * precision * recall / (precision + recall) if (precision + recall) > 0 else 0.0
    return precision, recall, f1


# ---------- 6. Исследование гиперпараметров ----------

def hyperparameter_search(
        X_train: np.ndarray,
        y_train: np.ndarray,
        X_test: np.ndarray,
        y_test: np.ndarray
) -> pd.DataFrame:
    """
    Перебирает несколько комбинаций:
    - learning_rate
    - n_iter
    - method (gradient_descent / newton)
    Считает accuracy, precision, recall, F1 на тесте.
    Возвращает DataFrame с результатами.
    """
    learning_rates = [0.001, 0.01, 0.1]
    n_iters = [300, 1000, 2000]
    methods = ["gradient_descent", "newton"]

    results: List[Dict] = []

    for lr in learning_rates:
        for n_it in n_iters:
            for method in methods:
                print(f"Обучение: method={method}, lr={lr}, n_iter={n_it}")
                model = LogisticRegressionScratch(
                    learning_rate=lr,
                    n_iter=n_it,
                    method=method
                )
                model.fit(X_train, y_train)
                y_pred = model.predict(X_test)

                acc = accuracy_score(y_test, y_pred)
                prec, rec, f1 = precision_recall_f1(y_test, y_pred)

                results.append({
                    "method": method,
                    "learning_rate": lr,
                    "n_iter": n_it,
                    "accuracy": acc,
                    "precision": prec,
                    "recall": rec,
                    "f1": f1
                })

    results_df = pd.DataFrame(results)
    return results_df.sort_values(by="f1", ascending=False)


# ---------- 7. Основной сценарий ----------

def main():
    # 1–3. Загрузка, статистика, предобработка, train/test split
    df = load_titanic("train.csv")
    basic_statistics(df)

    X, y, feature_names = preprocess(df)
    print("Используемые признаки:", feature_names, "\n")

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    print(f"Размер обучающей выборки: {X_train.shape[0]}")
    print(f"Размер тестовой выборки: {X_test.shape[0]}\n")

    # 4–6. Обучение и исследование гиперпараметров
    results_df = hyperparameter_search(X_train, y_train, X_test, y_test)
    print("\nРезультаты исследования гиперпараметров (отсортировано по F1):")
    print(results_df.to_string(index=False))


if __name__ == "__main__":
    main()
