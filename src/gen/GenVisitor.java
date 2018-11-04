package gen;

import java.util.HashMap;

public interface GenVisitor<T> extends ast.ASTVisitor<T> {

    public HashMap<String, String> strings = new HashMap<>();


}