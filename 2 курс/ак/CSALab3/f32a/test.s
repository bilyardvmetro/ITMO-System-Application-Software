    .data

input_addr:      .word  0x80
output_addr:     .word  0x84

    .text

    \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

_start:
    @p input_addr a! @       \ n:[]

    factorial

    @p output_addr a! !
    halt

    \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

multiply:
    lit 31 >r                \ for R = 31
multiply_do:
    +*                       \ mres-high:acc-old:n:[]; mres-low in a
    next multiply_do
    drop drop a              \ mres-low:n:[] => acc:n:[]
    ;

    \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

factorial:
    lit 1 over               \ n:acc:[]
factorial_while:
    dup                      \ n:n:acc:[]
    if factorial_finish      \ n:acc:[]

    dup a!                   \ n:acc:[]

    over                     \ acc:n:[]
    lit 0                    \ 0:acc:n:[]

    multiply

    over                     \ n:acc
    lit -1 +                 \ n-1:acc

    factorial_while ;

factorial_finish:
    drop                     \ n:acc:[]
    ;