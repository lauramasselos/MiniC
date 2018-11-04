package gen;

import java.io.PrintWriter;

import ast.*;

public class GlobalVarDeclVisitor extends BaseVisitor<Void> {
	
	public GlobalVarDeclVisitor(PrintWriter writer) {
		super(writer);
	}

	public Void visitVarDecl(VarDecl vd) {
		if (!globalVarDecls.containsKey(vd)) {
			String vdLabel = "globalvar" + vdLabelTag;
			vdLabelTag++;
			globalVarDecls.put(vd, vdLabel);
			writer.println(vdLabel + ": .space " + vd.type.getByteSize(vd.type));
		}
		
		return null;
	}
	
	
}