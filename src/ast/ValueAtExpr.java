package ast;

public class ValueAtExpr extends Expr {
    public final Expr e;

    public ValueAtExpr(Expr e) {
	    this.e = e;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitValueAtExpr(this);
    }
}
