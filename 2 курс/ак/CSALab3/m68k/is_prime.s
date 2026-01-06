    .data

input_addr:      .word  0x80
output_addr:     .word  0x84

    .text
    .org     0x88

f_is_a_divisor:
    ; D0 = k; D1 = n
    ; n = (n / k) * k ? k is a divisor : k not a divisor

    move.l D1, D2;
    div.l D0, D2  ; D2 = n/k
    mul.l D0, D2    ; D2 *= k
    cmp.l D1, D2

    beq ret_true  ; if n == (n/k)*k

    move.b 0, D4
    rts
ret_true:
    move.b 1, D4
    rts

f_is_prime:
    ; D1 = n
    ; return : [0, 1, -1] in D5

    cmp.l 0, D1 ; D1 - 0
    ble is_negative

    cmp.l 1, D1 ;
    beq not_prime


    move.l 2, D0 ; D0 = k = 2 init k

is_prime_while:
    move.l D0, D2
    mul.l D0, D2 ; D2 <- D2 * D0 <=> D2 = k^2
    cmp.l D2, D1  ; n - k^2
    blt is_prime

    jsr f_is_a_divisor
    cmp.b 1, D4
    beq not_prime

    add.l 1, D0
    jmp is_prime_while

is_prime:
    move.l 1, D5
    rts
is_negative:
    move.l -1, D5
    rts
not_prime:
    move.l 0, D5
    rts

_start:
    movea.l  0x500, A7

    movea.l  input_addr, A0                  ; move data from source to addr reg
    movea.l  (A0), A0
    movea.l  output_addr, A1
    movea.l  (A1), A1

    move.l (A0), D1 ; D1 = n

    jsr f_is_prime
    move.l D5, (A1)
    halt
