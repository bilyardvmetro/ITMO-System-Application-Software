import math

import numpy as np
from matplotlib import pyplot as plt
from scipy.stats import t
from statsmodels.tsa.stattools import acf

# --- Константы и настройки ---
FILE_NAME = 'data.txt'
# FILE_NAME = 'generated_sequence.txt'
# Размеры выборок для анализа
SAMPLE_SIZES = [10, 20, 50, 100, 200, 300]
# Доверительные вероятности (1 - alpha)
CONFIDENCE_LEVELS = [0.90, 0.95, 0.99]
# Уровень значимости для построения доверительного интервала на графике ACF.
# Для проверки гипотезы о том, что последовательность является белым шумом (случайной),
# используется уровень значимости 5% (альфа=0.05), что соответствует 95% доверительному интервалу.
ALPHA = 0.05

# --- КОНСТАНТЫ (скопированы из plot_sequence.py) ---
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        # Порог для равномерного распределения: C_v <= 1/sqrt(3) ≈ 0.57735
UNIFORM_CV_THRESHOLD = 1 / math.sqrt(3)
# Допуск для проверки C_v ≈ 1
EXPONENTIAL_CV_TOLERANCE = 0.01


def read_data(filename):
    """
    Считывает данные из файла, заменяя запятую на точку и преобразуя в список чисел.
    """
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            # Читаем строки, убираем пробелы и символы новой строки, заменяем запятую на точку
            data_str = [line.strip().replace(',', '.') for line in f if line.strip()]
        return np.array([float(x) for x in data_str])
    except FileNotFoundError:
        print(f"Ошибка: Файл '{filename}' не найден. Убедитесь, что он находится в той же папке.")
        return np.array([])
    except ValueError:
        print("Ошибка: Данные в файле содержат нечисловые значения.")
        return np.array([])


def calculate_statistics(sample, reference_results=None):
    """
    Рассчитывает все запрашиваемые статистические моменты и интервалы.

    :param sample: NumPy array, текущая выборка данных.
    :param reference_results: Словарь с эталонными результатами для расчета относительного отклонения.
    :return: Словарь с результатами, включая относительные отклонения, если reference_results предоставлен.
    """
    N = len(sample)
    if N < 2:
        return {"Размер выборки слишком мал": N}

    # 1. Основные моменты
    mean_value = np.mean(sample)  # Математическое ожидание (Среднее)
    variance = np.var(sample, ddof=1)  # Дисперсия (несмещенная, ddof=1)
    std_dev = np.std(sample, ddof=1)  # Среднеквадратическое отклонение

    # Коэффициент вариации. Проверка на деление на ноль (хотя для положительных данных это не должно случиться)
    coef_variation = (std_dev / mean_value) * 100 if mean_value != 0 else 0

    results = {
        "Размер выборки (N)": N,
        "Математическое ожидание (Mean)": mean_value,
        "Дисперсия (Variance)": variance,
        "Среднеквадратическое отклонение (Std Dev)": std_dev,
        "Коэффициент вариации (%)": coef_variation,
        "Доверительные интервалы для Mean": {}  # Будет хранить (mean, margin_of_error)
    }

    # 2. Доверительные интервалы для оценки математического ожидания
    degrees_of_freedom = N - 1
    std_error = std_dev / np.sqrt(N)  # Стандартная ошибка среднего

    for confidence_level in CONFIDENCE_LEVELS:
        alpha = 1 - confidence_level
        t_critical = t.ppf(1 - alpha / 2, degrees_of_freedom)

        margin_of_error = t_critical * std_error

        # Сохраняем (среднее, предельная ошибка)
        results["Доверительные интервалы для Mean"][f"{confidence_level * 100:.0f}%"] = \
            (mean_value, margin_of_error)

    # 3. Расчет относительных отклонений от эталонных значений
    if reference_results is not None:
        relative_deviations = {}

        # Вспомогательная функция для расчета относительного отклонения
        def calc_dev(current_val, ref_val):
            # Проверяем, чтобы избежать деления на ноль, если эталонное значение близко к нулю
            return abs(current_val - ref_val) / abs(ref_val) * 100 if abs(ref_val) > 1e-9 else 0

        # Отклонение Математического ожидания
        ref_mean = reference_results['Математическое ожидание (Mean)']
        relative_deviations['Mean (%)'] = calc_dev(mean_value, ref_mean)

        # Отклонение Дисперсии
        ref_variance = reference_results['Дисперсия (Variance)']
        relative_deviations['Variance (%)'] = calc_dev(variance, ref_variance)

        # Отклонение Среднеквадратического отклонения
        ref_std_dev = reference_results['Среднеквадратическое отклонение (Std Dev)']
        relative_deviations['Std Dev (%)'] = calc_dev(std_dev, ref_std_dev)

        # Отклонение Коэффициента вариации
        ref_coef_variation = reference_results['Коэффициент вариации (%)']
        relative_deviations['Коэффициент вариации (%)'] = calc_dev(coef_variation, ref_coef_variation)

        # Отклонение Доверительных интервалов (сравниваем только предельные ошибки)
        relative_deviations['Доверительные интервалы (%)'] = {}
        for level, (mean, margin) in results["Доверительные интервалы для Mean"].items():
            # Эталонный результат содержит (mean, margin_of_error)
            ref_mean_ref, ref_margin = reference_results["Доверительные интервалы для Mean"][level]

            # Отклонение предельной ошибки
            dev_margin = calc_dev(margin, ref_margin)

            relative_deviations['Доверительные интервалы (%)'][level] = {
                "Предельная ошибка (Margin of Error)": dev_margin,
            }

        results["Относительные отклонения от эталона"] = relative_deviations

    return results


