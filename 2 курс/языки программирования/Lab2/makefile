ASM=nasm
ASMFLAGS=-f elf64
LD=ld

SRC:=$(wildcard *.asm)
OBJ_DIR = obj
OBJ = $(patsubst %.asm,$(OBJ_DIR)/%.o,$(SRC))

TARGET=main
PYTHON=python3



$(shell mkdir -p $(OBJ_DIR))

$(OBJ_DIR)/%.o: %.asm 
	$(ASM) $(ASMFLAGS) -o $@ $<

$(TARGET): $(OBJ)
	$(LD) -o $@ $^
	
.PHONY: clean test all

all: $(TARGET)

clean:
	rm -rf $(OBJ_DIR) $(TARGET)
	
test:
	chmod 777 $(TARGET)
	$(PYTHON) test.py