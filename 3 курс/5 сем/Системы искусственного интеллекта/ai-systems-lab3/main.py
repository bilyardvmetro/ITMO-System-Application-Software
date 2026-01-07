from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# -------------------- Конфигурация --------------------
DATA_PATH = Path("Student_Performance.csv")
SEP = ";"
OUT_DIR = Path("linreg_output")
PLOTS_DIR = OUT_DIR / "plots"
REPORT_MD = OUT_DIR / "linreg_report.md"
RANDOM_STATE = 42
TRAIN_RATIO = 0.8


# -------------------- Утилиты --------------------
def ensure_dirs():
	OUT_DIR.mkdir(exist_ok=True, parents=True)
	PLOTS_DIR.mkdir(exist_ok=True, parents=True)


def rmse(y_true: np.ndarray, y_pred: np.ndarray) -> float:
	return float(np.sqrt(np.mean((y_true - y_pred) ** 2)))


def r2(y_true: np.ndarray, y_pred: np.ndarray) -> float:
	ss_res = float(np.sum((y_true - y_pred) ** 2))
	ss_tot = float(np.sum((y_true - y_true.mean()) ** 2))
	return 1 - ss_res / ss_tot if ss_tot != 0 else float("nan")


def fit_normal_equation(Xb: np.ndarray, y: np.ndarray) -> np.ndarray:
	# МНК вручную через аналитическое решение
	XT = Xb.T
	XTX = XT @ Xb
	XTy = XT @ y
	theta = np.linalg.inv(XTX) @ XTy
	return theta


def predict(Xb: np.ndarray, theta: np.ndarray) -> np.ndarray:
	return Xb @ theta


def to_design_matrix(X: np.ndarray) -> np.ndarray:
	# Добавляет столбец единиц слева
	return np.c_[np.ones((X.shape[0], 1)), X]


