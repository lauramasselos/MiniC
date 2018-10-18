package ast;

public class ArrayType implements Type {
	public final Type type;
	public final int n;
	
	public ArrayType(Type type, int n) {
		this.type = type;
		this.n = n;
	}

	public <T> T accept(ASTVisitor<T> v) {
		return v.visitArrayType(this);
	}

}
