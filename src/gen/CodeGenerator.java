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

    public boolean inGlobalScope;

    private PrintWriter writer; // use this writer to output the assembly instructions


    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        
    	// DONT TOUCH
    	writer = new PrintWriter(outputFile);
        visitProgram(program);
        writer.close();
        // END
    }

    
    
    public LinkedList<VarDecl> globalVars = new LinkedList<>();
    
    @Override
    public Register visitBaseType(BaseType bt) {
    	
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
        // TODO: to complete
    	writer.println(fd.name + ": ");
    	for (VarDecl vd : fd.params) vd.accept(this);
    	fd.block.accept(this);
    	
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
    	inGlobalScope = false;
    	writer.println(".text");
    	//writer.println("j main");
    	for (StructTypeDecl st : p.structTypeDecls) {
    		st.accept(this);
    	}
    	for (VarDecl vd : p.varDecls) {
    		vd.accept(this);
    	}
    	for (FunDecl fd : p.funDecls) {
    		fd.accept(this);
    	}
    	writer.println("li $v0, 10");
    	writer.println("syscall");
    	
        return null;
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        // TODO: to complete
    	vd.type.accept(this);
        return null;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: to complete
    	if (addressAccessed) {
//    		System.out.println("HERE");
    		Register reg = getRegister();
    		if (globalVarDecls.containsKey(v.vd)) {
//    			if (v.vd.type instanceof ArrayType) {}
//    			else {
    				writer.println("la " + reg.toString() + ", " + globalVarDecls.get(v.vd));
    		}
    		else {
    			writer.println("la " + reg.toString() + ", " + -1*v.vd.vdOffset +  "($fp)");
    		}
    			
    		
    		return reg;}
    		
    	
    	else {
    		Register reg = getRegister(); Register reg1 = getRegister();
    		if (globalVarDecls.containsKey(v.vd)) {
//    			if (v.vd.type instanceof ArrayType) {}
//    			else {
    				writer.println("la " + reg1.toString() + ", " + globalVarDecls.get(v.vd));
//    			}
    			
    		}
    		writer.println("lw " + reg.toString() + ", 0(" + reg1.toString() + ")"); freeRegister(reg1);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitPointerType(PointerType pt) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public Register visitArrayType(ArrayType at) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public Register visitStrLiteral(StrLiteral sl) {
		// TODO Auto-generated method stub
		Register use = getRegister();
		writer.println("la " + use.toString() + ", " + strings.get(sl.str));
		return use;
	}

	@Override
	public Register visitChrLiteral(ChrLiteral cl) {
		// TODO Auto-generated method stub
		Register use = getRegister();
		int asciiChar = (int) cl.c;
		writer.println("li " + use.toString() + ", " + asciiChar);
		return use;
	}

	@Override
	public Register visitIntLiteral(IntLiteral il) {
		// TODO Auto-generated method stub
		Register use = getRegister();
		writer.println("li " + use.toString() + ", " + il.n);
		return use;
	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fce) {
		// TODO Auto-generated method stub
		Register reg;  // if (e instanceof StrLiteral), base address of string stored in reg
		for (Expr e : fce.args) {
			reg = e.accept(this);
			writer.println("move $a0, " + reg.toString());
			freeRegister(reg);
		}
		
		
		if (fce.name.equals("print_i")) {
			writer.println("li $v0, 1"); // load int to be printed into $a0
			writer.println("syscall");
		}
		else if (fce.name.equals("print_s")) {
			writer.println("li $v0, 4"); // load address of null-terminated string to print into $a0
			writer.println("syscall");
		}
		
		else if (fce.name.equals("print_c")) {
			writer.println("li $v0, 11"); // load ASCII character to print into $a0
			writer.println("syscall");
		}
		
		else if (fce.name.equals("read_i")) {
			writer.println("li $v0, 5"); 
			writer.println("syscall"); // $v0 now contains integer read
		}
		
		else if (fce.name.equals("read_c")) {
			writer.println("li $v0, 12");
			writer.println("syscall"); // $v0 now contains character read
		}
		else {
			writer.println("jal " + fce.name);
			fce.fd.accept(this);
			writer.println("jr $ra");
		}
		
		return null;
	}

	@Override
	public Register visitBinOp(BinOp bo) {
		// TODO Auto-generated method stub
		writer.println("\n# Binary Operation");
		Register lhs = bo.lhs.accept(this);
		Register rhs = bo.rhs.accept(this);
		Register res = getRegister();
		switch(bo.op) {
			case ADD: writer.println("add " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case SUB: writer.println("sub " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case MUL: writer.println("mul " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case DIV: writer.println("div " + res.toString() + ", " + lhs.toString() + ", " + rhs.toString()); break;
			case MOD: writer.println("div " + lhs.toString() + ", " + rhs.toString()); writer.println("mfhi " + res.toString()); break;
			case GT: {
				writer.println("\n# GT BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("ble " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case LT: {
				writer.println("\n# LT BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bge " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case GE: {
				writer.println("\n# GE BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("blt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case LE: {
				writer.println("\n# LE BinOp");
				writer.println("li " + res.toString() + ", 0");
				writer.println("bgt " + lhs.toString() + ", " + rhs.toString() + ", binOp" + binOpTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case EQ: {
				writer.println("\n# EQ BinOp");
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
				writer.println("\n# AND BinOp");
				writer.println("li " + res.toString() + ", 1");
				writer.println("bne " + lhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("bne " + rhs.toString() + ", 1, binOp" + binOpTag);
				writer.println("j binOp"+(binOpTag+1));
				writer.println("\nbinOp"+binOpTag+": ");
				writer.println("li " + res.toString() + ", 0"); binOpTag++;
				writer.println("\nbinOp" + binOpTag + ": ");
				//writer.println("\n#END OF OP\n");
				binOpTag++; break;
			}
			case OR: {
				writer.println("\n# OR BinOp");
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
		freeRegister(lhs); freeRegister(rhs);
		return res;
	}

	@Override
	public Register visitOp(Op o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitValueAtExpr(ValueAtExpr vae) {
		// TODO Auto-generated method stub
		if (addressAccessed) {
			Register res = getRegister();
			Register reg = vae.e.accept(this);
			writer.println("la " + res.toString() + ", (" + reg.toString() + ")");
			freeRegister(reg); return res;
		}
		else {
			Register res = getRegister();
			Register reg = vae.e.accept(this);
			writer.println("lw " + res.toString() + ", (" + reg.toString() + ")");
			freeRegister(reg); return res;
		}
	}

	@Override
	public Register visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		Register reg = getRegister();
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
		// TODO Auto-generated method stub
		Register reg;
		reg = es.e.accept(this);
		if (reg != null) freeRegister(reg);
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
		freeRegister(reg);
		return null;
	}
	
	

	@Override
	public Register visitIf(If i) {
		// TODO Auto-generated method stub
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
		freeRegister(reg);
		return null;
	}

	@Override
	public Register visitAssign(Assign a) {
		// TODO Auto-generated method stub
		addressAccessed = true;
		Register lhs = a.lhs.accept(this);
		addressAccessed = false;
		Register rhs = a.rhs.accept(this);
		writer.println("sw " + rhs.toString() + ", 0(" + lhs.toString() + ")");
		freeRegister(lhs); freeRegister(rhs); //addressAccessed = true;
		return null;
	}

	@Override
	public Register visitReturn(Return r) {
		// TODO Auto-generated method stub
		Register reg;
		reg = r.e.accept(this);
		if (reg != null) freeRegister(reg);
		
		return null;
	}
	
	
	
	
	
	
}

