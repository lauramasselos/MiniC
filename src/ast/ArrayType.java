package ast;

public class ArrayType implements Type {
	public final Type typeA;
	public final int n;
	
	public ArrayType(Type typeA, int n) {
		this.typeA = typeA;
		this.n = n;
	}

	public <T> T accept(ASTVisitor<T> v) {
		return v.visitArrayType(this);
	}

}
