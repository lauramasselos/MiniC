package sem;

import ast.*;
import java.util.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {
// create ErrorType implements Type class; return that instead of nulls
	@Override
	public Type visitBaseType(BaseType bt) {
		return bt;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		for (VarDecl v : st.varDecls) {
			v.accept(this);
		}
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		List<Stmt> statements = new LinkedList<Stmt>(b.stmts);
		Type stmtT = null;
		for (int i = 0; i < statements.size(); i++) {
			Stmt s = statements.get(i);
			Type t = s.accept(this);
			if (t != null) {
				if (stmtT == null) stmtT = t;
			}
			//if (s instanceof Return) b.type = s.type;
		}
		if (stmtT == null) stmtT = BaseType.VOID;
		return stmtT;
	}

	@Override
	public Type visitFunDecl(FunDecl fd) {
		Type blockT = fd.block.accept(this);
		Type fdT = fd.type;
		if (!blockT.equals(fdT)) error("FunDecl type does not match Return type");
		
		//fd.type == fd.block.accept(this);
		// To be completed...
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		/*for (StructTypeDecl s : p.structTypeDecls) {
			s.accept(this);
		}
		for (VarDecl v : p.varDecls) {
			v.accept(this);
		}
		for (FunDecl f : p.funDecls) {
			f.accept(this);
		}*/
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		if (vd.type == BaseType.VOID) error("VarDecl type is VOID");
		return null;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		v.type = v.vd.type;
		return v.vd.type;
	}
	
	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
		List<Expr> argts = new LinkedList<Expr>(fce.args); 
		List<VarDecl> paramts = new LinkedList<VarDecl>(fce.fd.params);
		// check correct number of args in funCall  
		  if (argts.size() != paramts.size()) {
		    error("Incorrect number of arguments: FUNCALLEXPR");
		    return null; 
		  }
		 // check each arg type matches vd type 
		  for (int i = 0; i < argts.size(); i++) {
		    Expr arg = argts.get(i);
		    VarDecl vd = paramts.get(i);
		    
		    Type argT = arg.accept(this);
		    Type vdT = vd.type;

		    if (!(argT.equals(vdT))) {
		      error("Argument types and variable types don't match: FUNCALLEXPR");
		    }
		  }
		  fce.type = fce.fd.type;
		  return fce.type;
	}

	@Override
	public Type visitStructType(StructType st) {
		return st;
	}

	@Override
	public Type visitPointerType(PointerType pt) {
		return pt.type;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
		return at.type;
	}

	@Override
	public Type visitStrLiteral(StrLiteral sl) {
		return new ArrayType(BaseType.CHAR, sl.str.length()+1);
	}

	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		return BaseType.CHAR;
	}

	@Override
	public Type visitIntLiteral(IntLiteral il) {
		return BaseType.INT;
	}

	@Override
	public Type visitBinOp(BinOp bo) {
		Type lhsT = bo.lhs.accept(this);
		Type rhsT = bo.rhs.accept(this);
		if (bo.op != Op.NE && bo.op != Op.EQ) {
			if (lhsT == BaseType.INT && rhsT == BaseType.INT) {
				bo.type = BaseType.INT;
				return BaseType.INT;
			}
			else error("Not an INT expression: BINOP");
		}
		
		else if (bo.op == Op.NE || bo.op == Op.EQ) {
			if ((lhsT != BaseType.VOID) && !(lhsT instanceof StructType) && !(lhsT instanceof ArrayType) && lhsT == rhsT) {
				bo.type = BaseType.INT;
				return bo.type;
			}
			else error("Incorrect type expression: BINOP");
		}
		
		return null;
	}

	@Override
	public Type visitOp(Op o) {
		return null;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
		// TODO Auto-generated method stub
		Type arrT = aae.array.accept(this);
		Type indT = aae.index.accept(this);
		if (indT == BaseType.INT && (arrT instanceof ArrayType || arrT instanceof PointerType)) {
			aae.type = arrT.accept(this);
			return aae.type;
		}
		else error("Incorrect type expression: ARRAYACCESSEXPR");
		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO Auto-generated method stub
		Type structT = fae.struct.accept(this);
		if (!(structT instanceof StructType)) error("Incorrect type expression: FIELDACCESSEXPR");
		else {
			//fae.type = fae.name;
		}
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		Type expT = vae.e.accept(this);
		if (!(expT instanceof PointerType)) error("Incorrect type expression: VALUEATEXPR");
		else vae.type = expT.accept(this);
		return vae.type;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		return BaseType.INT;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr te) {
		Type expT = te.typeC.accept(this);
		if (expT == BaseType.CHAR) {
			te.type = BaseType.INT;
			return te.type;
		}
		else if (expT instanceof ArrayType && te.typeC instanceof PointerType) {
			Type t = expT.accept(this);
			te.type = new PointerType(t);
			return te.type;
		} // check exp below
		else if (expT instanceof PointerType && te.typeC instanceof PointerType) {
			te.type = new PointerType(((PointerType) te.typeC).type);
		}
		
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		es.e.accept(this);
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		// TODO Auto-generated method stub
		Type expT = w.e.accept(this);
		if (expT != BaseType.INT) error("Not an INT expression: WHILE");
		else w.s.accept(this);
		return null;
	}

	@Override
	public Type visitIf(If i) {
		Type expT = i.e.accept(this);
		if (expT != BaseType.INT) error ("Not an INT expression: IF");
		else {
			i.s1.accept(this);
			if (i.s2 != null) i.s2.accept(this);
		}
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type lhsT = a.lhs.accept(this);
		Type rhsT = a.rhs.accept(this);
		if (!(lhsT instanceof ArrayType) && !(lhsT == BaseType.VOID) && lhsT==rhsT) {
			return null;
		}
		else error("Incorrect type expression: ASSIGN");
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		if (r.e == null) r.type = BaseType.VOID;
		else r.type = r.e.type;
		return null;
	}

	// To be completed...


}
