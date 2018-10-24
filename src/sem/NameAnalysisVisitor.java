package sem;

import java.util.*;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {
	
	Scope scope = new Scope();
	
	@Override
	public Void visitProgram(Program p) {
		// To be completed...
		List<VarDecl> vds = new LinkedList<>();
		Type type = BaseType.CHAR; String varName = "s";
		PointerType ptype = new PointerType(type);
		VarDecl vd1 = new VarDecl(ptype, varName); vds.add(vd1);
		
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_s", vds, new Block(null, null))));
		
		vds.remove(0); varName = "i";
		VarDecl vd2 = new VarDecl(BaseType.INT, varName); vds.add(vd2);
		
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_i", vds, new Block(null, null))));
		
		vds.remove(0); varName = "c";
		VarDecl vd3 = new VarDecl(BaseType.CHAR, varName); vds.add(vd3);
		
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_c", vds, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.CHAR, "read_c", null, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.INT, "read_i", null, new Block(null, null))));
		
		vds.remove(0); varName = "size";
		VarDecl vd4 = new VarDecl(BaseType.INT, varName); vds.add(vd4);
		
		scope.put(new FunSymbol(new FunDecl(new PointerType(BaseType.VOID), "mcalloc", vds, new Block(null, null))));

		for (StructTypeDecl s : p.structTypeDecls) {
			s.accept(this);
		}
		for (VarDecl v : p.varDecls) {
			v.accept(this);
		}
		for (FunDecl f : p.funDecls) {
			f.accept(this);
		}
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		Symbol s = scope.lookupCurrent(sts.structType.name);
		if (s != null) error("error");
		else scope.put(new StructSymbol(sts));
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (VarDecl vd : sts.varDecls) {
			vd.accept(this);
		}
		scope = oldScope;
		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl fd) {
		Symbol s = scope.lookupCurrent(fd.name);
		if (s!= null) error("FUNCTION ALREADY DECLARED");
		else scope.put(new FunSymbol(fd));
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (VarDecl vd : fd.params) {
			vd.accept(this);
		}
		fd.block.accept(this);
		scope = oldScope;
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		Symbol s = scope.lookupCurrent(vd.varName);
		if (s != null) error("VARIABLE ALREADY DECLARED");
		else scope.put(new VarSymbol(vd));
		return null;
	}


	@Override
	public Void visitVarExpr(VarExpr v) {
		// To be completed...
		Symbol vs = scope.lookup(v.name);
		if (vs == null) error("VARIABLE NOT DECLARED");
		else if (!vs.isVar()) error("NOT A VARIABLE");
		else v.vd = ((VarSymbol) vs).vd;
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (VarDecl vd : b.vds) {
			vd.accept(this);
		}
		for (Stmt s : b.stmts) {
			s.accept(this);
		}
		scope = oldScope;
		return null;
	}
	
	@Override
	public Void visitStructType(StructType st) {
		Symbol sts = scope.lookup(st.name);
		if (sts == null) error("STRUCTTYPE NOT DECLARED");
		else if (!sts.isStruct()) error("NOT A STRUCTTYPE");
		else st.name = ((StructSymbol) sts).name;
		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral sl) {
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral cl) {
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral il) {
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
		// TODO Auto-generated method stub
		Symbol fs = scope.lookup(fce.name);
		if (fs==null) error("FUNCTION NOT DECLARED");
		else if (!fs.isFun()) error("NOT A FUNCTION");
		else fs.name = ((FunSymbol) fs).fd.name;
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (Expr e : fce.args) {
			e.accept(this);
		}
		scope = oldScope;
		return null;
	}

	@Override
	public Void visitBinOp(BinOp bo) {
		bo.lhs.accept(this);
		bo.rhs.accept(this);
		return null;
	}

	@Override
	public Void visitOp(Op o) {
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.array.accept(this);
		aae.index.accept(this);
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.struct.accept(this);
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		vae.e.accept(this);
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		soe.typeSOE.accept(this);
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr te) {
		te.typeC.accept(this);
		te.e.accept(this);
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		es.e.accept(this);
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		w.e.accept(this);
		w.s.accept(this);
		return null;
	}

	@Override
	public Void visitIf(If i) {
		i.e.accept(this);
		i.s1.accept(this);
		if (i.s2 != null) i.s2.accept(this);
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		a.lhs.accept(this);
		a.rhs.accept(this);
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		if (r.e != null) r.e.accept(this);
		return null;
	}
	
	@Override
	public Void visitBaseType(BaseType bt) {
		return null;
	}

	// To be completed...


}
