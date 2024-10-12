section .data
%define EXIT_SYSCALL 60
%define STD_OUT 1
%define STD_IN  0
%define WRITE_SYSCALL 1
%define READ_SYSCALL 0

section .text
; Принимает код возврата и завершает текущий процесс
exit: 
    mov rax, EXIT_SYSCALL
				; в rdi уже лежит код возврата
    syscall




; Принимает указатель на нуль-терминированную строку, возвращает её длину
; rdi - адрес первого символа строки, rax - длина строки на выходе
string_length: ; 8
	xor rax, rax ; очистка rax от мусора.
				 ; после выполнения в нем лежит длина строки
	.loop:
		cmp byte[rdi + rax], 0
		je .return
		
		inc rax
		
		jmp .loop
	
	.return:
		ret




; Принимает указатель на нуль-терминированную строку, выводит её в stdout
; rdi - адрес первого символа строки
print_string: ; 8
	
	push rdi ; 16
	call string_length
	pop rsi 	; адрес начала строки теперь в rsi
				; теперь в rax лежит длина строки
	
	mov rdx, rax ; передача длины строки для последующего сискола
	mov rax, WRITE_SYSCALL   ; номер сискола
	mov rdi, STD_OUT   ; дескриптор stdout
	
	syscall
	
	ret



; Переводит строку (выводит символ с кодом 0xA)
print_newline:
    mov rdi, `\n`




; Принимает код символа и выводит его в stdout
; rdi - код символа в ASCII
print_char:
	push di 		; кладем код символа на вершину стека по адресу

	mov rsi, rsp 	; кладем адрес символа для сискола
	mov rdx, 1
    mov rax, WRITE_SYSCALL
	mov rdi, STD_OUT

	syscall

	pop di

    ret



; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
; rdi - число для вывода
print_uint: ; 8
	mov rax, rdi 	; число в регистр rax, тк его будем делить
	mov r8, 10		; делитель, чтобы найти цифры числа
	
	push 0 			; нуль-терминатор для строки | 16
	mov rdi, rsp 	; сохраняем старый rsp 
	sub rsp, 32		; аллоцируем место на стеке | 48
	
	
    .div_loop:
		xor rdx, rdx ; очистить остаток от предыдущего деления
		div r8
		add dl, '0'	; преобразуем число в его ascii код
		
		dec rdi
		mov [rdi], dl	; сохраняем цифру на стек
		
		test rax, rax		; делим, пока в rax не станет равен 0
		je .print_num
		
		jmp .div_loop
	
	.print_num:
		call print_string
		
					; приводим стек в порядок
		add rsp, 32 ; 16
		pop rax 	; 8
		
    ret




; Выводит знаковое 8-байтовое число в десятичном формате 
print_int: ; 8
    mov rax, rdi
	
	cmp rax, 0		; если число отрицательное, печатаем минус перед ним
	jl .print_minus
	
	jmp print_uint
	
	.print_minus:
		mov rdi, '-'
		push rax	; 16
					; выравниваем стек, 
					; одновременно сохраняя caller-saved регистр
		call print_char
		pop rax		; 8
		
		neg rax		; "убираем" минус
		mov rdi, rax ; кладем число в регистр для аргумента
		
		jmp print_uint





; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
; rdi - адрес 1-й строки, rsi - адрес 2-й строки
string_equals: ; 8
	xor rcx, rcx 	; обнуляем счетчик
	.loop:
		mov dl, byte[rdi+rcx] ; сравнение символа
		cmp dl, byte[rsi+rcx]
		jnz .false
		
		test dl, dl 	; если обе строки закончились на нуль-терминаторе, то они равны
		je .true
		
		inc rcx
		jmp .loop
	
	.false:
		xor rax, rax
		ret
		
	.true:
		mov rax, 1
		ret




; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char: ; 8
	xor rax, rax	; обнуляем rax перед сохранением символа
	push rax		; увеличиваем стек, для последющей записи символа
    mov rax, READ_SYSCALL		; аргументы для read сискола
	mov rdi, STD_IN
	mov rsi, rsp	; адрес буфера, куда запишем символ
	mov rdx, 1
	syscall
	
	pop rax			; забираем со стека прочитанный символ
	
	ret 




; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор

