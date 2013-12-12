package uk.co.objecttechnologies.natter;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class with code shared by client and server.
 *
 * @author Timothy Mowlem
 */
public abstract class AbstractNatter
{
    public static final int DEFAULT_PORT = 12560;

    public static final String ACK = "OK";

    protected static Pattern postingPattern = Pattern.compile ("^(\\w+) -> (.+)$");
    protected static Pattern followsPattern = Pattern.compile ("^(\\w+) follows (\\w+)$", Pattern.CASE_INSENSITIVE);
    protected static Pattern wallPattern = Pattern.compile ("^(\\w+) wall$", Pattern.CASE_INSENSITIVE);
    protected static Pattern usernamePattern = Pattern.compile ("^(\\w+)$");

    protected Matcher postingMatcher;
    protected Matcher followsMatcher;
    protected Matcher wallMatcher;
    protected Matcher usernameMatcher;

    protected int port;


    protected AbstractNatter (Integer port)
    {
        this.port = port != null ? port : DEFAULT_PORT;

        usernameMatcher = usernamePattern.matcher ("");
        postingMatcher = postingPattern.matcher ("");
        followsMatcher = followsPattern.matcher ("");
        wallMatcher = wallPattern.matcher ("");
    }
}
