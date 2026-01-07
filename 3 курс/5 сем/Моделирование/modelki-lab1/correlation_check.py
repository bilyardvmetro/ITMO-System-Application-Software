import numpy as np
import os


def load_and_clean_data(filepath):
	"""
	Загружает числовые данные из файла, обрабатывая формат с запятыми
	вместо десятичной точки, и возвращает массив numpy.
	"""
	if not os.path.exists(filepath):
		print(f"Ошибка: Файл не найден по пути: {filepath}")
		return None

	try:
		# Читаем файл как строку, заменяем запятые на точки для корректного парсинга чисел
		with open(filepath, 'r', encoding='utf-8') as f:
			content = f.read().replace(',', '.')

		# Загружаем данные из строки
		data = np.fromstring(content, sep='\n')
		return data

	except ValueError:
		print(f"Ошибка: Не удалось преобразовать данные в файле {filepath} в числа.")
		return None
	except Exception as e:
		print(f"Непредвиденная ошибка при загрузке {filepath}: {e}")
		return None


def perform_correlation_analysis(file1, file2):
	"""
	Проводит корреляционный анализ между двумя последовательностями.
	"""
	print("=" * 80)
	print("АНАЛИЗ КОРРЕЛЯЦИИ МЕЖДУ ПОСЛЕДОВАТЕЛЬНОСТЯМИ")
	print("-" * 80)

	# 1. Загрузка и очистка данных
	data_original = load_and_clean_data(file1)
	data_generated = load_and_clean_data(file2)

	if data_original is None or data_generated is None:
		return

	N1 = len(data_original)
	N2 = len(data_generated)

	if N1 != N2:
		print(f"Внимание: Размеры последовательностей не совпадают (N1={N1}, N2={N2}).")

		# Обрезаем большую последовательность до размера меньшей
		min_N = min(N1, N2)
		data_original = data_original[:min_N]
		data_generated = data_generated[:min_N]
		print(f"Для анализа обе последовательности обрезаны до минимального размера: N = {min_N}")

	elif N1 == 0:
		print("Ошибка: Последовательности пусты.")
		return

	# 2. Расчет коэффициента корреляции Пирсона
	# np.corrcoef возвращает матрицу корреляции. Нас интересует элемент [0, 1]
	# (корреляция между первой и второй последовательностями).
	correlation_matrix = np.corrcoef(data_original, data_generated)
	correlation_coefficient = correlation_matrix[0, 1]

	# 3. Вывод результатов
	print("-" * 80)
	print(f"Исходный файл: {file1} (N={len(data_original)})")
	print(f"Сгенерированный файл: {file2} (N={len(data_generated)})")
	print("-" * 80)
	print(f"Коэффициент корреляции Пирсона (r): $\\mathbf{{r \\approx {correlation_coefficient:.4f}}}$")
	print("-" * 80)

	# Интерпретация результата
	if abs(correlation_coefficient) > 0.8:
		interpretation = "Сильная корреляция. Взаимосвязь между последовательностями очень выражена."
	elif abs(correlation_coefficient) > 0.5:
		interpretation = "Умеренная корреляция. Наблюдается заметная взаимосвязь."
	elif abs(correlation_coefficient) > 0.2:
		interpretation = "Слабая корреляция. Взаимосвязь присутствует, но незначительна."
	else:
		interpretation = "Корреляция отсутствует (или крайне слабая). Последовательности, по сути, независимы."

	if correlation_coefficient > 0:
		direction = "положительная"
	else:
		direction = "отрицательная"

	# Примечание:
	# Ожидаемый результат для данного случая (сгенерированная последовательность по моменту)
	# — r ≈ 0, так как процесс генерации использует случайные числа,
	# и нет причин для связи между исходным порядком чисел и новым.

	print(f"Интерпретация: {interpretation}")
	print(f"Направление связи: {direction}")
	print("=" * 80)


if __name__ == "__main__":
	# Укажите имена файлов, которые вы используете
	FILE_ORIGINAL = 'data.txt'
	FILE_GENERATED = 'generated_sequence.txt'

	perform_correlation_analysis(FILE_ORIGINAL, FILE_GENERATED)
