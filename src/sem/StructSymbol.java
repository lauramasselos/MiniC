package sem;

import ast.*;

public class StructSymbol extends Symbol {
	public StructTypeDecl st;
	public StructSymbol(StructTypeDecl st) {
		super(st.structType.name);
		this.st = st;
	}

}