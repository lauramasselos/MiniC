package sem;

import java.util.ArrayList;

public class SemanticAnalyzer {
	
	public int analyze(ast.Program prog) {
		// List of visitors
		int errors = 0;

		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor());
			add(new TypeCheckVisitor());
		}};
		// Error accumulators
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		
		return errors;
	}
}
