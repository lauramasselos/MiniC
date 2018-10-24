package parser;

import ast.*;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;
    
    private int par_count = 0;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


/*	LAST COMMIT ERRORS:			(October 22)
 * 
 *AST ERRORS (all expected 0, returned 224)
 * unary_operator_2
 * return_1
 */
 

    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds = parseStructDecls();
        List<VarDecl> vds = parseVarDecls();
        List<FunDecl> fds = parseFunDecls();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

// structdecl ::= "struct" IDENT "{" (vardecl)+ "}" ";"
    // StructTypeDecl ::= StructType VarDecl*
    private List<StructTypeDecl> parseStructDecls() {
        if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
        	nextToken();
        	String name = expect(TokenClass.IDENTIFIER).data; 	// structtype name
        	StructType st = new StructType(name);
        	expect(TokenClass.LBRA);
        	List<VarDecl> vds =  parseVarDecls(); 				// vardecls for particular structtype
        	expect(TokenClass.RBRA);
        	expect(TokenClass.SC);
        	StructTypeDecl std = new StructTypeDecl(st, vds);  	// store vardecls in structtype
        	List<StructTypeDecl> stds = parseStructDecls();  	// parse all other structdecls
        	stds.add(0, std); 									// add current structdecl to head of list
        	return stds;										// return structdecls
        } 
        else {
        	return new LinkedList<StructTypeDecl>();
        }

    }
//  vardecl ::= type IDENT ";"| type IDENT "[" INT_LITERAL "]" ";"
//VarDecl    ::= Type String
//ArrayType   ::= Type int 
    private List<VarDecl> parseVarDecls() {
    	if ((accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.ASTERIX && (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.IDENTIFIER && (lookAhead(2).tokenClass == TokenClass.SC || lookAhead(2).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.ASTERIX && (lookAhead(4).tokenClass == TokenClass.SC || lookAhead(4).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR))) {
    		
    		Type type = parseTypes();
    		
            if (accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass == TokenClass.SC) {
            	String varName = expect(TokenClass.IDENTIFIER).data;
            	nextToken();
            	VarDecl vd = new VarDecl(type, varName);
            	List<VarDecl> vds = parseVarDecls();
            	vds.add(0,vd);
            	return vds;
           }
            else if (accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass == TokenClass.LSBR) {
            	String varName = expect(TokenClass.IDENTIFIER).data;
            	nextToken();
            	int n = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
            	expect(TokenClass.RSBR);
            	expect(TokenClass.SC);
            	Type t = new ArrayType(type, n);
            	VarDecl vd = new VarDecl(t, varName);
            	List<VarDecl> vds = parseVarDecls();
            	vds.add(0, vd);
            	return vds;
            }
            else {
            	return new LinkedList<VarDecl>();
            }
    	}
    	
    	else {
    		return new LinkedList<VarDecl>();
    	}

    }
    // FunDecl  ::= Type String VarDecl* Block
// fundecl ::= type IDENT "(" params ")" block
    private List<FunDecl> parseFunDecls() {
    	if ((accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.ASTERIX && lookAhead(3).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.IDENTIFIER && lookAhead(2).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.ASTERIX && lookAhead(4).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && lookAhead(3).tokenClass == TokenClass.LPAR)) {
    		List<VarDecl> params = new LinkedList<>();
    		Type type = parseTypes();
    		String name = expect(TokenClass.IDENTIFIER).data;
    		expect(TokenClass.LPAR);
    		if (!accept(TokenClass.RPAR)) {
    			params = parseParams();
    		}
    		expect(TokenClass.RPAR);
    		Block block = parseBlock();
    		FunDecl fd = new FunDecl(type, name, params, block);
    		List<FunDecl> fds = parseFunDecls();
    		fds.add(0, fd);
    		return fds;
    	}
    	else {
    		return new LinkedList<FunDecl>();
    	}
    }
  
    
 // types ::= ("int" | "void" | "char" | "struct" IDENT) ["*"]
    //Type        ::= BaseType | PointerType | StructType | ArrayType
    private Type parseTypes() {
    	if (accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR)) {
    		Type type;
    		switch(expect(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR).tokenClass) {
    			case INT: type = BaseType.INT; break;
    			case VOID: type = BaseType.VOID; break;
    			default: type = BaseType.CHAR;
    		}
    		if (accept(TokenClass.ASTERIX)) {
    			nextToken();
    			return new PointerType(type);
    		}
    		else {
    			return type;
    		}
    	}
    	
    	else if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass != TokenClass.LBRA) {
    		nextToken();
    		String name = expect(TokenClass.IDENTIFIER).data;
    		if (accept(TokenClass.ASTERIX)) {
    			nextToken();
    			return new PointerType(new StructType(name));
    		}
    		else {
    			return new StructType(name);
    		}
    	}
    	else {
    		error(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT);
    		return null;
    	}
    }  
 // params ::= [ type IDENT ("," type IDENT)* ]  

    private List<VarDecl> parseParams() {
    	List<VarDecl> params = new LinkedList<>();
    	List<VarDecl> otherParams = new LinkedList<>();
    	Type type = parseTypes();
    	String varName = expect(TokenClass.IDENTIFIER).data;
    	VarDecl param = new VarDecl(type, varName);
    	params.add(0, param);
    	if (accept(TokenClass.COMMA)) {
    		otherParams = parseParamsRep();
    		params.addAll(otherParams);
    	}
    	return params;
    }
    
    private List<VarDecl> parseParamsRep() {
    	if (accept(TokenClass.COMMA)) {
    		List<VarDecl> vds = new LinkedList<>(); // list of vds initialized
    		List<VarDecl> othervds = new LinkedList<>();
    		nextToken();
    		Type type = parseTypes();
    		String varName = expect(TokenClass.IDENTIFIER).data;
    		VarDecl newParam = new VarDecl(type, varName); 	// new vardecl
    		vds.add(newParam); 								// first vardecl added to list of vds
    		if (accept(TokenClass.COMMA)) {
    			othervds = parseParamsRep();				// tail of list
    			vds.addAll(othervds);						// append tail to head of list
    		}
    		return vds;
    	}
    	else {
    		return new LinkedList<VarDecl>();
    	}
    }
