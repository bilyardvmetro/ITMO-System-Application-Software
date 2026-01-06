#define _DEFAULT_SOURCE

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mem_internals.h"
#include "mem.h"
#include "util.h"

void debug_block(struct block_header *b, const char *fmt, ...);

void debug(const char *fmt, ...);

extern inline block_size size_from_capacity(block_capacity cap);

extern inline block_capacity capacity_from_size(block_size sz);

// на вход размер который хотим аллоцировать и хэдэр блока. Если вместимость блока >= запрошенного размера true, иначе false
static bool block_is_big_enough(size_t query, struct block_header *block) { return block->capacity.bytes >= query; }

static size_t pages_count(size_t mem) { return mem / getpagesize() + ((mem % getpagesize()) > 0); }

static size_t round_pages(size_t mem) { return getpagesize() * pages_count(mem); }

// инициализируем новый свободный блок
static void block_init(void *restrict addr, block_size block_sz, void *restrict next) {
    *((struct block_header *) addr) = (struct block_header) {
            .next = next,
            .capacity = capacity_from_size(block_sz),
            .is_free = true
    };
}

static size_t region_actual_size(size_t query) { return size_max(round_pages(query), REGION_MIN_SIZE); }

// если указатель на регион = null
extern inline bool region_is_invalid(const struct region *r);


static void *map_pages(void const *addr, size_t length, int additional_flags) {
    return mmap((void *) addr, length, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | additional_flags, -1, 0);
}

/*  аллоцировать регион памяти и инициализировать его блоком */
static struct region alloc_region(void const *addr, size_t query) {
    // TODO: alloc_region
    size_t actual_block_size = size_from_capacity((block_capacity) {query}).bytes;
    size_t actual_region_size = region_actual_size(actual_block_size);

    void* page_ptr = map_pages(addr, actual_region_size, MAP_FIXED_NOREPLACE);
    if (page_ptr == MAP_FAILED) page_ptr = map_pages(addr, actual_region_size, 0);
    if (page_ptr == MAP_FAILED) return REGION_INVALID;

    block_init(page_ptr, (block_size) {actual_region_size}, NULL);
    struct region new_region = {page_ptr, actual_region_size, page_ptr == addr};

    return new_region;
}

static void *block_after(struct block_header const *block);

void *heap_init(size_t initial) {
    const struct region region = alloc_region(HEAP_START, initial);
    if (region_is_invalid(&region)) return NULL;

    return region.addr;
}

static bool blocks_continuous(
        struct block_header const *fst,
        struct block_header const *snd) {
    return (void *) snd == block_after(fst);
}

/*  освободить всю память, выделенную под кучу */
void heap_term() {
    // TODO: heap_term
    struct block_header* curr_block = (struct block_header*) HEAP_START;

    struct block_header* start_block = curr_block;
    size_t mem_to_unmap = 0;

    while (curr_block) {
        struct block_header* next_block = curr_block->next;

        mem_to_unmap += size_from_capacity(curr_block->capacity).bytes;

        if (!blocks_continuous(curr_block, next_block)) {
            munmap(start_block, mem_to_unmap);
            start_block = next_block;
            mem_to_unmap = 0;
        }

        curr_block = next_block;
    }
}

#define BLOCK_MIN_CAPACITY 24

/*  --- Разделение блоков (если найденный свободный блок слишком большой )--- */

static bool block_splittable(struct block_header *restrict block, size_t query) {
    return block->is_free &&
           query + offsetof(struct block_header, contents) + BLOCK_MIN_CAPACITY <= block->capacity.bytes;
}

static bool split_if_too_big(struct block_header *block, size_t query) {
    // TODO: split_if_too_big
    size_t remainer = block->capacity.bytes - query - offsetof(struct block_header, contents);
    if (!block_splittable(block, query) || remainer < BLOCK_MIN_CAPACITY) return false;

    struct block_header* splitted_block = (struct block_header* ) (block->contents + query);

    block_init(splitted_block, size_from_capacity((block_capacity) {remainer}), block->next);

    block->capacity.bytes = query;
    block->next = splitted_block;

    return true;
}


