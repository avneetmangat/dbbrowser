package org.dbbrowser.security;

public class EncryptionEngineException extends Exception
{
	private static final long serialVersionUID = 1l;

    /**
     * Constructer
     * @param exc
     */
    public EncryptionEngineException(Exception exc)
    {
        super( exc );
    }
}
