package gen;

import java.io.PrintWriter;

import ast.*;

public class StrLiteralVisitor extends BaseVisitor<Void> {


	
	public StrLiteralVisitor(PrintWriter writer) {
		super(writer);
	}

	public Void visitStrLiteral(StrLiteral sl) {
		String slLabel = "string" + slLabelTag;
		slLabelTag++;
		strings.put(sl.str, slLabel);
		writer.println(slLabel + ": .asciiz \"" + sl.str + "\"");
		return null;
	}
	
	
}
