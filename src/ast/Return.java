package ast;

public class Return extends Stmt {
	public final Expr e;
	
	public Return(Expr e) {
		this.e = e;
	}

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitReturn(this);
    }
}
