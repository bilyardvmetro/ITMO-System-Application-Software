import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from scipy import stats

data = pd.read_csv("fifa_players_stats.csv", sep=";")
data = data[["Overall", "Nationality"]].dropna()
bins = [0, 60, 75, 100]
labels = ["Низкий", "Средний", "Высокий"]
data["Rating_Category"] = pd.cut(data["Overall"], bins=bins, labels=labels, include_lowest=True)
top_nationalities = data["Nationality"].value_counts().head(5).index
data = data[data["Nationality"].isin(top_nationalities)]
contingency_table = pd.crosstab(data["Rating_Category"], data["Nationality"])
expected = stats.contingency.expected_freq(contingency_table)
if np.any(expected < 5):
    print("Некоторые ожидаемые частоты < 5, результаты могут быть ненадежными.")
chi_square_stat, p_value_chi, df, _ = stats.chi2_contingency(contingency_table)
print("Критерий хи-квадрат независимости:")
print(f"Статистика хи-квадрат: {chi_square_stat:.4f}")
print(f"Степени свободы: {df}")
print(f"P-value: {p_value_chi:.4f}")
if p_value_chi < 0.05:
    print("Отвергаем H0: рейтинг и национальность зависимы.")
else:
    print("Не отвергаем H0: рейтинг и национальность независимы.")
plt.figure(figsize=(10, 6))
sns.heatmap(contingency_table, annot=True, fmt="d", cmap="YlGnBu")
plt.xlabel("Национальность")
plt.ylabel("Категория рейтинга")
plt.savefig("task_3_chi_square_heatmap.png")
plt.close()
n = contingency_table.sum().sum()
phi2 = chi_square_stat / n
r, k = contingency_table.shape
cramer_v = np.sqrt(phi2 / min(r-1, k-1))
print("\nКритерий Крамера V:")
print(f"Коэффициент Крамера V: {cramer_v:.4f}")
if cramer_v < 0.1:
    print("Слабая ассоциация между рейтингом и национальностью.")
elif cramer_v < 0.3:
    print("Умеренная ассоциация между рейтингом и национальностью.")
else:
    print("Сильная ассоциация между рейтингом и национальностью.")

from statsmodels.graphics.mosaicplot import mosaic

plt.figure(figsize=(12, 8))
mosaic(data, ["Rating_Category", "Nationality"])
plt.savefig("task_3_cramer_mosaic.png")
plt.close()