def print_results(results_dict, sample_size):
    """
    Форматированный вывод результатов для одной выборки, включая относительные отклонения.
    """
    print("=" * 70)
    print(f"РАСЧЕТЫ ДЛЯ ВЫБОРКИ РАЗМЕРОМ N = {sample_size}")
    print("-" * 70)

    print(f"  Математическое ожидание (Mean):             {results_dict['Математическое ожидание (Mean)']:.4f}")
    print(f"  Дисперсия (Variance):                       {results_dict['Дисперсия (Variance)']:.4f}")
    print(
        f"  Среднеквадратическое отклонение (Std Dev):  {results_dict['Среднеквадратическое отклонение (Std Dev)']:.4f}")
    print(f"  Коэффициент вариации:                       {results_dict['Коэффициент вариации (%)']:.2f} %")

    print("  Доверительные интервалы для Mean:")
    # Интервалы теперь содержат (среднее, предельная ошибка)
    for level, (mean_value, margin_of_error) in results_dict["Доверительные интервалы для Mean"].items():
        print(
            f"    - {level}: {mean_value:.4f} \u00B1 {margin_of_error:.4f} (Margin of Error)")  # Используем символ плюс-минус

    # Вывод относительных отклонений
    if "Относительные отклонения от эталона" in results_dict:
        deviations = results_dict["Относительные отклонения от эталона"]
        print("\n  Относительные отклонения от эталона (в %):")

        print(f"    - Mean:                               {deviations['Mean (%)']:.4f} %")
        print(f"    - Variance:                           {deviations['Variance (%)']:.4f} %")
        print(f"    - Std Dev:                            {deviations['Std Dev (%)']:.4f} %")
        print(f"    - Коэффициент вариации:               {deviations['Коэффициент вариации (%)']:.4f} %")

        print("    - Доверительные интервалы (отклонения предельной ошибки):")
        for level, dev in deviations["Доверительные интервалы (%)"].items():
            print(f"      - {level}:                          {dev['Предельная ошибка (Margin of Error)']:.4f} %")

    print("=" * 70 + "\n")


