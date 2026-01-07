import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import confusion_matrix, accuracy_score
import random

# Загрузка датасета
df = pd.read_csv("diabetes.csv")
print("Размер датасета:", df.shape)
print(df.head())

# Проверим пропуски
print(df.isnull().sum())

# Заполним возможные 0 в некоторых признаках медианами
cols_with_zero = ["Glucose", "BloodPressure", "SkinThickness", "Insulin", "BMI"]
for col in cols_with_zero:
    df[col] = df[col].replace(0, df[col].median())

# Масштабирование признаков
features = df.drop("Outcome", axis=1)
target = df["Outcome"]

scaler = StandardScaler()
X_scaled = scaler.fit_transform(features)
y = target.values

desc = df.describe()
print(desc)

# Гистограммы признаков
df.hist(figsize=(12, 10))
plt.suptitle("Распределение признаков диабета")
plt.show()

# 3D визуализация (пример)
fig = plt.figure(figsize=(8, 6))
ax = fig.add_subplot(111, projection='3d')
ax.scatter(df["Glucose"], df["BMI"], df["Age"], c=df["Outcome"], cmap='coolwarm')
ax.set_xlabel("Glucose")
ax.set_ylabel("BMI")
ax.set_zlabel("Age")
plt.title("3D визуализация признаков")
plt.show()

def euclidean_distance(a, b):
    return np.sqrt(np.sum((a - b) ** 2))

def knn_predict(X_train, y_train, X_test, k):
    y_pred = []
    for x in X_test:
        # Расстояния до всех обучающих
        distances = [euclidean_distance(x, x_train) for x_train in X_train]
        # Индексы k ближайших
        k_indices = np.argsort(distances)[:k]
        # Метки
        k_labels = y_train[k_indices]
        # Голосование
        values, counts = np.unique(k_labels, return_counts=True)
        y_pred.append(values[np.argmax(counts)])
    return np.array(y_pred)

# Разделение данных (80/20)
np.random.seed(42)
indices = np.arange(len(X_scaled))
np.random.shuffle(indices)

train_size = int(0.8 * len(indices))
train_idx, test_idx = indices[:train_size], indices[train_size:]

X_train, X_test = X_scaled[train_idx], X_scaled[test_idx]
y_train, y_test = y[train_idx], y[test_idx]

def select_random_features(X, num_features):
    feature_indices = random.sample(range(X.shape[1]), num_features)
    return X[:, feature_indices], feature_indices

X_train_m1, feat_idx_m1 = select_random_features(X_train, 4)
X_test_m1 = X_test[:, feat_idx_m1]

fixed_features = ["Glucose", "BMI", "Age", "BloodPressure"]
feat_idx_m2 = [df.columns.get_loc(f) - 1 for f in fixed_features]  # -1 из-за Outcome
X_train_m2 = X_train[:, feat_idx_m2]
X_test_m2 = X_test[:, feat_idx_m2]

def evaluate_model(X_train, y_train, X_test, y_test, k_values, model_name):
    print(f"\n=== {model_name} ===")
    for k in k_values:
        y_pred = knn_predict(X_train, y_train, X_test, k)
        acc = accuracy_score(y_test, y_pred)
        cm = confusion_matrix(y_test, y_pred)
        print(f"k={k} | Accuracy={acc:.3f}")
        print("Confusion matrix:\n", cm)
        print("-" * 30)

# Проверим для разных k
k_values = [3, 5, 7, 10]

evaluate_model(X_train_m1, y_train, X_test_m1, y_test, k_values, "Модель 1 (случайные признаки)")
evaluate_model(X_train_m2, y_train, X_test_m2, y_test, k_values, "Модель 2 (фиксированные признаки)")

