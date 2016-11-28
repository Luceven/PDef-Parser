package parser;

import exceptions.*;
import exceptions.ParseException;
import tokenizer.*;
import debug.*;

import java.text.*;


/**
This class defines a recursive descent parser for the language PDef-light with context free grammar as follows:

    Program     --> Block
    Block       --> lcbT StmtList rcbT
    StmtList    --> Stmt { commaT Stmt }
    Stmt        --> Declaration | Assignment | Block
    Declaration --> typeT identT
    Assignment  --> identT assignT identT
    
In this grammar the terminals lcbT, rcbT, commaT, identT, typeT, assignT represent tokens in the lexical description for PDef-light.

STATE:

The state of an object includes a variable currentToken holding the last read token from the input stream (received from the tokenizer via the method getNextToken) and the input stream itself tokenStream -- in addition there is a object debug to control the display of debug output (see class Debug for information):

   ParserDebug debug
   TokenType currentToken
   Tokenizer tokenStream
   
INTERFACE:

The interface of this class includes the constructor and the parse method for the non-terminal (Program) that acts as the start symbol for the grammar:
    
   Parser(Tokenizer tokenStream)  
   void parseProgram()
   
HELPER METHODS:
   
The Helper methods in this class include a parse method for each of the grammar non-terminals (other than Program):

   void parseBlock()
   void parseStmtList()
   void parseStmt()
   void parseDeclaration()
   void parseAssignmentment()
   
There are two additional helper methods:

   void error() 
        called when a parse error is detected -- it displays a generic
        error message and then halts the program (calls System.exit)
   void consume(Token.TokenType type)
        this method checks that the value of currentToken matches 
        the argument type and if it does reads a new value for
        currentToken -- if it doesn't match a call to error() is made
   
CLASS INVARIANT:  

The class has an invariant which specifies requirements for the parse methods relative to the value of the state variable currentToken and the input stream tokenStream.

    Class Invariant: 
          When a parse function is called the next 
          token to be considered is already read 
          into currentToken.
          
@author J. Mead -- July '08
*/


public class Parser {

    // Class Invariant: 
    //       When a parse function is called the next 
    //       token to be considered is already read 
    //       into currentToken

    ParserDebug debug;      // control display of debug output
    Token currentToken;     // value of next token to be processed
    Tokenizer tokenStream;  // reference to the input token stream

    public Parser(Tokenizer tokenStream)  
    // Pre:  tokenStream has a value
    // Post: debug == new ParserDebug() AND 
    //       this.tokenStream == tokenStream AND
    //       class invariant is true
    {  
        this.debug = new ParserDebug(); 
        this.tokenStream = tokenStream;

        currentToken = tokenStream.getNextToken();
             // This makes the class invariant true
             // for the call to parseProgram
    }

    public void parseProgram() throws ParseException
    // Grammar Rule:  Program --> Block eofT
            // Pre: Input is a valid token
            //Post: Program is parsed, exception handled
    {		
        debug.show(">>> Entering parseProgram");

        parseBlock();
        
        consume(Token.TokenType.EOF_T);

        debug.show("<<< Leaving parseProgram");
    }


    private void parseBlock() throws ParseException
    // Grammar Rule:  Block --> lcbT Block rcbT
            //Pre: Input is a valid token
            //Post: Block is parsed, exception handled
    {
        debug.show(">>> Entering parseBlock");

        consume(Token.TokenType.LCB_T);
        
        parseStmtList();
        
        consume(Token.TokenType.RCB_T);
        
        debug.show("<<< Leaving parseBlock");
    }
    
    
    private void parseStmtList() throws ParseException
    // Grammar Rule: Block --> Stmt { commaT Stmt }
            //Pre: Input is a valid token
            //Post: Block is parsed, exception handled
    {
        debug.show(">>> Entering parseStmtList");

        parseStmt();
        while (currentToken.getType() == Token.TokenType.COMMA_T) {
            consume(Token.TokenType.COMMA_T);
            parseStmt();
        }

        debug.show("<<< Leaving parseStmtList");
    }


    private void parseStmt() throws ParseException
    // Grammar Rule:  Stmt --> Declaration
    //                     --> Assignment
    //                     --> Block
            //Pre: Input is a valid token
            //Post: Statement is parsed, Declaration, Assignment, and Block will be parsed
            //depending on their first set. Exception is handled.
    {
        debug.show(">>> Entering parseStmt");
        try {
            switch(currentToken.getType()) {
                case TYPE_T:
                    parseDeclaration();
                    break;
                case IDENT_T:
                    parseAssignment();
                    break;
                case LCB_T:
                    parseBlock();
                    break;
                default:
                    throw new ParseException("Expected to see type, identifier, or left brace!", currentToken);
            }
            //Assert: currentToken != COMMA_T or RCB_T or EOF_T
            if ( !canFollowStatement(currentToken) )
                throw new ParseException("Expected a statement terminator.", currentToken);
        }//End of try
        catch (ParseException exc) {
            exc.print();
            consume2StatementEnd();
            //Assert: currentToken == COMMA_T or RCB_T or EOF_T
        }

        debug.show("<<< Leaving parseStmt");
    }