def plot_sequence(data):
    """
    Строит график числовой последовательности.

    :param data: NumPy array с числовыми значениями.
    """
    N = len(data)
    if N == 0:
        print("Нет данных для построения графика.")
        return

    # Ось X: Порядковый номер значения (от 1 до N)
    x_indices = np.arange(1, N + 1)

    plt.figure(figsize=(12, 6))  # Устанавливаем размер фигуры

    # Строим график: значение (Y) по порядковому номеру (X)
    plt.plot(x_indices, data,
             linestyle='-',  # Сплошная линия
             marker='o',  # Круглые маркеры для каждой точки
             markersize=4,  # Размер маркеров
             color='#1f77b4',  # Стандартный синий цвет
             alpha=0.7,  # Прозрачность для лучшей видимости
             label='Значение $x_i$')

    # Добавление заголовков и подписей
    plt.title('График числовой последовательности из data.txt', fontsize=16)
    plt.xlabel('Порядковый номер значения ($i$)', fontsize=12)
    plt.ylabel('Значение', fontsize=12)

    # Добавляем сетку
    plt.grid(True, linestyle='--', alpha=0.6)

    # Добавляем легенду, если есть метки
    plt.legend()

    # Улучшаем расположение элементов, чтобы избежать обрезания подписей
    plt.tight_layout()

    # Сохранение графика в файл
    output_filename = 'sequence_plot.png'
    try:
        plt.savefig(output_filename, dpi=300)
        print(f"\nГрафик успешно сохранен в файл '{output_filename}'")
    except Exception as e:
        print(f"Ошибка при сохранении графика: {e}")


def perform_autocorrelation_analysis(data):
    """
    Выполняет автокорреляционный анализ, выводит коэффициенты и строит коррелограмму.

    :param data: NumPy array с числовыми значениями.
    """
    N = len(data)
    if N < 2:
        print("\nНедостаточно данных для автокорреляционного анализа (N < 2).")
        return

    # Максимальный лаг (обычно N/4 или N/3)
    max_lag = 10

    # Расчет автокорреляционной функции (ACF)
    # fft=True ускоряет расчет, nlags - максимальный лаг, alpha - для расчета доверительного интервала
    # cft - массив коэффициентов ACF, confint - 95% доверительный интервал
    acf_coeffs, confint = acf(data, nlags=max_lag, alpha=ALPHA, fft=True)

    # --- Табличное представление коэффициентов ---
    print("\n" + "=" * 70)
    print("РЕЗУЛЬТАТЫ АВТОКОРРЕЛЯЦИОННОГО АНАЛИЗА")
    print("-" * 70)
    print(f"N = {N} значений, Максимальный лаг = {max_lag}")
    print(f"Критический уровень значимости (alpha) для CI: {ALPHA * 100:.0f}%")
    print("-" * 70)
    print(f"{'Лаг (k)':<10} | {'Коэффициент ACF r(k)':<25} | {'Граница CI (95%)':<25}")
    print("-" * 70)

    is_random = True  # Флаг для проверки случайности

    # Выводим коэффициенты, начиная с лага k=1 (r(0)=1 не включаем)
    for k in range(1, max_lag + 1):
        r_k = acf_coeffs[k]
        # Граница доверительного интервала (берем положительное значение из confint)
        ci_bound = confint[k][1] - r_k

        print(f"{k:<10} | {r_k:<25.4f} | {ci_bound:<25.4f}")

        # Проверка случайности: если коэффициент выходит за пределы CI, последовательность неслучайна
        if abs(r_k) > ci_bound:
            is_random = False

    print("-" * 70)

    # Вывод результата проверки случайности
    if is_random:
        print("ВЫВОД: Последовательность можно считать случайной (White Noise) на уровне 5%.")
        print("Все коэффициенты автокорреляции (r(k) для k>0) находятся в пределах доверительного интервала.")
    else:
        print("ВЫВОД: Последовательность НЕ является случайной.")
        print(
            "По крайней мере один коэффициент автокорреляции (r(k) для k>0) ВЫХОДИТ за пределы доверительного интервала, что указывает на наличие зависимости.")
    print("=" * 70 + "\n")

    # --- Графическое представление (Коррелограмма) ---

    plt.figure(figsize=(12, 6))

    # Строим коррелограмму
    plt.bar(range(max_lag + 1), acf_coeffs, width=0.1, color='#ff7f0e', alpha=0.9, label='Коэффициент ACF')

    # Добавляем доверительный интервал (CI)
    # Верхняя граница CI (confint[:, 1]) и Нижняя граница CI (confint[:, 0])
    plt.plot(confint[:, 1], linestyle='--', color='red', label='95% Доверительный интервал')
    plt.plot(confint[:, 0], linestyle='--', color='red')

    # Добавляем линию r=0
    plt.axhline(y=0, color='black', linewidth=0.8)

    # Добавление заголовков и подписей
    plt.title('Коррелограмма (Autocorrelation Function - ACF)', fontsize=16)
    plt.xlabel('Лаг ($k$)', fontsize=12)
    plt.ylabel('Коэффициент автокорреляции $r(k)$', fontsize=12)

    # Ограничение оси Y для лучшей визуализации
    plt.ylim(-1.1 * np.max(confint), 1.1 * np.max(confint))

    plt.legend()
    plt.tight_layout()

    # Сохранение графика в файл
    output_filename = 'autocorrelation_plot.png'
    try:
        plt.savefig(output_filename, dpi=300)
        print(f"График коррелограммы сохранен в файл '{output_filename}'")
    except Exception as e:
        print(f"Ошибка при сохранении графика коррелограммы: {e}")


