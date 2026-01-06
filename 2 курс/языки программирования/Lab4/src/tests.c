//
// Created by bilyardvmetro on 13.12.2024.
//

#include "tests.h"
#include "mem.h"

bool malloc_test(){
    printf("Successful memory allocation testing...\n");
    void* block = _malloc(1024);

    if (block) printf("TEST 1: SUCCESS\nblock allocated at %p\n", block);
    else printf("TEST 1: FAIL\nblock cannot be allocated :(\n");

    _free(block);
    printf("==========================================================");
    return true;
}

bool free_one_block_test(){
    printf("Free one allocated block testing...\n");
    void* block1 = _malloc(512);
    void* block2 = _malloc(256);

    if (block1 && block2) printf("Blocks allocated:\n1st block allocated on %p\n"
                                 "2st block allocated on %p\n", block1, block2);
    else{
        printf("TEST 2: FAILED\nError occurred while allocating blocks\n");
        return false;
    }
    _free(block2);
    printf("TEST 2: SUCCESS\n2nd block freed on address %p\n", block2);
    printf("----------------------------------------------------------");
    _free(block1);
    printf("1st block freed on address %p\n", block1);
    printf("==========================================================");
    return true;
}

bool free_two_blocks_test(){
    printf("Free two allocated blocks testing...\n");
    void* block1 = _malloc(512);
    void* block2 = _malloc(256);
    void* block3 = _malloc(128);

    if (block1 && block2 && block3) printf("Blocks allocated:\n1st block allocated on %p\n"
                                           "2st block allocated on %p\n"
                                           "3rd block allocated on %p\n", block1, block2, block3);
    else{
        printf("TEST 3: FAILED\nError occurred while allocating blocks\n");
        return false;
    }
    _free(block1);
    _free(block2);
    printf("TEST 3: SUCCESS\n1st block freed on address %p\n2nd block freed on address %p\n", block1, block2);
    printf("----------------------------------------------------------");
    _free(block3);
    printf("3rd block freed on address %p\n", block3);
    printf("==========================================================");
    return true;
}

bool extend_existing_region_test(){
    printf("Extend existing region testing...\n");
    void* block1 = _malloc(1024 * 1024);
    void* block2 = _malloc(1024 * 1024);

    if (block1 && block2) printf("Blocks allocated:\n1st block allocated on %p\n"
                                 "2st block allocated on %p\n", block1, block2);
    else{
        printf("TEST 4: FAILED\nError occurred while allocating blocks\n");
        return false;
    }
    _free(block2);
    _free(block1);
    printf("TEST 4: SUCCESS\n1st block freed on address %p\n"
           "2nd block freed on address %p\n", block1, block2);
    printf("==========================================================");
    return true;
}

bool allocate_new_region_test(){
    printf("Extend existing region testing...\n");
    void* block1 = _malloc(1024 * 1024);
    void* block2 = _malloc(1024 * 1024);
    void* block3 = _malloc(1024 * 1024);

    if (block1 && block2) printf("Blocks allocated:\n1st block allocated on %p\n"
                                 "2st block allocated on %p\n"
                                 "3rd block allocated on %p\n", block1, block2, block3);
    else{
        printf("TEST 5: FAILED\nError occurred while allocating blocks\n");
        return false;
    }
    _free(block2);
    _free(block1);
    _free(block3);
    printf("TEST 5: SUCCESS\n1st block freed on address %p\n"
           "2nd block freed on address %p\n"
           "3rd block freed on address %p\n", block1, block2, block3);
    printf("==========================================================");
    return true;
}

void execute_test(bool (*test) (void), uint8_t* counter, uint8_t* successes) {
    (*counter)++;
    if (test()) (*successes)++;
}

void run_tests(){
    uint8_t tests_ran = 0;
    uint8_t  tests_success = 0;

    heap_init(1024*1024);

    execute_test(malloc_test, &tests_ran, &tests_success);
    execute_test(free_one_block_test, &tests_ran, &tests_success);
    execute_test(free_two_blocks_test, &tests_ran, &tests_success);
    execute_test(extend_existing_region_test, &tests_ran, &tests_success);
    execute_test(allocate_new_region_test, &tests_ran, &tests_success);

    heap_term();
    printf("All tests ran. Succeed %d out of %d test", tests_success, tests_ran);
}