    private void consume2StatementEnd() {
        //Post: currentToken == COMMA_T or RCB_T or EOF_T
        while (!canFollowStatement(currentToken))
            currentToken = tokenStream.getNextToken();
        //Assert: currentToken == COMMA_T or RCB_T or EOF_T
    }

    private boolean canFollowStatement(Token t) {
        //Post: return true if t == COMMA_T or RCB_T or EOF_T
        Token.TokenType type = t.getType();
        return ( type == Token.TokenType.COMMA_T ||
                    type == Token.TokenType.RCB_T ||
                    type == Token.TokenType.EOF_T);
    }


    private void parseAssignment() throws ParseException
    // Grammar Rule:  Assignment --> identT assignT identT
            // Updated Rule: Assignment --> identT assignT Exp
            //Pre: Input is a valid token
            //Post: Assignment is parsed, valid token will be consumed and exception
            //will be handled.
    {
        debug.show(">>> Entering parseAssignment");
        consume(Token.TokenType.IDENT_T);
        consume(Token.TokenType.ASSIGN_T);
        //consume(Token.TokenType.IDENT_T);
        parseExp();
        debug.show("<<< Leaving parseAssignment");
    }


    private void parseDeclaration() throws ParseException
    // Grammar Rule:  Declaration --> typeT identT
            //Pre: Input is a valid token
            //Post: Declaration is parsed, valid token will be consumed and
            //exception will be handled.
    {
        debug.show(">>> Entering parseDeclaration");
        consume(Token.TokenType.TYPE_T);
        consume(Token.TokenType.IDENT_T);
        debug.show("<<< Leaving parseDeclaration");
    }

    private void parseExp() throws ParseException{
        //Grammar Rule : Exp --> Exp (addT | subT) Term
        //                   --> Term
        //After Left recursion elimination:
        // Exp --> Term E'
        //E' --> addT Term E' | subT Term E' | Epsilon
        //Pre: Input is a valid token
        //Post: Expression is parsed, exception handled.
        debug.show(">>> Entering parseExp");
        parseTerm();
        parseEprime();
        debug.show("<<< Leaving parseExp");
    }

    private void parseEprime() throws ParseException {
        //This is the second part of the original parseExp()
        //Grammar Rule: E' --> addT Term E' | subT Term E' | Epsilon
        //Pre: Input is valid token
        //Post: The second part of Expression parsing is parsed, will consume valid
        //token and parse Term depending on first set
        debug.show(">>> Entering parseEprime");
        if (currentToken.getType() == Token.TokenType.ADD_OP_T ||
                currentToken.getType() == Token.TokenType.SUB_OP_T) {
            consume(currentToken.getType());
            parseTerm();
            parseEprime();
        }
        debug.show("<<< Leaving parseEprime");
    }

    private void parseTerm() throws ParseException{
        //Grammar Rule: Term --> Term (mulT | divT | modT) Factor
        //                   --> Factor
        // After Left recursion elimination:
        // Term --> Factor T'
        // T --> mulT Factor T' | divT Factor T' | modT Factor T' | Epsilon
        //Pre: Input is a valid token
        //Post: Term is parsed. Exception is handled.
        debug.show(">>> Entering parseTerm");
        parseFactor();
        parseTprime();
        debug.show("<<< Leaving parseTerm");
    }

    private void parseTprime() throws ParseException {
        //This is the second part of the original parseTerm()
        //Grammar Rule: T --> mulT Factor T' | divT Factor T' | modT Factor T' | Epsilon
        //Pre: Input is valid token
        //Post: The rest part of Term will be parsed, valid token will be consumed and Factor will be parsed
        //depending on first set.
        debug.show(">>> Entering parseTerm");
        if (currentToken.getType() == Token.TokenType.MUL_OP_T ||
                currentToken.getType() == Token.TokenType.DIV_OP_T ||
                currentToken.getType() == Token.TokenType.MOD_OP_T) {
            consume(currentToken.getType());
            parseFactor();
            parseTprime();
        }
        debug.show("<<< Leaving parseTerm");
    }

    private void parseFactor() throws ParseException {
        //Grammar Rule: Factor --> intT | floatT | identT | lpT Exp rpT
        //Pre: Input is a valid token
        //Post: Factor is parsed, valid token is consumed, exception is handled.
        switch (currentToken.getType()) {
            case INT_T:
                consume(Token.TokenType.INT_T);
                break;
            case FLOAT_T:
                consume(Token.TokenType.FLOAT_T);
                break;
            case IDENT_T:
                consume(Token.TokenType.IDENT_T);
                break;
            case LP_T:
                consume(Token.TokenType.LP_T);
                parseExp();
                consume(Token.TokenType.RP_T);
                break;
            default:
                throw new ParseException("Expected integer, float, identifier, or left parentheses ", currentToken);
        }
    }
    
    
    private void error() { 
        System.out.println("Parse error occurred!");
        System.exit(0);
    }
    
    private void consume(Token.TokenType type) throws ParseException {
        //Pre: Input is a valid token
        //Post: valid token is consumed. Exception is handled.
        if (currentToken.getType() != type) {
            String msg = "Expected to see token " + type + " but saw token " + currentToken.getType() + "\n";
            throw new ParseException(msg, currentToken);
        }
        currentToken = tokenStream.getNextToken();
     }
}
