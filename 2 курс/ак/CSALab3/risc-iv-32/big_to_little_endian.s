.data
	input_addr: .word 0x80
	output_addr: .word 0x84
	byte_mask: .word 0x000000FF	; маска для сохранения младшего байта
	counter: .word 8			; счетчик для сдвига (в битах)
	cycle_counter: .word 4

.text
.org 136
; t0 - адрес input/output стрима
; a0 - маска для младшего байта
; a1 - счетчик для сдвига (в битах)
; t2 - исходное число
; t3 - нижний байт
; t4 - итоговое число
; t1 - счетчик цикла (логический сдвиг некорректно реализован)
_start:
	lui t0, %hi(input_addr)		; верхние 20 бит адреса метки
	addi t0, t0, %lo(input_addr); нижние 12 бит адреса метки
	lw t0, 0(t0)				; загрузили адрес input стрима с адреса метки
	
	;lui t1, %hi(cycle_counter)		
	;addi t1, t1, %lo(cycle_counter)
	;lw t1, 0(t1)				; загрузили счетчик
	
	lui a0, %hi(byte_mask)
	addi a0, a0, %lo(byte_mask)
	lw a0, 0(a0)				; загрузили маску
	
	lui a1, %hi(counter)
	addi a1, a1, %lo(counter)
	lw a1, 0(a1)				; загрузили счетчик
	
	lw t2, 0(t0)				; загрузили число
	
while:
	;beqz t1, exit
	 beqz t2, exit				; проверка, что байты числа "закончились"
	
	and t3, t2, a0				; сохраняем только младший байт
	srl t2, t2, a1				; сдвигаем исходное число на байт вправо
	
	sll t4, t4, a1				; сдвигаем итоговое число на байт влево
	add t4, t4, t3				; добавляем младший байт к итоговому числу
	
	addi t1, t1, -1
	j while						; цикл

exit:
	lui t0, %hi(output_addr)
	addi t0, t0, %lo(output_addr)
	lw t0, 0(t0)				; загрузили адрес output стрима
	
	sw t4, 0(t0)				; записали в него итоговое число
	
	halt