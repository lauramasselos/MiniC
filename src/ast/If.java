package ast;

public class If extends Stmt {
	public final Expr e;
    public final Stmt s1, s2;
    
    public If(Expr e, Stmt s1, Stmt s2) {
    	this.e = e;
    	this.s1 = s1;
    	this.s2 = s2;
    }


    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitIf(this);
    }
}
