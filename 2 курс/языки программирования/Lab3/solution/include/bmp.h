//
// Created by bilyardvmetro on 29.11.2024.
//

#ifndef BMP_H
#define BMP_H

#include "errors.h"
#include "image.h"
#include <stdint.h>
#include <stdio.h>

#pragma pack(push, 1)

struct bmp_header {
    uint16_t bfType;
    uint32_t bfileSize;
    uint32_t bfReserved;
    uint32_t bfOffBits;
    uint32_t biSize;
    uint32_t biWidth;
    uint32_t biHeight;
    uint16_t biPlanes;
    uint16_t biBitCount;
    uint32_t biCompression;
    uint32_t biSizeImage;
    uint32_t biXPelsPerMeter;
    uint32_t biYPelsPerMeter;
    uint32_t biClrUsed;
    uint32_t biClrImportant;
};

#pragma pack(pop)

#define BMP_FILE_TYPE 0x4D42
#define BMP_BITS 24
#define BMP_SIZE 40

#define BMP_LAYERS 1
#define BMP_RESERVE 0
#define BMP_COMPRESSION 0
#define BMP_X_PELS_PER_METER 0
#define BMP_Y_PELS_PER_METER 0
#define BMP_CLR_USED 0
#define BMP_CLR_IMPORTANT 0

enum read_status from_bmp(FILE* file, struct image* img);
enum write_status to_bmp(FILE* file, struct image const* img);
uint8_t calculate_padding(uint64_t width);

#endif
