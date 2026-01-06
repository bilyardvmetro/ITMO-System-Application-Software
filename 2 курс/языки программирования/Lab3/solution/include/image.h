//
// Created by bilyardvmetro on 29.11.2024.
//


#ifndef IMAGE_H
#define IMAGE_H

#include  <stdint.h>


#pragma pack(push, 1)
struct pixel {
    uint8_t r, g, b;
};
#pragma pack(pop)


#pragma pack(push, 1)
struct image{
    uint64_t width, height;
    struct pixel* data;
};
#pragma pack(pop)

void free_image(struct image* img);

#endif
