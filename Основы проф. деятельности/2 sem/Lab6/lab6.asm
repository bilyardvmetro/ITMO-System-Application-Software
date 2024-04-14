ORG 0x000
V0: 	WORD $default, 0x180
V1: 	WORD $int1, 0x180
V2: 	WORD $int2, 0x180
V3: 	WORD $default, 0x180
V4: 	WORD $default, 0x180
V5: 	WORD $default, 0x180
V6: 	WORD $default, 0x180
V7: 	WORD $default, 0x180

ORG 0x00F
default: IRET 

ORG 0x048
X: 	WORD 0
min: 	WORD 0xFFE5 ;-27
max: 	WORD 0x0018 ;24

START: 	DI 	;запрет на прерывания неиспользуемых кву
	CLA
	OUT 0x1
	OUT 0x7
	OUT 0xB
	OUT 0xE
	OUT 0x12
	OUT 0x16
	OUT 0x1A
	OUT 0x1E

	LD #0x9 ;задаем векторы для ву1 и ву2
	OUT 0x3
	LD #0xA
	OUT 0x5
	EI

MAIN:	DI 	;главная программа
	LD X
	INC
	INC
	CALL check
	ST X
	EI
	JUMP MAIN

check:	CMP min ;проверка на одз
	BLT ldMin
	CMP max
	BGE ldMin
	JUMP return
ldMin:	LD min
return:	RET

int1:	DI
	LD X
	HLT 	;для отладки. Проверить аккум
	ASL
	ASL
	ADD X
	SUB #8
	HLT 	;для отладки. Проверить результат
	OUT 0x2
	EI
	IRET

int2:	DI
	IN 0x4
	HLT	;для отладки. Проверить, что лежало в ву2
	ST X
	HLT	;для отладки. Проверить, что все сохранилось в X
	EI
	IRET
