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

}