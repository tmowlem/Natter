package uk.co.objecttechnologies.natter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Client part of Natter service.
 *
 * @author Timothy Mowlem
 */
public class NatterClient extends AbstractNatter
{
    private static final String DEFAULT_SERVER = "localhost";

    private static final String PROMPT = "> ";

    private static final String BYE  = "bye";
    private static final String QUIT = "quit";
    private static final String EXIT = "exit";
    private static final String HELP = "help";


    private String server;


    public NatterClient (String server, Integer port)
    {
        super (port);
        this.server = server != null ? server : DEFAULT_SERVER;
    }

    /**
     * ENTRY POINT
     */
    public static void main (String[] args)
    {
        // Set server and port.
        String server = null;
        Integer port = null;
        for (String arg : args)
        {
            String s = arg.toLowerCase();
            if (s.startsWith ("server="))
            {
                server = s.substring (7);
            }
            else if (s.startsWith ("port="))
            {
                try
                {
                    port = Integer.parseInt (s.substring (5));
                }
                catch (NumberFormatException nfex)
                {
                    // Do nothing.
                }
            }
        }

        new NatterClient (server, port).process();
    }

    // Processes user input.
    private void process()
    {
        BufferedReader rdr = null;
        try
        {
            rdr = new BufferedReader (new InputStreamReader (System.in));
            while (true)
            {
                System.out.print (PROMPT);

                String line = rdr.readLine();
                if (line == null)
                {
                    break;
                }

                // Exit if any of the words BYE, QUIT or EXIT is typed by itself.
                line = line.trim();
                if (line.equalsIgnoreCase (BYE) || line.equalsIgnoreCase (QUIT) || line.equalsIgnoreCase (EXIT))
                {
                    break;
                }

                // Print help info if requested.
                if (line.equalsIgnoreCase (HELP))
                {
                    help();
                    continue;
                }

                usernameMatcher.reset (line);
                postingMatcher.reset (line);
                followsMatcher.reset (line);
                wallMatcher.reset (line);

                if (postingMatcher.matches() || followsMatcher.matches() || wallMatcher.matches() || usernameMatcher.matches())
                {
                    Socket socket = null;
                    try
                    {
                        socket = new Socket (server, port);
                        PrintWriter out = new PrintWriter (socket.getOutputStream());
                        out.println (line);
                        out.flush();

                        BufferedReader sin = new BufferedReader (new InputStreamReader (socket.getInputStream()));
                        while (true)
                        {
                            String output = sin.readLine();
                            output = output.trim();
                            if (output == null || output.equals (ACK))
                            {
                                break;
                            }

                            System.out.println (output);
                        }
                    }
                    catch (IOException ioex)
                    {
                        ioex.printStackTrace();
                    }
                    finally
                    {
                        if (socket != null)
                        {
                            socket.close();
                        }
                    }
                }
            }
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
        finally
        {
            if (rdr != null)
            {
                try
                {
                    rdr.close();
                }
                catch (IOException e)
                {
                    // Ignore.
                }
            }
        }
    }

    // Prints help info to the console.
    private void help()
    {
        System.out.println ("\n====================================================================");
        System.out.println ("Natter - a command line based Twitter like service\n");
        System.out.println ("Allowed commands are:");
        System.out.println ("bye, quit or exit\t\tExits the application");
        System.out.println ("help\t\t\t\t\tPrints this help message");
        System.out.println ("<username>\t\t\t\tDisplays all messages for the given user");
        System.out.println ("<username> -> <message>\tPosts the given message from the given user");
        System.out.println ("<username1> follows <username2>\tPrints all messages from the given user");
        System.out.println ("<username> all\t\t\tPrints all messages from the user and those followed\n");
        System.out.println ("<username> is a single word only, no spaces, case insensitive");
        System.out.println ("<message> is the message to display");
        System.out.println ("====================================================================\n");
    }}
