import tkinter as tk
from tkinter import ttk, filedialog, messagebox

import numpy as np
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg


def compute_illuminance(
    Wm, Hm, Wres, Hres,
    xL, yL, zL, I0,
    xc, yc, radius
):
    """
    Расчет освещенности на плоскости z=0 от точечного источника.

    Все геометрические величины здесь В МЕТРАХ:
      Wm, Hm            -- размеры области (м)
      Wres, Hres        -- разрешение (пиксели)
      (xL, yL, zL)      -- координаты источника (м)
      I0                -- сила излучения (Вт/ср) по условию задачи
      (xc, yc, radius)  -- круг интереса (м)

    Выход:
      xs, ys     -- координаты сетки (м)
      E_masked   -- освещенность (Вт/м^2) в пределах круга, вне круга 0
      norm_img   -- картинка 0..255 для отображения
      circle_mask
    """

    # Координатная сетка (м)
    xs = np.linspace(0, Wm, Wres)
    ys = np.linspace(0, Hm, Hres)
    X, Y = np.meshgrid(xs, ys)

    # Вектор от точки плоскости к источнику
    dx = xL - X
    dy = yL - Y
    dz = zL  # источник над плоскостью z=0

    r2 = dx**2 + dy**2 + dz**2
    r = np.sqrt(r2)

    # Новая формула из задания:
    # E = I0 * cos^2(theta) / r^4, где cos(theta)=dz/r
    # => E = I0 * dz^2 / r^6
    with np.errstate(divide='ignore', invalid='ignore'):
        E = I0 * (dz**2) / (r2**3)   # r^6 = (r^2)^3
        E[~np.isfinite(E)] = 0.0

    # Маска круга (в метрах)
    dx_c = X - xc
    dy_c = Y - yc
    circle_mask = dx_c**2 + dy_c**2 <= radius**2

    # Вне круга гасим освещенность
    E_masked = np.where(circle_mask, E, 0.0)

    # Нормировка в 0..255 (для картинки)
    maxE = E_masked.max()
    if maxE <= 0 or not np.isfinite(maxE):
        norm_img = np.zeros_like(E_masked, dtype=np.uint8)
    else:
        norm_img = np.clip(E_masked / maxE * 255.0, 0, 255).astype(np.uint8)

    return xs, ys, E_masked, norm_img, circle_mask


