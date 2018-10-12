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

<<<<<<< HEAD
/*	LAST COMMIT ERRORS:			(October 10)
 * 
 * struct_declaration (expected 0, returned 245)
 * variable_initialization (expected 245, returned 124)
 */
    private void parseProgram() {
=======

    
>>>>>>> 4b7cd8d310c7373cbaee99c0cc32d333151136b8
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
<<<<<<< HEAD
// structdecl ::= "struct" IDENT "{" (vardecl)+ "}" ";"
    private void parseStructDecls() {
        if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
        	nextToken();
        	expect(TokenClass.IDENTIFIER);
        	expect(TokenClass.LBRA);
        	parseVarDecls();
        	expect(TokenClass.RBRA);
        	expect(TokenClass.SC);
        	parseStructDecls();
        }
=======

    
>>>>>>> 4b7cd8d310c7373cbaee99c0cc32d333151136b8
    }
//  vardecl ::= type IDENT ";"| type IDENT "[" INT_LITERAL "]" ";"

<<<<<<< HEAD
    private void parseVarDecls() {
    	if ((accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.ASTERIX && (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.IDENTIFIER && (lookAhead(2).tokenClass == TokenClass.SC || lookAhead(2).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.ASTERIX && (lookAhead(4).tokenClass == TokenClass.SC || lookAhead(4).tokenClass == TokenClass.LSBR))
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR))) {
    		parseTypes();
            if (accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass == TokenClass.SC) {
            	nextToken();
            	nextToken();
            	parseVarDecls();
           }
            else if (accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass == TokenClass.LSBR) {
            	nextToken();
            	nextToken();
            	expect(TokenClass.INT_LITERAL);
            	expect(TokenClass.RSBR);
            	expect(TokenClass.SC);
            	parseVarDecls();
            }
            else parseFunDecls();
    	}
    }
// fundecl ::= type IDENT "(" params ")" block
    private void parseFunDecls() {
    	if ((accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.ASTERIX && lookAhead(3).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID) && lookAhead(1).tokenClass == TokenClass.IDENTIFIER && lookAhead(2).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.ASTERIX && lookAhead(4).tokenClass == TokenClass.LPAR)
    	|| (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && lookAhead(3).tokenClass == TokenClass.LPAR)) {
    		parseTypes();
    		expect(TokenClass.IDENTIFIER);
    		expect(TokenClass.LPAR);
    		if (!accept(TokenClass.RPAR)) parseParams();
    		expect(TokenClass.RPAR);
    		parseBlock();
    		parseFunDecls();
    	}
    }
 // types ::= ("int" | "void" | "char" | "struct" IDENT) ["*"]
    private void parseTypes() {
    	if (accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR)) {
    		nextToken();
    		if (accept(TokenClass.ASTERIX)) nextToken();
    	}
    	else if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass != TokenClass.LBRA) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    		if (accept(TokenClass.ASTERIX)) nextToken();
    	}
    	else error(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT);
    }  
 // params ::= [ type IDENT ("," type IDENT)* ]   
    private void parseParams() {
    	parseTypes();
    	expect(TokenClass.IDENTIFIER);
    	if (accept(TokenClass.COMMA)) parseParamsRep();
    }
    
    private void parseParamsRep() {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		parseTypes();
    		expect(TokenClass.IDENTIFIER);
    		if (accept(TokenClass.COMMA)) parseParamsRep();
    	}
    }
