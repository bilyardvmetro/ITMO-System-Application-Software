import tkinter as tk
from tkinter import ttk, filedialog, messagebox

import matplotlib.pyplot as plt
import numpy as np
from PIL import Image, ImageTk

M_TO_MM = 1000.0

def _save_square_png(I01, filename):
    """
    I01: float array H×W×3 в 0..1
    Сохраняет PNG с квадратным холстом (без растяжения), дополняя черным.
    """
    img = (I01 * 255).clip(0, 255).astype(np.uint8)
    H, W, _ = img.shape

    if H == W:
        Image.fromarray(img, mode="RGB").save(filename)
        return

    S = max(H, W)
    canvas = np.zeros((S, S, 3), dtype=np.uint8)

    y0 = (S - H) // 2
    x0 = (S - W) // 2
    canvas[y0:y0+H, x0:x0+W] = img

    Image.fromarray(canvas, mode="RGB").save(filename)


# ----------------------- ПАРСИНГ ИСТОЧНИКОВ ----------------------- #

def parse_lights(text: str):
	"""
	Формат строки:
	xL yL zL I0 RL GL BL
	где все значения float, R,G,B ∈ [0.0..1.0]

	Пример:
			0 0 3000 2000 1 1 1
			1500 0 3500 1000 1 0.3 0.3
	"""
	lights = []
	for line in text.strip().splitlines():
		line = line.strip()
		if not line or line.startswith("#"):
			continue

		parts = line.replace(",", " ").split()
		if len(parts) != 7:
			raise ValueError(f"Неверный формат источника света:\n'{line}'\n"
			                 f"Ожидалось: x y z I0 RL GL BL")

		xL, yL, zL, I0, RL, GL, BL = map(float, parts)

		xL *= M_TO_MM
		yL *= M_TO_MM
		zL *= M_TO_MM

		lights.append((xL, yL, zL, I0, RL, GL, BL))

	if not lights:
		raise ValueError("Нужно задать хотя бы один источник света.")

	return lights


# ------------------------ МОДЕЛЬ БЛИНН-ФОНГА ------------------------ #

def blinn_phong_color(nx, ny, nz, vx, vy, vz, lx, ly, lz,
                      dist2, I0, light_rgb,
                      ka, kd, ks, n,
                      sphere_color):
	"""
	Векторная цветная модель Блинн-Фонга.
	Возвращает массив RGB яркостей.
	"""

	# diffuse = kd * max(N·L, 0)
	NdotL = nx * lx + ny * ly + nz * lz
	NdotL = np.clip(NdotL, 0, 1)

	# H = normalize(L + V)
	hx = lx + vx
	hy = ly + vy
	hz = lz + vz
	H_len = np.sqrt(hx * hx + hy * hy + hz * hz)
	H_len[H_len == 0] = 1
	hx /= H_len
	hy /= H_len
	hz /= H_len

	# specular = ks * max(N·H, 0)^n
	NdotH = nx * hx + ny * hy + nz * hz
	NdotH = np.clip(NdotH, 0, 1)
	specular = ks * (NdotH ** n)

	diffuse = kd * NdotL
	ambient = ka

	intensity = (ambient + diffuse + specular) * I0 / dist2

	# возвращаем 3-канальный цвет:
	# sphere_color * light_color * intensity
	RGB = (sphere_color[0] * light_rgb[0] * intensity,
	       sphere_color[1] * light_rgb[1] * intensity,
	       sphere_color[2] * light_rgb[2] * intensity)

	return RGB


# ------------------------ ОДНА СФЕРА ------------------------ #

