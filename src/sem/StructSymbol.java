package sem;

import ast.*;

public class StructSymbol extends Symbol {
	public StructTypeDecl stdec;
	public StructSymbol(StructTypeDecl stdec) {
		super(stdec.structType.name);
		this.stdec = stdec;
	}

}//hello