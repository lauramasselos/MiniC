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
    	localVarByteSize = 0;
    	
    	
        // TODO: to complete
		writer.println(fd.name + ": ");
    	
    	if (!fd.name.equals("main")) {
    		
    		// when jal STORING_ALL_REGISTERS_ONTO_STACK is called, 
    		

    		writer.println("addi $sp, $sp, -4");
    		writer.println("sw $fp, ($sp)");
    		writer.println("move $fp, $sp");
    		//writer.println("addi $sp, $sp, -4");
    		
    		int offset = 0;
    		for (VarDecl v : fd.params) {
    			Register reg = getRegister(); usedRegs.push(reg);
    			writer.println("addi $sp, $sp, -4");
    			writer.println("lw " + reg.toString() + ", " + offset + "($a3)");
    			offset += 4;
    			writer.println("sw " + reg.toString() + ", -" + offset + "($fp)");
    			v.vdOffset = -offset;
    			//v.isParam = true;
    		}
    		
    		
//    		int paramByteSize = 0;
//    		for (VarDecl v : fd.params) paramByteSize += getByteSize(v.type); // $fp stored at 0($sp)
//    		writer.println("addi $sp, $sp, -" + paramByteSize); // $fp stored at paramByteSize($sp)
//    		for (VarDecl v : fd.params) {
    			//AOSDGA
//    		}
    		
    		
    		
    		
    		
    		fd.block.accept(this);
    		
    		//writer.println("addi $sp, $sp, " + paramByteSize);
    		writer.println("jr $ra"); // check if this should be here
    	}

    	else {
//			for (VarDecl v : fd.block.vds) {
//				localVarByteSize-+= getByteSize(v.type);
//			}
			writer.println("addi $sp, $sp, -4");
			writer.println("sw $fp, ($sp)");
			writer.println("move $fp, $sp");

    		fd.block.accept(this); // $sp incremented here; don't worry!!
    		
    		writer.println("move $sp, $fp");
    		writer.println("addi $sp, $sp, 4");
    		writer.println("lw $fp, ($fp)");
//    		writer.println("addi $sp, $sp, " + localVarByteSize);
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
    	
    	writer.println("\n\n ##### STORING REGISTERS #####");
		writer.println("STORING_ALL_REGISTERS: ");
		for (int i = 0; i < Register.tmpRegs.size(); i++) {
    		Register r = Register.tmpRegs.get(i);
    		writer.println("addi $sp, $sp, -4");
    		writer.println("sw " + r.toString() + ", ($sp)" );
    		
    	} 
		writer.println("jr $ra\n\n");
		
		writer.println("\n\n ##### LOADING REGISTERS #####");
		writer.println("LOADING_ALL_REGISTERS: ");
		for (int i = Register.tmpRegs.size()-1; i >=0 ; i--) {
    		Register r = Register.tmpRegs.get(i);
    		writer.println("addi $sp, $sp, 4");
    		writer.println("lw " + r.toString() + ", ($sp)" );
    	} 
		writer.println("jr $ra\n\n");
		
		boolean main = false;
		for (FunDecl fd : p.funDecls) {
			if (fd.name.equals("main")) {
				main = true; break;
			}
		}
		if (!main) {
			writer.println("main: ");
			writer.println("li $v0, 10");
			writer.println("syscall");
		}
    	
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
    		vd.vdOffset = localVarByteSize - getByteSize(vd.type);
    		localVarByteSize -= getByteSize(vd.type); // to save local vars on stack; reset each time new fundecl 
    		writer.println("addi $sp, $sp, -" + getByteSize(vd.type) + "          # space for variables"); 
    	}
    	
        return null;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: VarExpr top
    	v.exprOffset = v.vd.vdOffset;
    	if (lhsOfAssign && !v.isParam) {
    		Register reg = getRegister(); usedRegs.push(reg);
    		if (globalVarDecls.containsKey(v.vd)) {
    			varIsGlobal = true;
    			writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));

    		}
    		else {
//    			if (rhsInstanceOfFunCallExpr) {
    				varIsGlobal = false;
    				writer.println("lw " + reg.toString() + ", " + v.vd.vdOffset +  "($fp)     # " + v.name + " lhsOfAssign"); 	
//    			}
    		}
    		return reg;
    	}
    		
    	
    	else if (!lhsOfAssign && !v.isParam) {		
    		Register reg = getRegister(); usedRegs.push(reg);
    		if (globalVarDecls.containsKey(v.vd)) {
    				varIsGlobal = true;
    				writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));
    				writer.println("lw " + reg.toString() + ", (" + reg.toString() + ")");	
    		} 
    		else {
    			varIsGlobal = false;	
    			writer.println("lw " + reg.toString() + ", " + v.vd.vdOffset +  "($fp)     # " + v.name); 
    		}
    		
    		
    		return reg;
    	}
    	
    	else if (v.isParam) {
    		Register reg = getRegister(); usedRegs.push(reg);
    		writer.println("lw " + reg.toString() + ", " + (v.vd.vdOffset) +  "($fp)     # " + v.name + " isParam");	
    		return reg;
    		
    	}
    	
    	else {
    		System.out.println("VAREXPR DONE INCORRECTLY"); return null;
    	}
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
			reg = getRegister(); usedRegs.push(reg);
			writer.println("li $v0, 5"); 
			writer.println("syscall"); // $v0 now contains integer read
			writer.println("move " + reg.toString() + ", $v0");
			return reg;
		}
		
		else if (fce.name.equals("read_c")) {
			reg = getRegister(); usedRegs.push(reg);
			writer.println("li $v0, 12");
			writer.println("syscall"); // $v0 now contains character read
			writer.println("move " + reg.toString() + ", $v0");
			return reg;
		}
		else if (fce.name.equals("mcmalloc")) {
			reg = getRegister(); usedRegs.push(reg);
			writer.println("li $v0, 9");
			writer.println("syscall");
			writer.println("move " + reg.toString() + ", $v0");
			return reg;
		}
		else {
			funCallExpr = true;
			writer.println();
			for (Expr e : fce.args) {
				e.isParam = true;
			}
			reg = getRegister(); usedRegs.push(reg);
			
			
//			writer.println("jal STORING_ALL_REGISTERS_ONTO_STACK");
			int argsSize = fce.args.size();
			for (int i = 0; i < fce.args.size(); i++) {
				Register r = fce.args.get(i).accept(this);
				writer.println("addi $sp, $sp, -4");
	    		writer.println("sw " + r.toString() + ", ($sp)");
	    		freeRegister(r); usedRegs.remove(r);
				
			}
			
			writer.println("move $a3, $sp");
			writer.println("addi $sp, $sp, -4");
			writer.println("sw $ra, ($sp)");
		// PUSH REGISTERS AFTER STORING RA (this is all different; keep above as is!!)
			
			
			writer.println("jal STORING_ALL_REGISTERS");
			
			writer.println("jal " + fce.name);

//			writer.println("jal LOADING_ALL_REGISTERS_FROM_STACK");
			
			writer.println("move $sp, $fp");
			writer.println("addi $sp, $sp, 4");
    		writer.println("lw $fp, ($fp)");
    		writer.println("jal LOADING_ALL_REGISTERS");
    		
 			writer.println("lw $ra, ($sp)");
			writer.println("addi $sp, $sp, 4");
    		writer.println("move " + reg.toString() + ", $v0");
			writer.println("\n\n\n");
			
			
			funCallExpr = false;
			return reg;
			}
	}

	@Override
	public Register visitBinOp(BinOp bo) {
		// TODO make sure for AND and OR that rhs is not evaluated until after beq/bne lhs.tostring
		// (see below case AND)
		writer.println("\n# Binary Operation\n");
		Register lhs = bo.lhs.accept(this);
//		Register rhs = bo.rhs.accept(this);
		Register res = getRegister(); usedRegs.push(res);
		switch(bo.op) {
			case ADD: {
				Register rhs = bo.rhs.accept(this);
				writer.println("add " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); freeRegister(rhs); usedRegs.remove(rhs); break;
			}
			case SUB: {
				Register rhs = bo.rhs.accept(this);
				writer.println("sub " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); freeRegister(rhs); usedRegs.remove(rhs); break;
			}
			case MUL: {
				Register rhs = bo.rhs.accept(this);
				writer.println("mul " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); freeRegister(rhs); usedRegs.remove(rhs); break;
			}
			case DIV: {
				Register rhs = bo.rhs.accept(this);
				writer.println("div " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); freeRegister(rhs); usedRegs.remove(rhs); break;
			}
			case MOD: {
				Register rhs = bo.rhs.accept(this);
				writer.println("div " + lhs.toString() + ", " + rhs.toString()); writer.println("mfhi " + res.toString()); freeRegister(rhs); usedRegs.remove(rhs); break;
			}
			case GT: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# GT BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("ble " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case LT: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# LT BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bge " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case GE: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# GE BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("blt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case LE: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# LE BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bgt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); break;
			}
			case EQ: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# EQ BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bne " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case NE: {
				Register rhs = bo.rhs.accept(this);
				writer.println("\n# NE BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("beq " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case AND: {
				writer.println("\n# AND BinOp\n");
				writer.println("li " + res.toString() + ", 1");
				writer.println("bne " + lhs.toString() + ", 1, binOp" + binOpTag);
				Register rhs = bo.rhs.accept(this);
				writer.println("bne " + rhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("j binOp"+(binOpTag+1));
				writer.println("\nbinOp"+binOpTag+": ");
				writer.println("li " + res.toString() + ", 0"); binOpTag++;
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			case OR: {
				writer.println("\n# OR BinOp\n");
				writer.println("li " + res.toString() + ", 0");
				writer.println("beq " + lhs.toString() + ", 1, binOp" + binOpTag);
				Register rhs = bo.rhs.accept(this);
				writer.println("beq " + rhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("j binOp"+(binOpTag+1));
				writer.println("\nbinOp"+binOpTag+": ");
				writer.println("li " + res.toString() + ", 1"); binOpTag++;
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; freeRegister(rhs); usedRegs.remove(rhs);break;
			}
			
		}
		freeRegister(lhs);  usedRegs.remove(lhs); 
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
		rhsInstanceOfFunCallExpr = false;
		// TODO Auto-generated method stub
		if (!(a.lhs instanceof FieldAccessExpr)) { // Martin's trick
			Register rhs = a.rhs.accept(this); // word in rhs loaded into register
			lhsOfAssign = true;
			Register lhs = a.lhs.accept(this); // address of lhs loaded into register
			lhsOfAssign = false;
			
			if (a.rhs instanceof FunCallExpr) rhsInstanceOfFunCallExpr = true;
//			a.type = a.lhs.type;
//			System.out.println(a.type);
//			int offset = ((VarExpr) a.rhs).vd.v.vdOffset;
			if (!varIsGlobal) {
				writer.println("sw " + rhs.toString() + ", " + a.lhs.exprOffset + "($fp)     # Assigning a variable");
			}
			else {
				writer.println("sw " + rhs.toString() + ", (" + lhs.toString() + ")");
			}
			freeRegister(lhs); usedRegs.remove(lhs);
			freeRegister(rhs); usedRegs.remove(rhs);
				
			//addressAccessed = true;
//			vd.vdOffset = localVarByteSize - getByteSize(vd.type);
//    		localVarByteSize -= getByteSize(vd.type); v.exprOffset is set
		}
		
		return null;
	}


	@Override
	public Register visitReturn(Return r) {
		Register reg;
		reg = r.e.accept(this);
		if (reg != null) {
			writer.println("move $v0, " + reg.toString() + "     # Returning a variable");
			freeRegister(reg); usedRegs.remove(reg);
		}
		
		return null;
	}
	
	
	
	
	
	
}

