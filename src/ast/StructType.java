package ast;

//StructType  ::= String
public class StructType implements Type {
	public String name;
	public StructTypeDecl stdec;
    
    public StructType(String name) {
    	this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

	@Override
	public int getByteSize(Type t) {
		// TODO Auto-generated method stub
		if (t instanceof StructType) {
			int count = 0;
			for (VarDecl vd : ((StructType) t).stdec.varDecls) {
				count += vd.type.getByteSize(vd.type);
			}
			return count;
		}
		System.out.println("This shouldn't happen [StructType]");
		return 0;
	}

}