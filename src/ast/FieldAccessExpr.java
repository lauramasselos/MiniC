package ast;


public class FieldAccessExpr extends Expr {
    public final Expr struct;
    public final String name;

    public FieldAccessExpr(Expr struct, String name) {
	    this.struct = struct;
	    this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitFieldAccessExpr(this);
    }
}
