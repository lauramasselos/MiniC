package ast;
//Stmt       ::= Block | While | If | Assign | Return | ExprStmt
public abstract class Stmt implements ASTNode {
	public Type type;
    public abstract <T> T accept(ASTVisitor<T> v);
}
