# Лабораторная 3. Линейная регрессия — Отчёт
## Датасет
- Файл: `Student_Performance.csv`
- Строк: **10000**, признаков (без цели): **5**
- Целевая переменная: **Performance_Index**
## Предобработка
- Пропуски: числовые → медиана; категориальные → мода
- Кодирование категориальных — One-Hot (drop_first=True)
- Стандартизация числовых — z-score (только числовые колонки)
- Z-score (mean, std) по числовым:
  - Hours_Studied: mean=4.9929, std=2.5893
  - Previous_Scores: mean=69.4457, std=17.3432
  - Sleep_Hours: mean=6.5306, std=1.6959
  - Sample_Question_Papers_Practiced: mean=4.5833, std=2.8673
## Реализация ЛР
МНК через нормальное уравнение с псевдообратной матрицей: `θ = pinv(X) @ y`; к X добавлен столбец единиц (интерцепт).
## Наборы признаков
- Модель A: только числовые: Hours_Studied, Previous_Scores, Sleep_Hours, Sample_Question_Papers_Practiced
- Модель B: все признаки после предобработки (числовые + OHE); всего: 5
- Модель C: как B + синтетика: Hours_Studied_x_Sample_Question_Papers_Practiced; всего: 6
## Результаты

## Файлы
- Превью данных: `linreg_output\preview.csv`
- Описательная статистика: `linreg_output\describe.csv`
- Сравнение моделей: `linreg_output\model_comparison.csv`
- Графики сохранены в `linreg_output\plots/`
