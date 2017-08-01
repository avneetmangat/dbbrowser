package org.dbbrowser.db.engine.exception;

/**
 * DBEngineException is thrown when somethning goes wrong with the DB Engine 
 * @author amangat
 *
 */
public class DBEngineException extends Exception
{
	private static final long serialVersionUID = 1l;
	
	/**
	 * Constrcuter
	 * @param message
	 */
	public DBEngineException(String message)
	{
		super( message );
	}
}
