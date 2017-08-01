package org.dbbrowser.drivermanager;

/**
 * Exception of this class is thrown by the DBBrowserDriverManager if there is a problem whicl getting connection to the database
 */
public class DriverManagerException extends Exception
{
	private static final long serialVersionUID = 1l;

    /**
     * Constructer
     * @param exc
     */
    public DriverManagerException(Exception exc)
    {
        super( exc );
    }
}
