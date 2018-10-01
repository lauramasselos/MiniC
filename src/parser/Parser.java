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
        // to be completed ...
    	if (accept(TokenClass.STRUCT)) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    		expect(TokenClass.LBRA);
    		parseVarDeclsRep();
    		expect(TokenClass.RBRA);
    		expect(TokenClass.SC);
    	}
    }

    private void parseVarDecls() {
        // to be completed ...
    	parseTypes();
    	expect(TokenClass.IDENTIFIER);
		if (lookAhead(1).tokenClass == TokenClass.SC) {
			expect(TokenClass.SC);
		}
		if (lookAhead(1).tokenClass == TokenClass.LSBR) {
			expect(TokenClass.LSBR);
			expect(TokenClass.INT_LITERAL);
			expect(TokenClass.RSBR);
			expect(TokenClass.SC);
		}
    }
    
    private void parseVarDeclsRep() {
    	parseVarDecls();
    	parseVarDeclsRep();
    }

    private void parseFunDecls() {
        // to be completed ...
    	parseTypes();
    	expect(TokenClass.IDENTIFIER);
    	expect(TokenClass.LPAR);
    	parseParams();
    	expect(TokenClass.RPAR);
    	parseBlocks();
    }
    
    private void parseTypes() {
    	if (accept(TokenClass.INT) || accept(TokenClass.CHAR) || accept(TokenClass.VOID)) {
    		nextToken();
    		expect(TokenClass.ASTERIX);
    		//parseStarOpt();
    	}
    	if (accept(TokenClass.STRUCT)) {
    		nextToken();
    		expect(TokenClass.IDENTIFIER);
    		expect(TokenClass.ASTERIX);
    		//parseStarOpt();
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
    }
    
    private void parseStmnts() {
    	if (lookAhead(1).tokenClass == TokenClass.LBRA) {
    		parseBlocks();
    	}
    	if (accept(TokenClass.WHILE)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseExps();
    		expect(TokenClass.RPAR);
    		parseStmnts();
    	}
    	if (accept(TokenClass.IF)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseExps();
    		expect(TokenClass.RPAR);
    		parseStmnts();
    		parseWithElses();
    	}
    	if (accept(TokenClass.RETURN)) {
    		nextToken();
    		//parseExpOpts();
    		expect(TokenClass.SC);
    	}
    	// last two rules: exp "=" exp ";" || exp ";"
    	else {
    		parseExps();
    		if (accept(TokenClass.ASSIGN)) {
    			nextToken();
    			parseExps();
    			expect(TokenClass.SC);
    		}
    		if (accept(TokenClass.SC)) nextToken();
    	}
    }
    
    private void parseWithElses() {
    	if (accept(TokenClass.ELSE)) {
    		nextToken();
    		parseStmnts();
    	}
    }
    
    private void parseBlocks() {
    	if (accept(TokenClass.LPAR)) {
    		nextToken();
    		parseVarDeclRep2();
    		parseStmntsRep();
    		expect(TokenClass.RPAR);
    	}
    }
    
    private void parseVarDeclRep2() {
    	parseVarDecls();
    	parseVarDeclRep2();
    }
    
    private void parseStmntsRep() {
    	parseStmnts();
    	parseStmntsRep();
    }
    
    private void parseExps() {
    	if (accept(TokenClass.LPAR)) {
    		nextToken();
    		parseExps();
    		expect(TokenClass.RPAR);
    	}
    	if (accept(TokenClass.IDENTIFIER) || accept(TokenClass.INT_LITERAL)) nextToken();
    	
    	if (accept(TokenClass.MINUS)) {
    		nextToken();
    		parseExps();
    	}
    	if (accept(TokenClass.CHAR_LITERAL)) nextToken();
    	
    	if (accept(TokenClass.STRING_LITERAL)) nextToken();
    	
    	// parse exp (binary operator) exp ; arrayaccess ; fieldaccess
    	
    	if (accept(TokenClass.ASTERIX)) parseValueAt();
    	if (accept(TokenClass.IDENTIFIER) && (lookAhead(1).tokenClass == TokenClass.LPAR)) parseFunCalls();
    	if (accept(TokenClass.SIZEOF)) parseSizeOf();
    	// exp --> typecast
    }
    /*
    private void parseArrayAccess() {
    	
    }
    
    private void parseFieldAccess() {
    	
    }*/
    
    private void parseValueAt() {
    	if (accept(TokenClass.ASTERIX)) {
    		nextToken();
    		parseExps();
    	}
    }
    
    private void parseFunCalls() {
    	if (accept(TokenClass.IDENTIFIER)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		parseFunCallsOpt();
    		expect(TokenClass.RPAR);
    	}
    }
    
    private void parseFunCallsOpt() {
    	parseExps();
    	parseFunCallsOptRep();
    }
    
    private void parseFunCallsOptRep() {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		parseExps();
    		parseFunCallsOptRep();
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
    
    private void parseTypeCasts() {
    	if (accept(TokenClass.LPAR)) {
    		nextToken();
    		parseTypes();
    		expect(TokenClass.RPAR);
    		parseExps();
    	}
    }
    
    // to be completed ...
}
