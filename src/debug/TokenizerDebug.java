package debug;

/**
This class is a subclass of Debug (in this package) -- read the description with Debug to understand how this kind of class appears to the Debug class.

This class implements a mechanism for displaying debugging information.  The display of the information is controlled by the appearance of 't' in the string of commandline arguments for the application.

Read the documentation in Debug.java to see how calls to 'Debug.show' control display of messages. 

STATE: 

When instantiated the constructor registers with the Debug class and saves its registration number in 'regPsn'.  

INTERFACE:

An instantiated TokenizerDebug object can be used to specify the display of debug information -- implemented as a call to 'show' plus a string message. 

    // Constructor
    public TokenizerDebug() 
    // Post: regPsn == registration value returned by registerObject AND
    //       regPsn >= 0


@author J. Mead -- August '09
*/


public class TokenizerDebug extends Debug {

    // State
    
    private int regPsn;
    
    // Constructor
    
    public TokenizerDebug() 
    // Post: regPsn == registration value returned by Debug.registerObject AND
    //       regPsn >= 0
    {
       regPsn = registerObject('t'); 
    }

    public void show(String msg) {
        show(regPsn, msg);
    }

    public void show(String msg, char ch) {
        show(regPsn, msg + "(`" + ch + "')");
    }

}
