//
// Created by bilyardvmetro on 14.12.2024.
//

#include <stdbool.h>
#include <stdio.h>

#ifndef TESTS_H
#define TESTS_H
    bool malloc_test();
    bool free_one_block_test();
    bool free_two_blocks_test();
    bool extend_existing_region_test();
    bool allocate_new_region_test();
    void run_tests();
#endif
