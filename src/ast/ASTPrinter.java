
package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }
 
    @Override
    public Void visitBlock(Block b) {
        String delimiter = "";
    	writer.print("Block(");
        for (VarDecl vd : b.vds) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (Stmt s : b.stmts) {
        	writer.print(delimiter);
        	delimiter = ",";
        	s.accept(this);
        }
        writer.print(")");
        return null;
    }
 // FunDecl definition (the String is the name of the FunDecl)
// FunDecl  ::= Type String VarDecl* Block
    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(",");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = ",";
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ",";
            fd.accept(this);
        }
        writer.print(")");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
    	writer.print(bt.toString());
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
    	String delimiter = "";
    	writer.print("StructTypeDecl(");
    	st.structType.accept(this);
    	writer.print(",");
    	for (VarDecl vd : st.varDecls) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
    	writer.print(")");
        return null;
    }
    
	@Override
	public Void visitStructType(StructType st) {
		// TODO Auto-generated method stub
		writer.print("StructType(");
		writer.print(st.name);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {
		writer.print("PointerType(");
		pt.type.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitArrayType(ArrayType at) {
		writer.print("ArrayType(");
		at.type.accept(this);
		writer.print(",");
		writer.print(at.n);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral sl) {
		writer.print("StrLiteral(");
		writer.print(sl.str);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitChrLiteral(ChrLiteral cl) {
		writer.print("ChrLiteral(");
		writer.print(cl.c);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitIntLiteral(IntLiteral il) {
		writer.print("IntLiteral(");
		writer.print(il.n);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
		String delimiter = ",";
		writer.print("FunCallExpr(");
		writer.print(fce.name);
		for (Expr e : fce.args) {
            writer.print(delimiter);
            e.accept(this);
        }
		writer.print(")");
		return null;
	}
	@Override
	public Void visitBinOp(BinOp bo) {
		writer.print("BinOp(");
		bo.lhs.accept(this);
		writer.print(",");
		bo.op.accept(this);
		writer.print(",");
		bo.rhs.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitOp(Op o) {
		writer.print(o.toString());
		return null;
	}
	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		writer.print("ArrayAccessExpr(");
		aae.array.accept(this);
		writer.print(",");
		aae.index.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		writer.print("FieldAccessExpr(");
		fae.struct.accept(this);
		writer.print(",");
		writer.print(fae.name);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		writer.print("ValueAtExpr(");
		vae.e.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		writer.print("SizeOfExpr(");
		soe.type.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitTypecastExpr(TypecastExpr te) {
		writer.print("TypecastExpr(");
		te.type.accept(this);
		writer.print(",");
		te.e.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		writer.print("ExprStmt(");
		es.e.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitWhile(While w) {
		writer.print("While(");
		w.e.accept(this);
		writer.print(",");
		w.s.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitIf(If i) {
		writer.print("If(");
		i.e.accept(this);
		writer.print(",");
		i.s1.accept(this);
		if (i.s2 != null) {
			writer.print(",");
			i.s2.accept(this);
		}
		writer.print(")");
		return null;
	}
	@Override
	public Void visitAssign(Assign a) {
		writer.print("Assign(");
		a.lhs.accept(this);
		writer.print(",");
		a.rhs.accept(this);
		writer.print(")");
		return null;
	}
	@Override
	public Void visitReturn(Return r) {
		writer.print("Return(");
		if (r.e != null) r.e.accept(this);
		writer.print(")");
		return null;
	}

    
}
