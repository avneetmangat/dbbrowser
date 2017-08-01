package org.dbbrowser.ui.helper.exporthelper;

import org.dbbrowser.ui.UIControllerForQueries;

/**
 * Instance of this exception is thrown when there is any problem during exporting the results
 * @author amangat
 */
public class ExportHelperException extends Exception
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    /**
     * Constructer
     * @param message
     */
	public ExportHelperException(String message)
	{
		super(message);
	}
}
