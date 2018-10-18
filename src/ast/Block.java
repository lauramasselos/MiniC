package ast;

import java.util.List;

//Block      ::= VarDecl* Stmt*
public class Block extends Stmt {
    public final List<VarDecl> vds;
    public final List<Stmt> stmts;
    
    public Block(List<VarDecl> vds, List<Stmt> stmts) {
	    this.vds = vds;
	    this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
