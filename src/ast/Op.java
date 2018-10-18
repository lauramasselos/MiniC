package ast;

public enum Op {
    ADD, SUB, MUL, DIV, MOD, GT, LT, GE, LE, NE, EQ, AND, OR;

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitOp(this);
    }
}
