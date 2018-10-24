package sem;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {
// create ErrorType implements Type class; return that instead of nulls
	@Override
	public Type visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl fd) {
		// To be completed...
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		// To be completed...
		if (vd.type == BaseType.VOID) error("VarDecl type is VOID");
		return null;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		// To be completed...
		v.type = v.vd.type;
		return v.vd.type;
	}

	@Override
	public Type visitStructType(StructType st) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitPointerType(PointerType pt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitStrLiteral(StrLiteral sl) {
		// TODO Auto-generated method stub
		return new ArrayType(BaseType.CHAR, sl.str.length()+1);
	}

	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		// TODO Auto-generated method stub
		return BaseType.CHAR;
	}

	@Override
	public Type visitIntLiteral(IntLiteral il) {
		// TODO Auto-generated method stub
		return BaseType.INT;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
		// TODO Auto-generated method stub
		
		return null;
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
		// TODO Auto-generated method stub
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
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		Type expT = vae.e.accept(this);
		if (expT instanceof PointerType) {
			vae.type = expT.accept(this);
			return vae.type;
		}
		else error("Incorrect type expression: VALUEATEXPR");
		return null;
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
		else if (expT instanceof ArrayType) {
			Type t = expT.accept(this);
			te.type = new PointerType(t);
			return te.type;
		} // check exp below
		else if (expT instanceof PointerType) {
			te.type = new PointerType(((PointerType) te.typeC).type);
		}
		
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitIf(If i) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	// To be completed...


}
