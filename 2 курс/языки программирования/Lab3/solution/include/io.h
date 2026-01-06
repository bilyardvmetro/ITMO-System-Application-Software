//
// Created by bilyardvmetro on 29.11.2024.
//

#ifndef IO_H
#define IO_H

#include <stdio.h>

enum file_open_status open_file(const char* filename, FILE** file, const char* mode);
enum file_close_status close_file(FILE** file);

#endif
