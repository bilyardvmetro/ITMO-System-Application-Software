import time
import task1, task2, task3, task4


def benchmark(script):
    start_time = time.time()
    for i in range(100):
        script()
    print("--- %s seconds ---" % (time.time() - start_time))


print('> stupid parser')
benchmark(task1)

print('> lib parser')
benchmark(task2)

print('> regexp parser')
benchmark(task3)

print('> bilyardvmetro parser')
benchmark(task4)
