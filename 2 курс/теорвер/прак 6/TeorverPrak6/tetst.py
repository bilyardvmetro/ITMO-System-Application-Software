import numpy as np
import matplotlib.pyplot as plt


def empirical_cdf(data):
	"""
	Строит эмпирическую функцию распределения (ЭФР) для заданных данных.
	:param data: список или numpy-массив с данными
	"""
	hist, edges = np.histogram(data, bins=len(data))
	Y = hist.cumsum()
	for i in range(len(Y)):
		plt.plot([edges[i], edges[i + 1]], [Y[i], Y[i]], c="blue")
	plt.xlabel('Значение')
	plt.ylabel('F(x)')
	plt.title('Эмпирическая функция распределения')
	plt.grid()
	plt.legend()
	plt.show()


# Пример использования
data = np.random.normal(loc=0, scale=1, size=100)  # Генерация случайных данных
empirical_cdf(data)