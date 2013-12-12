package uk.co.objecttechnologies.natter;


import java.util.Date;


/**
 * A natter including unique ID, user, message and timestamp.
 *
 * @author Timothy Mowlem
 */
public class Natter
{
    private static int nextId = -1;

    private int id;
    private String user;
    private String message;
    private Date date;


    public Natter (String user, String message)
    {
        this.id = generateId();
        this.user = user;
        this.message = message;
        this.date = new Date();
    }

    private static synchronized int generateId()
    {
        return nextId++;
    }

    public int getId()
    {
        return id;
    }

    public String getUser()
    {
        return user;
    }

    public String getMessage()
    {
        return message;
    }

    public Date getDate()
    {
        return date;
    }
}
