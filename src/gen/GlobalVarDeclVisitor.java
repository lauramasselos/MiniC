package gen;

import java.io.PrintWriter;

import ast.*;

public class GlobalVarDeclVisitor extends BaseVisitor<Void> {
	
	public GlobalVarDeclVisitor(PrintWriter writer) {
		super(writer);
	}

	public Void visitVarDecl(VarDecl vd) {
		if (inGlobalScope) {
			if (!globalVarDecls.containsKey(vd)) {
				if (!(vd.type instanceof StructType)) {
					String vdLabel = "global_var_" + vd.varName + "_" + vdLabelTag;
					vdLabelTag++;
					globalVarDecls.put(vd, vdLabel);
//					if (vd.type == BaseType.CHAR) {
//						writer.println(".align 2");
//					}
					writer.println(vdLabel + ": .space " + getByteSize(vd.type));	
				}
				else {
//					for (VarDecl v : ((StructType) vd.type).stdec.varDecls) {
//						String vdLabel = "struct_" + vd.varName + "_var_" + v.varName + "_" + vdLabelTag;
//						vdLabelTag++;
//						globalVarDecls.put(v, vdLabel);
////						if (v.type == BaseType.CHAR) {
////							writer.println(".align 2");
////						}
//						writer.println(vdLabel + ": .space " + getByteSize(v.type));
//					}
					String vdLabel = "struct_"+vd.varName+"_"+vdLabelTag;
					vdLabelTag++;
					globalVarDecls.put(vd, vdLabel);
						writer.println(vdLabel + ": .space " + getByteSize(vd.type));

				}
	
			}
		}
		return null;
	}
	

//				if ((vd.type instanceof StructType)) {
//					//allocate space for entire struct in data
//					// offset for each field (new method)
//
//					String vdLabel = "struct_"+vd.varName+"_"+vdLabelTag;
//					vdLabelTag++;
//					globalVarDecls.put(vd, vdLabel);
//						writer.println(vdLabel + ": .space " + getByteSize(vd.type));
//					}	
	
	
}