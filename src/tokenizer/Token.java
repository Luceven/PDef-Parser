package tokenizer;


/**
This class defines the form of the abstract values appearing on an input stream. 

Each Token object has a type defined by TokenType and a character string containing the sequence of characters the token abstracts. 

@author J. Mead -- July '08
*/

public class Token {

    // State 
    
	public enum TokenType { IDENT_T, TYPE_T, ASSIGN_T, 
	                        RCB_T, LCB_T, RP_T, LP_T, COMMA_T, 
	                        INT_T, FLOAT_T, ADD_OP_T, SUB_OP_T, 
	                        MUL_OP_T, DIV_OP_T, MOD_OP_T, ERROR_T, 
	                        EOF_T };

	private TokenType type;  // type of this particular token
	private String    name;  // string of characters associated with
						     // this particular token
	private int position;
	private int line;
	
	// Constructor

    public Token(TokenType t, String s, int l, int pos) {
       type = t;
       name = s;
       line = l;
       position = pos;
    }
    
    // Interface -- public methods

	public TokenType getType() { return type; }
		// Pre:  type has a value
		// Post: return type
	public String getName() { return name; }
		// Pre:  name has a value
		// Post: return name

	public String toString() 
		// Pre:  type and name have values
		// Post: return string containing character form of
		//       "<line, position> type(name)"
	{
		return  
		       type.toString() + "( " + name + " )" +
		       "<" + line + "," + position + ">";
	}

}
