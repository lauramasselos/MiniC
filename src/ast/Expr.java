package ast;
// Expr       ::= IntLiteral | StrLiteral | ChrLiteral | VarExpr | FunCallExpr | BinOp | ArrayAccessExpr | FieldAccessExpr | ValueAtExpr | SizeOfExpr | TypecastExpr

public abstract class Expr implements ASTNode {
	public Type type; public boolean isParam;
    public abstract <T> T accept(ASTVisitor<T> v);
}
