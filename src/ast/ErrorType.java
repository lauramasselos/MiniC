package ast;

public class ErrorType implements Type {
	public final String error;
	
	public ErrorType(String error) {
		this.error = error;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		// TODO Auto-generated method stub
		return null;
	}
}
