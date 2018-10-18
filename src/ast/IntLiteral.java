package ast;

public class IntLiteral extends Expr {
    public final int n;
    
    public IntLiteral(int n){
	this.n = n;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitIntLiteral(this);
    }
}