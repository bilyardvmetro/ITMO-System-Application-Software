//
// Created by bilyardvmetro on 30.11.2024.
//

#include "bmp.h"
#include "image.h"
#include <stdlib.h>


enum read_status from_bmp(FILE* file, struct image* img){
    struct bmp_header header = {0};

    if (fread(&header, sizeof(struct bmp_header), 1, file) != 1) return READ_HEADER_ERROR;

    if (header.bfType != BMP_FILE_TYPE) return READ_SIGNATURE_ERROR;
    if (header.biBitCount != BMP_BITS) return READ_BIT_COUNT_ERROR;
    if (header.biHeight < 0  || header.biWidth < 0) return READ_HEADER_ERROR;

    img->width = header.biWidth;
    img->height = header.biHeight;

    img->data = malloc(sizeof(struct pixel) * img->height * img->width);

    if (!img->data) return MEMORY_ALLOCATION_ERROR;

    if (fseek(file, (long) header.bfOffBits, SEEK_SET)){
        free_image(img);
        return READ_PIXELS_ERROR;
    }

    const uint8_t padding = calculate_padding(img->width);

    for (size_t i = 0; i < img->height; i++){
        if (fread(&img->data[i * img->width], sizeof(struct pixel),img->width, file) != img->width){
            free_image(img);
            return READ_PIXELS_ERROR;
        }

        if (padding > 0)
            if(fseek(file, padding, SEEK_CUR)){
                free_image(img);
                return READ_PIXELS_ERROR;
            }
    }
    return READ_OK;
}

enum write_status to_bmp(FILE* file, struct image const* img){
    struct bmp_header header;
    const uint8_t padding = calculate_padding(img->width);
    const size_t img_size = sizeof(struct pixel) * img->width * img->height + padding;

    header.bfType = BMP_FILE_TYPE;
    header.bfileSize = sizeof(struct bmp_header) + img_size; // размер картинки с хэдэром
    header.bfReserved = BMP_RESERVE;
    header.bfOffBits = sizeof(struct bmp_header); // сдвиг на хэдэр относительно начала файла
    header.biSize = BMP_SIZE;
    header.biWidth = img->width;
    header.biHeight = img->height;
    header.biPlanes = BMP_LAYERS;
    header.biBitCount = BMP_BITS;
    header.biCompression = BMP_COMPRESSION;
    header.biSizeImage = img_size; // размер картинки без хэдера
    header.biXPelsPerMeter = BMP_X_PELS_PER_METER;
    header.biYPelsPerMeter = BMP_Y_PELS_PER_METER;
    header.biClrUsed = BMP_CLR_USED;
    header.biClrImportant = BMP_CLR_IMPORTANT;

    if (fwrite(&header, sizeof(struct bmp_header), 1, file) != 1) return WRITE_HEADER_ERROR;

    uint8_t padding_bytes[3] = {0, 0, 0};

    for (size_t i = 0; i < img->height; i++){
        if (fwrite(&img->data[i * img->width], sizeof(struct pixel),img->width, file) != img->width) return WRITE_ERROR;

        if (padding > 0)
            if (fwrite(padding_bytes, 1, padding, file) != padding) return WRITE_ERROR;
    }
    return WRITE_OK;
}

uint8_t calculate_padding(uint64_t width){
    return 4 - (sizeof(struct pixel) * width) % 4;
}

void free_image(struct image* img){
    if (img) {
        if (img->data) {
            free(img->data);
            img->data = NULL;
        }
    }
}
