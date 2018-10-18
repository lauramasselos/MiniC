package ast;

public class TypecastExpr extends Expr {
    public final Type type;
    public final Expr e;

    public TypecastExpr(Type type, Expr e) {
	    this.type = type;
	    this.e = e;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitTypecastExpr(this);
    }
}
