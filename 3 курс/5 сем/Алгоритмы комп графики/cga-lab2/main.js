function setupFileInputBinding() {
    const selectFileButton = document.getElementById('selectFileButton');
    const fileInput = document.getElementById('fileInput');

    // Собираем все <img> элементы, которые нужно обновить
    const allImages = [
        document.getElementById('originalImage'),
        document.getElementById('brightnessImage'),
        document.getElementById('contrastImage'),
        document.getElementById('grayscaleImage'),
        document.getElementById('blurImage')
    ];

    selectFileButton.addEventListener('click', () => {
        fileInput.click();
    });

    // 2. Обработка выбора файла (опционально, но полезно)
    fileInput.addEventListener('change', (event) => {
        const file = event.target.files[0];

        if (file) {
            const reader = new FileReader();

            reader.onload = function (e) {
                // Когда файл прочитан, его Data URL содержится в e.target.result
                const imageUrl = e.target.result;

                allImages.forEach(img => {
                    img.src = imageUrl;
                    img.style.backgroundColor = 'transparent'; // Убираем заглушку
                });

                console.log('Изображение загружено и отображено.');
            };

            // Читаем файл как Data URL (Base64-кодированная строка)
            reader.readAsDataURL(file);

            // Пример: Показываем имя файла на кнопке
            selectFileButton.textContent = 'Файл: ' + file.name.substring(0, 15) + (file.name.length > 15 ? '...' : '');
        }
    });
}

function setupBrightnessControl() {
    const slider = document.getElementById('brightnessSlider');
    const valueSpan = document.getElementById('brightnessValue');
    const applyButton = document.getElementById('applyBrightnessButton');
    const image = document.getElementById('brightnessImage');

    // Устанавливаем начальное значение в процентах (если 100 - это 0%)
    let currentBrightness = 100;

    // 1. Отображение значения ползунка
    slider.addEventListener('input', () => {
        currentBrightness = slider.value;
        // Отображаем значение. Я использую проценты, где 100 - это норма (без изменений)
        valueSpan.textContent = `${currentBrightness}%`;
    });

    // 2. Применение яркости по нажатию кнопки
    applyButton.addEventListener('click', () => {
        // CSS filter brightness() принимает значение в процентах.
        // 100% - нормальная яркость (эквивалент 0 изменения)
        // 50% - вдвое темнее; 200% - вдвое ярче.

        image.style.filter = `brightness(${currentBrightness}%)`;
        console.log(`Применена яркость: ${currentBrightness}%`);

        // Также, если вы хотите, чтобы измененная картинка под "Яркость"
        // стала базой для следующей настройки, нужно обновить её src.
        // В данной упрощенной модели мы просто применяем фильтр.
    });
}

function setupContrastControl() {
    const slider = document.getElementById('contrastSlider');
    const valueSpan = document.getElementById('contrastValue');
    const applyButton = document.getElementById('applyContrastButton');
    const image = document.getElementById('contrastImage');

    // Начальное значение: 100% - нормальный контраст
    let currentContrast = 100;

    // 1. Отображение значения ползунка
    slider.addEventListener('input', () => {
        currentContrast = slider.value;
        // Отображаем значение в процентах
        valueSpan.textContent = `${currentContrast}%`;
    });

    // 2. Применение контраста по нажатию кнопки
    applyButton.addEventListener('click', () => {
        // CSS filter contrast() принимает значение в процентах.
        // 100% - норма; 0% - полностью серый; >100% - увеличенный контраст.

        image.style.filter = `contrast(${currentContrast}%)`;
        console.log(`Применен контраст: ${currentContrast}%`);
    });
}

function setupGrayscaleControl() {
    const applyButton = document.getElementById('applyGrayscaleButton');
    const image = document.getElementById('grayscaleImage');

    applyButton.addEventListener('click', () => {
        // CSS filter grayscale(1) делает изображение полностью черно-белым.
        // 1 (или 100%) - полный эффект, 0 - нет эффекта.

        image.style.filter = 'grayscale(1)';
        console.log('Применен черно-белый фильтр.');
    });
}

function setupBlurControl() {
    const slider = document.getElementById('blurSlider');
    const valueSpan = document.getElementById('blurValue');
    const applyButton = document.getElementById('applyBlurButton');
    const image = document.getElementById('blurImage');

    // Начальное значение: 0 пикселей (нет размытия)
    let currentBlur = 0;

    // 1. Отображение значения ползунка
    slider.addEventListener('input', () => {
        currentBlur = slider.value;
        // Отображаем значение в пикселях
        valueSpan.textContent = `${currentBlur}px`;
    });

    // 2. Применение блюра по нажатию кнопки
    applyButton.addEventListener('click', () => {
        // CSS filter blur() принимает значение в пикселях (px).

        image.style.filter = `blur(${currentBlur}px)`;
        console.log(`Применен блюр: ${currentBlur}px`);
    });
}

