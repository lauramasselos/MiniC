package ast;

import java.util.List;

//StructTypeDecl ::= StructType VarDecl*
public class StructTypeDecl implements ASTNode {
	public final StructType structType;
    public final List<VarDecl> varDecls;
    
    public StructTypeDecl(StructType structType, List<VarDecl> varDecls) {
    	this.structType = structType;
    	this.varDecls = varDecls;
    }

    // to be completed

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
