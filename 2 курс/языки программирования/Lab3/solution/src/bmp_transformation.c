//
// Created by bilyardvmetro on 30.11.2024.
//

#include "bmp_transformation.h"
#include <errno.h>
#include <stdlib.h>


struct image rotate_90_right(struct image const source){
    struct image res_img = {.width=source.height, .height=source.width};
    res_img.data = malloc(sizeof(struct pixel) * res_img.height * res_img.width);

    if (!res_img.data) exit(ENOMEM);

    for (size_t i = 0; i < source.height; ++i){
        for (size_t j = 0; j < source.width; ++j){
            res_img.data[res_img.width * j + i] = source.data[source.width * i + (source.width - j - 1)];
        }
    }
    return res_img;
}

struct image rotate_90_left(struct image const source){
    struct image res_img = {.width=source.height, .height=source.width};
    res_img.data = malloc(sizeof(struct pixel) * res_img.height * res_img.width);

    if (!res_img.data) exit(ENOMEM);

    for (size_t i = 0; i < source.height; ++i) {
        for (size_t j = 0; j < source.width; ++j) {
            res_img.data[res_img.width * j + (res_img.width - i - 1)] = source.data[source.width * i + j];
        }
    }
    return res_img;
}

struct image flip_horizontal(struct image const source){
    struct image res_img = {.width=source.width, .height=source.height};
    res_img.data = malloc(sizeof(struct pixel) * source.height * source.width);

    if (!res_img.data) exit(ENOMEM);

    for (size_t i = 0; i < res_img.height; ++i) { // флипаем до центра
        for (size_t j = 0; j < (res_img.width + 1) / 2; ++j) {
            res_img.data[res_img.width * i + j] = source.data[source.width * i + (source.width - j - 1)];
            res_img.data[res_img.width * i + (res_img.width - j - 1)] = source.data[source.width * i + j];
        }
    }
    return res_img;
}

struct image flip_vertical(struct image const source){
    struct image res_img = {.width=source.width, .height=source.height};
    res_img.data = malloc(sizeof(struct pixel) * source.height * source.width);

    if (!res_img.data) exit(ENOMEM);

    for (size_t i = 0; i < (res_img.height + 1) / 2; ++i) { // флипаем до центра
        for (size_t j = 0; j < res_img.width; ++j) {
            res_img.data[res_img.width * i + j] = source.data[(source.height - i - 1) * source.width + j];
            res_img.data[(res_img.height - i - 1) * res_img.width + j] = source.data[source.width * i + j];
        }
    }
    return res_img;
}

struct image do_nothing(struct image const source){
    return source;
}