def compute_sphere(
		X, Y,
		xC, yC, zC, R,
		zO,
		lights,
		ka, kd, ks, n,
		sphere_color
):
	"""
	Возвращает:
			I_rgb  — (H, W, 3) цветовая яркость
			mask   — маска сферических точек
			Z      — глубина точки сферы
	"""

	dx = X - xC
	dy = Y - yC
	r2 = dx * dx + dy * dy
	mask = r2 <= R * R

	H, W = X.shape
	I_rgb = np.zeros((H, W, 3), dtype=np.float64)

	if not np.any(mask):
		return I_rgb, mask, np.full_like(X, zC, float)

	front_sign = -1 if zO < zC else 1
	dz = np.zeros_like(X)
	dz[mask] = front_sign * np.sqrt(R * R - r2[mask])

	Z = np.full_like(X, zC, float)
	Z[mask] = zC + dz[mask]

	Nx = np.zeros_like(X)
	Ny = np.zeros_like(Y)
	Nz = np.zeros_like(Z)

	Nx[mask] = dx[mask] / R
	Ny[mask] = dy[mask] / R
	Nz[mask] = (Z[mask] - zC) / R

	# Вектор к наблюдателю
	Ox, Oy, Oz = 0, 0, zO

	Vx = np.zeros_like(X)
	Vy = np.zeros_like(Y)
	Vz = np.zeros_like(Z)

	Vx[mask] = Ox - X[mask]
	Vy[mask] = Oy - Y[mask]
	Vz[mask] = Oz - Z[mask]

	V_len = np.sqrt(Vx * Vx + Vy * Vy + Vz * Vz)
	V_len[V_len == 0] = 1
	Vx /= V_len
	Vy /= V_len
	Vz /= V_len

	# Складываем вклад от всех источников
	for (xL, yL, zL, I0, RL, GL, BL) in lights:
		lx = np.zeros_like(X)
		ly = np.zeros_like(Y)
		lz = np.zeros_like(Z)

		lx[mask] = xL - X[mask]
		ly[mask] = yL - Y[mask]
		lz[mask] = zL - Z[mask]

		L_len = np.sqrt(lx * lx + ly * ly + lz * lz)
		L_len[L_len == 0] = 1

		lx_n = lx / L_len
		ly_n = ly / L_len
		lz_n = lz / L_len

		light_rgb = (RL, GL, BL)

		dist2 = L_len * L_len
		dist2[dist2 == 0] = 1

		Rr, Gg, Bb = blinn_phong_color(
			Nx, Ny, Nz,
			Vx, Vy, Vz,
			lx_n, ly_n, lz_n,
			dist2,
			I0,
			light_rgb,
			ka, kd, ks, n,
			sphere_color
		)

		I_rgb[..., 0] += Rr
		I_rgb[..., 1] += Gg
		I_rgb[..., 2] += Bb

	return I_rgb, mask, Z


# ----------------------- ДВЕ СФЕРЫ + ПЕРЕКРЫТИЕ ----------------------- #

def compute_two_spheres_color(
		W, H, Wres, Hres,
		x1, y1, z1, R1,
		x2, y2, z2, R2,
		zO,
		lights,
		ka1, kd1, ks1, n1, col1,
		ka2, kd2, ks2, n2, col2
):
	xs = np.linspace(-W / 2, W / 2, Wres)
	ys = np.linspace(-H / 2, H / 2, Hres)
	X, Y = np.meshgrid(xs, ys)

	I1, m1, Z1 = compute_sphere(X, Y, x1, y1, z1, R1, zO, lights,
	                            ka1, kd1, ks1, n1, col1)
	I2, m2, Z2 = compute_sphere(X, Y, x2, y2, z2, R2, zO, lights,
	                            ka2, kd2, ks2, n2, col2)

	Hh, Ww = X.shape
	I = np.zeros((Hh, Ww, 3), float)
	mask = m1 | m2

	big = 1e12
	d1 = np.where(m1, np.abs(Z1 - zO), big)
	d2 = np.where(m2, np.abs(Z2 - zO), big)

	only1 = m1 & ~m2
	only2 = m2 & ~m1
	both = m1 & m2

	I[only1] = I1[only1]
	I[only2] = I2[only2]

	nearer1 = both & (d1 <= d2)
	nearer2 = both & (d2 < d1)

	I[nearer1] = I1[nearer1]
	I[nearer2] = I2[nearer2]

	return I, mask


def _ray_sphere_t(Ox, Oy, Oz, Dx, Dy, Dz, Cx, Cy, Cz, R):
	"""
	Векторизованное пересечение луча со сферой.
	Возвращает t (H,W) и hit (H,W)
	"""
	OCx = Ox - Cx
	OCy = Oy - Cy
	OCz = Oz - Cz

	b = 2.0 * (Dx * OCx + Dy * OCy + Dz * OCz)
	c = OCx * OCx + OCy * OCy + OCz * OCz - R * R
	disc = b * b - 4.0 * c

	hit = disc >= 0.0
	t = np.full_like(disc, np.inf, dtype=np.float64)

	if np.any(hit):
		sd = np.sqrt(np.maximum(disc, 0.0))
		t0 = (-b - sd) / 2.0
		t1 = (-b + sd) / 2.0
		t_hit = np.where(t0 > 1e-6, t0, np.where(t1 > 1e-6, t1, np.inf))
		t = np.where(hit, t_hit, np.inf)

	hit = np.isfinite(t)
	return t, hit


