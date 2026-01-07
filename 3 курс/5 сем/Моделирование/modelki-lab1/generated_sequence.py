import numpy as np

k_order = 3
lambda_rate = 0.0170  # Параметр масштаба (scale) в NumPy - это 1/lambda
output_filename='generated_sequence.txt'

# 1. Параметр масштаба (scale) для NumPy:
scale_param = 1.0 / lambda_rate

# 2. Генерация 300 случайных чисел по закону Эрланга (k=3, lambda=0.0170)
N_values = 300
erlang_sequence = np.random.gamma(
	shape=k_order,  # Параметр формы (k)
	scale=scale_param,  # Параметр масштаба (1/lambda)
	size=N_values
)

np.savetxt(output_filename, erlang_sequence, fmt='%.4f', delimiter=',')

print(f"Сгенерировано {N_values} значений, среднее: {np.mean(erlang_sequence):.4f}")