class LightingApp:
    def __init__(self, master):
        self.master = master
        master.title("Расчет освещенности (ЛР3 АКГ)")

        # Последнее рассчитанное изображение (нормированное 0-255)
        self.last_image = None
        self.last_W = None
        self.last_H = None

        self._build_ui()

    def _build_ui(self):
        main_frame = ttk.Frame(self.master, padding=5)
        main_frame.pack(fill=tk.BOTH, expand=True)

        # Левая панель с параметрами
        params_frame = ttk.Frame(main_frame)
        params_frame.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 5))

        # Правая панель с графикой
        plots_frame = ttk.Frame(main_frame)
        plots_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        # --- Параметры ---
        # Размеры области (мм)
        ttk.Label(params_frame, text="Размер области, м").grid(row=0, column=0, columnspan=2, pady=(0, 2))

        self.var_W = tk.StringVar(value="1")
        self.var_H = tk.StringVar(value="1")
        ttk.Label(params_frame, text="W (ширина):").grid(row=1, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_W, width=10).grid(row=1, column=1, sticky="w")

        ttk.Label(params_frame, text="H (высота):").grid(row=2, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_H, width=10).grid(row=2, column=1, sticky="w")

        # Разрешение (пиксели)
        ttk.Label(params_frame, text="Разрешение, пиксели").grid(row=3, column=0, columnspan=2, pady=(5, 2))

        self.var_Wres = tk.StringVar(value="400")
        self.var_Hres = tk.StringVar(value="400")
        ttk.Label(params_frame, text="Wres:").grid(row=4, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_Wres, width=10).grid(row=4, column=1, sticky="w")

        ttk.Label(params_frame, text="Hres:").grid(row=5, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_Hres, width=10).grid(row=5, column=1, sticky="w")

        # Источник света
        ttk.Label(params_frame, text="Источник света").grid(row=6, column=0, columnspan=2, pady=(5, 2))

        self.var_xL = tk.StringVar(value="0.5")
        self.var_yL = tk.StringVar(value="0.5")
        self.var_zL = tk.StringVar(value="0.5")
        self.var_I0 = tk.StringVar(value="100.0")

        ttk.Label(params_frame, text="xL  (м):").grid(row=7, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_xL, width=10).grid(row=7, column=1, sticky="w")

        ttk.Label(params_frame, text="yL (м):").grid(row=8, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_yL, width=10).grid(row=8, column=1, sticky="w")

        ttk.Label(params_frame, text="zL (м):").grid(row=9, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_zL, width=10).grid(row=9, column=1, sticky="w")

        ttk.Label(params_frame, text="I0 (Вт/cp):").grid(row=10, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_I0, width=10).grid(row=10, column=1, sticky="w")

        # Круг
        ttk.Label(params_frame, text="Круг (область интереса), м").grid(row=11, column=0, columnspan=2, pady=(5, 2))

        self.var_xc = tk.StringVar(value="0.5")
        self.var_yc = tk.StringVar(value="0.5")
        self.var_radius = tk.StringVar(value="0.4")

        ttk.Label(params_frame, text="x центра:").grid(row=12, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_xc, width=10).grid(row=12, column=1, sticky="w")

        ttk.Label(params_frame, text="y центра:").grid(row=13, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_yc, width=10).grid(row=13, column=1, sticky="w")

        ttk.Label(params_frame, text="Радиус:").grid(row=14, column=0, sticky="e")
        ttk.Entry(params_frame, textvariable=self.var_radius, width=10).grid(row=14, column=1, sticky="w")

        # Кнопки
        ttk.Button(params_frame, text="Рассчитать", command=self.on_compute).grid(
            row=15, column=0, columnspan=2, pady=(10, 2), sticky="ew"
        )
        ttk.Button(params_frame, text="Сохранить изображение", command=self.on_save_image).grid(
            row=16, column=0, columnspan=2, pady=(2, 10), sticky="ew"
        )

        # Поле для краткой информации (макс/мин/среднее)
        ttk.Label(params_frame, text="Статистика по кругу:").grid(row=17, column=0, columnspan=2, pady=(5, 2))
        self.stats_text = tk.Text(params_frame, width=25, height=5, state=tk.DISABLED)
        self.stats_text.grid(row=18, column=0, columnspan=2, sticky="nsew")

        # --- Графика (matplotlib) ---
        self.fig = Figure(figsize=(6, 8), dpi=100)
        self.ax_img = self.fig.add_subplot(3, 1, 1)
        self.ax_cross_h = self.fig.add_subplot(3, 1, 2)
        self.ax_cross_v = self.fig.add_subplot(3, 1, 3)

        self.ax_img.set_title("Распределение освещенности (нормированное)")
        self.ax_img.set_xlabel("x, мм")
        self.ax_img.set_ylabel("y, мм")

        self.ax_cross_h.set_title("Горизонтальное сечение через центр (y = yc)")
        self.ax_cross_h.set_xlabel("x, м")
        self.ax_cross_h.set_ylabel("E, Вт/м²")

        self.ax_cross_v.set_title("Вертикальное сечение через центр (x = xc)")
        self.ax_cross_v.set_xlabel("y, м")
        self.ax_cross_v.set_ylabel("E, Вт/м²")

        self.fig.subplots_adjust(
            top=0.93,  # отступ от верхнего края окна
            bottom=0.07,  # отступ снизу
            left=0.10,
            right=0.95,
            hspace=0.55  # ВАЖНО: расстояние между графиками по вертикали
        )

        self.canvas = FigureCanvasTkAgg(self.fig, master=plots_frame)
        self.canvas.draw()
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    def on_compute(self):
        try:
            Wm = float(self.var_W.get())  # м
            Hm = float(self.var_H.get())  # м
            Wres = int(self.var_Wres.get())
            Hres = int(self.var_Hres.get())
            xL = float(self.var_xL.get())  # м
            yL = float(self.var_yL.get())  # м
            zL = float(self.var_zL.get())  # м
            I0 = float(self.var_I0.get())
            xc = float(self.var_xc.get())  # м
            yc = float(self.var_yc.get())  # м
            radius = float(self.var_radius.get())  # м
        except ValueError:
            messagebox.showerror("Ошибка", "Не удалось преобразовать параметры. Проверьте ввод.")
            return

        W = Wm * 1000.0  # мм (для проверок/подписей)
        H = Hm * 1000.0  # мм
        xL_mm = xL * 1000.0
        yL_mm = yL * 1000.0
        zL_mm = zL * 1000.0
        xc_mm = xc * 1000.0
        yc_mm = yc * 1000.0
        radius_mm = radius * 1000.0

        # Простая проверка/ограничения по диапазонам из ТЗ
        if not (100 <= W <= 10000 and 100 <= H <= 10000):
            messagebox.showwarning("Предупреждение", "W и H должны быть в диапазоне 100..10000 мм.")
        if not (200 <= Wres <= 800 and 200 <= Hres <= 800):
            messagebox.showwarning("Предупреждение", "Wres и Hres должны быть в диапазоне 200..800 пикселей.")
        if not (-10000 <= xL_mm <= 10000 and -10000 <= yL_mm <= 10000):
            messagebox.showwarning("Предупреждение", "xL, yL должны быть в диапазоне -10000..10000 мм.")
        if not (100 <= zL_mm <= 10000):
            messagebox.showwarning("Предупреждение", "zL должен быть в диапазоне 100..10000 мм.")
        if not (0.01 <= I0 <= 10000):
            messagebox.showwarning("Предупреждение", "I0 должен быть в диапазоне 0.01..10000 Вт/ср.")

        pixel_size_x = Wm / Wres
        pixel_size_y = Hm / Hres

        if abs(pixel_size_x - pixel_size_y) > 1e-12:
            Hres_new = int(round(Hm / pixel_size_x))
            Hres_new = max(1, Hres_new)
            Hres = Hres_new
            self.var_Hres.set(str(Hres))

        # Расчет
        xs, ys, E_masked, norm_img, circle_mask = compute_illuminance(
            Wm, Hm, Wres, Hres,
            xL, yL, zL, I0,
            xc, yc, radius
        )

        self.last_image = norm_img
        self.last_W = Wm
        self.last_H = Hm

        # --- Обновляем картинку освещенности ---
        self.ax_img.clear()
        self.ax_img.set_title("Распределение освещенности (нормированное)")
        self.ax_img.set_xlabel("x, мм")
        self.ax_img.set_ylabel("y, мм")

        self.ax_img.imshow(
            norm_img,
            origin="lower",
            extent=[xs.min() * 1000, xs.max() * 1000, ys.min() * 1000, ys.max() * 1000],
            cmap="gray"
        )

        circle = self._make_circle_patch(xc * 1000, yc * 1000, radius * 1000)
        self.ax_img.add_patch(circle)

        # --- Сечение через центр круга (по y = yc) ---
        # --- Горизонтальное сечение через центр (y = yc) ---
        self.ax_cross_h.clear()
        self.ax_cross_h.set_title("Горизонтальное сечение через центр (y = yc)")
        self.ax_cross_h.set_xlabel("x, м")
        self.ax_cross_h.set_ylabel("E, Вт/м²")

        y_index = int(round((yc / Hm) * (len(ys) - 1)))
        y_index = max(0, min(len(ys) - 1, y_index))
        E_line_h = E_masked[y_index, :]
        self.ax_cross_h.plot(xs, E_line_h)
        self.ax_cross_h.grid(True)

        # --- Вертикальное сечение через центр (x = xc) ---
        self.ax_cross_v.clear()
        self.ax_cross_v.set_title("Вертикальное сечение через центр (x = xc)")
        self.ax_cross_v.set_xlabel("y, м")
        self.ax_cross_v.set_ylabel("E, Вт/м²")

        x_index = int(round((xc / Wm) * (len(xs) - 1)))
        x_index = max(0, min(len(xs) - 1, x_index))
        E_line_v = E_masked[:, x_index]
        self.ax_cross_v.plot(ys, E_line_v)
        self.ax_cross_v.grid(True)

        self.canvas.draw()

        # --- Статистика внутри круга ---
        circle_values = E_masked[circle_mask]
        if circle_values.size > 0:
            maxE = float(circle_values.max())
            minE = float(circle_values.min())
            meanE = float(circle_values.mean())
        else:
            maxE = minE = meanE = 0.0

        stats_str = (
            f"Макс E: {maxE:.6g} Вт/м²\n"
            f"Мин E:  {minE:.6g} Вт/м²\n"
            f"Среднее E: {meanE:.6g} Вт/м²\n"
        )

        self.stats_text.configure(state=tk.NORMAL)
        self.stats_text.delete("1.0", tk.END)
        self.stats_text.insert(tk.END, stats_str)
        self.stats_text.configure(state=tk.DISABLED)

    def _make_circle_patch(self, xc, yc, radius):
        from matplotlib.patches import Circle
        return Circle((xc, yc), radius, fill=False, linestyle="--")

    def on_save_image(self):
        if self.last_image is None or self.last_W is None or self.last_H is None:
            messagebox.showerror("Ошибка", "Сначала выполните расчет, чтобы получить изображение.")
            return

        file_path = filedialog.asksaveasfilename(
            defaultextension=".png",
            filetypes=[("PNG", "*.png"), ("JPEG", "*.jpg;*.jpeg"), ("BMP", "*.bmp"), ("Все файлы", "*.*")]
        )
        if not file_path:
            return

        try:
            from matplotlib.figure import Figure

            Wm = self.last_W
            Hm = self.last_H
            img = self.last_image

            aspect = Hm / Wm if Wm != 0 else 1.0
            fig = Figure(figsize=(4, 4 * aspect), dpi=200)
            ax = fig.add_axes([0, 0, 1, 1])

            ax.imshow(
                img,
                origin="lower",
                extent=[0, Wm * 1000, 0, Hm * 1000],  # мм
                cmap="gray"
            )
            ax.set_axis_off()
            ax.set_aspect("equal", adjustable="box")

            ax.set_axis_off()
            ax.set_aspect("equal", adjustable="box")

            fig.savefig(file_path, dpi=200, bbox_inches="tight", pad_inches=0)
            # очень важно закрывать временную фигуру
            import matplotlib.pyplot as plt
            plt.close(fig)

            messagebox.showinfo("Готово", f"Изображение сохранено в:\n{file_path}")
        except Exception as e:
            messagebox.showerror("Ошибка", f"Не удалось сохранить изображение:\n{e}")



if __name__ == "__main__":
    root = tk.Tk()
    app = LightingApp(root)
    root.mainloop()

# яркость света I0 должна измеряться в Вт/м^2. В этой программе она измеряется в Вт/ср. Так как размер области в мм, то и при расчете освещения нужно учесть это
