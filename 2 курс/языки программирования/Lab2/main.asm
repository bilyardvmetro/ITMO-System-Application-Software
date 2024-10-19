%include "lib.inc"
%include "dict.inc"

%xdefine WRITE_SYSCALL 1
%xdefine STD_ERR 2
%xdefine BUFFER_SIZE 255

section .rodata
%include "words.inc"
	welcome_msg: db "Введите ключ для поиска: ", 0
	BOF_msg: db "Превышен размера буфера. Введите ключ длиной меньше 255 символов", 0
	.end:	; some hack for calculating msg length
	no_matches_msg: db "К сожалению такого ключа не существует", 0
	.end:

section .data
	buf: times 255 db 0

section .text
	global _start
	
	_start:
		mov rdi, welcome_msg	; user greeting
		call print_string
		
		mov rdi, buf			; read key
		mov rsi, BUFFER_SIZE
		call read_string
		
		test rdx, rdx			; check BOF
		jz .print_bof_err
		
		push rdi
		push rsi
		push rax
		push rdx
		call print_newline
		pop rdx
		pop rax
		pop rsi
		pop rdi
		
		mov rdi, rax			; searching for key
		mov rsi, previous_element
		call find_word
		
		test rax, rax			; if no matches
		jz .print_word_not_found
		
		mov rdi, rax			; getting values
		call string_length		; get key length
		add rdi, rax			; set rdi to key end
		inc rdi					; don't forget 'bout null-terminator
		
		call print_string		; print value
		call print_newline
		call exit
		
		.print_bof_err:
			mov rax, WRITE_SYSCALL
			mov rdi, STD_ERR
			mov rsi, BOF_msg
			mov rdx, BOF_msg.end - BOF_msg - 1
			
			syscall
			
			call print_newline
			call exit
		
		.print_word_not_found:
			mov rax, WRITE_SYSCALL
			mov rdi, STD_ERR
			mov rsi, no_matches_msg
			mov rdx, no_matches_msg.end - no_matches_msg - 1
			
			syscall
			
			call print_newline
			call exit



	read_string: ; 8
	xor rax, rax
	push r12
	push r13
	push r14
	mov r12, rdi
	mov r13, rsi
	xor r14, r14	; обнуляем счетчик
	
		
	.loop:			
		call read_char		; считываем символ
		
		cmp rax, `\n` 		
		je .success_by_enter
		
		mov [r12 + r14], rax		; закидываем символ в буфер
		
		test rax, rax 		; проверка на нуль-терминатор. Если он есть, то мы уже дописали его к строке
							; при помощи инструкции выше
		je .success
		
		inc r14				; увеличиваем длину строки
		
		cmp r14, r13		; проверка на выход за пределы буфера
		jge .fail
		
		jmp .loop
	
	.success_by_enter:
		mov byte[r12 + r14], 0
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