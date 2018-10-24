package ast;

public class TypecastExpr extends Expr {
    public final Type typeC;
    public final Expr e;

    public TypecastExpr(Type typeC, Expr e) {
	    this.typeC = typeC;
	    this.e = e;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitTypecastExpr(this);
    }
}