//     stmt       ::= block
//             | "while" "(" exp ")" stmt              # while loop
//             | "if" "(" exp ")" stmt ["else" stmt]   # if then else
//             | "return" [exp] ";"                    # return
//             | exp "=" exp ";"                      # assignment
//             | exp ";"                               # expression statement, e.g. a function call
// first set = “{“, “while”, “if”, “return”, “(“, IDENT, INT_LIT, “-“, CHAR_LIT, STRING_LIT, “*”, “sizeof”}

    private void parseStmnt() {
    	if (accept(TokenClass.LBRA)) parseBlock();
    	else if (accept(TokenClass.WHILE)) { 
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseExp();
    		expect(TokenClass.RPAR);
    		parseStmnt();
    	}
    	else if (accept(TokenClass.IF)) { 
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseExp();
    		expect(TokenClass.RPAR);
    		parseStmnt();
    		if (accept(TokenClass.ELSE)) {
    			nextToken();
    			parseStmnt();
    		}
    	}
    	else if (accept(TokenClass.RETURN)) {
    		nextToken();
    		if (accept(TokenClass.SC)) nextToken();
    		else if (accept(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)){
    			parseExp();
    			expect(TokenClass.SC);
    		}
    		else error(TokenClass.SC, TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF);
    	} 
    	else if (accept(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)) {
    		parseExp();
    		if (accept(TokenClass.SC)) nextToken();
    		else if (accept(TokenClass.ASSIGN)) {
    			nextToken();
    			parseExp();
    			expect(TokenClass.SC);
    		}
    		else error(TokenClass.SC, TokenClass.ASSIGN);
    	}
    }
    // block ::= "{" (vardecl)* (stmt)* "}"
    private void parseBlock() {
    	if (accept(TokenClass.LBRA) && lookAhead(1).tokenClass == TokenClass.RBRA ) {
    		nextToken(); 
    		nextToken();
    	}
    	else if (accept(TokenClass.LBRA)) {
    		nextToken();
    		if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) parseVarDecls();
    		while (accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN, TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF)) parseStmnt();
    		expect(TokenClass.RBRA);
    	}
    	else error(TokenClass.LBRA);
    }
    // first = {“(“, IDENT, INT_LITERAL, “-“, CHAR_LITERAL, STRING_LITERAL, “*”, “sizeof”}
    private void parseExp() {
    	if (accept(TokenClass.LPAR) && lookAhead(1).tokenClass != TokenClass.INT  && lookAhead(1).tokenClass != TokenClass.CHAR && lookAhead(1).tokenClass != TokenClass.VOID && lookAhead(1).tokenClass != TokenClass.STRUCT) {
    		nextToken(); 	
    		parseExp();
    		expect(TokenClass.RPAR);
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.IDENTIFIER)) {
    		if (lookAhead(1).tokenClass == TokenClass.LPAR) parseFunCall();
    		else {
    			nextToken();
    			parseOtherExp();
    		}
    	}
    	else if (accept(TokenClass.INT_LITERAL)) {
    		nextToken();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.MINUS)) {
    		nextToken();
    		parseExp();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.CHAR_LITERAL)) {
    		nextToken();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.STRING_LITERAL)) {
    		nextToken();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.ASTERIX)) {
    		parseValueAt();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.SIZEOF)) {
    		parseSizeOf();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.LPAR) && (lookAhead(1).tokenClass == TokenClass.INT || lookAhead(1).tokenClass == TokenClass.CHAR || lookAhead(1).tokenClass == TokenClass.VOID || lookAhead(1).tokenClass == TokenClass.STRUCT)) {
    		parseTypeCast();
    		parseOtherExp();
    	}
    	else error(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.SIZEOF);
    }
    
    private void parseOtherExp() {
    	if (accept(TokenClass.GT) || accept(TokenClass.LT) || accept(TokenClass.GE) || accept(TokenClass.LE) || accept(TokenClass.NE) || accept(TokenClass.EQ) || accept(TokenClass.PLUS) || accept(TokenClass.MINUS) || accept(TokenClass.ASTERIX) || accept(TokenClass.DIV) || accept(TokenClass.REM) || accept(TokenClass.OR) || accept(TokenClass.AND)) {
    		nextToken();
    		parseExp();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.LSBR)) {
    		parseArrayAccess();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.DOT)) {
    		parseFieldAccess();
    		parseOtherExp();
    	} // no error here, since parseOtherExp() can be empty!
    }
    
    private void parseArrayAccess () {
    	if (accept(TokenClass.LSBR)) {
    		nextToken();
    		parseExp();
    		expect(TokenClass.RSBR);
    	}
    }
    
    private void parseFieldAccess() {
    	if (accept(TokenClass.DOT)) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    	}
    }
    
    private void parseValueAt() {
    	if (accept(TokenClass.ASTERIX)) {
    		nextToken();
    		parseExp();
    	}
    }
    
    private void parseSizeOf() {
    	if (accept(TokenClass.SIZEOF)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseTypes();
    		expect(TokenClass.RPAR);
    	}
    }
    
    private void parseTypeCast() {
    	if (accept(TokenClass.LPAR)) {
    		nextToken();
    		parseTypes();
    		expect(TokenClass.RPAR);
    		parseExp();
    	}
    }
    
    private void parseFunCall() {
    	if (accept(TokenClass.IDENTIFIER)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		if (accept(TokenClass.RPAR)) nextToken();
    		else {
    			parseExp();
    			if (accept(TokenClass.COMMA)) parseFunCallRep();
    			expect(TokenClass.RPAR);
    		}
    		parseOtherExp();
    	}
    }
    
    private void parseFunCallRep() {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		parseExp();
    		parseFunCallRep();			
    	}
=======
    
>>>>>>> 4b7cd8d310c7373cbaee99c0cc32d333151136b8
    }
}
