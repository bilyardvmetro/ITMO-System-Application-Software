import math
import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from typing import List, Tuple

import numpy as np
from PIL import Image, ImageTk


# --------------------------- МАТЕМАТИКА ОСВЕЩЕНИЯ --------------------------- #

def parse_lights(text: str) -> List[Tuple[float, float, float, float]]:
    """
    Парсим источники света из многострочного поля.
    Формат строки: xL yL zL I0
    Например:
        0 0 5000 1000
        2000 0 3000 500
    """
    lights = []
    for line in text.strip().splitlines():
        line = line.strip()
        if not line:
            continue
        parts = line.replace(",", " ").split()
        if len(parts) != 4:
            raise ValueError(
                f"Неверный формат строки источника света: '{line}'. Ожидается: x y z I0"
            )
        xL, yL, zL, I0 = map(float, parts)
        lights.append((xL*1000, yL*1000, zL*1000, I0))
    if not lights:
        raise ValueError("Нужно задать хотя бы один источник света.")
    return lights


def compute_brightness_distribution(
    W: float,
    H: float,
    Wres: int,
    Hres: int,
    xC: float,
    yC: float,
    zC: float,
    R: float,
    zO: float,
    lights: List[Tuple[float, float, float, float]],
    k_a: float,
    k_d: float,
    k_s: float,
    shininess: float,
):
    """
    Расчёт распределения яркости по диску сферы (ортографическая проекция).
    - Экран лежит в плоскости z = zC (через центр сферы).
    - Координаты пикселей (x, y) задаются в пределах [-W/2, W/2] × [-H/2, H/2]
      и смещаются относительно (xC, yC), чтобы сфера могла быть не в центре.
    - Для каждого пикселя, попавшего внутрь проекции сферы, находим точку на
      видимой полусфере (ближайшая к наблюдателю точка по z).
    - Далее считаем освещённость по модели Блинна–Фонга.
    """

    # Сетка координат пикселей на экране (в мм)
    xs = np.linspace(xC - W / 2, xC + W / 2, Wres, dtype=np.float64)
    ys = np.linspace(yC - H / 2, yC + H / 2, Hres, dtype=np.float64)
    X, Y = np.meshgrid(xs, ys)  # shape (Hres, Wres)

    # Сдвиг относительно центра сферы
    dx = X - xC
    dy = Y - yC

    # Маска точек, попадающих в проекцию сферы
    r2 = dx**2 + dy**2
    inside = r2 <= R**2

    # Инициализируем яркость
    I = np.zeros_like(X, dtype=np.float64)

    if not np.any(inside):
        # Сфера вообще не попала в экран
        return I, inside, {
            "I_max": 0.0,
            "I_min": 0.0,
            "points": [],
        }

    # Координата z на сфере (берём полусферу, "смотрящую" на наблюдателя)
    # Наблюдатель сидит в точке (0, 0, zO).
    # Если zO < zC, то ближе к наблюдателю z меньше -> z = zC - sqrt(...)
    # Если zO > zC, то наоборот.
    front_sign = -1.0 if zO < zC else 1.0
    dz = np.zeros_like(X)
    dz[inside] = front_sign * np.sqrt(R**2 - r2[inside])

    Z = np.full_like(X, zC)
    Z[inside] = zC + dz[inside]

    # Нормаль к поверхности сферы
    Nx = np.zeros_like(X)
    Ny = np.zeros_like(Y)
    Nz = np.zeros_like(Z)

    Nx[inside] = dx[inside] / R
    Ny[inside] = dy[inside] / R
    Nz[inside] = (Z[inside] - zC) / R

    # Вектор к наблюдателю (точка наблюдателя (0, 0, zO))
    Ox, Oy, Oz = 0.0, 0.0, zO
    Vx = np.zeros_like(X)
    Vy = np.zeros_like(Y)
    Vz = np.zeros_like(Z)

    Vx[inside] = Ox - X[inside]
    Vy[inside] = Oy - Y[inside]
    Vz[inside] = Oz - Z[inside]

    V_len = np.sqrt(Vx**2 + Vy**2 + Vz**2)
    # Избегаем деления на ноль
    V_len[V_len == 0] = 1.0
    Vx /= V_len
    Vy /= V_len
    Vz /= V_len

    # Считаем вклад от каждого источника
    for (xL, yL, zL, I0) in lights:
        Lx = np.zeros_like(X)
        Ly = np.zeros_like(Y)
        Lz = np.zeros_like(Z)

        Lx[inside] = xL - X[inside]
        Ly[inside] = yL - Y[inside]
        Lz[inside] = zL - Z[inside]

        L_len = np.sqrt(Lx**2 + Ly**2 + Lz**2)
        L_len[L_len == 0] = 1.0

        # Нормированный вектор на источник
        Lx_n = np.zeros_like(X)
        Ly_n = np.zeros_like(Y)
        Lz_n = np.zeros_like(Z)

        Lx_n[inside] = Lx[inside] / L_len[inside]
        Ly_n[inside] = Ly[inside] / L_len[inside]
        Lz_n[inside] = Lz[inside] / L_len[inside]

        # Косинус угла между нормалью и направлением на источник
        NdotL = Nx * Lx_n + Ny * Ly_n + Nz * Lz_n
        NdotL = np.clip(NdotL, 0.0, 1.0)

        # Вектор полунаправления H (Блинн-Фонг)
        Hx = Lx_n + Vx
        Hy = Ly_n + Vy
        Hz = Lz_n + Vz
        H_len = np.sqrt(Hx**2 + Hy**2 + Hz**2)
        H_len[H_len == 0] = 1.0

        Hx /= H_len
        Hy /= H_len
        Hz /= H_len

        NdotH = Nx * Hx + Ny * Hy + Nz * Hz
        NdotH = np.clip(NdotH, 0.0, 1.0)

        # Интенсивность от источника (учитываем обратную пропорциональность r^2)
        dist2 = L_len**2
        dist2[dist2 == 0] = 1.0

        diffuse = k_d * NdotL
        specular = k_s * (NdotH**shininess)

        I[inside] += I0 * (k_a + diffuse + specular)[inside] / dist2[inside]

    # Вычисляем минимальную и максимальную яркость по сфере
    I_inside = I[inside]
    I_max = float(np.max(I_inside)) if I_inside.size > 0 else 0.0
    I_min = float(np.min(I_inside)) if I_inside.size > 0 else 0.0

    # Функция для яркости в одной точке на сфере
    def brightness_at_point(px: float, py: float, pz: float) -> float:
        # Нормаль:
        nx = (px - xC) / R
        ny = (py - yC) / R
        nz = (pz - zC) / R

        # Вектор к наблюдателю
        vx = Ox - px
        vy = Oy - py
        vz = Oz - pz
        v_len = math.sqrt(vx * vx + vy * vy + vz * vz) or 1.0
        vx /= v_len
        vy /= v_len
        vz /= v_len

        I_point = 0.0
        for (xL, yL, zL, I0) in lights:
            lx = xL - px
            ly = yL - py
            lz = zL - pz
            l_len = math.sqrt(lx * lx + ly * ly + lz * lz) or 1.0
            lx_n = lx / l_len
            ly_n = ly / l_len
            lz_n = lz / l_len

            ndotl = max(0.0, nx * lx_n + ny * ly_n + nz * lz_n)

            hx = lx_n + vx
            hy = ly_n + vy
            hz = lz_n + vz
            h_len = math.sqrt(hx * hx + hy * hy + hz * hz) or 1.0
            hx /= h_len
            hy /= h_len
            hz /= h_len

            ndoth = max(0.0, nx * hx + ny * hy + nz * hz)
            diffuse_p = k_d * ndotl
            specular_p = k_s * (ndoth**shininess)
            I_point += I0 * (k_a + diffuse_p + specular_p) / (l_len * l_len)
        return I_point

    # Три точки на сфере (на "видимой" полусфере)
    # 1. "Вершина" по направлению к наблюдателю вдоль z
    p1 = (xC, yC, zC + front_sign * (-R))  # сдвиг в сторону наблюдателя
    # 2. Точка на экваторе по x
    p2_xy = (R / math.sqrt(2.0), 0.0)
    p2 = (xC + p2_xy[0], yC + p2_xy[1], zC + front_sign * (-R / math.sqrt(2.0)))
    # 3. Точка на экваторе по y
    p3_xy = (0.0, R / math.sqrt(2.0))
    p3 = (xC + p3_xy[0], yC + p3_xy[1], zC + front_sign * (-R / math.sqrt(2.0)))

    points_info = []
    for label, (px, py, pz) in [
        ("P1 (центр видимой полусферы)", p1),
        ("P2 (смещение по +X)", p2),
        ("P3 (смещение по +Y)", p3),
    ]:
        I_p = brightness_at_point(px, py, pz)
        points_info.append(
            {
                "label": label,
                "coords": (px, py, pz),
                "I": I_p,
            }
        )

    stats = {
        "I_max": I_max,
        "I_min": I_min,
        "points": points_info,
    }

    return I, inside, stats