def calculate_frequency_distribution(data):
    """
    Рассчитывает частотное распределение и выводит таблицу.
    """
    N = len(data)
    if N == 0:
        return None, None, 0

    # 1. Определение количества интервалов (по правилу Стерджеса: k = 1 + log2(N))
    # Это стандартный метод, дающий научно обоснованное количество интервалов.
    k = int(1 + math.log2(N)) if N > 1 else 1

    # 2. Расчет гистограммы: bin_edges - границы интервалов, counts - частоты
    # np.histogram автоматически рассчитывает ширину h
    counts, bin_edges = np.histogram(data, bins=k)

    # Ширина интервала
    h = bin_edges[1] - bin_edges[0]

    # --- Табличное представление распределения частот ---
    print("\n" + "=" * 70)
    print(f"ТАБЛИЦА ЧАСТОТНОГО РАСПРЕДЕЛЕНИЯ (N = {N})")
    print("-" * 70)
    print(f"Количество интервалов (по правилу Стерджеса): k = {k}")
    print(f"Ширина интервала: h ≈ {h:.4f}")
    print("-" * 70)

    # Заголовки таблицы, похожие на формат на картинке
    print(f"{'№':<5} | {'Левая граница':<20} | {'Правая граница':<20} | {'Частота (n_i)':<15}")
    print("-" * 70)

    # Вывод данных
    for i in range(k):
        left_bound = bin_edges[i]
        right_bound = bin_edges[i + 1]
        frequency = counts[i]

        # Обработка последнего интервала: для гистограммы правая граница включается
        # только в последний интервал, но для вывода оставим общий формат.
        boundary_info = f"[{left_bound:.4f}; {right_bound:.4f})"
        if i == k - 1:
            boundary_info = f"[{left_bound:.4f}; {right_bound:.4f}]"

        print(f"{i + 1:<5} | {left_bound:<20.4f} | {right_bound:<20.4f} | {frequency:<15}")

    print("-" * 70)
    print(f"{'Итого':<5} | {'-':<20} | {'-':<20} | {np.sum(counts):<15}")
    print("=" * 70 + "\n")

    return counts, bin_edges, k


def plot_histogram(counts, bin_edges, k):
    """
    Строит гистограмму распределения частот.
    """
    if counts is None or len(counts) == 0:
        return

    plt.figure(figsize=(12, 6))

    # Строим гистограмму: передаем границы и веса (частоты)
    plt.hist(bin_edges[:-1], bin_edges, weights=counts,
             edgecolor='black',
             color='#2ca02c',
             alpha=0.8)

    # Добавление заголовков и подписей
    plt.title(f'Гистограмма распределения частот (k={k} интервалов)', fontsize=16)
    plt.xlabel('Значение', fontsize=12)
    plt.ylabel('Частота (количество попаданий)', fontsize=12)

    # Обозначение границ интервалов на оси X
    plt.xticks(bin_edges, rotation=45, ha='right')

    plt.grid(axis='y', linestyle='--', alpha=0.6)
    plt.tight_layout()

    # Сохранение графика
    output_filename = 'frequency_histogram.png'
    try:
        plt.savefig(output_filename, dpi=300)
        print(f"Гистограмма распределения частот сохранена в файл '{output_filename}'")
    except Exception as e:
        print(f"Ошибка при сохранении гистограммы: {e}")


