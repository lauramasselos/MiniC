package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class CodeGenerator extends BaseVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();
    private Stack<Register> usedRegs = new Stack<Register>();

    public CodeGenerator() {
        freeRegs.addAll(Register.tmpRegs);
    }

    private class RegisterAllocationError extends Error {}

    private Register getRegister() {
        try {
            return freeRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
        }
    }

    private void freeRegister(Register reg) {
        freeRegs.push(reg);
    }
    

    

    private PrintWriter writer; // use this writer to output the assembly instructions


    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        
    
    	writer = new PrintWriter(outputFile);
        visitProgram(program);
        writer.close();
  
    }

    
    
    @Override
    public Register visitBaseType(BaseType bt) {
    	if (!inGlobalScope) {
//    		writer.print("-4");
    	}
        return null;
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    public Register visitBlock(Block b) {
        // TODO: to complete
    	for (VarDecl vd : b.vds) vd.accept(this);
    	for (Stmt s : b.stmts) s.accept(this);
        return null;
    }

    @Override
    public Register visitFunDecl(FunDecl fd) {
    	varOffset = 0;
        // TODO: to complete
    	if (!funCallExpr) writer.println(fd.name + ": ");
    	
    	if (!fd.name.equals("main")) {
    		int paramByteSize = 0;
    		for (VarDecl v : fd.params) paramByteSize += getByteSize(v.type); // $fp stored at 0($sp)
    		writer.println("addi $sp, $sp, -" + paramByteSize); // $fp stored at paramByteSize($sp)
//    		for (VarDecl v : fd.params) {
    			//AOSDGA
//    		}
    		
    		
    		
    		
    		
    		fd.block.accept(this);
    		writer.println("addi $sp, $sp, " + paramByteSize);
    		if (!funCallExpr) writer.println("jr $ra"); // check if this should be here
    	}

    	else {
			for (VarDecl v : fd.block.vds) {
				localVarByteSize += getByteSize(v.type);
			}
			
			writer.println("addi $sp, $sp, -" + (localVarByteSize+8));
			writer.println("sw $ra, " + (localVarByteSize+4) + "($sp)");
			writer.println("sw $fp, " + localVarByteSize + "($sp)");
			writer.println("move $fp, $sp");
    		fd.block.accept(this);
    		writer.println("lw $fp, ($fp)");
    		writer.println("addi $sp, $sp, " + (localVarByteSize+8));
    		writer.println("li $v0, 10");
    		writer.println("syscall");
    	}
    	
        return null;
    }

    @Override
    public Register visitProgram(Program p) {
        // TODO: to complete
    	inGlobalScope = true;
    	writer.println(".data");
//    	p.accept(new GlobalStructTypeDeclVisitor(writer));
    	p.accept(new GlobalVarDeclVisitor(writer));
    	p.accept(new StrLiteralVisitor(writer));
    	writer.println(".text");
    	writer.println("j main");
    	writer.println("STORING_ALL_REGISTERS_ONTO_STACK: ");
    	
    	writer.println("sw $fp, ($sp)"); // old frame pointer stored at top of stack
    	writer.println("add $fp, $sp, $zero"); // update frame pointer
    	// nb check when $sp will be updated-- atm only for funcallexpr and nested blocks
    	for (int i = 0; i < Register.tmpRegs.size(); i++) {
    		storingOffset-=4; Register r = Register.tmpRegs.get(i);
    		writer.println("sw " + r.toString() + ", " + storingOffset + "($sp)" );
    	} 
    	writer.println("addi $sp, $sp, " + storingOffset); // update stack pointer 
    	
    	writer.println("jr $ra"); // storingOffset = -72
    	
    	
    	
    	// CHECK THIS BC ALLY IS A CUCK TODO 
    	writer.println("LOADING_ALL_REGISTERS_FROM_STACK: ");
    	writer.println("addi $sp, $sp, " + (-1*storingOffset));
    	
    	for (int i = Register.tmpRegs.size()-1; i >=0 ; i--) {
    		Register r = Register.tmpRegs.get(i);
    		writer.println("lw " + r.toString() + ", " + storingOffset + "($sp)" );
    		storingOffset+=4;
    	} 
    	writer.println("lw $fp, ($fp)"); // restore old framepointer

    	
    	writer.println("jr $ra");
    	
    	
    	
    	
    	
    	
    	for (StructTypeDecl st : p.structTypeDecls) {
    		st.accept(this);
    	}
    	for (VarDecl vd : p.varDecls) {
    		vd.accept(this);
    	}
    	inGlobalScope = false;
    	for (FunDecl fd : p.funDecls) {
    		if (fd.name.equals("main")) fd.accept(this);
    		writer.println("\n\n");
    		
    	}
    	for (FunDecl fd : p.funDecls) {
    		if (!fd.name.equals("main")) fd.accept(this);
    	}
        return null;
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        // TODO: VarDecl: I think this is done?
    	//vd.type.accept(this);
    	if (!inGlobalScope) {
//    		writer.print("addi $sp, $sp, ");
//    		vd.type.accept(this);
//    		writer.println();
    		vd.vdOffset = varOffset + getByteSize(vd.type);
    		varOffset += getByteSize(vd.type); // to save local vars on stack; reset each time new fundecl 
    	}
        return null;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: VarExpr top
    	if (lhsOfAssign) {
//    		System.out.println("HERE");
    		Register reg = getRegister(); usedRegs.push(reg);
    		if (globalVarDecls.containsKey(v.vd)) {
//    			if (v.vd.type instanceof ArrayType) {
//    			}
//    			else {
    				writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));
//    			}
    		}
    		else {
    			writer.println("la " + reg.toString() + ", " + v.vd.vdOffset +  "($fp)"); // set offset laura dammit
    		}
    			
    		
    		return reg;
    	}
    		
    	
    	else {
    		Register reg = getRegister(); //Register reg1 = getRegister(); usedRegs.push(reg); usedRegs.push(reg1);
    		if (globalVarDecls.containsKey(v.vd)) {
//    			if (v.vd.type instanceof ArrayType) {}
//    			else {
    				writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));
