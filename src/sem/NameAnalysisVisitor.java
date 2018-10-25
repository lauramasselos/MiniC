package sem;

import java.util.*;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {
	
	Scope scope = new Scope();
	
	@Override
	public Void visitProgram(Program p) {
		// To be completed...
		List<VarDecl> vds1 = new LinkedList<>();
		List<VarDecl> vds2 = new LinkedList<>();
		List<VarDecl> vds3 = new LinkedList<>();
		List<VarDecl> vds4 = new LinkedList<>();
		List<VarDecl> vdsEMPTY = new LinkedList<>();
		
		Type type = BaseType.CHAR; PointerType ptype = new PointerType(type);
		
		String varName = "s"; VarDecl vd1 = new VarDecl(ptype, varName); vds1.add(vd1); // print_s
		varName = "i"; VarDecl vd2 = new VarDecl(BaseType.INT, varName); vds2.add(vd2);// print_i
		varName = "c"; VarDecl vd3 = new VarDecl(BaseType.CHAR, varName); vds3.add(vd3);// print_c 
		varName = "size"; VarDecl vd4 = new VarDecl(BaseType.INT, varName);vds4.add(vd4); // mcalloc
		
		
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_s", vds1, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_i", vds2, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.VOID, "print_c", vds3, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.CHAR, "read_c", vdsEMPTY, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(BaseType.INT, "read_i", vdsEMPTY, new Block(null, null))));
		scope.put(new FunSymbol(new FunDecl(new PointerType(BaseType.VOID), "mcalloc", vds4, new Block(null, null))));

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
		if (s != null) error("STRUCTURE ALREADY DECLARED");
		else scope.put(new StructSymbol(sts));
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (VarDecl vd : sts.varDecls) {
			vd.accept(this);
		}
		scope = oldScope;
		return null;
	}

	private boolean inFunDecl = false;
	// figure out when to change inFunDecl
	@Override
	public Void visitFunDecl(FunDecl fd) {
		Symbol s = scope.lookupCurrent(fd.name);
		if (s!= null) error("FUNCTION ALREADY DECLARED");
		else scope.put(new FunSymbol(fd));
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		inFunDecl = true;
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
		if (s != null) {
			System.out.println(vd.varName);
			error("VARIABLE ALREADY DECLARED");
		}
		else scope.put(new VarSymbol(vd));
		return null;
	}


	@Override
	public Void visitVarExpr(VarExpr v) {
		Symbol vs = scope.lookup(v.name);
		if (vs == null) error("VARIABLE NOT DECLARED");
		else if (!vs.isVar()) error("NOT A VARIABLE");
		else v.vd = ((VarSymbol) vs).vd;
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
		
		if (!inFunDecl) scope = new Scope(oldScope);
		else inFunDecl = false;
		
		for (VarDecl v : b.vds) v.accept(this);
		for (Stmt s : b.stmts) s.accept(this);
		
		scope = oldScope;
		
		return null;
	}
	
	@Override
	public Void visitStructType(StructType st) {
		Symbol sts = scope.lookup(st.name);
		if (sts == null) error("STRUCTTYPE NOT DECLARED");
		else if (!sts.isStruct()) error("NOT A STRUCTTYPE");
		else st.stdec = ((StructSymbol) sts).stdec;
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
		else {
			fce.fd = ((FunSymbol) fs).fd;
			fs.name = ((FunSymbol) fs).fd.name;
		}
		for (Expr e : fce.args) {
			e.accept(this);
		}
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
		//scope = oldScope;
		return null;
	}

	@Override
	public Void visitIf(If i) {
		i.e.accept(this);

		i.s1.accept(this);
		if (i.s2 != null) i.s2.accept(this);
		//scope = oldScope;
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
