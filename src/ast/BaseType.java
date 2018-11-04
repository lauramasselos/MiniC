package ast;

public enum BaseType implements Type {
    INT, CHAR, VOID;
	

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }

	@Override
	public int getByteSize(Type t) {
		// TODO Auto-generated method stub
		if (t instanceof BaseType) {
			switch((BaseType) t) {
				case INT: return 4;
				case CHAR: return 1;
				default: break;
			}
		}
		System.out.println("This shouldn't happen: BaseType");
		return 0;
	}
}
