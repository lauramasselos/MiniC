package sem;

import ast.*;
import java.util.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

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
		Type blockT = null;
		for (VarDecl v : b.vds) v.accept(this);
		for (Stmt s : b.stmts) {
			Type t = s.accept(this);
			if (!(s instanceof Assign)&& !(s instanceof ExprStmt)){
			if (t != null) {
				if (blockT == null){
					blockT = t;
				}
			}
			} // TODO doesn't detect mismatch
		}
		return blockT;
	}

	@Override
	public Type visitFunDecl(FunDecl fd) {
		Type blockT = fd.block.accept(this);
		if (blockT == null && fd.name.equals("main")) blockT = fd.type;
		if (blockT == null && !fd.name.equals("main")) blockT = BaseType.VOID;
		Type fdT = fd.type;
		if (!equalTypes(blockT, fdT)) {
//			System.out.println("\n" + fd.name);
//			System.out.println(fdT);
//			System.out.println(blockT);
			error("FunDecl type does not match Return type");
		}
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
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
		List<Expr> argts = fce.args; 
		//System.out.println(fce.fd);
		List<VarDecl> paramts = fce.fd.params; // null pointer exception
	
		// check correct number of args in funCall  
		  if (fce.args.size() != fce.fd.params.size()) {
		    error("Incorrect number of arguments: FUNCALLEXPR");
		    //return new ErrorType("FunCallExpr 1");
		  }
		 // check each arg type matches vd type 
		  for (int i = 0; i < argts.size(); i++) {
			    
		    Expr arg = argts.get(i);
		    VarDecl vd = paramts.get(i);
		   
		    Type argT = arg.accept(this);
		    Type vdT = vd.type;

		    if (!equalTypes(argT, vdT)) {
		    	//System.out.println("\n" + argT);
		    	//System.out.println(vdT);
		      error("Argument types and variable types don't match: FUNCALLEXPR " + fce.name + " " + fce.args.toString());
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
		return pt;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
		return at;
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
			if ((lhsT != BaseType.VOID) && !(lhsT instanceof StructType) && !(lhsT instanceof ArrayType) && equalTypes(lhsT, rhsT)) {
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
		Type structT = fae.struct.accept(this);
		Type t = null;
		if (!(structT instanceof StructType)) error("Incorrect type expression: FIELDACCESSEXPR" + structT.toString());
		else {
			StructTypeDecl s = ((StructType) structT).stdec;
			for (VarDecl v : s.varDecls) {
				if (v.varName.equals(fae.name)) {
					t = v.type; break;
				}
			}
			if (t==null) error("Field doesn't exist in StructTypeDecl");
		}
		return t;
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
			te.type = new PointerType(((PointerType) te.typeC).typeP);
			return te.type;
		}
		
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		es.e.accept(this); // try returning this
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		// TODO Auto-generated method stub
		Type expT = w.e.accept(this);
		Type stT = w.s.accept(this);
		if (expT != BaseType.INT) error("Not an INT expression: WHILE");
		w.type = stT;
		return w.type;
	}

	@Override
	public Type visitIf(If i) {
		Type expT = i.e.accept(this);
		Type s1T = i.s1.accept(this); //System.out.println(s1T);
		Type s2T = null;
		if (i.s2 != null) s2T = i.s2.accept(this); // not picking up this statement
		//System.out.println(s2T);
		if (expT != BaseType.INT) error ("Not an INT expression: IF");
		if (i.s2 != null && equalTypes(s1T, s2T)) {
			i.type = s1T;
		}
		//System.out.println("IF type is:" + i.type);
		return i.type;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type lhsT = a.lhs.accept(this);
		Type rhsT = a.rhs.accept(this);
		if (!(lhsT instanceof ArrayType) && !(lhsT == BaseType.VOID) && equalTypes(lhsT, rhsT)) {
			return lhsT;
		}
		else error("Incorrect type expression: ASSIGN");
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		if (r.e.accept(this) == null) r.type = BaseType.VOID;
		else r.type = r.e.accept(this);
		//System.out.println("RETURNS: " + r.type);
		return r.type;
	}

	// To be completed...
	public boolean equalTypes(Type type1, Type type2) {
		if (type1 instanceof BaseType && type2 instanceof BaseType) {
			return type1 == type2;
		}
		else if (type1 instanceof StructType && type2 instanceof StructType) {
			String name1 = ((StructType)type1).name;
			String name2 = ((StructType)type2).name;
			return name1.equals(name2);
		}
		else if (type1 instanceof PointerType && type2 instanceof PointerType) {
			Type t1 = ((PointerType)type1).typeP;
			Type t2 = ((PointerType)type2).typeP;
			return equalTypes(t1, t2);
		}
		else if(type1 instanceof ArrayType && type2 instanceof ArrayType) {
			Type t1 = ((ArrayType)type1).typeA;
			Type t2 = ((ArrayType)type2).typeA;
			return equalTypes(t1, t2);
		}
		
		return false;
	}

}
