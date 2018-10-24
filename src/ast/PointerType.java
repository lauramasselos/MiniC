package ast;

public class PointerType implements Type {
    public final Type typeP;

    public PointerType(Type typeP) {
	    this.typeP = typeP;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitPointerType(this);
    }
}