# -------------------- Главный сценарий --------------------
def main():
	ensure_dirs()
	assert DATA_PATH.exists(), f"Не найден файл {DATA_PATH.resolve()}"

	# 1) Загрузка
	df = pd.read_csv(DATA_PATH, sep=SEP)
	df.columns = [c.strip().replace(" ", "_") for c in df.columns]

	# Выберем целевую
	target = "Performance_Index"

	# 2) Базовая статистика
	preview_path = OUT_DIR / "preview.csv"
	stats_path = OUT_DIR / "describe.csv"
	df.head(20).to_csv(preview_path, index=False)
	df.describe(include="all").T.to_csv(stats_path)

	# 3) Типы признаков
	numeric_cols = df.select_dtypes(include=[np.number]).columns.tolist()
	categorical_cols = [c for c in df.columns if c not in numeric_cols]

	# убрать target из списков
	if target in numeric_cols:
		numeric_cols.remove(target)
	if target in categorical_cols:
		categorical_cols.remove(target)

	# 4) Графики
	for col in numeric_cols + ([target] if target in df.columns else []):
		plt.figure()
		df[col].dropna().plot(kind="hist", bins=30, title=f"Histogram: {col}")
		plt.xlabel(col)
		plt.ylabel("Count")
		plt.savefig(PLOTS_DIR / f"hist_{col}.png", bbox_inches="tight")
		plt.close()

	for col in categorical_cols:
		if df[col].nunique(dropna=True) <= 20:
			plt.figure()
			df[col].astype("object").fillna("NA").value_counts().plot(kind="bar", title=f"Counts: {col}")
			plt.xlabel(col)
			plt.ylabel("Count")
			plt.savefig(PLOTS_DIR / f"bar_{col}.png", bbox_inches="tight")
			plt.close()

	# 5) Предобработка
	df_proc = df.copy()

	# Импутация
	for col in numeric_cols + ([target] if target in df_proc.columns else []):
		df_proc[col] = df_proc[col].fillna(df_proc[col].median())

	for col in categorical_cols:
		mode = df_proc[col].mode(dropna=True)
		fill_value = mode.iloc[0] if not mode.empty else "NA"
		df_proc[col] = df_proc[col].fillna(fill_value).astype(str)

	# OHE
	df_ohe = pd.get_dummies(df_proc, columns=categorical_cols, drop_first=True)

	# Разделение X/y
	y_all = df_ohe[target].astype(float).values.reshape(-1, 1)
	X_all = df_ohe.drop(columns=[target])

	# Стандартизация числовых колонок
	X_std = X_all.copy()
	# карта средних/стд
	zscore_stats = {}
	for col in numeric_cols:
		if col in X_std.columns:
			mean = X_std[col].mean()
			std = X_std[col].std()
			if std == 0 or np.isnan(std):
				# удалить константу
				X_std.drop(columns=[col], inplace=True)
			else:
				X_std[col] = (X_std[col] - mean) / std
				zscore_stats[col] = (float(mean), float(std))

	# 6) Синтетический признак
	def add_synth(df_in: pd.DataFrame):
		df_ = df_in.copy()
		created = []
		pairs = [
			("Hours_Studied", "Sample_Question_Papers_Practiced"),
			("Hours_Studied", "Sleep_Hours"),
			("Previous_Scores", "Hours_Studied"),
		]
		for a, b in pairs:
			if a in df_.columns and b in df_.columns:
				name = f"{a}_x_{b}"
				df_[name] = df_[a] * df_[b]
				created.append(name)
				break
		if not created:
			# fallback: квадрат самого вариативного числового
			num_candidates = [c for c in df_.columns if c in X_std.columns]
			if num_candidates:
				var_col = sorted(num_candidates, key=lambda c: df_[c].var(), reverse=True)[0]
				name = f"{var_col}_squared"
				df_[name] = df_[var_col] ** 2
				created.append(name)
		return df_, created

	X_std_synth, synth_created = add_synth(X_std)

	# 7) Train/Test split
	rng = np.random.default_rng(RANDOM_STATE)
	idx = np.arange(len(X_std))
	rng.shuffle(idx)
	split = int(TRAIN_RATIO * len(idx))
	train_idx, test_idx = idx[:split], idx[split:]

	def split_arrays(Xdf: pd.DataFrame):
		X = Xdf.values.astype(float)
		Xb = to_design_matrix(X)
		return Xb[train_idx], Xb[test_idx]

	y_train = y_all[train_idx]
	y_test = y_all[test_idx]

	# 8) Модели
	# A: только числовые
	numeric_only = [c for c in ["Hours_Studied", "Previous_Scores", "Sleep_Hours", "Sample_Question_Papers_Practiced"] if
	                c in X_std.columns]
	results = []

	if numeric_only:
		XA_train, XA_test = split_arrays(X_std[numeric_only])
		thetaA = fit_normal_equation(XA_train, y_train)
		yhat_tr = predict(XA_train, thetaA)
		yhat_te = predict(XA_test, thetaA)
		results.append({
			"model": "A_numeric_only",
			"n_features": len(numeric_only),
			"RMSE_train": rmse(y_train, yhat_tr),
			"RMSE_test": rmse(y_test, yhat_te),
			"R2_train": r2(y_train, yhat_tr),
			"R2_test": r2(y_test, yhat_te),
		})
	else:
		results.append({
			"model": "A_numeric_only",
			"n_features": 0,
			"RMSE_train": float("nan"),
			"RMSE_test": float("nan"),
			"R2_train": float("nan"),
			"R2_test": float("nan"),
		})

	# B: все признаки (стандартизованные числовые + OHE)
	feats_B = list(X_std.columns)
	XB_train, XB_test = split_arrays(X_std[feats_B])
	thetaB = fit_normal_equation(XB_train, y_train)
	yhat_tr = predict(XB_train, thetaB);
	yhat_te = predict(XB_test, thetaB)
	results.append({
		"model": "B_num_plus_cat",
		"n_features": len(feats_B),
		"RMSE_train": rmse(y_train, yhat_tr),
		"RMSE_test": rmse(y_test, yhat_te),
		"R2_train": r2(y_train, yhat_tr),
		"R2_test": r2(y_test, yhat_te),
	})

	# C: B + синтетика
	feats_C = list(X_std_synth.columns)
	XC_train, XC_test = split_arrays(X_std_synth[feats_C])
	thetaC = fit_normal_equation(XC_train, y_train)
	yhat_tr = predict(XC_train, thetaC);
	yhat_te = predict(XC_test, thetaC)
	results.append({
		"model": "C_with_synthetic",
		"n_features": len(feats_C),
		"RMSE_train": rmse(y_train, yhat_tr),
		"RMSE_test": rmse(y_test, yhat_te),
		"R2_train": r2(y_train, yhat_tr),
		"R2_test": r2(y_test, yhat_te),
		"synthetic_features": synth_created
	})

	res_df = pd.DataFrame(results)
	res_path = OUT_DIR / "model_comparison.csv"
	res_df.to_csv(res_path, index=False)

	# 9) Отчёт
	report = []
	report.append("# Лабораторная 3. Линейная регрессия — Отчёт\n")
	report.append("## Датасет\n")
	report.append(f"- Файл: `{DATA_PATH.name}`\n- Строк: **{len(df)}**, признаков (без цели): **{df.shape[1] - 1}**\n")
	report.append(f"- Целевая переменная: **{target}**\n")
	report.append("## Предобработка\n")
	report.append("- Пропуски: числовые → медиана; категориальные → мода\n")
	report.append("- Кодирование категориальных — One-Hot (drop_first=True)\n")
	report.append("- Стандартизация числовых — z-score (только числовые колонки)\n")
	if zscore_stats:
		report.append("- Z-score (mean, std) по числовым:\n")
		for k, (m, s) in zscore_stats.items():
			report.append(f"  - {k}: mean={m:.4f}, std={s:.4f}\n")
	report.append("## Реализация ЛР\n")
	report.append(
		"МНК через нормальное уравнение с псевдообратной матрицей: `θ = pinv(X) @ y`; к X добавлен столбец единиц (интерцепт).\n")
	report.append("## Наборы признаков\n")
	report.append(f"- Модель A: только числовые: {', '.join(numeric_only) if numeric_only else '—'}\n")
	report.append(f"- Модель B: все признаки после предобработки (числовые + OHE); всего: {len(feats_B)}\n")
	report.append(
		f"- Модель C: как B + синтетика: {', '.join(synth_created) if synth_created else '—'}; всего: {len(feats_C)}\n")
	report.append("## Результаты\n")
	report.append("\n## Файлы\n")
	report.append(f"- Превью данных: `{preview_path}`\n")
	report.append(f"- Описательная статистика: `{stats_path}`\n")
	report.append(f"- Сравнение моделей: `{res_path}`\n")
	report.append(f"- Графики сохранены в `{PLOTS_DIR}/`\n")

	REPORT_MD.write_text("".join(report), encoding="utf-8")

	print("Готово. Результаты в:", OUT_DIR.resolve())


if __name__ == "__main__":
	main()
