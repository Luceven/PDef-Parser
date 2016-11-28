package exceptions;

/**
This class is the more abstract exception for the PDef system.  This exception expects all necessary information to be transmitted by the argument to the constructor.  That information is stored in the part of the PDefException state defined in the Java class Exception. If more fine grained exception handling is to be done then appropriate new classes should be defined as subclasses of this one.

STATE:

This class has no state.
    
INTERFACE:

This class has a simple constructor and public method: the constructor passes its parameter on to its super class constructor, while the public method simply displays the error message passed to the constructor.

    public PDefException(String msg)
    // Pre:  msg has a value
    // Post: super.getMessage() == msg

    public void print() 
    // Post: super.getMessage() displayed on standard out
    
HELPERS:

This class has no helper methods.

CLASS INVARIANT:

This class has no class invariant.

@author J. Mead -- August '09
*/


public class PDefException extends Exception {

    // State -- that inherited from Exception

    // Constructor
    
    public PDefException(String msg)
    // Pre:  msg has a value
    // Post: super.getMessage() == msg
    { super(msg); }

    // Interface
    
    public void print() 
    // Post: super.getMessage() displayed on standard out
    { System.out.println(super.getMessage()); }

}
