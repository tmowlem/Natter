Natter

A twitter-like client server application. It consists of two applications, client and server. Both client and server use a default server of localhost and a default port of 12560.


SERVER

Runs with no GUI and non-interactively. Runs indefinitely, Ctrl+C in your terminal to kill it. Has a single optional argument:
port=<number> where <number> is a port number in the standard range

To run the client:

Open a shell
cd to the directory containing the root of the class hierarchy, e.g. if you unpacked the Natter directory to C:\dev then:
cd C:\dev\Natter\out\production\Natter
java -cp . uk.co.objecttechnologies.natter.NatterServer [port=4560]


CLIENT

Runs as a command line application. Runs indefinitely, Ctrl+C in your terminal to kill it. 

Has two optional arguments:

server=<name> where <name> is a string containing the servers network name
port=<number> where <number> is a port number in the standard range

Help info is:

Natter - a command line based Twitter like service

Allowed commands are:

bye, quit or exit							Exits the application
help									Prints this help message
<username>							Displays all messages for the given user
<username> -> <message>			Posts the given message from the given user
<username1> follows <username2>	Prints all messages from the given user
<username> all						Prints all messages from the user and those followed

<username> is a single word only, no spaces, case insensitive
<message> is the message to display

To run the client:

Open a shell
cd to the directory containing the root of the class hierarchy, e.g. if you unpacked the Natter directory to C:\dev then:
cd C:\dev\Natter\out\production\Natter

java -cp . uk.co.objecttechnologies.natter.NatterClient [server=myhost] [port=4560]

