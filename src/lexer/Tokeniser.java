package lexer;

import lexer.Scanner;
import lexer.Token;
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
        	if (c == '/') {
        		while (c != '\n' && c != '\r') {
        	   		c = scanner.next();
        	   	}
        		return next();
        	}
       		if (c == '*') {
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
        		}
       		else return new Token(TokenClass.DIV, line, column);
        }
 	   
        
        if (c == '#') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	if (scanner.peek() == -1 || Character.isWhitespace(scanner.peek())) return new Token(TokenClass.INVALID, line, column);
        	c = scanner.next();
        	while (Character.isLetterOrDigit(c)) {
        		sb.append(c);
        		if ((Character.isWhitespace(scanner.peek()) || scanner.peek() == -1) && sb.toString().equals("#include")) return new Token(TokenClass.INCLUDE, line, column);
         	   if (Character.isDefined(scanner.peek()) && !(Character.isLetterOrDigit(scanner.peek())) && !(scanner.peek() == '_') && sb.toString().equals("#include")) return new Token(TokenClass.INCLUDE, line, column);
         	   if ((Character.isWhitespace(scanner.peek()) || scanner.peek() == -1) && !(sb.toString().equals("#include"))) break;
         	   c = scanner.next();
        	}
        	return new Token(TokenClass.INVALID, line, column);
        }
        
        if (c == '&') {
        	char p = scanner.peek();
        	if (p == '&') {
        		c = scanner.next();
        		return new Token(TokenClass.AND, line, column);
        	}
        	else error (c, line, column); return new Token(TokenClass.INVALID, line, column);
        }
        
        if (c == '|') {
        	char p = scanner.peek();
        	if (p == '|') {
        		c = scanner.next(); 
        		return new Token(TokenClass.OR, line, column);
        	}
        	else error (c, line, column); return new Token(TokenClass.INVALID, line, column);
        }
        
        if (c == '<') {
        	char p = scanner.peek();
        	if (p == '=') {
        		c = scanner.next(); 
        		return new Token(TokenClass.LE, line, column);
        	}
        	else return new Token(TokenClass.LT, line, column);
        }
        
        if (c == '>') {
        	char p = scanner.peek();
        	if (p == '=') {
        		c = scanner.next(); 
        		return new Token(TokenClass.GE, line, column);
        	}
        	else return new Token(TokenClass.GT, line, column);
        }
        
        if (c == '!') {
        	char p = scanner.peek();
        	if (p == '=') {
        		c = scanner.next(); 
        		return new Token(TokenClass.NE, line, column);
        	}
        	else error (c, line, column); return new Token(TokenClass.INVALID, line, column);
        }
        
        if (c == '=') {
        	char p = scanner.peek();
        	if (p == '=') {
        		c = scanner.next(); 
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
      	   c = scanner.next();
      	   char p = scanner.peek();
      	   
      	   if (c == '\\') {
      		   switch (p) {
      		   case 't': sb.append('\t'); break;
      		   case 'b': sb.append('\b'); break;
      		   case 'n': sb.append('\n'); break;
      		   case 'r': sb.append('\r'); break;
      		   case 'f': sb.append('\f'); break;
      		   case '\'': sb.append('\''); break;
      		   case '\"': sb.append('\"'); break;
      		   case '\\': sb.append('\\'); break;
      		   case '0': sb.append('\0'); break;
      		   default: error (c, line, column); return new Token(TokenClass.INVALID, line, column);
      		   }
      		   c = scanner.next();
      	   }
      	   else if (c != '\\' && c != '\'') sb.append(c);
      	 
      	 c = scanner.next();
      	 
      	 if (c == '\'') {
      		 sb.append(c);
      		 return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column);
      	 }
      	 else {
      		 error (c, line, column);
      		 return new Token(TokenClass.INVALID, line, column);
      	 }

        }
        
        // STRING_LITERAL
        
        if (c == '\"') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
      	   c = scanner.next();
      	   char p = scanner.peek();
      	   
      	   while (c != '\"') {
      		   if (c == '\\') {
      			   switch (p) {
      			   	case 't': sb.append('\t');  break;
      			   	case 'b': sb.append('\b'); break;
      			   	case 'n': sb.append('\n'); break;
      			   	case 'r': sb.append('\r'); break;
      			   	case 'f': sb.append('\f'); break;
      			   	case '\'': sb.append('\''); break;
      			   	case '\"': sb.append('\"'); break;
      			   	case '\\': sb.append('\\'); break;
      			   	case '0': sb.append('\0'); break;
      			   	default: error (c, line, column); return new Token(TokenClass.INVALID, line, column);
      			   	}
      			   c = scanner.next();
      			   p = scanner.peek();
      		   }
      		   else sb.append(c);
      		   
      		   c = scanner.next();
      		   p = scanner.peek();
      	   }
      	   
      	   sb.append(c);
      	   return new Token(TokenClass.STRING_LITERAL, sb.toString(), line, column);
        }
        	
        
        
        // IDENTIFIERS, TYPES, KEYWORDS
        
        if (Character.isLetter(c) || c == '_') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	while (Character.isLetterOrDigit(scanner.peek()) || scanner.peek() == '_') {
        		c = scanner.next();
        		sb.append(c);
        	}
        	
        	if (sb.toString().equals("int")) return new Token(TokenClass.INT, line, column); 
			if (sb.toString().equals("void")) return new Token(TokenClass.VOID, line, column);
			if (sb.toString().equals("char")) return new Token(TokenClass.CHAR, line, column);
			if (sb.toString().equals("if")) return new Token(TokenClass.IF, line, column);
			if (sb.toString().equals("else")) return new Token(TokenClass.ELSE, line, column);
			if (sb.toString().equals("while")) return new Token(TokenClass.WHILE, line, column);
			if (sb.toString().equals("return")) return new Token(TokenClass.RETURN, line, column);
			if (sb.toString().equals("struct")) return new Token(TokenClass.STRUCT, line, column);
			if (sb.toString().equals("sizeof")) return new Token(TokenClass.SIZEOF, line, column);
			else return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        	
        }
        
       

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

        
}
