package gen;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ast.*;

public class StrLiteralVisitor extends BaseVisitor<Void> {


	
	public StrLiteralVisitor(PrintWriter writer) {
		super(writer);
	}

	public Void visitStrLiteral(StrLiteral sl) {
		String slLabel = "string" + slLabelTag;
		slLabelTag++;
		
		
		
		StringBuilder sb = new StringBuilder();
		char[] chars = sl.str.toCharArray();
		for (char ch : chars) {
			if (ch == '\n') sb.append("\\n");
			else if (ch == '\r') sb.append("\\r");
			else sb.append(ch);
		}
		String string = sb.toString();
		strings.put(sl.str, slLabel);
		writer.println(slLabel + ": .asciiz \"" + string + "\"");
		return null;
	}
	
	
}
