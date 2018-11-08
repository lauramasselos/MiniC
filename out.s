.data
globalvar0: .space 4
string0: .asciiz "I'm greater than 3!\n"
string1: .asciiz "I'm greater than 1!\n"
string2: .asciiz "Blast Off.\n"
.text
main: 
la $t9, globalvar0
li $t8, 5
sw $t8, 0($t9)

startWhile_0: 

# Binary Operation
la $t9, globalvar0
lw $t8, 0($t9)
li $t9, 0

# GT BinOp
li $s7, 0
ble $t8, $t9, binOp0
li $s7, 1

binOp0: 

#END OF OP

beq $s7, 0, exitLoop_2

while_1: 
la $t8, globalvar0
lw $t9, 0($t8)
move $a0, $t9
li $v0, 1
syscall
li $t9, 10
move $a0, $t9
li $v0, 11
syscall

startWhile_3: 

# Binary Operation
la $t8, globalvar0
lw $t9, 0($t8)
li $t8, 3

# GT BinOp
li $s6, 0
ble $t9, $t8, binOp1
li $s6, 1

binOp1: 

#END OF OP

beq $s6, 0, exitLoop_5

while_4: 
la $t8, string0
move $a0, $t8
li $v0, 4
syscall
la $t8, globalvar0

# Binary Operation
la $s5, globalvar0
lw $t9, 0($s5)
li $s5, 1
sub $s4, $t9, $s5
sw $s4, 0($t8)
la $t8, globalvar0
lw $s4, 0($t8)
move $a0, $s4
li $v0, 1
syscall
li $s4, 10
move $a0, $s4
li $v0, 11
syscall
j startWhile_3

exitLoop_5: 

startWhile_6: 

# Binary Operation
la $s4, globalvar0
lw $s6, 0($s4)
li $s4, 1

# GT BinOp
li $t8, 0
ble $s6, $s4, binOp2
li $t8, 1

binOp2: 

#END OF OP

beq $t8, 0, exitLoop_8

while_7: 
la $s4, string1
move $a0, $s4
li $v0, 4
syscall
la $s4, globalvar0

# Binary Operation
la $s5, globalvar0
lw $s6, 0($s5)
li $s5, 1
sub $t9, $s6, $s5
sw $t9, 0($s4)
la $s4, globalvar0
lw $t9, 0($s4)
move $a0, $t9
li $v0, 1
syscall
li $t9, 10
move $a0, $t9
li $v0, 11
syscall
j startWhile_6

exitLoop_8: 
la $t8, globalvar0

# Binary Operation
la $s4, globalvar0
lw $t9, 0($s4)
li $s4, 1
sub $s5, $t9, $s4
sw $s5, 0($t8)
j startWhile_0

exitLoop_2: 
la $s7, string2
move $a0, $s7
li $v0, 4
syscall
la $s5, globalvar0
lw $s7, 0($s5)
move $a0, $s7
li $v0, 1
syscall
li $s7, 10
move $a0, $s7
li $v0, 11
syscall
li $v0, 10
syscall
