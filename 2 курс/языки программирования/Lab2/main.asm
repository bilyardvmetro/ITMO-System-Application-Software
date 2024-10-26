%include "lib.inc"
%include "dict.inc"

%xdefine BUFFER_SIZE 256
%xdefine SUCCESS_CODE 0
%xdefine STD_ERR 2
%xdefine WRITE_SYSCALL 1

section .rodata
%include "words.inc"
	welcome_msg: db "Введите ключ для поиска: ", 0
	BOF_msg: db "Превышен размера буфера. Введите ключ длиной меньше 255 символов", 0
	.end:	; some hack for calculating msg length
	no_matches_msg: db "К сожалению такого ключа не существует", 0
	.end:

section .bss
	buf: resb 256	

section .text
	global _start
	
	_start:
		mov rdi, welcome_msg	; user greeting
		call print_string
		
		mov rdi, buf			; read key
		mov rsi, BUFFER_SIZE
		.debug:
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
		
		mov rdi, rax			; getting value
		call get_value
		
		mov rdi, rax
		call print_string		; print value
		call print_newline
		
		mov rdi, SUCCESS_CODE
		call exit
		
		.print_bof_err:
			mov rsi, BOF_msg
			mov rdx, BOF_msg.end - BOF_msg - 1
			
			call print_error
			
		
		.print_word_not_found:
			mov rsi, no_matches_msg
			mov rdx, no_matches_msg.end - no_matches_msg - 1
			
			call print_error
