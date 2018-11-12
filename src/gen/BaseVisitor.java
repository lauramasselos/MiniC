package gen;

import java.io.PrintWriter;
import java.util.HashMap;

import ast.*;

public class BaseVisitor<T> implements GenVisitor<T> {
	
	public int binOpTag;
	public int slLabelTag;
	public int vdLabelTag;
	public int stdLabelTag;
	public boolean lhsOfAssign;
	public boolean inGlobalScope;
	public boolean funCallExpr;
	public int storingOffset = 0;
	public int varOffset;
//	public int offset = 0;
	public int counter = 0;
	
	
	public String label(String s) {
    	String str = s+counter;
		counter++;
    	return str;
    }
	
//	int charsize = 1;
	@SuppressWarnings("incomplete-switch")
	public int getByteSize(Type t) {
		// TODO Auto-generated method stub
		if (t instanceof BaseType) {
			switch((BaseType) t) {
				case INT: return 4;
				case CHAR: return 4;
			}
		}
		
		else if (t instanceof ArrayType) {
//			charsize = 4;				//word alignment
			int res =  ((ArrayType) t).n * getByteSize(((ArrayType) t).typeA);
//			charsize = 1;
			return res;
		}
		
		else if (t instanceof PointerType) return 4;
		
		else if (t instanceof StructType) {
			int size = 0;
//			charsize = 4;
			//System.out.println(((StructType) t).stdec.varDecls);
			for (VarDecl vd : ((StructType) t).stdec.varDecls) {
				//System.out.println(vd.toString());
				size += getByteSize(vd.type);
			}
//			charsize = 1;
			return size;
		}
		
		
		System.out.println("This shouldn't happen: BaseType");
		return 0;
	}
	
	
	
	PrintWriter writer;
	public BaseVisitor(PrintWriter writer) {
		this.writer = writer;
	}
	
	public BaseVisitor() {
		
	}
	
	@Override
	public T visitBaseType(BaseType bt) {
		return null;
	}

	@Override
	public T visitStructTypeDecl(StructTypeDecl st) {
		st.structType.accept(this);
		int i = 0;
		for (VarDecl vd : st.varDecls) {
			vd.accept(this);
			i += getByteSize(vd.type);
		}
		st.structType.sizeOfStruct = i;
		return null;
	}

	@Override
	public T visitBlock(Block b) {
		for (VarDecl vd : b.vds) {
			vd.accept(this);
		}
		for (Stmt s : b.stmts) {
			s.accept(this);
		}
		return null;
	}

	@Override
	public T visitFunDecl(FunDecl fd) {
		fd.type.accept(this);
		for (VarDecl vd : fd.params) {
			vd.accept(this);
		}
		fd.block.accept(this);
		return null;
	}

	@Override
	public T visitProgram(Program p) {
		
		for (StructTypeDecl st : p.structTypeDecls) {
			st.accept(this);
		} 
		inGlobalScope = true;
		for (VarDecl vd : p.varDecls) {
			vd.accept(this);
		} 
		inGlobalScope = false;
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}
		return null;
	}

	@Override
	public T visitVarDecl(VarDecl vd) {
		vd.type.accept(this);
		return null;
	}

	@Override
	public T visitVarExpr(VarExpr v) {
		return null;
	}

	@Override
	public T visitStructType(StructType st) {
		return null;
	}

	@Override
	public T visitPointerType(PointerType pt) {
		pt.typeP.accept(this);
		return null;
	}

	@Override
	public T visitArrayType(ArrayType at) {
		at.typeA.accept(this);
		return null;
	}

	@Override
	public T visitStrLiteral(StrLiteral sl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visitChrLiteral(ChrLiteral cl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visitIntLiteral(IntLiteral il) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visitFunCallExpr(FunCallExpr fce) {
		for (Expr e : fce.args) {
			e.accept(this);
		}
		return null;
	}

	@Override
	public T visitBinOp(BinOp bo) {
		bo.lhs.accept(this);
		bo.op.accept(this);
		bo.rhs.accept(this);
		return null;
	}

	@Override
	public T visitOp(Op o) {
		return null;
	}

	@Override
	public T visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.array.accept(this);
		aae.index.accept(this);
		return null;
	}

	@Override
	public T visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.struct.accept(this);
		return null;
	}

	@Override
	public T visitValueAtExpr(ValueAtExpr vae) {
		vae.e.accept(this);
		return null;
	}

	@Override
	public T visitSizeOfExpr(SizeOfExpr soe) {
		soe.typeSOE.accept(this);
		return null;
	}

	@Override
	public T visitTypecastExpr(TypecastExpr te) {
		te.typeC.accept(this);
		te.e.accept(this);
		return null;
	}

	@Override
	public T visitExprStmt(ExprStmt es) {
		es.e.accept(this);
		return null;
	}

	@Override
	public T visitWhile(While w) {
		w.s.accept(this);
		w.e.accept(this);
		return null;
	}

	@Override
	public T visitIf(If i) {
		i.e.accept(this);
		i.s1.accept(this);
		if (i.s2 != null) i.s2.accept(this);
		return null;
	}

	@Override
	public T visitAssign(Assign a) {
		a.lhs.accept(this);
		a.rhs.accept(this);
		return null;
	}

	@Override
	public T visitReturn(Return r) {
		if (r.e != null) r.e.accept(this);
		return null;
	}
	
	
}