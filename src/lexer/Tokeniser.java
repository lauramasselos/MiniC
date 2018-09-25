package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token comment() throws IOException {
    	char c = scanner.next();
    	while (c != '\n') {
    		scanner.next();
    	}
		return next();
    }
    
   private Token multiComment() throws IOException {
	   char c = scanner.next();
	   while (c != '*' && scanner.peek() != '/') {
   			scanner.next();
   		}
		return next();
    }
   // look up #include in C (i.e. #include "str" and #include"str" are valid, not #include 231)
  /* private Token include() throws IOException {
       int line = scanner.getLine();
       int column = scanner.getColumn();
	   char c = scanner.next();
	   StringBuilder sb = new StringBuilder();
	   sb.append(c);
	   c = scanner.peek();
	   while (Character.isLetter(c)) {
		   sb.append(c);
		   scanner.next();
		   c = scanner.peek();
		   if (sb.toString().equals("include")) break;
	   }

    }
    */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();
        
        
        if (c == '{') return new Token(TokenClass.LBRA, line, column);
        if (c == '}') return new Token(TokenClass.RBRA, line, column);
        if (c == '(') return new Token(TokenClass.LPAR, line, column);
        if (c == ')') return new Token(TokenClass.RPAR, line, column);
        if (c == '[') return new Token(TokenClass.LSBR, line, column);
        if (c == ']') return new Token(TokenClass.RSBR, line, column);
        if (c == ';') return new Token(TokenClass.SC, line, column);
        if (c == ',') return new Token(TokenClass.COMMA, line, column);
        if (c == '.') return new Token(TokenClass.DOT, line, column);
        if (c == '+') return new Token(TokenClass.PLUS, line, column);
        if (c == '-') return new Token(TokenClass.MINUS, line, column);
        if (c == '*') return new Token(TokenClass.ASTERIX, line, column);
        if (c == '%') return new Token(TokenClass.REM, line, column);
        if (c == '/') {
        	c = scanner.peek();
        	if (c == '/') comment();
        	if (c == '*') multiComment();
        	else {
        		return new Token(TokenClass.DIV, line, column);
        	}
        }
        
       // if (c == '#') include();
        
        if (c == '&') {
        	scanner.next();
        	return new Token(TokenClass.AND, line, column);
        }
        
        if (c == '|') {
        	scanner.next();
        	return new Token(TokenClass.OR, line, column);
        }
        
        if (c == '<') {
        	c = scanner.peek();
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.LE, line, column);
        	}
        	else return new Token(TokenClass.LT, line, column);
        }
        
        if (c == '>') {
        	c = scanner.peek();
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.GE, line, column);
        	}
        	else return new Token(TokenClass.GT, line, column);
        }
        
        if (c == '!') {
        	scanner.next();
        	return new Token(TokenClass.NE, line, column);
        }
        
        if (c == '=') {
        	c = scanner.peek();
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.EQ, line, column);
        	}
        	else return new Token(TokenClass.ASSIGN, line, column);
        }
        /*
        if (c == '_' || Character.isLetterOrDigit(c)) {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	c = scanner.peek();
        	while (Character.isLetterOrDigit(c) || c == '_') {
        		sb.append(c);
        		scanner.next();
        		c = scanner.peek();
        	}
        	String str = sb.toString();
        	return new Token(TokenClass.IDENTIFIER, str, line, column);
        }

*/

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }


}
