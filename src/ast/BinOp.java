package ast;


// BinOp      ::= Expr Op Expr
public class BinOp extends Expr {
	public Expr lhs;
	public final Op op;
	public Expr rhs;
	public int n; // checks how binop is grouped in ()
	
	public BinOp(Expr lhs, Op op, Expr rhs, int n) {
	    this.lhs = lhs;
	    this.op = op;
	    this.rhs = rhs;
	    this.n = n;
    }
	

    public <T> T accept(ASTVisitor<T> v) {
    	return v.visitBinOp(this);
        }

}