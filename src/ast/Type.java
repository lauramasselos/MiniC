package ast;

public interface Type extends ASTNode {
	public int getByteSize(Type t);

    public <T> T accept(ASTVisitor<T> v);

}