def _soft_shadow_k(
		Px, Py, Pz,  # (N,) точки на поверхности (в маске)
		light_xyz,  # (xL,yL,zL)
		light_radius,  # радиус "диска" источника (мм)
		other_sphere,  # (Cx,Cy,Cz,R)
		samples=16,
		seed=1
):
	"""
	Векторный коэффициент видимости источника: 1..0
	Имитируем площадочный источник (диск) через несколько точечных сэмплов.
	"""
	xL, yL, zL = light_xyz
	Cx, Cy, Cz, R = other_sphere

	rng = np.random.default_rng(seed)

	visible = np.zeros(Px.shape, dtype=np.float64)

	# сэмплы на диске в плоскости XY (достаточно для твоих картинок)
	# (если хочешь "строго", можно ориентировать диск по направлению на сцену, но обычно не нужно)
	for _ in range(samples):
		a = rng.uniform(0.0, 2 * np.pi)
		r = light_radius * np.sqrt(rng.uniform(0.0, 1.0))
		sx = xL + r * np.cos(a)
		sy = yL + r * np.sin(a)
		sz = zL

		lx = sx - Px
		ly = sy - Py
		lz = sz - Pz

		L_len = np.sqrt(lx * lx + ly * ly + lz * lz)
		L_len[L_len == 0] = 1.0

		lx_n = lx / L_len
		ly_n = ly / L_len
		lz_n = lz / L_len

		eps = 1e-3
		Ox = Px + lx_n * eps
		Oy = Py + ly_n * eps
		Oz = Pz + lz_n * eps

		t, hit = _ray_sphere_t(Ox, Oy, Oz, lx_n, ly_n, lz_n, Cx, Cy, Cz, R)

		occluded = hit & (t < L_len)
		visible += (~occluded).astype(np.float64)

	return visible / samples


def _normalize_image01(Irgb, mask):
	"""
	Нормировка 0..1 по ОДНОМУ общему максимуму внутри mask
	"""
	out = np.zeros_like(Irgb, dtype=np.float64)
	if not np.any(mask):
		return out

	mx = Irgb[mask].max()
	if mx > 0:
		out = Irgb / mx
	return np.clip(out, 0.0, 1.0)


