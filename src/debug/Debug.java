package debug;

import java.util.*;

/**
This class is the top of a debugging hierarchy.  This class maintains static data including 'commandLine', which is a string of command line arguments -- each specified by a known character, 'FlagNum', which contains the position in 'flags' that can allocated next, 'flag', which is an ArrayList holding the debug flag values for each command line flag.

When a command line argument is encountered (typically in the main method) the static method 'registerFlag' is called with the particular argument character as argument.  That character is added to 'commandLine'.  

When a debuggable class is instantiated its constructor will instantiate its corresponding subclass of 'Debug' and call the method 'registerObject' and pass as argument its assigned command line character.  'registerObject' will first assign a position p in 'flags' and initialize flags[p] to true if the argument character appears in 'commandLine' and false otherwise.  The value of 'p' is returned to the caller as the registration number. 

STATE:

See the description above.

   private static String commandLine = "";
   private static int flagNum = 0;
   private static ArrayList<Boolean> flags = new ArrayList<Boolean>();
   
INTERFACE:

   public static void registerFlag(char ch)  
   // Pre:  ch is a commandline character to be registered -- 
   // Post: commandLine.indexOf(ch) != -1 

   public int registerObject(char ch) 
   // Post: psn == (in)flagNum AND psn >= 0 AND flagNum == (in)flagNum+1
   //       flags.get(psn) == ( commandLine.indexOf(ch) != -1 ) AND
   //       return psn

   public void show(int psn, String msg) 
   // Pre:  0 <= psn < FlagNum AND
   //       psn is a registration number sent from 
   //       an instance of a subclass of Debug

HELPERS:

This class has no helper methods.

CLASS INVARIANT:

This class has no class invariant.

@author J. Mead -- July '08
*/


public abstract class Debug {

   // Static class state
   
   private static String commandLine = "";
   private static int flagNum = 0;
   private static ArrayList<Boolean> flags = new ArrayList<Boolean>();
   
   // Interface -- public methods
   
   public static void registerFlag(char ch)  
   // Pre:  ch is a commandline character to be registered -- 
   // Post: commandLine.indexOf(ch) != -1 
   {
      commandLine += ch; 
   }

   public int registerObject(char ch) 
   // Post: psn == (in)flagNum AND psn >= 0 AND flagNum == (in)flagNum+1
   //       flags.get(psn) == ( commandLine.indexOf(ch) != -1 ) AND
   //       return psn
   {
       int psn = flagNum++;
       flags.add(psn,  commandLine.indexOf(ch) != -1 ); 
       return psn;
   }
   
   public void show(int psn, String msg) 
   // Pre:  0 <= psn < FlagNum AND
   //       psn is a registration number sent from 
   //       an instance of a subclass of Debug
   {
      if (flags.get(psn) )
          System.out.println(msg);
   }
}
