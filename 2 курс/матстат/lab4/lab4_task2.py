import pandas as pd
from matplotlib import pyplot as plt
import seaborn as sns
from scipy import stats


def calculate_mean(data):
    """Вычисляет среднее значение списка чисел."""
    return sum(data) / len(data)

def calculate_ss_between(overall_mean, group_means, group_sizes):
    """Вычисляет межгрупповую сумму квадратов (SSB)."""
    ss_between = 0
    for i in range(len(group_means)):
        ss_between += group_sizes[i] * (group_means[i] - overall_mean)**2
    return ss_between

def calculate_ss_within(groups):
    """Вычисляет внутригрупповую сумму квадратов (SSW)."""
    ss_within = 0
    for group in groups:
        group_mean = calculate_mean(group)
        for x in group:
            ss_within += (x - group_mean)**2
    return ss_within

def calculate_df_between(num_groups):
    """Вычисляет степени свободы между группами (df_between)."""
    return num_groups - 1

def calculate_df_within(total_observations, num_groups):
    """Вычисляет степени свободы внутри групп (df_within)."""
    return total_observations - num_groups

def calculate_ms_between(ss_between, df_between):
    """Вычисляет средний квадрат между группами (MSB)."""
    return ss_between / df_between

def calculate_ms_within(ss_within, df_within):
    """Вычисляет средний квадрат внутри групп (MSW)."""
    return ss_within / df_within

def calculate_f_statistic(ms_between, ms_within):
    """Вычисляет F-статистику."""
    return ms_between / ms_within

def manual_one_way_anova(data, factor_column, dependent_column):
    """
    Выполняет однофакторный дисперсионный анализ вручную.

    Args:
        data (pd.DataFrame): DataFrame с данными.
        factor_column (str): Название столбца с фактором (ценовая категория).
        dependent_column (str): Название столбца с зависимой переменной (емкость аккумулятора).

    Returns:
        dict: Словарь с результатами ANOVA (F-статистика, степени свободы).
    """
    groups_data = data.groupby(factor_column, observed=True)[dependent_column].apply(list).tolist()
    group_means = [calculate_mean(group) for group in groups_data]
    group_sizes = [len(group) for group in groups_data]
    overall_data = data[dependent_column].tolist()
    overall_mean = calculate_mean(overall_data)
    num_groups = len(groups_data)
    total_observations = len(overall_data)

    ss_between = calculate_ss_between(overall_mean, group_means, group_sizes)
    ss_within = calculate_ss_within(groups_data)

    df_between = calculate_df_between(num_groups)
    df_within = calculate_df_within(total_observations, num_groups)

    ms_between = calculate_ms_between(ss_between, df_between)
    ms_within = calculate_ms_within(ss_within, df_within)

    f_statistic = calculate_f_statistic(ms_between, ms_within)

    results = {
        'F-статистика': f_statistic,
        'df_между': df_between,
        'df_внутри': df_within
    }
    return results

# 1. Load the data
file_path = 'mobile_phones.csv'  # Замените на фактический путь к вашему CSV файлу
try:
    data = pd.read_csv(file_path)
except FileNotFoundError:
    print(f"Ошибка: Файл не найден по пути '{file_path}'")
    exit()

# Assuming your data has columns named 'ценовая категория' and 'емкость аккумулятора'
factor_column = 'price_range'
dependent_column = 'battery_power'

# Ensure the factor column is treated as categorical
data[factor_column] = data[factor_column].astype('category')

# 2. Perform manual one-way ANOVA
anova_results = manual_one_way_anova(data.copy(), factor_column, dependent_column)

# 3. Print the results
print("Результаты однофакторного дисперсионного анализа (ANOVA) (ручной расчет):")
for key, value in anova_results.items():
    print(f"{key}: {value:.4f}")

print("\nДля определения p-значения и принятия решения о гипотезе,")
print("необходимо обратиться к F-распределению с соответствующими степенями свободы (df_между и df_внутри).")
print("P-значение можно найти с помощью таблиц F-распределения или статистических функций в библиотеках. Рассчитаем его при помощи scipy.stats.f.sf.")

# 4. Visualize the data
plt.figure(figsize=(10, 6))
sns.boxplot(x=factor_column, y=dependent_column, data=data)
plt.title(f'Распределение емкости аккумулятора по {factor_column}')
plt.xlabel(factor_column)
plt.ylabel('Емкость аккумулятора')
plt.grid(True)
plt.show()

# Visualize the means with error bars
group_means = data.groupby(factor_column, observed=True)[dependent_column].mean()
group_stds = data.groupby(factor_column, observed=True)[dependent_column].std()

plt.figure(figsize=(8, 5))
group_means.plot(kind='bar', yerr=group_stds, capsize=5, color='skyblue')
plt.title(f'Средняя емкость аккумулятора по {factor_column} с стандартным отклонением')
plt.xlabel(factor_column)
plt.ylabel('Средняя емкость аккумулятора')
plt.xticks(rotation=45, ha='right')
plt.tight_layout()
plt.grid(axis='y')
plt.show()

# Пример расчета p-значения (требуется scipy.stats)
try:
    p_value = stats.f.sf(anova_results['F-статистика'], anova_results['df_между'], anova_results['df_внутри'])
    alpha = 0.05  # Уровень значимости
    print(f"\nP-значение (рассчитано с помощью scipy.stats): {p_value:.4f}")
    if p_value < alpha:
        print("Отвергаем нулевую гипотезу.")
        print("Существуют статистически значимые различия в среднем значении емкости аккумулятора между различными ценовыми категориями.")
    else:
        print("Не отвергаем нулевую гипотезу.")
        print("Нет статистически значимых различий в среднем значении емкости аккумулятора между различными ценовыми категориями на уровне значимости 0.05.")
except ImportError:
    print("\nДля автоматического расчета p-значения установите библиотеку scipy: pip install scipy")

# --- Сравнение с встроенной функцией ANOVA из scipy ---
# Группируем данные по фактору
grouped_data = [group[dependent_column].values for name, group in data.groupby(factor_column, observed=True)]

# Выполняем ANOVA с помощью scipy
f_stat_scipy, p_value_scipy = stats.f_oneway(*grouped_data)

print("\nРезультаты встроенной функции scipy.stats.f_oneway:")
print(f"F-статистика: {f_stat_scipy:.4f}")
print(f"P-значение: {p_value_scipy:.4f}")

# Сравнение с ручным результатом
print("\nСравнение с ручным расчетом:")
print(f"F-статистика (ручной расчет): {anova_results['F-статистика']:.4f}")
