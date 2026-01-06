import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm, chi2, kstest

# Загрузка данных
df = pd.read_csv("fifa_players_stats.csv", sep=";")  # Укажите путь к файлу
ratings = df['Overall'].dropna()
n = len(ratings)

# Параметры нормального распределения
mu, sigma = ratings.mean(), ratings.std()

# --- Визуализация распределения ---
plt.figure(figsize=(10, 6))
count, bins, ignored = plt.hist(ratings, bins=15, density=True, alpha=0.6, color='skyblue', label='Empirical')
plt.plot(bins, norm.pdf(bins, mu, sigma), 'r--', label='Normal PDF')
plt.title('Распределение рейтинга Overall игроков')
plt.xlabel('Overall')
plt.ylabel('Плотность')
plt.legend()
plt.grid(True)
plt.show()

# --- Критерий хи-квадрат Пирсона ---
# Количество интервалов по правилу Стерджеса
k = int(np.ceil(1 + np.log2(n)))
counts, bin_edges = np.histogram(ratings, bins=k)
expected_freqs = []

# Ожидаемые частоты
for i in range(len(bin_edges) - 1):
    p = norm.cdf(bin_edges[i+1], mu, sigma) - norm.cdf(bin_edges[i], mu, sigma)
    expected_freqs.append(p * n)

chi_square_stat = np.sum((counts - expected_freqs) ** 2 / expected_freqs)
df_chi = k - 1 - 2  # минус 2 параметра: μ и σ
critical_value = chi2.ppf(0.95, df_chi)
p_value_chi = 1 - chi2.cdf(chi_square_stat, df_chi)

print("=== Критерий Пирсона ===")
print(f"Хи-квадрат статистика: {chi_square_stat:.2f}")
print(f"Критическое значение (95%): {critical_value:.2f}")
print(f"p-value: {p_value_chi:.4f}")
print("Гипотеза отвергнута" if chi_square_stat > critical_value else "Нет оснований отвергать гипотезу")

# --- Критерий Колмогорова-Смирнова ---
normalized_ratings = (ratings - mu) / sigma
ks_statistic, ks_p_value = kstest(normalized_ratings, 'norm')

print("\n=== Критерий Колмогорова-Смирнова ===")
print(f"KS-статистика: {ks_statistic:.4f}")
print(f"p-value: {ks_p_value:.4e}")
print("Гипотеза отвергнута" if ks_p_value < 0.05 else "Нет оснований отвергать гипотезу")

