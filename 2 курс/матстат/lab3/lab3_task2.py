import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import mannwhitneyu, chi2

# Загрузка данных
df = pd.read_csv("fifa_players_stats.csv", sep=";")

# Отбираем нужные столбцы
df = df[['Age', 'Overall']].dropna()

# Разделение по возрасту
age_threshold = 25
young = df[df['Age'] < age_threshold]['Overall']
old = df[df['Age'] >= age_threshold]['Overall']

print(f"Молодых игроков: {len(young)}, Возрастных игроков: {len(old)}")

# Визуализация распределений
plt.figure(figsize=(10, 6))
plt.hist(young, bins=15, alpha=0.6, label=f'Молодые (<{age_threshold})', color='skyblue', density=True)
plt.hist(old, bins=15, alpha=0.6, label=f'Возрастные (≥{age_threshold})', color='orange', density=True)
plt.title('Сравнение распределений рейтинга Overall')
plt.xlabel('Overall')
plt.ylabel('Плотность')
plt.legend()
plt.grid(True)
plt.show()

# === Критерий Манна-Уитни ===
u_stat, p_value = mannwhitneyu(young, old, alternative='two-sided')

print("\n=== Критерий Манна-Уитни ===")
print(f"U-статистика: {u_stat:.2f}")
print(f"p-value: {p_value:.4f}")
if p_value < 0.05:
    print("Распределения отличаются (гипотеза об однородности отвергнута)")
else:
    print("Нет оснований отвергать гипотезу об однородности")

# === Критерий однородности хи-квадрат ===

# Объединяем выборки и создаём интервалы
combined = pd.concat([young, old])
bins = np.histogram_bin_edges(combined, bins='sturges')

# Считаем частоты в каждом интервале
young_counts, _ = np.histogram(young, bins=bins)
old_counts, _ = np.histogram(old, bins=bins)

# Формируем таблицу наблюдаемых значений
observed = np.array([young_counts, old_counts])

# Суммы по строкам и столбцам
row_sums = observed.sum(axis=1, keepdims=True)
col_sums = observed.sum(axis=0, keepdims=True)
total = observed.sum()

# Вычисляем ожидаемые значения
expected = row_sums @ col_sums / total

# Хи-квадрат статистика
chi2_stat = ((observed - expected) ** 2 / expected).sum()

# Степени свободы: (кол-во интервалов - 1)
df_chi2 = len(bins) - 2  # -1 за количество интервалов, -1 за строку (2 строки)
p_val_chi2 = 1 - chi2.cdf(chi2_stat, df_chi2)

print("\n=== Критерий однородности Хи-квадрат ===")
print(f"Хи-квадрат статистика: {chi2_stat:.2f}")
print(f"p-value: {p_val_chi2:.4f}")
if p_val_chi2 < 0.05:
    print("Распределения неоднородны (гипотеза отвергнута)")
else:
    print("Нет оснований отвергать гипотезу об однородности")
