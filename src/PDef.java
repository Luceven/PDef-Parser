import java.io.BufferedReader;
import java.io.IOException;

import java.io.*;

import exceptions.*;
import tokenizer.*;
import parser.*;
import debug.*;

/**
This is the driver class which tests the Parser classe. The method main implements command line entry of both an input file name and a string of (character) arguments each of which signals a particular debugging capability:
    char     capability
    'e'      echo to the screen the input data as it is read 
    't'      turn on debugging code in the Tokenizer object
    'p'      turn on debugging code in the Parser object
    
If the first argument is not a valid input file name then the program will terminate with an error message indicating the problem.

The output from main will be either "Program Parsed", if no syntax errors were found, or an error message indicating the nature of the error found.
     

@author J. Mead -- August '09
*/



public class PDef {

    public static void main(String[] args) throws PDefException {

        System.out.println("Yunjia Zeng");
        // local variables
                    
        BufferedReader in = null;   // the input character stream
        boolean echo      = false;  // if true the input is echoed --
                                    // value is 'false' by default
                                    // and set to true if 'e' appears
                                    // as a command line argument
        
        int numArgs = args.length;  // number of command line arguments
                                    // (doen't include command name)

        if (numArgs < 1) {
            // There must be a file name!
            System.out.println("Not enough arguments!\n");
            System.exit(0);
        }
        else  { 
            // args[0] is the data file name
            try { 
               in = new BufferedReader(new FileReader(args[0])); 
               if (numArgs > 1)  // args[1] holds debug flags
                  for (int i = 0; i != args[1].length(); i++) {
                      switch (args[1].charAt(i)) {
                      case 'e': echo    = true; break;
                      case 't': Debug.registerFlag ('t'); break;
                      case 'p': Debug.registerFlag ('p'); break;
                      }
                      // ignore invalid flag names
                  }
            }
            catch (FileNotFoundException e) {
               System.out.printf("Could not open file `%s'\n", args[0]);
               System.exit(0);
            }
        }

        Tokenizer tokenStream = new Tokenizer(in, echo);
        Parser parse = new Parser(tokenStream);

        try {
            parse.parseProgram();
            System.out.println("Program parsed!");
        }
        catch (ParseException exc) {
            exc.print();
        }
    }

}
