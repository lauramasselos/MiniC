package ast;

public class While extends Stmt {
    public final Expr e;
    public final Stmt s;
    
    public While(Expr e, Stmt s) {
    	this.e = e;
    	this.s = s;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitWhile(this);
    }
}
