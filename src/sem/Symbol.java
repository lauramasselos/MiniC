package sem;

public abstract class Symbol {
	public String name;
	// helper methods to check if something is var/fun/struct
	public boolean isStruct() {
		if (this instanceof StructSymbol) return true;
		else return false;
	}
	public boolean isVar() {
		if (this instanceof VarSymbol) return true;
		else return false;
	}
	public boolean isFun() {
		if (this instanceof FunSymbol) return true;
		else return false;
	}
	
	public Symbol(String name) {
		this.name = name;
	}
}
