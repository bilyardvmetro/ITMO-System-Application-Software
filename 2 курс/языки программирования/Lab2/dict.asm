%include "lib.inc"


section .text

global find_word

; rdi - key to search
; rsi - map beginnig pointer
find_word:	; sp 8
	push rsi ; sp 16	
	push r12 ; sp 24
	push r13 ; sp 32
	mov r12, rdi
	mov r13, rsi
	xor r8, r8
	
	.loop:
		test r13, r13 ; проверка на конец словаря
		jz .no_match
		
		lea r8, [r13+8] ; загружаем адрес начала ключа

		mov rsi, r8
		mov rdi, r12	; после string_equals rdi содержит мусор,
						; потому его нужно восстановить
		call string_equals
		
		cmp rax, 1	; если ключи равны
		je .match
		
		mov r13, [r13] ; кладем в r13 указатель на следующий элемент
		
		jmp .loop
		
	.match:
		mov rax, r13 ; возвращаем адрес найденного ключа
		add rax, 8
		jmp .return
		
	.no_match:
		xor rax, rax ; возвращаем 0
		
	.return:
		mov rdi, r12	; приводим calle-saved регистры и rdi в порядок
		pop r13
		pop r12
		pop rsi
		ret