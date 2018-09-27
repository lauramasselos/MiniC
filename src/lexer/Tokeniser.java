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
    		c = scanner.next();
    	}
		return next();
    }
    

   private Token multiComment() throws IOException {
	   char c = scanner.next(); // pointing at * now
	   int line = scanner.getLine(); 
	   int column = scanner.getColumn();
	   while (true) {
		   if (scanner.peek() == -1) break; // if we reach EOF without reaching end of comment, this is INVALID
   			c = scanner.next();
   			if (c == '*') {
   				if (scanner.peek() == '/') {
   					scanner.next();
   					return next();
   				}
   			}
   		}
	   return new Token(TokenClass.INVALID, line, column);
    }
   
    
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();
        
        if (c == -1) return new Token(TokenClass.EOF, line, column);
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
 	   
 	   // before returning include token peek that next character is NOT a digit letter or '_'
 	   // e.g. #include= ---> SHOULD LEX OKAY, BUT RETURN A PARSING ERROR
 	   // BUT #includeeeeeeeeeee --> RETURN WHOLE TOKEN INVALID, LEXING ERROR
       /*if (c == '#') {
    	   StringBuilder sb = new StringBuilder();
    	   sb.append(c);
    	   if (scanner.peek() == -1) return new Token(TokenClass.INVALID, line, column);
    	   c = scanner.next();
    	   for (int i = 0; i < 7; i++) {
    		   sb.append(c);
    		   if (Character.isWhitespace(scanner.peek()) || scanner.peek() == -1) break;
    		   c = scanner.next(); 
    	   }
    	   if (Character.isWhitespace(c) && sb.toString().equals("#include")) return new Token(TokenClass.INCLUDE, line, column);
    	   if (scanner.peek() == -1 && sb.toString().equals("#include")) return new Token(TokenClass.INCLUDE, line, column);
    	   if (Character.isDefined(c) && !(Character.isLetterOrDigit(c)) && !(c == '_') && sb.toString().equals("#include")) return new Token(TokenClass.INCLUDE, line, column);
    	   else return new Token(TokenClass.INVALID, line, column);
    	   
       }*/
        
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
        
        // INT_LITERAL
        
        if (Character.isDigit(c)) {
           StringBuilder sb = new StringBuilder();
     	   sb.append(c);
     	   c = scanner.peek();
     	   while (Character.isDigit(c)) {
     		   sb.append(c);
     		   scanner.next();
     		   c = scanner.peek();
     	   }
     	   return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        }
       
        
        // CHAR_LITERAL
        
        
        if (c == '\'') {
           StringBuilder sb = new StringBuilder();
     	   sb.append(c);
     	   c = scanner.peek();
     	   
     	   if (c == '\\') {
     		   sb.append(c);
     		   c = scanner.next();
     		   c = scanner.peek();
     		   if (c == 't' || c == 'b' || c == 'n' || c == 'r' || c == 'f' || c == '\'' || c == '\"' || c == '\\' || c == '0') {
     			   sb.append(c);
     			   c = scanner.next();
     			   c = scanner.peek();
     			   if (c == '\'') {
     				   sb.append(c);
     				   c = scanner.next();
     				   return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column);
     			   }
     			   else return new Token(TokenClass.INVALID, line, column);
     		   }
     		   else return new Token(TokenClass.INVALID, line, column);
     	   }
     	   
     	   else if (Character.isDefined(c) && c != '\\' && c!= '\'' && c!= '\"') {
     		   sb.append(c);
     		   c = scanner.next();
     		   c = scanner.peek();
     		   if (c == '\'') {
     			   sb.append(c);
     			   c = scanner.next();
     			   return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column);
     		   }
     		   else return new Token(TokenClass.INVALID, line, column);
     	   }
     	   else return new Token(TokenClass.INVALID, line, column);
     	   
        }
        
        // FIX STRING_LITERAL (add escape character exceptions, e.g. \" will be lexed as " !)
        // i.e. "I said \"Hello!\"" is not currently lexed as "I said "Hello!""; this needs to be fixed
        
        if (c == '\"') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	c = scanner.next();
        	while (Character.isDefined(c) || Character.isWhitespace(c)) {
        		if (c  != '\\' && scanner.peek() == '\"') break;
        		sb.append(c);
        		if (scanner.peek() == -1) return new Token(TokenClass.INVALID, line, column);
        		c = scanner.next();
        	} 
        	sb.append(c);
        	c = scanner.next();
        	sb.append(c);
        	return new Token(TokenClass.STRING_LITERAL, sb.toString(), line, column);
        }
        
        
        // IDENTIFIERS, TYPES, KEYWORDS
        
        if (Character.isLetter(c) || c == '_') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	c = scanner.next();
        	while(Character.isLetterOrDigit(c) || c == '_') {
        		sb.append(c);
        		c = scanner.next();
        		if (sb.toString().equals("int") && Character.isWhitespace(c)) return new Token(TokenClass.INT, line, column); 
    			if (sb.toString().equals("void") && Character.isWhitespace(c)) return new Token(TokenClass.VOID, line, column);
    			if (sb.toString().equals("char") && Character.isWhitespace(c)) return new Token(TokenClass.CHAR, line, column);
    			if (sb.toString().equals("if") && Character.isWhitespace(c)) return new Token(TokenClass.IF, line, column);
    			if (sb.toString().equals("else") && Character.isWhitespace(c)) return new Token(TokenClass.ELSE, line, column);
    			if (sb.toString().equals("while") && Character.isWhitespace(c)) return new Token(TokenClass.WHILE, line, column);
    			if (sb.toString().equals("return") && Character.isWhitespace(c)) return new Token(TokenClass.RETURN, line, column);
    			if (sb.toString().equals("struct") && Character.isWhitespace(c)) return new Token(TokenClass.STRUCT, line, column);
    			if (sb.toString().equals("sizeof") && Character.isWhitespace(c)) return new Token(TokenClass.SIZEOF, line, column);
    			if (Character.isWhitespace(c)) return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
    			if (scanner.peek() == -1) {
    				sb.append(c);
    				return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
    			}
        	}
        	return new Token(TokenClass.INVALID, line, column);
        }
        

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

        
}
