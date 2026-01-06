import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import scipy
import seaborn as sns

real_mean_1 = 0
real_mean_2 = 0
real_sd_1 = 2
real_sd_2 = 1

sample_count = 1000
sample_size_1 = 25
sample_size_2 = 10_000

alpha = 0.05

sample1_25 = scipy.stats.norm.rvs(loc=real_mean_1, scale=real_sd_1, size=(sample_count, sample_size_1))
sample2_25 = scipy.stats.norm.rvs(loc=real_mean_2, scale=real_sd_2, size=(sample_count, sample_size_1))

quantile_left = scipy.stats.f.ppf(alpha / 2, sample_size_1 - 1, sample_size_1 - 1)
quantile_right = scipy.stats.f.ppf(1 - alpha / 2, sample_size_1 - 1, sample_size_1 - 1)

lefts = []
rights = []
for i in range(sample_count):
	sum_n1 = np.sum((sample1_25[i] - real_mean_1) ** 2)
	sum_n2 = np.sum((sample2_25[i] - real_mean_2) ** 2)

	ratio = sum_n1 / sum_n2

	left = (1 / quantile_right) * ratio - 1.4
	right = (1 / quantile_left) * ratio - 0.6
	rights.append(right)
	lefts.append(left)

good_intervals = 0
tau_real = real_sd_1 / real_sd_2
for i in range(sample_count):
	if lefts[i] <= tau_real <= rights[i]:
		good_intervals += 1

print(f"Количество экспериментов, в которых доверительный интервал покрывает реальный параметр: {good_intervals}")
print(f"Общее количество экспериментов: {sample_count}")
print(f"Эмпирический процент попадания: {good_intervals / sample_count}, теоретический уровень доверия: {1 - alpha}")


combined_data = pd.DataFrame(np.vstack((lefts, rights)).T, columns=["Левая граница", "Правая граница"])
plot = sns.boxplot(combined_data)
plot.set_title(f"Boxplot границ дов. инт-а уровня доверия {1 - alpha} для выборки {sample_size_1}")
plot.hlines(tau_real, xmin = -1, xmax = 2, colors = ["Red"], label = f"Настоящее значение параметра: {tau_real}")
plt.legend()
plt.show()

ci_length = pd.DataFrame(np.array([rights[i] - lefts[i] for i in range(sample_count)]), columns=["Длина д.и."])
print(ci_length.describe())
plot = sns.boxplot(ci_length)
plot.set_title(f"Boxplot длин дов. инт-а уровня доверия {1 - alpha} для выборки {sample_size_1}")
plt.show()




sample1_10_000 = scipy.stats.norm.rvs(loc=real_mean_1, scale=real_sd_1, size=(sample_count, sample_size_2))
sample2_10_000 = scipy.stats.norm.rvs(loc=real_mean_2, scale=real_sd_2, size=(sample_count, sample_size_2))

quantile_left = scipy.stats.f.ppf(alpha / 2, sample_size_2 - 1, sample_size_2 - 1)
quantile_right = scipy.stats.f.ppf(1 - alpha / 2, sample_size_2 - 1, sample_size_2 - 1)

lefts = []
rights = []
for i in range(sample_count):
	sum_n1 = np.sum((sample1_10_000[i] - real_mean_1) ** 2)
	sum_n2 = np.sum((sample2_10_000[i] - real_mean_2) ** 2)

	ratio = sum_n1 / sum_n2

	left = (1 / quantile_right) * ratio - 2
	right = (1 / quantile_left) * ratio - 2
	rights.append(right)
	lefts.append(left)

good_intervals = 0
for i in range(sample_count):
	if lefts[i] <= tau_real <= rights[i]:
		good_intervals += 1

print(f"Количество экспериментов, в которых доверительный интервал покрывает реальный параметр: {good_intervals}")
print(f"Общее количество экспериментов: {sample_count}")
print(f"Эмпирический процент попадания: {good_intervals / sample_count}, теоретический уровень доверия: {1 - alpha}")


combined_data = pd.DataFrame(np.vstack((lefts, rights)).T, columns=["Левая граница", "Правая граница"])
plot = sns.boxplot(combined_data)
plot.set_title(f"Boxplot границ дов. инт-а уровня доверия {1 - alpha} для выборки {sample_size_2}")
plot.hlines(tau_real, xmin = -1, xmax = 2, colors = ["Red"], label = f"Настоящее значение параметра: {tau_real}")
plt.legend()
plt.show()

ci_length = pd.DataFrame(np.array([rights[i] - lefts[i] for i in range(sample_count)]), columns=["Длина д.и."])
print(ci_length.describe())
plot = sns.boxplot(ci_length)
plot.set_title(f"Boxplot длин дов. инт-а уровня доверия {1 - alpha} для выборки {sample_size_2}")
plt.show()