//     stmt       ::= block
//             | "while" "(" exp ")" stmt              # while loop
//             | "if" "(" exp ")" stmt ["else" stmt]   # if then else
//             | "return" [exp] ";"                    # return
//             | exp "=" exp ";"                      # assignment
//             | exp ";"                               # expression statement, e.g. a function call
// first set = “{“, “while”, “if”, “return”, “(“, IDENT, INT_LIT, “-“, CHAR_LIT, STRING_LIT, “*”, “sizeof”}

    private Stmt parseStmnt() {
    	if (accept(TokenClass.LBRA)) {
    		Block block = parseBlock();
    		return block;
    	}
    	else if (accept(TokenClass.WHILE)) { 
    		nextToken();
    		expect(TokenClass.LPAR);
    		Expr e = parseExp();
    		expect(TokenClass.RPAR);
    		Stmt s = parseStmnt();
    		return new While(e, s);
    	}
    	else if (accept(TokenClass.IF)) {
    		Stmt s2 = null;
    		nextToken();
    		expect(TokenClass.LPAR);
    		Expr e = parseExp();
    		expect(TokenClass.RPAR);
    		Stmt s1 = parseStmnt();
    		if (accept(TokenClass.ELSE)) {
    			nextToken();
    			s2 = parseStmnt();
    		}
    		return new If(e, s1, s2);
    	}
    	else if (accept(TokenClass.RETURN)) {
    		Expr e = null;
    		nextToken();
    		if (accept(TokenClass.SC)) {
    			nextToken();
    			return new Return(e);
    		}
    		else if (accept(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)){
    			e = parseExp();
    			expect(TokenClass.SC);
    			return new Return(e);
    		}
    		else error(TokenClass.SC, TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF);
    	} 
    	else if (accept(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)) {
    		Expr rhs = null;
    		Expr lhs = parseExp();
    		if (accept(TokenClass.SC)) {
    			nextToken();
    			return new ExprStmt(lhs);
    		}
    		else if (accept(TokenClass.ASSIGN)) {
    			nextToken();
    			rhs = parseExp();
    			expect(TokenClass.SC);
    			return new Assign(lhs, rhs);
    		}
    		else {
    			error(TokenClass.SC, TokenClass.ASSIGN);
    			return null;
    		}
    	}
		return null;
    }
    // block ::= "{" (vardecl)* (stmt)* "}"
    private Block parseBlock() {
    	List<VarDecl> vds = new LinkedList<>();
    	List<Stmt> stmts = new LinkedList<>();
    	if (accept(TokenClass.LBRA) && lookAhead(1).tokenClass == TokenClass.RBRA ) {
    		nextToken(); 
    		nextToken();
    	}
    	else if (accept(TokenClass.LBRA)) {
    		nextToken();
    		if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
    			vds = parseVarDecls();
    		}
    		while (accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN, TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)) {
    			Stmt stmt = parseStmnt();
    			stmts.add(stmt);
    		}
    		expect(TokenClass.RBRA);
    	}
    	else {
    		error(TokenClass.LBRA);
    	}
    	return new Block(vds, stmts);
    }
    // first = {“(“, IDENT, INT_LITERAL, “-“, CHAR_LITERAL, STRING_LITERAL, “*”, “sizeof”}
    private Expr parseExp() {
    	Expr e;
    	if (accept(TokenClass.LPAR) && lookAhead(1).tokenClass != TokenClass.INT  && lookAhead(1).tokenClass != TokenClass.CHAR && lookAhead(1).tokenClass != TokenClass.VOID && lookAhead(1).tokenClass != TokenClass.STRUCT) {
    		nextToken();
    		par_count++;
    		e = parseExp();
    		expect(TokenClass.RPAR);
    		par_count--;
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.IDENTIFIER)) {
    		if (lookAhead(1).tokenClass == TokenClass.LPAR) {
    			e = parseFunCall();
    			return parseOtherExp(e);
    		}
    		else {
    			String id = expect(TokenClass.IDENTIFIER).data;
    			e = new VarExpr(id);
    			return parseOtherExp(e);
    		}
    	}
    	else if (accept(TokenClass.INT_LITERAL)) {
    		int n = Integer.parseInt(expect(TokenClass.INT_LITERAL).data);
    		IntLiteral int_lit = new IntLiteral(n);
    		e = int_lit;
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.MINUS)) {
    		nextToken();
    		e = parseExp();
    		BinOp binOp = new BinOp(new IntLiteral(0), Op.SUB, e, -1);
    		return parseOtherExp(binOp);
    	}
    	else if (accept(TokenClass.CHAR_LITERAL)) {
    		String str = expect(TokenClass.CHAR_LITERAL).data;
    		ChrLiteral chr_lit = new ChrLiteral(str.charAt(0));
    		e = chr_lit;
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.STRING_LITERAL)) {
    		String str = expect(TokenClass.STRING_LITERAL).data;
    		StrLiteral str_lit = new StrLiteral(str);
    		e = str_lit;
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.ASTERIX)) {
    		e = parseValueAt();
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.SIZEOF)) {
    		e = parseSizeOf();
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.LPAR) && (lookAhead(1).tokenClass == TokenClass.INT || lookAhead(1).tokenClass == TokenClass.CHAR || lookAhead(1).tokenClass == TokenClass.VOID || lookAhead(1).tokenClass == TokenClass.STRUCT)) {
    		e = parseTypeCast();
    		return parseOtherExp(e);
    	}
    	else {
    		error(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF);
    		return new StrLiteral("Something's gone horribly wrong!");
    	}
    }
    // add precedence rules here
    private Expr parseOtherExp(Expr lhs) {
    	if (accept(TokenClass.GT, TokenClass.LT, TokenClass.GE, TokenClass.LE, TokenClass.NE, TokenClass.EQ, TokenClass.PLUS, TokenClass.MINUS, TokenClass.ASTERIX, TokenClass.DIV, TokenClass.REM, TokenClass.OR, TokenClass.AND)) {
    		Op op;
    		TokenClass operation = token.tokenClass;
    		nextToken();
    		Expr rhs = parseExp();
    		switch(operation) {
				case ASTERIX: op = Op.MUL; break;
				case DIV: op = Op.DIV; break;
				case REM: op = Op.MOD; break;
				case PLUS: op = Op.ADD; break;
				case MINUS: op = Op.SUB; break;
				case GE: op = Op.GE; break;
				case LE: op = Op.LE; break;
				case GT: op = Op.GT; break;
				case LT: op = Op.LT; break;
				case EQ: op = Op.EQ; break;
				case NE: op = Op.NE; break;
				case AND: op = Op.AND; break;
				default: op = Op.OR;
				
		}
    		BinOp e = new BinOp(lhs, op, rhs, par_count);
    		return parseOtherExp(fixPrecedence(e));
    	}
    	else if (accept(TokenClass.LSBR)) {
    		Expr e = parseArrayAccess(lhs);
    		return parseOtherExp(e);
    	}
    	else if (accept(TokenClass.DOT)) {
    		Expr e = parseFieldAccess(lhs);
    		return parseOtherExp(e);
    	} // no error here, since parseOtherExp() can be empty!
    	else return lhs;
    }
    
    
    private Expr fixPrecedence(BinOp b) {
    	if (b.lhs instanceof BinOp) { 													// lhs is a BinOp, so it has its own lhs and rhs
    		BinOp lhs = (BinOp) b.lhs;
    		if (precedence(lhs.op) >= precedence(b.op) && (lhs.n == b.n)) { 								// if op in lhs is less binding than op in BinOp b
    			return fixPrecedence(new BinOp(lhs.lhs, lhs.op, new BinOp(lhs.rhs, b.op, b.rhs, lhs.n), lhs.n));
    		}
    	}
    	if (b.rhs instanceof BinOp) { 													// rhs is a BinOp, so it has its own lhs and rhs
    		BinOp rhs = (BinOp) b.rhs;
    		if (precedence(rhs.op) >= precedence(b.op) && (rhs.n == b.n)) { 								// if op in rhs is less binding than op in BinOp b
    			return fixPrecedence(new BinOp(new BinOp(b.lhs, b.op, rhs.lhs, rhs.n), rhs.op, rhs.rhs, rhs.n));
    		}
    	}
    	
    	return b;
    }
    
    private int precedence(Op op) {
    	int n = 0;
    	switch(op) {
    	case MUL: n = 3; break;
		case DIV: n = 3; break;
		case MOD: n = 3; break;
		case ADD: n = 4; break;
		case SUB: n = 4; break;
		case GE: n = 5; break;
		case LE: n = 5; break;
		case GT: n = 5; break;
		case LT: n = 5; break;
		case EQ: n = 6; break;
		case NE: n = 6; break;
		case AND: n = 7; break;
		case OR: n = 8; break;
    	}
    	return n;
    }
    
    
    
    
    
    private ArrayAccessExpr parseArrayAccess(Expr arr) {
    	if (accept(TokenClass.LSBR)) {
    		Expr ind;
    		nextToken();
    		ind = parseExp();
    		expect(TokenClass.RSBR);
    		return new ArrayAccessExpr(arr, ind);
    	}
    	return null;
    }
    
    private FieldAccessExpr parseFieldAccess(Expr struc) {
    	if (accept(TokenClass.DOT)) {
    		nextToken();
    		String field = expect(TokenClass.IDENTIFIER).data;
    		return new FieldAccessExpr(struc, field);
    	}
    	return null;
    }
    
    private ValueAtExpr parseValueAt() {
    	if (accept(TokenClass.ASTERIX)) {
    		nextToken();
    		Expr e = parseExp();
    		return new ValueAtExpr(e);
    	}
    	return null;
    }
    
    private SizeOfExpr parseSizeOf() {
    	if (accept(TokenClass.SIZEOF)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		Type type = parseTypes();
    		expect(TokenClass.RPAR);
    		return new SizeOfExpr(type);
    	}
    	return null;
    }
    
    private TypecastExpr parseTypeCast() {
    	if (accept(TokenClass.LPAR)) {
    		nextToken();
    		Type type = parseTypes();
    		expect(TokenClass.RPAR);
    		Expr e = parseExp();
    		return new TypecastExpr(type, e);
    	}
    	return null;
    }
  // FunCallExpr ::= Strint Expr*  
    private FunCallExpr parseFunCall() {
    	if (accept(TokenClass.IDENTIFIER)) {
    		String name = expect(TokenClass.IDENTIFIER).data;
    		List<Expr> exps = new LinkedList<>();
    		expect(TokenClass.LPAR);
    		
    		
    		if (accept(TokenClass.RPAR)) {
    			nextToken();
    		}
    		
    		
    		else {
    			Expr e = parseExp();
    			exps.add(e);
    			while (accept(TokenClass.COMMA)) {
    				nextToken();
    				e = parseExp();
    				exps.add(e);
    			}
    			expect(TokenClass.RPAR);
    		}
    		return new FunCallExpr(name, exps);
    	}
    	return null;
    }

}