def normalize_to_8bit(I: np.ndarray, mask: np.ndarray) -> np.ndarray:
    """
    Нормируем массив яркости I по максимальному значению внутри mask
    в диапазон [0, 255].
    """
    I_inside = I[mask]
    if I_inside.size == 0:
        return np.zeros_like(I, dtype=np.uint8)

    I_max = float(np.max(I_inside))
    if I_max <= 0:
        return np.zeros_like(I, dtype=np.uint8)

    result = np.zeros_like(I, dtype=np.float64)
    # Нормировка на максимум
    result[mask] = (I[mask] / I_max) * 255.0

    result = np.clip(result, 0, 255)
    return result.astype(np.uint8)


# --------------------------- ГРАФИЧЕСКИЙ ИНТЕРФЕЙС --------------------------- #

class SphereBrightnessApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("ЛР4: Яркость на сфере от точечных источников (Blinn-Phong)")
        self.geometry("1100x700")

        self.image = None
        self.photo = None

        self._build_gui()

    def _build_gui(self):
        main_frame = ttk.Frame(self)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Левая панель: параметры
        left_frame = ttk.Frame(main_frame)
        left_frame.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 5))

        # Правая панель: изображение и лог
        right_frame = ttk.Frame(main_frame)
        right_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        # --- Параметры экрана и сферы ---
        params_frame = ttk.LabelFrame(left_frame, text="Параметры сцены")
        params_frame.pack(fill=tk.X, pady=5)

        self.var_W = tk.DoubleVar(value=1)     # мм
        self.var_H = tk.DoubleVar(value=1)     # мм
        self.var_Wres = tk.IntVar(value=400)       # пиксели
        self.var_Hres = tk.IntVar(value=400)       # пиксели

        self.var_xC = tk.DoubleVar(value=0.5)
        self.var_yC = tk.DoubleVar(value=0.5)
        self.var_zC = tk.DoubleVar(value=2)
        self.var_R = tk.DoubleVar(value=0.5)

        self.var_zO = tk.DoubleVar(value=0.0)  # наблюдатель в (0, 0, zO)

        row = 0
        ttk.Label(params_frame, text="Ширина экрана W [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_W, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="Высота экрана H [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_H, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="Разрешение Wres [px]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_Wres, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="Разрешение Hres [px]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_Hres, width=10).grid(row=row, column=1, sticky="we")
        row += 1

        ttk.Label(params_frame, text="xC [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_xC, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="yC [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_yC, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="zC [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_zC, width=10).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(params_frame, text="R (радиус сферы) [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_R, width=10).grid(row=row, column=1, sticky="we")
        row += 1

        ttk.Label(params_frame, text="zO наблюдателя [м]:").grid(row=row, column=0, sticky="w")
        ttk.Entry(params_frame, textvariable=self.var_zO, width=10).grid(row=row, column=1, sticky="we")
        row += 1

        # --- Параметры Блинн-Фонга ---
        phong_frame = ttk.LabelFrame(left_frame, text="Параметры модели Блинн-Фонга")
        phong_frame.pack(fill=tk.X, pady=5)

        self.var_ka = tk.DoubleVar(value=0.1)
        self.var_kd = tk.DoubleVar(value=1.6)
        self.var_ks = tk.DoubleVar(value=0.9)
        self.var_n = tk.DoubleVar(value=100.0)

        row = 0
        ttk.Label(phong_frame, text="k_a (ambient):").grid(row=row, column=0, sticky="w")
        ttk.Entry(phong_frame, textvariable=self.var_ka, width=8).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(phong_frame, text="k_d (diffuse):").grid(row=row, column=0, sticky="w")
        ttk.Entry(phong_frame, textvariable=self.var_kd, width=8).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(phong_frame, text="k_s (specular):").grid(row=row, column=0, sticky="w")
        ttk.Entry(phong_frame, textvariable=self.var_ks, width=8).grid(row=row, column=1, sticky="we")
        row += 1
        ttk.Label(phong_frame, text="n (степень блеска):").grid(row=row, column=0, sticky="w")
        ttk.Entry(phong_frame, textvariable=self.var_n, width=8).grid(row=row, column=1, sticky="we")
        row += 1

        # --- Источники света ---
        lights_frame = ttk.LabelFrame(left_frame, text="Источники света (x y z I0)")
        lights_frame.pack(fill=tk.BOTH, pady=5, expand=True)

        self.txt_lights = tk.Text(lights_frame, width=30, height=8)
        self.txt_lights.pack(fill=tk.BOTH, expand=True)

        # Значение по умолчанию: один источник по оси z
        self.txt_lights.insert(
            "1.0",
            "0 0 1 2000\n"
            "# Формат: x y z I0 (м, м, м, Вт/м^2)\n"
            "# Доп. источники — по одной строке без '#'.\n"
            "1.8 0.5 2 2000\n",
        )

        # --- Кнопки управления ---
        buttons_frame = ttk.Frame(left_frame)
        buttons_frame.pack(fill=tk.X, pady=5)

        ttk.Button(buttons_frame, text="Рассчитать и отобразить", command=self.on_compute).pack(
            side=tk.LEFT, fill=tk.X, expand=True, padx=2
        )
        ttk.Button(buttons_frame, text="Сохранить изображение", command=self.on_save).pack(
            side=tk.LEFT, fill=tk.X, expand=True, padx=2
        )

        # --- Поле вывода численных результатов ---
        stats_frame = ttk.LabelFrame(left_frame, text="Численные результаты")
        stats_frame.pack(fill=tk.BOTH, pady=5, expand=True)

        self.txt_stats = tk.Text(stats_frame, width=30, height=12, state=tk.DISABLED)
        self.txt_stats.pack(fill=tk.BOTH, expand=True)

        # --- Область для изображения ---
        image_frame = ttk.LabelFrame(right_frame, text="Изображение распределения яркости")
        image_frame.pack(fill=tk.BOTH, expand=True)

        self.canvas = tk.Canvas(image_frame, bg="black")
        self.canvas.pack(fill=tk.BOTH, expand=True)

    # --------------------- Обработчики кнопок --------------------- #

    def on_compute(self):
        try:
            W = float(self.var_W.get()) * 1000
            H = float(self.var_H.get()) * 1000
            Wres = int(self.var_Wres.get())
            Hres = int(self.var_Hres.get())
            xC = float(self.var_xC.get()) * 1000
            yC = float(self.var_yC.get()) * 1000
            zC = float(self.var_zC.get()) * 1000
            R = float(self.var_R.get()) * 1000
            zO = float(self.var_zO.get()) * 1000

            if W <= 0 or H <= 0:
                raise ValueError("Размеры экрана W и H должны быть положительными.")
            if Wres <= 0 or Hres <= 0:
                raise ValueError("Разрешение Wres и Hres должно быть положительным.")
            if R <= 0:
                raise ValueError("Радиус сферы должен быть положительным.")

            # --- Автокоррекция разрешения для квадратных пикселей ---
            # Сохраняем W и Wres как задано пользователем.
            # Размер пикселя по X:
            pixel_size_x = W / Wres
            # Подбираем Hres так, чтобы размер пикселя по Y совпал:
            ideal_Hres = int(round(H / pixel_size_x))
            if ideal_Hres <= 0:
                ideal_Hres = 1
            if ideal_Hres != Hres:
                Hres = ideal_Hres
                self.var_Hres.set(Hres)

            lights = parse_lights(self._strip_comments(self.txt_lights.get("1.0", tk.END)))

            k_a = float(self.var_ka.get())
            k_d = float(self.var_kd.get())
            k_s = float(self.var_ks.get())
            n = float(self.var_n.get())

            I, mask, stats = compute_brightness_distribution(
                W=W,
                H=H,
                Wres=Wres,
                Hres=Hres,
                xC=xC,
                yC=yC,
                zC=zC,
                R=R,
                zO=zO,
                lights=lights,
                k_a=k_a,
                k_d=k_d,
                k_s=k_s,
                shininess=n,
            )

            img_array = normalize_to_8bit(I, mask)
            img = Image.fromarray(img_array, mode="L")
            self.image = img

            self._update_canvas_image()

            # Обновляем статистику
            self._update_stats(stats)

        except Exception as e:
            messagebox.showerror("Ошибка", str(e))

    def on_save(self):
        if self.image is None:
            messagebox.showwarning("Нет изображения", "Сначала выполните расчёт.")
            return
        fname = filedialog.asksaveasfilename(
            title="Сохранить изображение",
            defaultextension=".png",
            filetypes=[("PNG", "*.png"), ("BMP", "*.bmp"), ("JPEG", "*.jpg;*.jpeg"), ("Все файлы", "*.*")],
        )
        if not fname:
            return
        try:
            self.image.save(fname)
            messagebox.showinfo("Сохранено", f"Изображение сохранено в файл:\n{fname}")
        except Exception as e:
            messagebox.showerror("Ошибка сохранения", str(e))

    # --------------------- Вспомогательные методы --------------------- #

    @staticmethod
    def _strip_comments(text: str) -> str:
        """
        Удаляем строки, начинающиеся с '#'.
        """
        lines = []
        for line in text.splitlines():
            stripped = line.strip()
            if stripped.startswith("#") or stripped == "":
                continue
            lines.append(line)
        return "\n".join(lines)

    def _update_canvas_image(self):
        if self.image is None:
            return

        # Подгоняем под размер canvas (с сохранением пропорций)
        canvas_w = self.canvas.winfo_width()
        canvas_h = self.canvas.winfo_height()
        if canvas_w <= 1 or canvas_h <= 1:
            # Canvas ещё не инициализирован — запросим обновление позже
            self.after(100, self._update_canvas_image)
            return

        img = self.image.copy()
        img.thumbnail((canvas_w, canvas_h))
        self.photo = ImageTk.PhotoImage(img)

        self.canvas.delete("all")
        self.canvas.create_image(canvas_w // 2, canvas_h // 2, image=self.photo)

    def _update_stats(self, stats):
        self.txt_stats.config(state=tk.NORMAL)
        self.txt_stats.delete("1.0", tk.END)

        I_max = stats.get("I_max", 0.0)*1000000
        I_min = stats.get("I_min", 0.0)*1000000

        self.txt_stats.insert(tk.END, f"Максимальная яркость (абс.): {I_max:.6g}\n")
        self.txt_stats.insert(tk.END, f"Минимальная яркость (абс.): {I_min:.6g}\n\n")

        self.txt_stats.insert(tk.END, "Яркость в трёх точках на сфере:\n")
        for p in stats.get("points", []):
            label = p["label"]
            (px, py, pz) = p["coords"]
            I_p = p["I"]*1000
            self.txt_stats.insert(
                tk.END,
                f"{label}:\n"
                f"  coords = ({px:.2f}, {py:.2f}, {pz:.2f}) мм\n"
                f"  I = {I_p:.6g}\n",
            )
        self.txt_stats.config(state=tk.DISABLED)


if __name__ == "__main__":
    app = SphereBrightnessApp()
    app.mainloop()