def render_view(
		view_name,
		W, H, Wres, Hres,
		spheres,  # [(x,y,z,R,ka,kd,ks,n,color), ...]  ровно 2 сферы
		lights,
		zO,
		with_shadow=True,
):
	"""
	Реальный "3D" рендер сцены в ортографической проекции:
		front -> XY (лучи вдоль +Z или -Z)
		right -> XZ (лучи вдоль +Y или -Y)
		top   -> YZ (лучи вдоль +X или -X)

	Возвращает Irgb в диапазоне 0..1 (Hres×Wres×3)
	"""

	# Оси плоскости и ось "в глубину" (along ray)
	# u,v — координаты на плоскости картинки
	# XY, depth = Z
	if view_name == "front":
		u_name, v_name, d_name = "x", "y", "z"
		u_min, u_max = -W / 2, W / 2
		v_min, v_max = -H / 2, H / 2
		# ось глубины = Z, камера на z=zO
		cam_d = zO
		# средняя глубина сцены
		scene_d = 0.5 * (spheres[0][2] + spheres[1][2])
		# если камера "перед" сценой -> смотрим в +Z, иначе в -Z
		d_dir = 1.0 if cam_d <= scene_d else -1.0
		# лучи: D=(0,0,d_dir)
		Dx, Dy, Dz = 0.0, 0.0, d_dir

		# O: (x=u, y=v, z=cam_d - d_dir*FAR)
		FAR = 1e6
		d0 = cam_d - d_dir * FAR

		# --- квадратные пиксели ---
		du = (u_max - u_min) / Wres
		dv = (v_max - v_min) / Hres
		pixel = max(du, dv)

		u_center = 0.5 * (u_min + u_max)
		v_center = 0.5 * (v_min + v_max)

		u = u_center + (np.arange(Wres) - Wres / 2 + 0.5) * pixel
		v = v_center + (np.arange(Hres) - Hres / 2 + 0.5) * pixel
		U, V = np.meshgrid(u, v)

		Ox = U
		Oy = V
		Oz = np.full_like(U, d0)
	# XZ, depth = Y
	elif view_name == "top":
		u_min, u_max = -W / 2, W / 2  # X
		z_center = 0.5 * (spheres[0][2] + spheres[1][2])  # средний Z сцены
		v_min, v_max = z_center - H / 2, z_center + H / 2

		cam_d = 0.0  # камера на Y (используем zO как "Y-камера"?)
		cam_d = zO
		scene_d = 0.5 * (spheres[0][1] + spheres[1][1])  # средний Y
		d_dir = 1.0 if cam_d <= scene_d else -1.0
		Dx, Dy, Dz = 0.0, d_dir, 0.0

		FAR = 1e6
		d0 = cam_d - d_dir * FAR

		# --- квадратные пиксели ---
		du = (u_max - u_min) / Wres
		dv = (v_max - v_min) / Hres
		pixel = max(du, dv)

		u_center = 0.5 * (u_min + u_max)
		v_center = 0.5 * (v_min + v_max)

		u = u_center + (np.arange(Wres) - Wres / 2 + 0.5) * pixel
		v = v_center + (np.arange(Hres) - Hres / 2 + 0.5) * pixel
		U, V = np.meshgrid(u, v)

		Ox = U
		Oy = np.full_like(U, d0)
		Oz = V
	# YZ, depth = X
	elif view_name == "right":
		u_min, u_max = -W / 2, W / 2  # Y
		z_center = 0.5 * (spheres[0][2] + spheres[1][2])  # средний Z сцены
		v_min, v_max = z_center - H / 2, z_center + H / 2

		cam_d = zO  # камера на X
		scene_d = 0.5 * (spheres[0][0] + spheres[1][0])  # средний X
		d_dir = 1.0 if cam_d <= scene_d else -1.0
		Dx, Dy, Dz = d_dir, 0.0, 0.0

		FAR = 1e6
		d0 = cam_d - d_dir * FAR

		# --- квадратные пиксели ---
		du = (u_max - u_min) / Wres
		dv = (v_max - v_min) / Hres
		pixel = max(du, dv)

		u_center = 0.5 * (u_min + u_max)
		v_center = 0.5 * (v_min + v_max)

		u = u_center + (np.arange(Wres) - Wres / 2 + 0.5) * pixel
		v = v_center + (np.arange(Hres) - Hres / 2 + 0.5) * pixel
		U, V = np.meshgrid(u, v)

		Ox = np.full_like(U, d0)
		Oy = U
		Oz = V

	else:
		raise ValueError("view_name must be one of: front, right, top")

	# Делаем t для обеих сфер и выбираем ближайшую (z-buffer)
	(x1, y1, z1, R1, ka1, kd1, ks1, n1, col1) = spheres[0]
	(x2, y2, z2, R2, ka2, kd2, ks2, n2, col2) = spheres[1]

	t1, hit1 = _ray_sphere_t(Ox, Oy, Oz, Dx, Dy, Dz, x1, y1, z1, R1)
	t2, hit2 = _ray_sphere_t(Ox, Oy, Oz, Dx, Dy, Dz, x2, y2, z2, R2)

	best_t = np.where(hit1, t1, np.inf)
	best_id = np.where(hit1, 0, -1)

	better2 = hit2 & (t2 < best_t)
	best_t = np.where(better2, t2, best_t)
	best_id = np.where(better2, 1, best_id)

	mask = best_id >= 0
	Hh, Ww = best_id.shape
	Irgb = np.zeros((Hh, Ww, 3), dtype=np.float64)

	if not np.any(mask):
		return Irgb

	# Точки пересечения
	Px = np.zeros_like(Ox, dtype=np.float64)
	Py = np.zeros_like(Oy, dtype=np.float64)
	Pz = np.zeros_like(Oz, dtype=np.float64)

	Px[mask] = Ox[mask] + best_t[mask] * Dx
	Py[mask] = Oy[mask] + best_t[mask] * Dy
	Pz[mask] = Oz[mask] + best_t[mask] * Dz

	# Вектор к наблюдателю для ортографики — постоянный: V = -D
	# (нормированный уже, т.к. Dx,Dy,Dz = ±1 по одной оси)
	Vx = -Dx
	Vy = -Dy
	Vz = -Dz

	# Векторизованный шейдинг по двум сферам
	for sid in (0, 1):
		m = best_id == sid
		if not np.any(m):
			continue

		if sid == 0:
			Cx, Cy, Cz, R = x1, y1, z1, R1
			ka, kd, ks, nn = ka1, kd1, ks1, n1
			scol = col1
			other = (x2, y2, z2, R2)
		else:
			Cx, Cy, Cz, R = x2, y2, z2, R2
			ka, kd, ks, nn = ka2, kd2, ks2, n2
			scol = col2
			other = (x1, y1, z1, R1)

		# Нормали
		nx = (Px[m] - Cx) / R
		ny = (Py[m] - Cy) / R
		nz = (Pz[m] - Cz) / R

		ka_eff = ka

		# V одинаковый
		vx = np.full(nx.shape, Vx, dtype=np.float64)
		vy = np.full(ny.shape, Vy, dtype=np.float64)
		vz = np.full(nz.shape, Vz, dtype=np.float64)

		# Суммируем источники
		R_acc = np.zeros(nx.shape, dtype=np.float64)
		G_acc = np.zeros(nx.shape, dtype=np.float64)
		B_acc = np.zeros(nx.shape, dtype=np.float64)

		for (xL, yL, zL, I0, RL, GL, BL) in lights:
			# L = light - P
			lx = xL - Px[m]
			ly = yL - Py[m]
			lz = zL - Pz[m]

			L_len = np.sqrt(lx * lx + ly * ly + lz * lz)
			L_len[L_len == 0] = 1.0

			lx_n = lx / L_len
			ly_n = ly / L_len
			lz_n = lz / L_len

			dist2 = L_len * L_len
			dist2[dist2 == 0] = 1.0

			# Тень (жёсткая): луч от точки к источнику, проверяем вторую сферу
			# --- считаем full и ambient-only один раз ---
			R_full, G_full, B_full = blinn_phong_color(
				nx, ny, nz, vx, vy, vz,
				lx_n, ly_n, lz_n,
				dist2, I0, (RL, GL, BL),
				ka_eff, kd, ks, nn,
				scol
			)

			R_amb, G_amb, B_amb = blinn_phong_color(
				nx, ny, nz, vx, vy, vz,
				lx_n, ly_n, lz_n,
				dist2, I0, (RL, GL, BL),
				ka_eff, 0.0, 0.0, nn,
				scol
			)

			# --- shadow_k: XY мягкая, остальные жёсткая ---
			if with_shadow:
				if view_name == "front":  # XY soft shadow
					# расстояние от источника до затеняющей сферы
					bx, by, bz, br = other
					d_light_blocker = np.sqrt(
						(xL - bx) ** 2 +
						(yL - by) ** 2 +
						(zL - bz) ** 2
					)

					# расстояние от блокера до точки на поверхности
					d_blocker_receiver = np.sqrt(
						(Px[m] - bx) ** 2 +
						(Py[m] - by) ** 2 +
						(Pz[m] - bz) ** 2
					)

					# базовый радиус источника (в метрах!)
					R_L = 0.25  # ← физический размер источника, а не "магия"

					# физически корректный радиус penumbra
					effective_light_radius = R_L * (d_blocker_receiver / d_light_blocker)

					# защита от нулей и перегибов
					effective_light_radius = np.clip(
						effective_light_radius,
						0.02,  # минимальная мягкость
						0.6  # максимальная
					)

					shadow_k = _soft_shadow_k(
						Px[m], Py[m], Pz[m],
						(xL, yL, zL),
						light_radius=effective_light_radius,
						other_sphere=other,
						samples=24,
						seed=123
					)
				else:
					ox2, oy2, oz2, r2 = other
					eps = 1e-3
					sOx = Px[m] + lx_n * eps
					sOy = Py[m] + ly_n * eps
					sOz = Pz[m] + lz_n * eps

					t_shadow, hit_shadow = _ray_sphere_t(
						sOx, sOy, sOz,
						lx_n, ly_n, lz_n,
						ox2, oy2, oz2, r2
					)
					in_shadow = hit_shadow & (t_shadow < L_len)
					shadow_k = (~in_shadow).astype(np.float64)

				# Смешивание как в рендерах:
				# ambient остаётся, direct (diffuse+spec) приглушаем shadow_k
				Rr = R_amb + shadow_k * (R_full - R_amb)
				Gg = G_amb + shadow_k * (G_full - G_amb)
				Bb = B_amb + shadow_k * (B_full - B_amb)

			else:
				Rr, Gg, Bb = R_full, G_full, B_full

			R_acc += Rr
			G_acc += Gg
			B_acc += Bb

		Irgb[m, 0] = R_acc
		Irgb[m, 1] = G_acc
		Irgb[m, 2] = B_acc

	# Нормируем в 0..1, чтобы было “как на картинке” (иначе может быть очень темно)
	I01 = _normalize_image01(Irgb, mask)

	# Повороты итоговых изображений по требованию
	if view_name == "top":  # XZ -> 180°
		I01 = np.rot90(I01, 2)  # 2 * 90° = 180°
		I01 = np.fliplr(I01)  # горизонтальный флип
	elif view_name == "right":  # YZ -> 90° вправо
		I01 = np.rot90(I01, -1)  # -1 = clockwise (вправо)

	return I01