//    				writer.println("lw " + reg.toString() + ", 0(" + reg1.toString() + ")"); freeRegister(reg1); usedRegs.remove(reg1);
//    			}
    			
    		} // TODO Fix VarExpr for RHSofAssign: am I loading from an offset of fp?
    		else {
    			writer.println("la " + reg.toString() + ", " + v.vd.vdOffset +  "($fp)");
    		}
    		writer.println("lw " + reg.toString() + ", (" + reg.toString() + ")");
    		
    		return reg;
    	}
    	

//    	if (globalVarDecls.containsKey(v.vd)) {
//    		writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));
//    	}
//    	
//        return reg;
    }

	@Override
	public Register visitStructType(StructType st) {
//		if (!inGlobalScope) {
//			writer.print(-1*st.sizeOfStruct);
//		}
		return null;
	}

	@Override
	public Register visitPointerType(PointerType pt) {
//		if (!inGlobalScope) {
//			writer.print("-4");
//		}
		
		return null;
	}

	@Override
	public Register visitArrayType(ArrayType at) {
//		if (!inGlobalScope) {
//			writer.print(-4*getByteSize(at));
//		}
//		
		return null;
	}

	@Override
	public Register visitStrLiteral(StrLiteral sl) {
		Register use = getRegister(); usedRegs.push(use);
		writer.println("la " + use.toString() + ", " + strings.get(sl.str));
		return use;
	}

	@Override
	public Register visitChrLiteral(ChrLiteral cl) {
		Register use = getRegister(); usedRegs.push(use);
		int asciiChar = (int) cl.c;
		writer.println("li " + use.toString() + ", " + asciiChar);
		return use;
	}

	@Override
	public Register visitIntLiteral(IntLiteral il) {
		Register use = getRegister(); usedRegs.push(use);
		writer.println("li " + use.toString() + ", " + il.n);
		return use;
	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fce) {
		// TODO FunCallExpr
		Register reg;  // if (e instanceof StrLiteral), base address of string stored in reg
		funCallExpr = true;
		
		
		if (fce.name.equals("print_i")) {
			for (Expr e : fce.args) {
				reg = e.accept(this);
				writer.println("move $a0, " + reg.toString());
				freeRegister(reg); usedRegs.remove(reg);
			}
			writer.println("li $v0, 1"); // load int to be printed into $a0
			writer.println("syscall"); 
			return null;
		}
		else if (fce.name.equals("print_s")) {
			for (Expr e : fce.args) {
				reg = e.accept(this);
				writer.println("move $a0, " + reg.toString());
				freeRegister(reg); usedRegs.remove(reg);
			}
			writer.println("li $v0, 4"); // load address of null-terminated string to print into $a0
			writer.println("syscall");
			return null;
		}
		
		else if (fce.name.equals("print_c")) {
			for (Expr e : fce.args) {
				reg = e.accept(this);
				writer.println("move $a0, " + reg.toString());
				freeRegister(reg); usedRegs.remove(reg);
			}
			writer.println("li $v0, 11"); // load ASCII character to print into $a0
			writer.println("syscall");
			return null;
		}
		
		else if (fce.name.equals("read_i")) {
			reg = getRegister();
			writer.println("li $v0, 5"); 
			writer.println("syscall"); // $v0 now contains integer read
			writer.println("move " + reg.toString() + ", $v0");
			return reg;
		}
		
		else if (fce.name.equals("read_c")) {
			reg = getRegister();
			writer.println("li $v0, 12");
			writer.println("syscall"); // $v0 now contains character read
			writer.println("move " + reg.toString() + ", $v0");
			return reg;
		}
		else if (fce.name.equals("mcmalloc")) {
	
		}
		else {
			// store each register in usedRegs stack onto the MIPS stack; set $fp == $sp; call and execute function; restore registers and $fp
			
//			int a = 0;
//			for (Expr e : fce.args) {
//				reg = e.accept(this);
//				writer.println("add $a" + a + ", $zero, " + reg.toString());
//				a++;  
//				if (a==4) {
//					System.out.println("NOPE"); break;
//				}
//				
////				paramsize += getByteSize(e.type);
//			}
			
//			for (Register r : Register.tmpRegs) {
//				storingOffset -= 4;
//				writer.println("sw " + r.toString() + ", " + storingOffset + "($sp)"); //freeRegister(r);
////				writer.println("addi $sp, $sp, -4");
//				
//			}
			
			
			//TODO push arguments onto the stack first
			
			int paramsize = fce.args.size();
			
			
			for (int i = 0; i < paramsize; i++) {
				Register arg = fce.args.get(i).accept(this);
				writer.println("sw " + arg.toString() + ", " + -1*i*4 + "($fp)"); // this frame pointer will be stored at the top of the stack
			}
			

			
			writer.println("jal STORING_ALL_REGISTERS_ONTO_STACK");
			writer.println("STORING_ALL_REGISTERS_ONTO_STACK: ");
	    	
//	    	writer.println("sw $fp, ($sp)"); // old frame pointer stored at top of stack
//	    	writer.println("add $fp, $sp, $zero"); // update frame pointer
//	    	// nb check when $sp will be updated-- atm only for funcallexpr and nested blocks
//	    	for (Register r : Register.tmpRegs) {
//	    		storingOffset-=4;
//	    		writer.println("sw " + r.toString() + ", " + storingOffset + "($sp)" );
//	    	} 
//	    	writer.println("addi $sp, $sp, " + storingOffset); // update stack pointer 
//	    	
//	    	writer.println("jr $ra"); // storingOffset = -72
//	    	
			
//			writer.println("add $fp, $sp, $zero");
			System.out.println("\n\n\n HI THERE \n\n\n");
			writer.println("jal " + fce.name);
			fce.fd.accept(this); if (funCallExpr) writer.println("\n# EXITING FUNCTION " + fce.name);
//			writer.println("add $fp, $sp, $zero");
			writer.println("jal LOADING_ALL_REGISTERS_FROM_STACK");
			
//			for (Register r : usedRegs) {
//				writer.println("lw " + r.toString() + ", " + storingOffset + "($sp)"); 
////				writer.println("addi $sp, $sp, 4");
//				storingOffset += 4;
//				
//			}
//			writer.println("jr $ra");
		}
		funCallExpr = false;
		return null;
	}

	@Override
	public Register visitBinOp(BinOp bo) {
		// TODO make sure for AND and OR that rhs is not evaluated until after beq/bne lhs.tostring
		// (see below case AND)
		writer.println("\n# Binary Operation\n");
		Register lhs = bo.lhs.accept(this);
		Register rhs = bo.rhs.accept(this);
		Register res = getRegister(); usedRegs.push(res);
		switch(bo.op) {
			case ADD: writer.println("add " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case SUB: writer.println("sub " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case MUL: writer.println("mul " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case DIV: writer.println("div " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case MOD: writer.println("div " + lhs.toString() + ", " + rhs.toString()); writer.println("mfhi " + res.toString()); break;
			case GT: {
				writer.println("\n# GT BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("ble " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case LT: {
				writer.println("\n# LT BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bge " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case GE: {
				writer.println("\n# GE BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("blt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case LE: {
				writer.println("\n# LE BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bgt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case EQ: {
				writer.println("\n# EQ BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bne " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case NE: {
				writer.println("\n# NE BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("beq " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case AND: {
				writer.println("\n# AND BinOp\n");
				writer.println("li " + res.toString() + ", 1");
				writer.println("bne " + lhs.toString() + ", 1, binOp" + binOpTag);
//				Register rhs = bo.rhs.accept(this);
				writer.println("bne " + rhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("j binOp"+(binOpTag+1));
				writer.println("\nbinOp"+binOpTag+": ");
				writer.println("li " + res.toString() + ", 0"); binOpTag++;
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case OR: {
				writer.println("\n# OR BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("beq " + lhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("beq " + rhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("j binOp"+(binOpTag+1));
				writer.println("\nbinOp"+binOpTag+": ");
				writer.println("li " + res.toString() + ", 1"); binOpTag++;
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			
		}
		freeRegister(lhs); freeRegister(rhs); usedRegs.remove(lhs); usedRegs.remove(rhs);
		return res;
	}

	@Override
	public Register visitOp(Op o) {
		return null;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
	
		if (lhsOfAssign) {
			Register reg = getRegister(); usedRegs.push(reg);
			Register r1 = aae.array.accept(this); lhsOfAssign = false;
			Register r2 = aae.index.accept(this);
			
			// space for size of array in bytes
			writer.println("mul " + r2.toString() + ", " + r2.toString() + ", -" + 4); // try getByteSize(aae.array.type) instead of 4
			writer.println("add " + reg.toString() + ", " + r1.toString() + ", " + r2.toString());
			
			freeRegister(r1); usedRegs.remove(r1);
			freeRegister(r2); usedRegs.remove(r2);
			lhsOfAssign = true;
			return reg;
		}
		
		else {
			Register reg = getRegister(); usedRegs.push(reg); lhsOfAssign = true;
			Register r1 = aae.array.accept(this); lhsOfAssign = false;
			Register r2 = aae.index.accept(this);
			
			// space for size of array in bytes
			writer.println("mul " + r2.toString() + ", " + r2.toString() + ", -" + 4); // try getByteSize(aae.array.type) instead of 4
			writer.println("add " + reg.toString() + ", " + r1.toString() + ", " + r2.toString());
			writer.println("lw " + reg.toString() + ", (" + reg.toString() + ")");
			
			freeRegister(r1); usedRegs.remove(r1);
			freeRegister(r2); usedRegs.remove(r2);
			return reg;
		}
	}

	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO EVERYTHING lol
		return null;
	}

	@Override
	public Register visitValueAtExpr(ValueAtExpr vae) {
		if (lhsOfAssign) {
			Register res = getRegister(); usedRegs.push(res);
			Register reg = vae.e.accept(this);
			writer.println("la " + res.toString() + ", (" + reg.toString() + ")");
			freeRegister(reg); usedRegs.remove(reg);
			return res;
		}
		else {
			Register res = getRegister(); usedRegs.push(res);
			Register reg = vae.e.accept(this);
			writer.println("lw " + res.toString() + ", (" + reg.toString() + ")");
			freeRegister(reg); usedRegs.remove(reg);
			return res;
		}
	}

	@Override
	public Register visitSizeOfExpr(SizeOfExpr soe) {
		// TODO SizeOf(<string>) should be size of chararray rounded up to the nearest 4
		Register reg = getRegister(); usedRegs.push(reg);
		int size = 0;
		if (soe.typeSOE instanceof BaseType || soe.typeSOE instanceof PointerType) {
			size = 4;
		}
		else if (soe.typeSOE instanceof ArrayType) {
			size = ((ArrayType) soe.typeSOE).n * 4;
		}
		else if (soe.typeSOE instanceof StructType) {
			size = ((StructType) soe.typeSOE).sizeOfStruct;
		}
		writer.println("li " + reg.toString() + ", " + size);
		return reg;
	}

	@Override
	public Register visitTypecastExpr(TypecastExpr te) {
		// DON'T TOUCH THIS THIS IS DONE
		Register res = te.e.accept(this);
		return res;
	}

	@Override
	public Register visitExprStmt(ExprStmt es) {
		Register reg;
		reg = es.e.accept(this);
		if (reg != null) {
			freeRegister(reg); usedRegs.remove(reg);
		}
		return null;
	}
	
	@Override
	public Register visitWhile(While w) {
		Register reg; 
		String label0 = label("start_while_");
		String label1 = label("while_");
		String label2 = label("exit_while_");
		writer.println("\n"+label0 + ": "); 
		reg = w.e.accept(this);
		writer.println("beq " + reg.toString() + ", 0, "+ label2); 
		writer.println("\n"+label1 + ": "); 
		w.s.accept(this);
		writer.println("j "+ label0);	
		writer.println("\n"+label2 +": "); 	
		freeRegister(reg); usedRegs.remove(reg);
		return null;
	}
	
	

	@Override
	public Register visitIf(If i) {
		Register reg;
		if (i.s2 != null) {
			String label0 = label("if_");
			String label1 = label("else_");
			String label2 = label("exit_if_");
			writer.println("\n" + label0 + ": ");
			reg = i.e.accept(this);
			writer.println("beq " + reg.toString() + ", 0, " + label1);
			i.s1.accept(this);
			writer.println("\nj " + label2);
			writer.println("\n" + label1 + ": ");
			i.s2.accept(this);
			writer.println("\n" + label2 + ": ");
		}
		else {
			String label0 = label("if_");
			String label1 = label("exit_if_");
			writer.println("\n" + label0 + ": ");
			reg = i.e.accept(this);
			writer.println("beq " + reg.toString() + ", 0, " + label1);
			i.s1.accept(this);
			writer.println("\n" + label1 + ": ");
		}
		freeRegister(reg); usedRegs.remove(reg);
		return null;
	}

	@Override
	public Register visitAssign(Assign a) {
		// TODO Auto-generated method stub
		if (!(a.lhs instanceof FieldAccessExpr)) { // Martin's trick
			lhsOfAssign = true;
			Register lhs = a.lhs.accept(this); // address of lhs loaded into register
			lhsOfAssign = false;
			Register rhs = a.rhs.accept(this); // word in rhs loaded into register
//			int offset = ((VarExpr) a.rhs).vd.v.vdOffset;
			writer.println("sw " + rhs.toString() + ", (" + lhs.toString() + ")");
			freeRegister(lhs); usedRegs.remove(lhs);
			freeRegister(rhs); usedRegs.remove(rhs);
			//addressAccessed = true;
		}
		
		return null;
	}

	@Override
	public Register visitReturn(Return r) {
		Register reg;
		reg = r.e.accept(this);
		if (reg != null) {
			writer.println("move $v0, " + reg.toString());
			freeRegister(reg); usedRegs.remove(reg);
		}
		
		return null;
	}
	
	
	
	
	
	
}

