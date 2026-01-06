//
// Created by bilyardvmetro on 30.11.2024.
//

#include "errors.h"
#include "io.h"


enum file_open_status open_file(const char* filename, FILE** file_ptr, const char* mode) {
    *file_ptr = fopen(filename, mode); // rb/wb для корректного чтения/записи символов перевода строки non-txt файлов

    if (*file_ptr) return OPEN_OK;
    return OPEN_ERROR;
}

enum file_close_status close_file(FILE** file_ptr){
    if (fclose(*file_ptr) == 0) return CLOSE_OK;
    return CLOSE_WARN;
}
