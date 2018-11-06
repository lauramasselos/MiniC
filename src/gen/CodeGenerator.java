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



    private PrintWriter writer; // use this writer to output the assembly instructions


    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        
    	// DONT TOUCH
    	writer = new PrintWriter(outputFile);
        visitProgram(program);
        writer.close();
        // END
    }

    
    public boolean inGlobalScope;
    public LinkedList<VarDecl> globalVars = new LinkedList<>();
    
    @Override
    public Register visitBaseType(BaseType bt) {
    	if (inGlobalScope) {
    		if (bt == BaseType.INT) {
    			writer.println(".space 4");
    		}
    		if (bt == BaseType.CHAR) {
    			writer.println(".space 1");
    		}
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
    	p.accept(new GlobalVarDeclVisitor(writer));
    	p.accept(new StrLiteralVisitor(writer));
    	inGlobalScope = false;
    	writer.println(".text");
    	//writer.println("j main");
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
    	vd.accept(this);
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
//    			}
    			
    		}
    		return reg;
    	}	
    	
    	else {
    		Register reg = getRegister(); Register reg1 = getRegister();
    		if (globalVarDecls.containsKey(v.vd)) {
//    			if (v.vd.type instanceof ArrayType) {}
//    			else {
    				writer.println("la " + reg1.toString() + ", " + globalVarDecls.get(v.vd));
//    			}
    			
    		}
    		writer.println("lw " + reg.toString() + ", (" + reg1.toString() + ")"); freeRegister(reg1);
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
		if (inGlobalScope) {
				writer.println(".space " + (at.n * at.typeA.getByteSize(at.typeA)));
		}
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
				writer.println("li " + res.toString() + ", 0");
				writer.println("ble " + lhs.toString() + ", " + rhs.toString() + ", line" + generalTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nline" + generalTag + ": ");
				generalTag++; break;
			}
			case LT: {
				writer.println("li " + res.toString() + ", 0");
				writer.println("bge " + lhs.toString() + ", " + rhs.toString() + ", line" + generalTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nline" + generalTag + ": ");
				generalTag++; break;
			}
			case GE: {
				writer.println("li " + res.toString() + ", 0");
				writer.println("blt " + lhs.toString() + ", " + rhs.toString() + ", line" + generalTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nline" + generalTag + ": ");
				generalTag++; break;
			}
			case LE: {
				writer.println("li " + res.toString() + ", 0");
				writer.println("bgt " + lhs.toString() + ", " + rhs.toString() + ", line" + generalTag);
				writer.println("li " + res.toString() + ", 1");
				writer.println("\nline" + generalTag + ": ");
				generalTag++; break;
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
		return null;
	}

	@Override
	public Register visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		Register reg; int temp = generalTag; String tempstr = "line"+temp;
		writer.println("\nline" + generalTag + ": "); // line0:
		generalTag++;
		reg = w.e.accept(this); generalTag++;
		writer.println("beq " + reg.toString() + ", 0, line" + generalTag); generalTag--;// line3
		writer.println("\nline" + generalTag + ": "); generalTag++;// line1
		w.s.accept(this);
		writer.println("j "+ tempstr);
		writer.println("\nline"+generalTag+": "); generalTag++;
		
		freeRegister(reg);
		return null;
	}
	
	

	@Override
	public Register visitIf(If i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitAssign(Assign a) {
		// TODO Auto-generated method stub
		addressAccessed = true;
		Register lhs = a.lhs.accept(this);
		addressAccessed = false;
		Register rhs = a.rhs.accept(this);
		writer.println("sw " + rhs.toString() + ", (" + lhs.toString() + ")");
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
