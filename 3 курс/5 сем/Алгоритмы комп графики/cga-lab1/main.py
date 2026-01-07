import tkinter as tk
from PIL import Image, ImageTk
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np

parent = tk.Tk()
parent.title('Поленов cga-lab1')
parent.geometry('1920x1080')

parent.columnconfigure(0, weight=3)
parent.columnconfigure(1, weight=1)
parent.rowconfigure(0, weight=1)

# -----------------------------------------------------
image_container = tk.Frame(parent)
image_container.grid(row=0, column=0, sticky=tk.NSEW)

canvas = tk.Canvas(image_container)
scrollbar = tk.Scrollbar(image_container, orient=tk.VERTICAL, command=canvas.yview)
scrollable_frame = tk.Frame(canvas)

scrollable_frame.bind(
	"<Configure>",
	lambda event: canvas.configure(scrollregion=canvas.bbox("all"))
)

canvas.create_window((0, 0), window=scrollable_frame, anchor=tk.NW)
canvas.configure(yscrollcommand=scrollbar.set)

canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
# -----------------------------------------------------

btn_frame = tk.Frame(parent)
btn_frame.grid(row=0, column=1, sticky=tk.NSEW)


# def load_image(title: str) -> tk.PhotoImage:
# 	image = Image.open(title)
# 	return ImageTk.PhotoImage(image)
#
# def show_image(photo: tk.PhotoImage):
# 	image_label = tk.Label(scrollable_frame, image=photo)
# 	image_label.image = photo
# 	image_label.pack()

def show_info(title: str):
	im = Image.open(title).convert('RGB')
	photo = ImageTk.PhotoImage(im)

	item_frame = tk.Frame(scrollable_frame)
	item_frame.pack(pady=10, anchor="w")

	image_label = tk.Label(item_frame, image=photo)
	image_label.image = photo
	image_label.grid(row=0, column=0, padx=5)

	# Вычисляем общее количество пикселей
	width, height = im.size
	total_pixels = width * height

	# Создаем текстовую строку с количеством пикселей для каждого канала
	info_text = (
		f"Количество пикселей в R-канале: {total_pixels} штук\n"
		f"Количество пикселей в G-канале: {total_pixels} штук\n"
		f"Количество пикселей в B-канале: {total_pixels} штук"
	)

	rgb_label = tk.Label(item_frame, text=info_text)
	rgb_label.grid(row=0, column=1, padx=5)
	show_histogram(im)


def show_histogram(im: Image.Image):
	# Получаем массив numpy
	arr = np.array(im)
	# Вычисляем общее количество пикселей
	total_pixels = arr.shape[0] * arr.shape[1]

	# Данные для диаграммы
	channels = ['R', 'G', 'B']
	values = [total_pixels, total_pixels, total_pixels]
	colors = ['red', 'green', 'blue']

	# Создаем фигуру и оси
	fig, ax = plt.subplots(figsize=(8, 6))

	# Строим диаграмму
	ax.bar(channels, values, color=colors)

	# Добавляем подписи и заголовок
	ax.set_title("Общее количество пикселей по каналам RGB")
	ax.set_ylabel("Количество пикселей")

	# Добавляем значения на столбики
	for i, v in enumerate(values):
		ax.text(i, v + 0.01 * total_pixels, f"{v:,}", ha='center')

	# Новое окно
	hist_window = tk.Toplevel(parent)
	hist_window.title("RGB-гистограмма")

	canvas_fig = FigureCanvasTkAgg(fig, master=hist_window)
	canvas_fig.draw()
	canvas_fig.get_tk_widget().pack(fill=tk.BOTH, expand=True)


def on_click(title: str):
	# 	# show_image(load_image(title))
	show_info(title)


btn1 = tk.Button(btn_frame, text="собака", command=lambda: on_click("beer_dawg.png"))
btn1.pack(pady=5, fill=tk.X)
btn2 = tk.Button(btn_frame, text="прикол", command=lambda: on_click("gpu.png"))
btn2.pack(pady=5, fill=tk.X)

parent.mainloop()
