package parser;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
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

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
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


    private void parseProgram() {
        parseIncludes();
        parseStructDecls();
        parseVarDecls();
        parseFunDecls();
        expect(TokenClass.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecls() {
        if (accept(TokenClass.STRUCT)) {
        	nextToken();
        	expect(TokenClass.IDENTIFIER);
        	expect(TokenClass.LBRA);
        	parseVarDecls();
        	while (!accept(TokenClass.RBRA)) parseVarDecls();
        	expect(TokenClass.RBRA);
        	expect(TokenClass.SC);
        	parseStructDecls();
        }
    }

    private void parseVarDecls() {
        parseTypes();
        expect(TokenClass.IDENTIFIER);
        if (lookAhead(1).tokenClass == TokenClass.SC) {
        	expect(TokenClass.SC);
        	parseVarDecls();
        }
        else if (lookAhead(1).tokenClass == TokenClass.LSBR) {
        	expect(TokenClass.LSBR);
        	expect(TokenClass.INT_LITERAL);
        	expect(TokenClass.RSBR);
        	expect(TokenClass.SC);
        	parseVarDecls();
        }
    }

    private void parseFunDecls() {
        parseTypes();
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        parseParams();
        expect(TokenClass.RPAR);
        parseBlock();
        parseFunDecls();
        
    }
    
    private void parseTypes() {
    	if (accept(TokenClass.INT) || accept(TokenClass.CHAR) || accept(TokenClass.VOID)) {
    		nextToken();
    		expect(TokenClass.ASTERIX);
    	}
    	else if (accept(TokenClass.STRUCT)) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    		expect(TokenClass.ASTERIX);
    	}
    }
    
    private void parseParams() {
    	parseTypes();
    	expect(TokenClass.IDENTIFIER);
    	parseParamsRep();
    }
    
    private void parseParamsRep() {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		parseTypes();
    		expect(TokenClass.IDENTIFIER);
    		parseParamsRep();
    	}
    	else nextToken();
    }
    
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
    		else {
    			parseExp();
    			expect(TokenClass.SC);
    		}
    	}
    	else {
    		parseExp();
    		if (accept(TokenClass.SC)) nextToken();
    		else {
    			expect(TokenClass.ASSIGN);
    			parseExp();
    		}
    	}
    }
    
    private void parseBlock() {
    	if (accept(TokenClass.LBRA)) {
    		nextToken();
    		while (accept(TokenClass.INT) || accept(TokenClass.CHAR) || accept(TokenClass.VOID) || accept(TokenClass.STRUCT)) parseVarDecls();
    		while (!accept(TokenClass.RBRA)) parseStmnt();
    		expect(TokenClass.RBRA);
    	}
    }
    
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
    }
    
    private void parseOtherExp() {
    	if (accept(TokenClass.GT) || accept(TokenClass.LT) || accept(TokenClass.GE) || accept(TokenClass.LE) || accept(TokenClass.NE) || accept(TokenClass.EQ) || accept(TokenClass.PLUS) || accept(TokenClass.MINUS) || accept(TokenClass.ASTERIX) || accept(TokenClass.DIV) || accept(TokenClass.REM) || accept(TokenClass.OR) || accept(TokenClass.AND)) {
    		nextToken();
    		parseExp();
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.LSBR)) {
    		nextToken();
    		parseExp();
    		expect(TokenClass.RSBR);
    		parseOtherExp();
    	}
    	else if (accept(TokenClass.DOT)) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    		parseOtherExp();
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
    	}
    }
    
    private void parseFunCallRep() {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		parseExp();
    		parseFunCallRep();			// might have to do something like if (!accept(TokenClass.RPAR)) parseFunCallRep();, wait for scoreboard update & try again
    	}
    }

    // to be completed ...
}
