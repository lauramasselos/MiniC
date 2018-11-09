package ast;

//StructType  ::= String
public class StructType implements Type {
	public String name;
	public StructTypeDecl stdec;
	public int sizeOfStruct;
    
    public StructType(String name) {
    	this.name = name;
    	this.sizeOfStruct = 0;
    	
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

}