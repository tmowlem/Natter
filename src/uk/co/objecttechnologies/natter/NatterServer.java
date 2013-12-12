package uk.co.objecttechnologies.natter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


/**
 * Server part of Natter service.
 *
 * @author Timothy Mowlem
 */
public class NatterServer extends AbstractNatter
{
    private List<Natter> natters;
    private Map<String, List<String>> followers;


    public NatterServer (Integer port)
    {
        super (port);

        natters = new ArrayList<Natter>();
        followers = new HashMap<String, List<String>>();
    }

    /**
     * ENTRY POINT
     */
    public static void main (String[] args)
    {
        // Set port.
        Integer port = null;
        for (String arg : args)
        {
            String s = arg.toLowerCase();
            if (s.startsWith ("port="))
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

        // Start server.
        try
        {
            new NatterServer (port).process();
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
    }

    // Processes user input.
    @SuppressWarnings ("InfiniteLoopStatement")
    private void process() throws IOException
    {
        // Listen for connections.
        ServerSocket serverSocket = new ServerSocket (port);

        // Process clients.
        while (true)
        {
            Socket socket = serverSocket.accept();

            BufferedReader rdr = null;
            try
            {
                rdr = new BufferedReader (new InputStreamReader (socket.getInputStream()));
                String line = null;
                while (true)
                {
                    line = rdr.readLine();
                    if (line == null)
                    {
                        break;
                    }

                    line = line.trim();

                    usernameMatcher.reset (line);
                    postingMatcher.reset (line);
                    followsMatcher.reset (line);
                    wallMatcher.reset (line);

                    PrintWriter out = new PrintWriter (socket.getOutputStream());
                    if (postingMatcher.matches())
                    {
                        postMessage (out, postingMatcher.group (1), postingMatcher.group (2));
                    }
                    else if (followsMatcher.matches())
                    {
                        follows (out, followsMatcher.group (1), followsMatcher.group (2));
                    }
                    else if (wallMatcher.matches())
                    {
                        displayAllMessages (out, wallMatcher.group (1));
                    }
                    else if (usernameMatcher.matches())
                    {
                        displayUserMessages (out, usernameMatcher.group (1));
                    }
                }
            }
            catch (IOException ioex)
            {
                ioex.printStackTrace();
            }
            finally
            {
                if (socket != null && !socket.isClosed())
                {
                    socket.close();
                }
            }
        }
    }

    // Posts the given message as the given user.
    private void postMessage (PrintWriter out, String username, String message)
    {
        Natter natter = new Natter (username, message);
        natters.add (0, natter);
        out.println (ACK);
        out.flush();
    }

    // Sets the given user as a follower of the given natterer.
    private void follows (PrintWriter out, String username, String natterer)
    {
        List<String> lf = followers.get (natterer);
        if (lf == null)
        {
            lf = new ArrayList<String>();
            followers.put (natterer, lf);
        }

        if (!lf.contains (username))
        {
            lf.add (username);
        }

        out.println (ACK);
        out.flush();
    }

    // Displays all messages posted by the given user or by a natterer that thegiven user is following.
    private void displayAllMessages (PrintWriter out, String username)
    {
        for (Natter natter : natters)
        {
            String user = null;
            if (natter.getUser().equals (username))
            {
                user = username;
            }
            else if (isFollower (username, natter.getUser()))
            {
                user = natter.getUser();
            }

            if (user != null)
            {
                int minutesElapsed = minutesLapsed (natter.getDate());
                if (minutesElapsed > 0)
                {
                    out.println (String.format ("%s -> %s (%d minute%s ago)", user, natter.getMessage(), minutesElapsed, minutesElapsed > 1 ? "s" : ""));
                }
                else
                {
                    out.println (String.format ("%s -> %s", user, natter.getMessage()));
                }
            }
        }

        out.println (ACK);
        out.flush();
    }

    // Displays all messages posted by the given user.
    private void displayUserMessages (PrintWriter out, String username)
    {
        for (Natter natter : natters)
        {
            if (natter.getUser().equals (username))
            {
                int minutesElapsed = minutesLapsed (natter.getDate());
                if (minutesElapsed > 0)
                {
                    out.println (String.format ("%s -> %s (%d minutes ago)", username, natter.getMessage(), minutesElapsed));
                }
                else
                {
                    out.println (String.format ("%s -> %s", username, natter.getMessage()));
                }
            }
        }

        out.println (ACK);
        out.flush();
    }

    // Returns if the given user is a follower of the given natterer.
    private boolean isFollower (String username, String natterer)
    {
        List<String> lf = followers.get (natterer);
        return lf != null && lf.contains (username);
    }

    // Returns the number of minutes elapsed since the message was posted.
    private int minutesLapsed (Date date)
    {
        Date now = new Date();
        long millis = now.getTime() - date.getTime();
        return (int) (millis / 60000L);
    }
}