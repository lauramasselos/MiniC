package ast;
//VarDecl    ::= Type String
public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public int vdOffset;
    public boolean isParam;

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
	    vdOffset = 0;
	    isParam = false;
    }

     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
    }
}
