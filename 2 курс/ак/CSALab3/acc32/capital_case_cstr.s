.data
	buf: .byte  '________________________________________'
	input_addr: .word 0x80
	output_addr: .word 0x84
	buf_ptr: .word  0x00
	
	current_sym: .word '\0'
	caps_next: .word 0
	
	inital_const: .byte '\0___'
	A: .word  'A'
	Z: .word 'Z'
	a: .word 'a'
	z: .word  'z'
	
	in: .word 0 ; сейвим input и output стримы
	out: .word 0 ; чтобы на них ничего не накладывалось
	
	caps_const: .word 32
	one: .word 1
	line_feed: .word '\n'
	space: .word ' '
	stdout_mask: .word 0x000000FF
	buf_size: .word 32
	buf_err_const: .word 0xCCCCCCCC
	
.text
.org 137
_start:
	load_addr one ; для первого символа ставим флаг, чтобы пройти капс чек
	store_addr caps_next
	
	load_ind input_addr ; читаем символ из stdin
	store_addr current_sym ; сохраняем символ для проверок
	
	jmp checks
	
read_sym:
	load_addr buf_ptr ; проверка на переполнение буфера
	xor buf_size
	beqz BOF
	
	load_ind input_addr ; читаем символ из stdin
	store_addr current_sym ; сохраняем символ для проверок
	
checks:
	xor line_feed ; проверка на \n
	beqz stop
	
	load_addr current_sym ; если текущий символ ' ', выставляем капс флаг и скипаем его
	xor space
	beqz set_caps_flag
	
	load_addr caps_next ; проверка капс флага (Текущий символ является первым в слове)
	xor one
	beqz caps_check
	
	load_addr current_sym ; если символ < 'A', скип
	sub A
	ble skip_sym
	
	load_addr current_sym ; если символ > 'Z', скип
	sub Z
	bgt skip_sym
	
	jmp switch_case ; если символ капс и он не первая буква слова, его нужно анкапснуть
	
caps_check:
	load_addr caps_next ; обнуляем капс флаг
	sub one
	store_addr caps_next
	
	load_addr current_sym ; если символ < 'a', скип
	sub a
	ble skip_sym
	
	load_addr current_sym ; если символ > 'z', скип
	sub z
	bgt skip_sym

; делаем символ капсовым, если он lowercase
switch_case:
	load_addr current_sym
	xor caps_const
	store_addr current_sym
	
	jmp skip_sym
	
set_caps_flag:
	load_addr one ; ставим капс флаг
	store_addr caps_next
	
skip_sym:
	load_addr current_sym
	add inital_const ; добавляем '_'
	store_ind buf_ptr ; закидываем в буфер
	
	load_addr buf_ptr ; увеличили поинтер на буфер
	add one
	store_addr buf_ptr
	
	bnez read_sym ; цикл
	
stop:
	add inital_const
	store_ind buf_ptr ; записали нуль-терминатор
	
	load_addr buf_ptr ; обнуляем поинтер на буфер
	xor buf_ptr
	store_addr buf_ptr
	
loop:
	load_ind buf_ptr
	and stdout_mask ; добавляем к символу хвост из нулей
	beqz exit ; проверка на нуль-терминатор
	store_ind output_addr ; выводим символ в stdout
	
	load_addr buf_ptr
	add one
	store_addr buf_ptr
	
	jmp loop
	
BOF:
	load_addr buf_err_const
	store_ind output_addr
	
exit:
	halt