; rdi - начало буфера, rsi - размер буфера
; rax - адрес слова, rdx - длина слова
read_word: ; 8
	xor rax, rax
				; используем caller-saved регистры, тк они не будут затираться тестами
				; следовательно меньше работы со стеком внутри функции перед вызовом read_char
	push r12
	push r13
	push r14
	mov r12, rdi
	mov r13, rsi
	xor r14, r14	; обнуляем счетчик
	
	.skip_sym:				; скипаем все пробельные символы в начале слова
		call read_char		; считываем символ
		
		cmp rax, `\n`		; проверка на перевод строки
		je .skip_sym
		
		cmp rax, ` `		; проверка на перевод пробел
		je .skip_sym
		
		cmp rax, `\t`		; проверка на перевод табуляцию
		je .skip_sym
		
		test rax, rax		; проверка на нуль-терминатор.
		jz .fail
		
	.loop:			
		cmp r14, r13		; проверка на выход за пределы буфера
		jge .fail
		
		mov [r12 + r14], rax		; закидываем символ в буфер
		
		test rax, rax 		; проверка на нуль-терминатор. Если он есть, то мы уже дописали его к строке
							; при помощи инструкции выше
		je .success
		
		inc r14				; увеличиваем длину строки
	
		call read_char		; считываем символ
		
		cmp rax, `\n`		; перевод строки = остановка проги. сюрпирз из тз))))
		je .success
		
		cmp rax, ` `		; пробел = остановка проги. сюрпирз из тз))))
		je .success
		
		cmp rax, `\t`		; табуляция  = остановка проги. сюрпирз из тз))))
		je .success
		
		jmp .loop
	
	.success:
		mov rax, r12			; адрес буфера в rax
		mov rdx, r14			; длину в rdx
		jmp .return
	
	.fail:
		xor rax, rax	; 0 в rax и rdx
		xor rdx, rdx
		
	.return:
		pop r14
		pop r13
		pop r12
		
		ret




; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, 
; в rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
parse_uint:
    mov r10, 10		; множитель, чтобы сдвигать разряды вправо
					; для записи новых цифр числа

    xor rax, rax
	xor rcx, rcx
	xor r11, r11
	
	.loop:
		mov cl, [rdi]	; читаем байт по адресу из rdi
		
		cmp cl, 0	; проверка на нуль-терминатор
		je .return
		
		cmp cl, '0'	; если символ "выше" 0, то это не цифра
		jb .return

		cmp cl, '9'	; если символ "ниже" 9, то это не цифра
		ja .return

		mul r10			; сдвигаем число вправо на один разряд
		add rax, rcx		; записываем цифру в новый разряд
		sub rax, '0'	; преобразуем число из ASCII кода

		inc rdi		; увеличиваем указатель на адрес строки
		inc r11		; прибавляем длину числа
		jmp .loop

	.return:
		mov rdx, r11
		ret




; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
parse_int: ; 8
    xor rax, rax
	xor rcx, rcx
	xor r11, r11
	
	mov cl, [rdi]	; читаем байт по адресу из rdi
		
	cmp cl, '-'		; проверка на знак
	je .parse_minus
	
	cmp cl, '+'		; проверка на знак
	je .parse_plus
	
	cmp cl, ' '	; проверка на пробел
	je .return
	
	cmp cl, '0'	; если символ "выше" 0, то это не цифра
	jb .return

	cmp cl, '9'	; если символ "ниже" 9, то это не цифра
	ja .return
	
	jmp parse_uint
	
	.parse_plus:
		cmp byte[rdi+1], ' '	; проверка на пробел
		je .return
		
		cmp byte[rdi+1], 0	; проверка на нуль-терминатор
		je .return
		
		cmp byte[rdi+1], '0'	; если символ "выше" 0, то это не цифра
		jb .return

		cmp byte[rdi+1], '9'	; если символ "ниже" 9, то это не цифра
		ja .return
		
		inc rdi		; знак уже распарсили
		
		jmp parse_uint
	
	.parse_minus:
		cmp byte[rdi+1], ' '	; проверка на пробел
		je .return
		
		cmp byte[rdi+1], 0	; проверка на нуль-терминатор
		je .return
		
		cmp byte[rdi+1], '0'	; если символ "выше" 0, то это не цифра
		jb .return

		cmp byte[rdi+1], '9'	; если символ "ниже" 9, то это не цифра
		ja .return
		
		inc rdi		; минус уже распарсили
		
		push rcx ; 16
		call parse_uint
		inc rdx			; минус входит в длину числа
		pop rcx ; 8
		
		neg rax			; в rax лежит модуль нашего числа, поэтому делаем его отрицательным
		
	.return:
		ret 




; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
; rdi - адрес строки, rsi - адрес буфера, rdx - длина строки
string_copy:
	xor rcx, rcx
	
	.loop:
		cmp rcx, rdx		; если мы дошли до конца строки
							; и не встретили нуль-терминатор, то строка больше буфера
		jge .str_overflow
		
		mov al, byte[rdi+rcx]	; копируем символ
		mov byte[rsi+rcx], al
		
		test al, al			; если дошли до нуль-терминатора, то брейкаемся из цикла
		je .return
		
		inc rcx
		jmp .loop
		
	.str_overflow:
		xor rax, rax
		
    .return:
		ret
