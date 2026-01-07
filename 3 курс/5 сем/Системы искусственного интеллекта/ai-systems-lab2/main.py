import re
from pyswip import Prolog

RUSSIAN_ARTISTS = [
    "i61",
    "хаски",
    "boulevard depo"
]

# Исполнители, поющие на английском
ENGLISH_ARTISTS = [
    "kendrick lamar",
    "kanye west",
    "fred again..",
    "aphex twin",
    "miles davis",
    "chet baker"
]

def get_recommendations(user_input):
    """
    Обрабатывает ввод пользователя, запрашивает Prolog и выводит рекомендации.

    :param user_input: Входная строка пользователя (например, "Мне нравится музыка на русском, хип-хоп, джаз")
    :return: Словарь с рекомендациями, разделенными по языку и жанру.
    """
    try:
        # 1. Инициализация Prolog и загрузка базы знаний
        prolog = Prolog()
        # Убедитесь, что файл AI-Systems-Lab1.pl находится в той же папке
        prolog.consult("AI-Systems-Lab1.pl")
    except Exception as e:
        print(f"Ошибка при инициализации Prolog/pyswip: {e}")
        print("Убедитесь, что Prolog установлен и 'pyswip' корректно работает.")
        return {}

    # 2. Обработка пользовательского ввода
    # Ищем жанры и языки в произвольном порядке
    genres_map = {
        "хип-хоп": "hip-hop",
        "джаз": "jazz",
        "электронная": "electronic"
    }

    languages_map = {
        "русском": "russian",
        "английском": "english"
    }

    # Извлечение жанров
    requested_genres = []
    for rus_genre, prolog_genre in genres_map.items():
        if re.search(r'\b' + rus_genre + r'\b', user_input, re.IGNORECASE):
            requested_genres.append(prolog_genre)

    # Извлечение языков
    requested_languages = []
    for rus_lang, eng_lang in languages_map.items():
        if re.search(r'\b' + rus_lang + r'\b', user_input, re.IGNORECASE):
            requested_languages.append(eng_lang)

    print(f"-> Запрошенные жанры: {requested_genres}")
    print(f"-> Запрошенные языки: {requested_languages}")

    if not requested_genres:
        print("Не удалось определить жанры. Попробуйте ввести 'хип-хоп', 'джаз' или 'электронная'.")
        return {}

    final_recommendations = {
        'russian': {},
        'english': {}
    }

    # 3. Выполнение логических запросов к Prolog
    for genre in requested_genres:
        print(f"\n-> Поиск исполнителей и треков в жанре: {genre}")

        # Prolog-запрос: найти всех артистов (A) данного жанра
        query_artists = f'performingGenre(A, {genre}).'
        artists_results = list(prolog.query(query_artists))

        # Собираем данные
        genre_artists = {
            'russian': [],
            'english': []
        }

        for result in artists_results:
            artist_bytes = result['A']  # Имя артиста
            artist_name = artist_bytes.decode('utf-8')
            artist_name_lower = artist_name.lower()  # Для сравнения с языковыми списками

            # Определяем язык по нашим заготовкам
            lang = None
            if artist_name_lower in RUSSIAN_ARTISTS:
                lang = 'russian'
            elif artist_name_lower in ENGLISH_ARTISTS:
                lang = 'english'

            # 4. Сбор треков для найденного исполнителя
            if lang:
                artist_data = {'artist': artist_name, 'songs': []}

                # Prolog-запрос: найти все песни (S), исполняемые артистом (A)
                query_songs = f'performingSong("{artist_name}", S).'
                songs_results = list(prolog.query(query_songs))

                artist_data['songs'] = [s['S'] for s in songs_results]

                for i, song in enumerate(artist_data['songs']):
                    artist_data['songs'][i] = song.decode('utf-8')

                # Добавляем данные в соответствующий языковой список
                genre_artists[lang].append(artist_data)

        # 5. Формирование рекомендаций
        # Фильтруем по запрошенным языкам
        if 'russian' in requested_languages:
            final_recommendations['russian'][genre] = genre_artists['russian']

        if 'english' in requested_languages:
            final_recommendations['english'][genre] = genre_artists['english']

    return final_recommendations


def display_recommendations(recommendations):
    """Красивый вывод результатов."""
    if not recommendations:
        print("\nНет рекомендаций для отображения.")
        return

    print("\n" + "=" * 50)
    print("🤲 РЕКОМЕНДАЦИИ НА ОСНОВЕ ВАШИХ ПРЕДПОЧТЕНИЙ 🤲")
    print("=" * 50)

    for lang, genres_data in recommendations.items():
        if genres_data:
            print(f"\n--- Музыка на {lang.upper()} языке ---")

            # Проверяем, есть ли данные в этой языковой категории
            has_data = any(artists for artists in genres_data.values())

            if has_data:
                for genre, artists_list in genres_data.items():
                    if artists_list:
                        print(f"\n[Жанр: {genre.capitalize()}]")
                        for artist_info in artists_list:
                            artist = artist_info['artist']
                            songs = artist_info['songs']
                            print(f"  👀 Исполнитель: {artist}")
                            # Выводим до 3 треков для примера
                            # songs_display = songs[:3]
                            if songs:
                                print(f"  🎶 Рекомендуемые треки: {', '.join(songs)}")
                            else:
                                print(f"  (Нет треков в базе)")
            else:
                print("Не найдено исполнителей, соответствующих запросу.")

if __name__ == "__main__":
    # Пример диалога с пользователем
    print("Система рекомендаций: Какой тип музыки вы предпочитаете?")

    # Пример ввода по заданию
    user_input = input("Ваш ввод (например, 'Мне нравится музыка на русском и английском, хип-хоп, джаз, электронная'):\n> ")

    # 1. Спарсить строку, разбить на факты -> DONE (извлечение жанров и языков)
    # 2. Построить запрос, используя предикаты -> DONE (performingGenre и performingSong)
    # 3. Выдать рекомендации после небольшого диалога -> DONE (запрос ввода и вывод)

    recs = get_recommendations(user_input)
    display_recommendations(recs)
