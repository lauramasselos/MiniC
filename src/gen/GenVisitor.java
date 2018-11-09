package gen;

import ast.*;
import java.util.HashMap;
import java.util.LinkedList;

public interface GenVisitor<T> extends ast.ASTVisitor<T> {

    public HashMap<String, String> strings = new HashMap<>();
    public HashMap<VarDecl, String> globalVarDecls = new HashMap<>();
    public HashMap<StructTypeDecl, String> globalStructTypeDecls = new HashMap<>();  

}