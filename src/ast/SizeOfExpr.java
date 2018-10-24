package ast;

public class SizeOfExpr extends Expr {
    public final Type typeSOE;

    public SizeOfExpr(Type typeSOE) {
	    this.typeSOE = typeSOE;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitSizeOfExpr(this);
    }
}
