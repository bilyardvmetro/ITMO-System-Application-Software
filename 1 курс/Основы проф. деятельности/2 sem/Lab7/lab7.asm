ORG 0x4E0
test1_n:	WORD 0x1FFF ; 8191
test2_n:	WORD 0x2000 ; 8192

test1_res:	WORD ?
test2_res:	WORD ?
	

ORG 0x4F1
start:	LD $test1_n
	WORD 0xFE03 ;goto test1_f
test1_p:	LD #0x1
	ST $test1_res
	JUMP test_2
test1_f:	CLA
	ST $test1_res
	JUMP test_2
			
test_2:	LD $test2_n
	WORD 0xFE03 ;goto test1_p
test2_f:	CLA
	ST $test2_res
	JUMP main
test2_p:	LD #0x1
	ST $test2_res
	JUMP main
			
main:	LD $test1_res
	AND $test2_res
	CMP #0x1
	BEQ success
	LD #0xFF
	HLT
success:	LD #0x1
	HLT