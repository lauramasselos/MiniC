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

	@Override
	public int getByteSize(Type t) {
		// TODO Auto-generated method stub
		if (t instanceof ArrayType) {
			BaseType bt = (BaseType) ((ArrayType) t).typeA;
			int type = 0;
			switch(bt) {
				case INT: type = 4; break;
				case CHAR: type = 1; break;
				default: break;
			}
			int n = ((ArrayType) t).n;
			return n * type;
		}
		System.out.println("I shouldn't be here: ArrayType");
		return 0;
	}

}