def perform_distribution_approximation(data):
    """
    Выполняет аппроксимацию закона распределения по двум начальным моментам
    (выборочное среднее и среднеквадратическое отклонение),
    выбирая между равномерным, экспоненциальным, Эрланга или гиперэкспоненциальным
    распределениями на основе коэффициента вариации (Cv).

    :param data: NumPy array с числовыми значениями.
    :return: Словарь с результатами аппроксимации.
    """
    N = len(data)
    if N < 2:
        print("\n[Аппроксимация] Недостаточно данных для расчета.")
        return None

    # 1. Расчет моментов
    # Математическое ожидание (выборочное среднее)
    mean_x = np.mean(data)
    # Среднеквадратическое отклонение (s, несмещенная оценка, ddof=1)
    # ddof=1 дает несмещенную оценку, как в стандартном статистическом анализе.
    std_dev_s = np.std(data, ddof=1)

    # Коэффициент вариации
    cv = std_dev_s / mean_x

    results = {
        "mean_x": mean_x,
        "std_dev_s": std_dev_s,
        "cv": cv,
        "distribution": "N/A",
        "parameters": {},
        "formula": "N/A"
    }

    # Вывод в консоль для наглядности
    print("\n" + "=" * 80)
    print("АППРОКСИМАЦИЯ ЗАКОНА РАСПРЕДЕЛЕНИЯ ПО ДВУМ МОМЕНТАМ")
    print("-" * 80)
    print(f"Выборочное среднее (Мат. ожидание): $\\bar{{x}} \\approx {mean_x:.4f}$")
    print(f"Среднеквадратическое отклонение: $s \\approx {std_dev_s:.4f}$")
    print(f"Коэффициент вариации: $C_v = s / \\bar{{x}} \\approx {cv:.4f}$")
    print("-" * 80)

    # 2. Выбор распределения на основе C_v

    if abs(cv - 1) < EXPONENTIAL_CV_TOLERANCE:
        # 1. Экспоненциальное распределение: C_v ≈ 1
        lambda_rate = 1.0 / mean_x

        results["distribution"] = "Экспоненциальное"
        results["parameters"] = {"lambda": lambda_rate}
        results["formula"] = "$f(x) = \\lambda e^{-\\lambda x}$"

        print(f"ВЫБОР: Экспоненциальное распределение (т.к. $C_v \\approx 1$).")
        print(f"Параметры: $\\lambda \\approx {lambda_rate:.4f}$")

    elif cv < 1:
        # 2. Нормированный Эрланга / Гипоэкспоненциальное: C_v < 1

        if cv <= UNIFORM_CV_THRESHOLD:
            # Равномерное распределение
            a = mean_x - math.sqrt(3) * std_dev_s
            b = mean_x + math.sqrt(3) * std_dev_s

            results["distribution"] = "Равномерное"
            results["parameters"] = {"a": a, "b": b}
            results["formula"] = "$f(x) = 1 / (b - a)$ для $a \\le x \\le b$"

            print(f"ВЫБОР: Равномерное распределение (т.к. $C_v \\le {UNIFORM_CV_THRESHOLD:.4f}$).")
            print(f"Параметры: $a \\approx {a:.4f}$, $b \\approx {b:.4f}$")

        else:
            # Распределение Эрланга (Нормированный k-го порядка)
            # k = ceil(1 / C_v^2), lambda = k / mean_x
            k_order = int(math.ceil(1 / (cv * cv)))
            lambda_rate = k_order / mean_x

            results["distribution"] = f"Нормированный Эрланга k={k_order}"
            results["parameters"] = {"k": k_order, "lambda": lambda_rate}
            results["formula"] = "$f(x) = \\frac{\\lambda^k x^{k-1}}{(k-1)!} e^{-\\lambda x}$"

            print(f"ВЫБОР: Нормированный Эрланга $k$-го порядка (т.к. $C_v < 1$ и $C_v > {UNIFORM_CV_THRESHOLD:.4f}$).")
            print(f"Параметры: $k \\approx {k_order}$, $\\lambda \\approx {lambda_rate:.4f}$")


    else:  # cv > 1
        # 3. Гиперэкспоненциальное распределение (двухфазное H2): C_v > 1

        cv2_minus_1 = cv * cv - 1

        # Расчет параметров p, lambda_slow, lambda_fast
        # sqrt_term используется для упрощения расчетов
        sqrt_term = math.sqrt(cv2_minus_1 / (cv * cv + 1))

        p_slow = 0.5 * (1 - sqrt_term)
        p_fast = 1.0 - p_slow

        lambda_slow = 1.0 / (mean_x * (1 + sqrt_term))
        lambda_fast = 1.0 / (mean_x * (1 - sqrt_term))

        results["distribution"] = "Гиперэкспоненциальное H2"
        results["parameters"] = {
            "p1": p_slow, "lambda1": lambda_slow,
            "p2": p_fast, "lambda2": lambda_fast
        }
        results["formula"] = "$f(x) = p_1 \\lambda_1 e^{-\\lambda_1 x} + p_2 \\lambda_2 e^{-\\lambda_2 x}$"

        print(f"ВЫБОР: Гиперэкспоненциальное $H_2$ (т.к. $C_v > 1$).")
        print("Параметры (Двухфазное распределение):")
        print(f"  $p_1$ (Вероятность фазы 1) $\\approx {p_slow:.4f}$")
        print(f"  $\\lambda_1$ (Интенсивность фазы 1) $\\approx {lambda_slow:.4f}$")
        print(f"  $p_2$ (Вероятность фазы 2) $\\approx {p_fast:.4f}$")
        print(f"  $\\lambda_2$ (Интенсивность фазы 2) $\\approx {lambda_fast:.4f}$")

    print("-" * 80)
    print(f"Теоретическая плотность вероятности $f(x)$: {results['formula']}")
    print("=" * 80 + "\n")

    return results