/**
 * Извлекает данные гистограммы (частоту R, G, B значений от 0 до 255) из изображения.
 * @param {HTMLImageElement} imageElement - Элемент <img>, из которого нужно получить данные.
 * @returns {object | null} Объект с массивами { r: [], g: [], b: [] } или null в случае ошибки.
 */
function getHistogramData(imageElement) {
    if (!imageElement || !imageElement.src) {
        console.error("Элемент изображения не найден или не загружен.");
        return null;
    }

    const canvas = document.getElementById('hiddenCanvas');
    if (!canvas) {
        console.error("Canvas для обработки данных не найден.");
        return null;
    }

    try {
        const width = imageElement.naturalWidth;
        const height = imageElement.naturalHeight;

        canvas.width = width;
        canvas.height = height;

        const ctx = canvas.getContext('2d');
        ctx.drawImage(imageElement, 0, 0, width, height);

        // Получаем все пиксельные данные изображения
        const imageData = ctx.getImageData(0, 0, width, height);
        const data = imageData.data;

        // Инициализируем массивы для хранения частоты (256 уровней: 0-255)
        const histogramR = new Array(256).fill(0);
        const histogramG = new Array(256).fill(0);
        const histogramB = new Array(256).fill(0);

        // Проходим по каждому пикселю.
        // Данные хранятся в формате [R1, G1, B1, A1, R2, G2, B2, A2, ...]
        for (let i = 0; i < data.length; i += 4) {
            const r = data[i];     // Красный канал (0-255)
            const g = data[i + 1]; // Зеленый канал (0-255)
            const b = data[i + 2]; // Синий канал (0-255)
            // data[i + 3] - Альфа-канал (прозрачность)

            histogramR[r]++;
            histogramG[g]++;
            histogramB[b]++;
        }

        return {
            r: histogramR,
            g: histogramG,
            b: histogramB
        };

    } catch (e) {
        console.error("Ошибка при обработке пиксельных данных:", e);
        return null;
    }
}

/**
 * Рисует гистограмму на заданном canvas-элементе с помощью Chart.js.
 * @param {string} canvasId - ID элемента <canvas> (например, 'chartOriginal').
 * @param {object} histogramData - Данные { r: [], g: [], b: [] }
 */
function drawHistogram(canvasId, histogramData) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    // Создаем массив меток от 0 до 255 для оси X
    const labels = Array.from({ length: 256 }, (_, i) => i);

    // Удаляем старый график, если он существует
    if (window[canvasId] instanceof Chart) {
        window[canvasId].destroy();
    }

    // Создаем новый график
    window[canvasId] = new Chart(ctx, {
        type: 'line', // Линейный график лучше подходит для гистограмм в Chart.js
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'R',
                    data: histogramData.r,
                    borderColor: 'rgba(255, 0, 0, 0.7)',
                    backgroundColor: 'rgba(255, 0, 0, 0.1)',
                    borderWidth: 1,
                    pointRadius: 0, // Убираем точки
                },
                {
                    label: 'G',
                    data: histogramData.g,
                    borderColor: 'rgba(0, 128, 0, 0.7)',
                    backgroundColor: 'rgba(0, 128, 0, 0.1)',
                    borderWidth: 1,
                    pointRadius: 0,
                },
                {
                    label: 'B',
                    data: histogramData.b,
                    borderColor: 'rgba(0, 0, 255, 0.7)',
                    backgroundColor: 'rgba(0, 0, 255, 0.1)',
                    borderWidth: 1,
                    pointRadius: 0,
                }
            ]
        },
        options: {
            maintainAspectRatio: false, // Позволяет контролировать размер графика через CSS
            scales: {
                x: {
                    type: 'linear', // Оси уровней насыщенности
                    title: { display: true, text: 'Уровень насыщенности (0-255)' }
                },
                y: {
                    title: { display: true, text: 'Частота (кол-во пикселей)' }
                }
            },
            plugins: {
                legend: { display: true }
            }
        }
    });
}

// Запускаем привязку после загрузки DOM
document.addEventListener('DOMContentLoaded', () => {
    setupFileInputBinding();
    setupBrightnessControl(); // Вызываем новую функцию
    setupContrastControl();
    setupGrayscaleControl()
    setupBlurControl()
});
