import numpy as np
from math import sqrt
import matplotlib.pyplot as plt


P_TRUE = 0.7
STUDENT_K = 1.96

n1 = 25
n2 = 10000


def solve(n, p):
    success = 0
    phats = []

    for _ in range(1000):
        array = np.random.geometric(p=p, size=n)

        x_mid = np.mean(array)
        p_ = 1 / x_mid

        half_width = STUDENT_K * p_ * sqrt((1 - p_) / n)
        trust_interval_left = p_ - half_width
        trust_interval_right = p_ + half_width

        if trust_interval_left <= p <= trust_interval_right:
            success += 1

        phats.append(p_)

    coverage = success / 1000
    return coverage, phats


print(f"геометрическое распределение для размера выборки 25 и параметра р=0.7:")
success_num, phats_n1 = solve(n1, P_TRUE)
print(f"доля попаданий = {success_num*100}%")
print('-' * 65)

print(f"геометрическое распределение для размера выборки 10 000 и параметра р=0.7:")
success_num, phats_n2 = solve(n2, P_TRUE)
print(f"доля попаданий = {success_num*100}%")


data_for_boxplot = [phats_n1, phats_n2]
labels = [f"n={n1}", f"n={n2}"]

plt.boxplot(data_for_boxplot, labels=labels)
plt.axhline(y=P_TRUE, linestyle='--', label='истинное p', linewidth='1.2', color='lime')
plt.title("распределение $\hat{p}$ при разных n (Geom(p=0.7))")
plt.ylabel("оценка $\hat{p}$")

plt.savefig("graphic.png", dpi=300, bbox_inches='tight')


plt.legend()
plt.show()


