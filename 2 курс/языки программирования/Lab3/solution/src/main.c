#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "bmp.h"
#include "bmp_transformation.h"
#include "errors.h"
#include "image.h"
#include "io.h"

int main( int argc, char** argv ) {
    // проверка на верные аргументы
    const int EXPECTED_ARGS_COUNT = 4;

    if (argc != EXPECTED_ARGS_COUNT) {
        fprintf(stderr,"Неверные аргументы для утилиты.\n Введите аргументы в формате:"
                        " <исходная картинка> <преобразованная картинка> <преобразование>");
        exit(EPERM);
    }

    const char* src_fname = argv[1];
    const char* out_fname = argv[2];
    const char* transformation = argv[3];

    // открываем исходный файл с проверками
    FILE* src_file;
    enum file_open_status src_open_status = open_file(src_fname, &src_file, "rb");

    uint8_t src_fopen_status_code = check_file_open_errors(src_open_status);
    if (src_fopen_status_code != 0) exit(src_fopen_status_code);

    // читаем bmp картинку с проверками
    struct image src_img = {0};
    enum read_status src_bmp_read_status = from_bmp(src_file, &src_img);

    uint8_t src_bmp_read_status_code = check_read_errors(src_bmp_read_status);
    if (src_bmp_read_status_code != 0){
        free_image(&src_img);
        exit(src_bmp_read_status_code);
    }

    // закрываем исходный файл
    enum file_close_status src_close_status = close_file(&src_file);
    uint8_t src_close_status_code = check_file_close_errors(src_close_status);
    if (src_close_status_code != 0) {
        free_image(&src_img);
        exit(src_close_status_code);
    }

    // обработка картинки
    struct image res_img = {0};
    if (strcmp(transformation, "none") == 0) res_img = src_img;
    else if (strcmp(transformation, "cw90") == 0) res_img = rotate_90_right(src_img);
    else if (strcmp(transformation, "ccw90") == 0) res_img = rotate_90_left(src_img);
    else if (strcmp(transformation, "fliph") == 0) res_img = flip_horizontal(src_img);
    else if (strcmp(transformation, "flipv") == 0) res_img = flip_vertical(src_img);
    else {
        fprintf(stderr, "Неизвестное преобразование");
        free_image(&src_img);
        free_image(&res_img);
        exit(EINVAL);
    }

    // открываем конечный файл
    FILE* out_file;
    enum file_open_status out_open_status = open_file(out_fname, &out_file, "wb");

    uint8_t out_fopen_status_code = check_file_open_errors(out_open_status);
    if (out_fopen_status_code != 0) {
        free_image(&src_img);
        free_image(&res_img);
        exit(out_fopen_status_code);
    }

    // сохраняем преобразованную картинку в формат bmp с проверками
    enum write_status out_bmp_write_status = to_bmp(out_file, &res_img);

    uint8_t out_bmp_write_status_code = check_write_errors(out_bmp_write_status);
    if (out_bmp_write_status_code != 0){
        free_image(&src_img);
        free_image(&res_img);
        exit(out_bmp_write_status_code);
    }

    // закрываем конечный файл
    enum file_close_status out_close_status = close_file(&out_file);
    uint8_t out_close_status_code = check_file_close_errors(out_close_status);
    if (out_close_status_code != 0) {
        free_image(&src_img);
        free_image(&res_img);
        exit(out_close_status_code);
    }

    free_image(&src_img);
    if (strcmp(transformation, "none") != 0) free_image(&res_img);
    return ALL_OK;
}
