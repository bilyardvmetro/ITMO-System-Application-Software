//
// Created by bilyardvmetro on 29.11.2024.
//

#ifndef BMP_TRANSFORMATION_H
#define BMP_TRANSFORMATION_H

#include "image.h"

struct image rotate_90_right(struct image const source);
struct image rotate_90_left(struct image const source);
struct image flip_horizontal(struct image const source);
struct image flip_vertical(struct image const source);
struct image do_nothing(struct image const source);

#endif
