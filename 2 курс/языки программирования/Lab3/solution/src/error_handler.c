//
// Created by user on 30.11.2024.
//

#include "errors.h"
#include <stdint.h>
#include <stdio.h>


uint8_t check_write_errors(enum write_status status){
    switch (status) {
        case WRITE_ERROR:
            fprintf(stderr, "Ошибка записи\n");
            return EIO;

        case WRITE_HEADER_ERROR:
            fprintf(stderr, "Ошибка записи заголовка файла\n");
            return EIO;

        default:
            fprintf(stdout, "Файл успешно сохранен\n");
            return WRITE_OK;
    }
}

uint8_t check_read_errors(enum read_status status){
    switch (status) {
        case READ_SIGNATURE_ERROR:
            fprintf(stderr, "Ошибка чтения сигнатуры файла\n");
            return EIO;

        case READ_BIT_COUNT_ERROR:
            fprintf(stderr, "Утилита поддерживает только 24-битные bmp-файлы\n");
            return EINVAL;

        case READ_PIXELS_ERROR:
            fprintf(stderr, "Ошибка чтения пикселей\n");
            return EINVAL;

        case MEMORY_ALLOCATION_ERROR:
            fprintf(stderr, "Файл слишком большой\n");
            return ENOMEM;

        case READ_HEADER_ERROR:
            fprintf(stderr, "Ошибка чтения заголовка файла\n");
            return EINVAL;

        default:
            fprintf(stdout, "Файл успешно прочитан\n");
            return READ_OK;
    }
}

uint8_t check_file_open_errors(enum file_open_status status){
    switch (status) {
        case OPEN_ERROR:
            fprintf(stderr, "Файл не найден\n");
            return ENOENT;

        default:
            fprintf(stdout, "Файл успешно открыт\n");
            return OPEN_OK;
    }
}

uint8_t check_file_close_errors(enum file_close_status status){
    switch (status) {
        case CLOSE_WARN:
            fprintf(stderr, "Файл не был закрыт");
            return CLOSE_OK;

        case CLOSE_ERROR:
            fprintf(stderr, "Произошла ошибка при закрытии файла");
            return EIO;

        default:
            fprintf(stdout, "Файл успешно закрыт\n");
            return CLOSE_OK;
    }
}
