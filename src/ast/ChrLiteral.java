package ast;

public class ChrLiteral extends Expr {
    public final char c;
    
    public ChrLiteral(char c) {
    	this.c = c;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitChrLiteral(this);
    }
}