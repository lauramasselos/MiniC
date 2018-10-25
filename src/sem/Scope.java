package sem;

import java.util.*;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable = new HashMap<String, Symbol>();
	
	public Scope(Scope outer) { 
		this.outer = outer; 
	}
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		// To be completed...
		Symbol curSymbol = lookupCurrent(name);
		if (curSymbol != null) return curSymbol;
		if (outer == null) {
			return new NullSymbol();
		}
		return outer.lookup(name);
	}
	
	public Symbol lookupCurrent(String name) {
		// To be completed...
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
	
}