class SphereBrightnessApp(tk.Tk):
	def __init__(self):
		super().__init__()
		self.title("ЛР5: Две цветные сферы + цветные источники света (Blinn-Phong)")
		self.geometry("1920x1080")

		self.image = None
		self.photo = None

		self._build_gui()

	def _build_gui(self):
		main_frame = ttk.Frame(self)
		main_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

		# Левая панель
		left_frame = ttk.Frame(main_frame)
		left_frame.pack(side=tk.LEFT, fill=tk.Y)

		# Правая панель
		right_frame = ttk.Frame(main_frame)
		right_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

		# ------------------ Параметры экрана ------------------
		scr = ttk.LabelFrame(left_frame, text="Параметры экрана")
		scr.pack(fill=tk.X, pady=5)

		self.var_W = tk.DoubleVar(value=2)
		self.var_H = tk.DoubleVar(value=2)
		self.var_Wres = tk.IntVar(value=1000)
		self.var_Hres = tk.IntVar(value=1000)
		self.var_zO = tk.DoubleVar(value=-2.5)

		row = 0
		ttk.Label(scr, text="W [м]:").grid(row=row, column=0, sticky="w")
		ttk.Entry(scr, textvariable=self.var_W, width=10).grid(row=row, column=1)
		row += 1
		ttk.Label(scr, text="H [м]:").grid(row=row, column=0, sticky="w")
		ttk.Entry(scr, textvariable=self.var_H, width=10).grid(row=row, column=1)
		row += 1
		ttk.Label(scr, text="Wres:").grid(row=row, column=0, sticky="w")
		ttk.Entry(scr, textvariable=self.var_Wres, width=10).grid(row=row, column=1)
		row += 1
		ttk.Label(scr, text="Hres:").grid(row=row, column=0, sticky="w")
		ttk.Entry(scr, textvariable=self.var_Hres, width=10).grid(row=row, column=1)
		row += 1
		ttk.Label(scr, text="zO [м]:").grid(row=row, column=0, sticky="w")
		ttk.Entry(scr, textvariable=self.var_zO, width=10).grid(row=row, column=1)
		row += 1

		# ------------------ СФЕРА 1 ------------------
		s1 = ttk.LabelFrame(left_frame, text="Сфера 1 (красная)")
		s1.pack(fill=tk.X, pady=5)

		self.var_xC1 = tk.DoubleVar(value=0)
		self.var_yC1 = tk.DoubleVar(value=0)
		self.var_zC1 = tk.DoubleVar(value=3.5)
		self.var_R1 = tk.DoubleVar(value=0.6)

		self.var_ka1 = tk.DoubleVar(value=0.2)
		self.var_kd1 = tk.DoubleVar(value=1.4)
		self.var_ks1 = tk.DoubleVar(value=0.5)
		self.var_n1 = tk.DoubleVar(value=35)

		# Цвет сферы 1
		self.var_Rcol1 = tk.DoubleVar(value=1.0)
		self.var_Gcol1 = tk.DoubleVar(value=0.2)
		self.var_Bcol1 = tk.DoubleVar(value=0.2)

		row = 0
		for lbl, var in [
			("xC1 [м]:", self.var_xC1),
			("yC1 [м]:", self.var_yC1),
			("zC1 [м]:", self.var_zC1),
			("R1 [м]:", self.var_R1),
		]:
			ttk.Label(s1, text=lbl).grid(row=row, column=0, sticky="w")
			ttk.Entry(s1, textvariable=var, width=10).grid(row=row, column=1)
			row += 1

		ttk.Label(s1, text="k_a1:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s1, textvariable=self.var_ka1, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s1, text="k_d1:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s1, textvariable=self.var_kd1, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s1, text="k_s1:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s1, textvariable=self.var_ks1, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s1, text="n1:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s1, textvariable=self.var_n1, width=8).grid(row=row, column=1)
		row += 1

		# Цвет
		ttk.Label(s1, text="Цвет (R,G,B):").grid(row=row, column=0, sticky="w")
		rgb1 = ttk.Frame(s1)
		rgb1.grid(row=row, column=1)
		ttk.Entry(rgb1, textvariable=self.var_Rcol1, width=4).pack(side=tk.LEFT)
		ttk.Entry(rgb1, textvariable=self.var_Gcol1, width=4).pack(side=tk.LEFT)
		ttk.Entry(rgb1, textvariable=self.var_Bcol1, width=4).pack(side=tk.LEFT)
		row += 1

		# ------------------ СФЕРА 2 ------------------
		s2 = ttk.LabelFrame(left_frame, text="Сфера 2 (синяя)")
		s2.pack(fill=tk.X, pady=5)

		self.var_xC2 = tk.DoubleVar(value=0)
		self.var_yC2 = tk.DoubleVar(value=-0.2)
		self.var_zC2 = tk.DoubleVar(value=2.4)
		self.var_R2 = tk.DoubleVar(value=0.2)

		self.var_ka2 = tk.DoubleVar(value=0.2)
		self.var_kd2 = tk.DoubleVar(value=1.4)
		self.var_ks2 = tk.DoubleVar(value=0.85)
		self.var_n2 = tk.DoubleVar(value=90)

		self.var_Rcol2 = tk.DoubleVar(value=0.15)
		self.var_Gcol2 = tk.DoubleVar(value=0.35)
		self.var_Bcol2 = tk.DoubleVar(value=0.85)

		row = 0
		for lbl, var in [
			("xC2 [м]:", self.var_xC2),
			("yC2 [м]:", self.var_yC2),
			("zC2 [м]:", self.var_zC2),
			("R2 [м]:", self.var_R2),
		]:
			ttk.Label(s2, text=lbl).grid(row=row, column=0, sticky="w")
			ttk.Entry(s2, textvariable=var, width=10).grid(row=row, column=1)
			row += 1

		ttk.Label(s2, text="k_a2:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s2, textvariable=self.var_ka2, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s2, text="k_d2:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s2, textvariable=self.var_kd2, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s2, text="k_s2:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s2, textvariable=self.var_ks2, width=8).grid(row=row, column=1)
		row += 1
		ttk.Label(s2, text="n2:").grid(row=row, column=0, sticky="w")
		ttk.Entry(s2, textvariable=self.var_n2, width=8).grid(row=row, column=1)
		row += 1

		ttk.Label(s2, text="Цвет (R,G,B):").grid(row=row, column=0, sticky="w")
		rgb2 = ttk.Frame(s2)
		rgb2.grid(row=row, column=1)
		ttk.Entry(rgb2, textvariable=self.var_Rcol2, width=4).pack(side=tk.LEFT)
		ttk.Entry(rgb2, textvariable=self.var_Gcol2, width=4).pack(side=tk.LEFT)
		ttk.Entry(rgb2, textvariable=self.var_Bcol2, width=4).pack(side=tk.LEFT)
		row += 1

		# ------------------ Источники света ------------------
		lights_frame = ttk.LabelFrame(left_frame, text="Источники света (x y z I0 RL GL BL)")
		lights_frame.pack(fill=tk.BOTH, pady=5, expand=True)

		self.txt_lights = tk.Text(lights_frame, width=32, height=8)
		self.txt_lights.pack(fill=tk.BOTH, expand=True)

		self.txt_lights.insert(
			"1.0",
			"1.6 -1.2 0.6 9500 1.0 1.0 1.0\n"
			"-0.9 -0.4 1.4 2200 0.7 0.7 0.9\n"
			"0.0 0.8 5.5 1200 1.0 0.4 0.4\n"
		)

		# ------------------ Кнопки ------------------
		btns = ttk.Frame(left_frame)
		btns.pack(fill=tk.X, pady=5)
		ttk.Button(btns, text="Рассчитать", command=self.on_compute).pack(side=tk.LEFT, expand=True, fill=tk.X)
		ttk.Button(btns, text="Сохранить", command=self.on_save).pack(side=tk.LEFT, expand=True, fill=tk.X)

		# ------------------ Статистика ------------------
		stats = ttk.LabelFrame(left_frame, text="Статистика")
		stats.pack(fill=tk.BOTH, expand=True)
		self.txt_stats = tk.Text(stats, width=30, height=12, state=tk.DISABLED)
		self.txt_stats.pack(fill=tk.BOTH, expand=True)

		# ------------------ CANVAS ------------------
		img_frame = ttk.LabelFrame(right_frame, text="Изображение")
		img_frame.pack(fill=tk.BOTH, expand=True)
		self.canvas = tk.Canvas(img_frame, bg="black")
		self.canvas.pack(fill=tk.BOTH, expand=True)

	# ------------------ КНОПКА "РАССЧИТАТЬ" ------------------
	def on_compute(self):
		try:
			# экран
			W = float(self.var_W.get()) * M_TO_MM
			H = float(self.var_H.get()) * M_TO_MM
			Wres = int(self.var_Wres.get())
			Hres = int(self.var_Hres.get())
			zO = float(self.var_zO.get()) * M_TO_MM

			lights = parse_lights(self.txt_lights.get("1.0", tk.END))

			# сфера 1
			x1 = float(self.var_xC1.get()) * M_TO_MM
			y1 = float(self.var_yC1.get()) * M_TO_MM
			z1 = float(self.var_zC1.get()) * M_TO_MM
			R1 = float(self.var_R1.get()) * M_TO_MM
			ka1 = float(self.var_ka1.get())
			kd1 = float(self.var_kd1.get())
			ks1 = float(self.var_ks1.get())
			n1 = float(self.var_n1.get())
			col1 = (
				float(self.var_Rcol1.get()),
				float(self.var_Gcol1.get()),
				float(self.var_Bcol1.get())
			)

			# сфера 2
			x2 = float(self.var_xC2.get()) * M_TO_MM
			y2 = float(self.var_yC2.get()) * M_TO_MM
			z2 = float(self.var_zC2.get()) * M_TO_MM
			R2 = float(self.var_R2.get()) * M_TO_MM
			ka2 = float(self.var_ka2.get())
			kd2 = float(self.var_kd2.get())
			ks2 = float(self.var_ks2.get())
			n2 = float(self.var_n2.get())
			col2 = (
				float(self.var_Rcol2.get()),
				float(self.var_Gcol2.get()),
				float(self.var_Bcol2.get())
			)

			# набор сфер для 3D-рендера
			spheres = [
				(x1, y1, z1, R1, ka1, kd1, ks1, n1, col1),
				(x2, y2, z2, R2, ka2, kd2, ks2, n2, col2),
			]

			# ---------------- GUI: 3D-рендер (front/XY) с тенями ----------------
			I_front = render_view(
				"front",
				W, H, Wres, Hres,
				spheres,
				lights,
				zO,
				with_shadow=True
			)  # 0..1

			I8 = (I_front * 255).clip(0, 255).astype(np.uint8)
			self.image = Image.fromarray(I8, mode="RGB")
			self._update_canvas_image()

			# ---------------- Статистика: старый расчёт (как было) ----------------
			# (чтобы не переписывать _update_stats и чтобы маска была корректной)
			Irgb_raw, mask = compute_two_spheres_color(
				W, H, Wres, Hres,
				x1, y1, z1, R1,
				x2, y2, z2, R2,
				zO,
				lights,
				ka1, kd1, ks1, n1, col1,
				ka2, kd2, ks2, n2, col2
			)
			self._update_stats(Irgb_raw, mask)

			# ---------------- Сохранение 3 проекций на диск ----------------
			I_xy = render_view("front", W, H, Wres, Hres, spheres, lights, zO, with_shadow=True)
			_save_square_png(I_xy, "render_XY.png")

			I_xz = render_view("top", W, H, Wres, Hres, spheres, lights, zO, with_shadow=True)
			_save_square_png(I_xz, "render_XZ.png")

			I_yz = render_view("right", W, H, Wres, Hres, spheres, lights, zO, with_shadow=True)
			_save_square_png(I_yz, "render_YZ.png")


		except Exception as e:
			messagebox.showerror("Ошибка", str(e))

	# ------------------ РИСОВАНИЕ ------------------
	def _update_canvas_image(self):
		if self.image is None:
			return
		w = self.canvas.winfo_width()
		h = self.canvas.winfo_height()
		if w < 10 or h < 10:
			self.after(100, self._update_canvas_image)
			return
		img = self.image.copy()
		img.thumbnail((w, h))
		self.photo = ImageTk.PhotoImage(img)
		self.canvas.delete("all")
		self.canvas.create_image(w // 2, h // 2, image=self.photo)

	# ------------------ СТАТИСТИКА ------------------
	def _update_stats(self, Irgb, mask):
		self.txt_stats.config(state=tk.NORMAL)
		self.txt_stats.delete("1.0", tk.END)

		inside = mask
		if not np.any(inside):
			self.txt_stats.insert(tk.END, "Сферы не попали в область экрана")
		else:
			mx = Irgb[inside].max() * M_TO_MM
			mn = Irgb[inside].min() * M_TO_MM
			self.txt_stats.insert(tk.END, f"Макс. яркость: {mx:.5g}\n")
			self.txt_stats.insert(tk.END, f"Мин. яркость: {mn:.5g}\n")

		self.txt_stats.config(state=tk.DISABLED)

	# ------------------ СОХРАНЕНИЕ ------------------
	def on_save(self):
		if self.image is None:
			messagebox.showinfo("Нет изображения", "Сначала рассчитай картинку")
			return
		fname = filedialog.asksaveasfilename(defaultextension=".png")
		if fname:
			self.image.save(fname)
			messagebox.showinfo("Сохранено", fname)


if __name__ == "__main__":
	app = SphereBrightnessApp()
	app.mainloop()
