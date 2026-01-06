//
// Created by bilyardvmetro on 29.11.2024.
//
#ifndef ERRORS_H
#define ERRORS_H

#include <errno.h>
#include <stdint.h>


enum write_status {
    WRITE_OK = 0,
    WRITE_ERROR,
    WRITE_HEADER_ERROR
};

enum read_status {
    READ_OK = 0,
    READ_SIGNATURE_ERROR,
    READ_PIXELS_ERROR,
    READ_HEADER_ERROR,
    MEMORY_ALLOCATION_ERROR,
    READ_BIT_COUNT_ERROR
};

enum file_open_status {
    OPEN_OK = 0,
    OPEN_ERROR
};

enum file_close_status {
    CLOSE_OK = 0,
    CLOSE_WARN,
    CLOSE_ERROR
};


#define ALL_OK 0

uint8_t check_write_errors(enum write_status status);
uint8_t check_read_errors(enum read_status status);
uint8_t check_file_open_errors(enum file_open_status status);
uint8_t check_file_close_errors(enum file_close_status status);

#endif
