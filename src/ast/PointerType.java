package ast;

public class PointerType implements Type {
    public final Type typeP;

    public PointerType(Type typeP) {
	    this.typeP = typeP;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitPointerType(this);
    }

	@Override
	public int getByteSize(Type t) {
		// TODO Auto-generated method stub
		return 4;
	}
	
}
