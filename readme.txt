
Java SDK for AI sandbox
by Matthias F. Brandstetter

http://aisandbox.com/


WHAT IS IT?
-----------

This is the Java SDK for the AI sandbox bot competition at aisandbox.com.
Please see the competition web site for more information. 


HOW TO USE THIS SDK?
--------------------

This SDK comes as a JAR file that you can include in your Java project.
To setup your Java project for AI sandbox bot development in the Eclipse IDE:
  
  1. Run Eclipse and create a new Java project, give that new project any name you like
  2. In the Package Exloprer right-click the new project -> New -> Folder, name it "libs"
  3. Copy all three JAR files from the SDK/libs directory to the newly created libs folder in Eclipse
  4. In the Package Exloprer right-click the new project -> Build Path -> Configure Build Path...
  5. On the "Libraries" tab in the popup window hit the "Add JARs..." button and select all three JAR files
  6. Confirm the changes by hitting the "OK" button on the popup window
  7. In the main Eclipse window add a new Java package to your project: "commander"
  8. Copy all source code files from the SDK/src/commander folder into this new Java package in Eclipse
  9. Open the "MyCommander" class in Eclipse and start coding your own bot commander!
  
That's it, now you have set up Eclipse to develop your own custom AI sandbox bot commander in Java.
The main class of the project is called "SandboxClientWrapper" which you have copied from the SDK's src folder.
Note that by default this client wrapper class tries to connect to localhost:41041, but you can change that
in the SandboxClientWrapper class.

Your commander Java class must be named "MyCommander", but you can set the commander name as you like (see sample).
Please see the sample implementation in "MyCommander.java" for a short introduction to this SDK.
You can find the SDK documentation in the doc folder.

In order to connect your commander to the AI sandbox server, simply open the simulate.py file and set the
variable "defaults" to 'game.NetworkCommander' and a second commander as you like, e.g.

  defaults = ['examples.Defender', 'game.NetworkCommander'] 
  
Run simulate.bat to start the game server, then connect from the Java client.