def main():
    """Главная функция выполнения анализа."""

    # 1. Чтение и подготовка данных
    all_data = read_data(FILE_NAME)
    if all_data.size == 0:
        return

    max_size = max(SAMPLE_SIZES)
    if all_data.size < max_size:
        print(f"Предупреждение: В файле всего {all_data.size} значений, но запрошена выборка до {max_size}.")
        # Используем все доступные данные для максимальной выборки
        final_sample_size = all_data.size
    else:
        final_sample_size = max_size

    # 2. Определение эталонных значений (для самой большой выборки)
    reference_sample = all_data[:final_sample_size]
    # Вычисляем ПОЛНЫЙ набор статистик для эталона
    reference_results = calculate_statistics(reference_sample)

    print(f"Данные успешно считаны. Общее количество значений: {all_data.size}")
    print(
        f"Эталонное Математическое ожидание (N={final_sample_size}): {reference_results['Математическое ожидание (Mean)']:.4f}\n")

    all_results = {}

    # 3. Итерация по всем заданным размерам выборок
    for size in SAMPLE_SIZES:
        if size > all_data.size:
            print(f"Пропуск: Недостаточно данных для выборки N={size}.")
            continue

        current_sample = all_data[:size]

        if size == final_sample_size:
            # Для эталонной выборки используем уже посчитанные reference_results
            results = reference_results
        else:
            # Для меньших выборок рассчитываем статистику и отклонения относительно эталона
            results = calculate_statistics(current_sample, reference_results)

        # Вывод результатов
        print_results(results, size)

        all_results[size] = results

    plot_sequence(all_data)
    perform_autocorrelation_analysis(all_data)

    counts, bin_edges, k = calculate_frequency_distribution(all_data)
    plot_histogram(counts, bin_edges, k)
    results = perform_distribution_approximation(all_data)
    print("\n[Результаты (словарь)]:", results)


if __name__ == "__main__":
    main()
