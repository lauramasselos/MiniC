package ast;

public class ExprStmt extends Stmt {
	public final Expr e;
	
	public ExprStmt(Expr e) {
		this.e = e;
	}

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitExprStmt(this);
    }
}
