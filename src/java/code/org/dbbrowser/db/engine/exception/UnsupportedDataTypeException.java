package org.dbbrowser.db.engine.exception;

import org.dbbrowser.ui.UIControllerForQueries;

/**
 * UnsupportedDataTypeException is thrown when the database contains a data type which DBBrowser cannot display
 */
public class UnsupportedDataTypeException extends RuntimeException
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	
	public UnsupportedDataTypeException(String message)
	{
		super( message );
	}
}
