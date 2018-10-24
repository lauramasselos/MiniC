package sem;

import ast.*;

public class FunSymbol extends Symbol {
	public FunDecl fd;
	public FunSymbol(FunDecl fd) {
		super(fd.name);
		this.fd = fd;
	}

}