/*  --- Слияние соседних свободных блоков --- */

static void *block_after(struct block_header const *block) {
    return (void *) (block->contents + block->capacity.bytes);
}

static bool mergeable(struct block_header const *restrict fst, struct block_header const *restrict snd) {
    return fst->is_free && snd->is_free && blocks_continuous(fst, snd);
}

static bool try_merge_with_next(struct block_header *block) {
    // TODO: try_merge_with_next
    if (!block || !block->next || !mergeable(block, block->next)) return false;

    block->capacity.bytes += size_from_capacity(block->next->capacity).bytes;
    block->next = block->next->next;

    return true;
}


/*  --- ... ecли размера кучи хватает --- */

struct block_search_result {
    enum {
        BSR_FOUND_GOOD_BLOCK, BSR_REACHED_END_NOT_FOUND, BSR_CORRUPTED
    } type;
    struct block_header *block;
};


static struct block_search_result find_good_or_last(struct block_header *restrict block, size_t sz) {
    // TODO: find_good_or_last
    struct block_header* last = NULL;
    bool allow_dirty = false;

    while (block) {

        if (block->capacity.bytes < BLOCK_MIN_CAPACITY) return (struct block_search_result) {.type = BSR_CORRUPTED, .block = block};

        while (try_merge_with_next(block));

        if (block_is_big_enough(sz, block) && (block->is_free || allow_dirty)) return (struct block_search_result) {.type = BSR_FOUND_GOOD_BLOCK, .block = block};

        if (block->is_free && block->capacity.bytes < sz) allow_dirty = true;

        last = block;
        block = block->next;
    }

    return (struct block_search_result){.type = BSR_REACHED_END_NOT_FOUND, .block = last};
}

/*  Попробовать выделить память в куче начиная с блока `block` не пытаясь расширить кучу
 Можно переиспользовать как только кучу расширили. */
static struct block_search_result try_memalloc_existing(size_t query, struct block_header *block) {
    // TODO: try_memalloc_existing

    struct block_search_result result = find_good_or_last(block, query);

    if (result.type == BSR_FOUND_GOOD_BLOCK) {
        if (split_if_too_big(result.block, query)) {
            result.block->is_free = false;
        }
    }

    return result;
}


static struct block_header *grow_heap(struct block_header *restrict last, size_t query) {
    // TODO: grow_heap
    if (!last) return NULL;

    struct region region = alloc_region(block_after(last), query);
    if (region_is_invalid(&region)) return NULL;

    last->next = region.addr;

    if (try_merge_with_next(last)) return last;
    else return last->next;
}

/*  Реализует основную логику malloc и возвращает заголовок выделенного блока */
static struct block_header *memalloc(size_t query, struct block_header *heap_start) {
    // TODO: memalloc
    query = size_max(BLOCK_MIN_CAPACITY, query);

    struct block_search_result bsr = try_memalloc_existing(query, heap_start);

    if (bsr.type == BSR_FOUND_GOOD_BLOCK) return bsr.block;

    if (bsr.type == BSR_REACHED_END_NOT_FOUND) {
        struct block_header *new_block = grow_heap(bsr.block, query);

        if (new_block) return try_memalloc_existing(query, new_block).block;
    }

    return NULL;
}

size_t aligned_query(size_t query) {
    if (!(query % 8)) return query;
    return query + (8 - query % 8);
}

void *_malloc(size_t query) {
    query = aligned_query(query);
    struct block_header *const addr = memalloc(query, (struct block_header *) HEAP_START);
    if (addr) return addr->contents;
    return NULL;
}

static struct block_header *block_get_header(void *contents) {
    return (struct block_header *) (((uint8_t *) contents) - offsetof(struct block_header, contents));
}

void _free(void *mem) {
    if (!mem) return;

    struct block_header *header = block_get_header(mem);
    if (!header) return;
    header->is_free = true;
    // TODO: free

    while (try_merge_with_next(header));
}
