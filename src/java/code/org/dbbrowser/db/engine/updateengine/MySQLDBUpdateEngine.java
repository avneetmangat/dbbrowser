package org.dbbrowser.db.engine.updateengine;

import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTableCell;

/**
 * This class is used to update the database
 * @author amangat
 */
public class MySQLDBUpdateEngine extends GenericDBUpdateEngine
{	
	private static final String MYSQL_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm";
	
	/**
	 * Constructer
	 * @param statement
	 */
	public MySQLDBUpdateEngine(Statement statement)
	{
	    super(statement);
	}
	
	/**
	 * Overrides date formatting for MySQL DB
	 */
	protected String formatValue(Object value, int dataType) throws DBEngineException
	{
	    String formattedValue = "";
	
	    if( dataType == Types.DATE || dataType == Types.TIMESTAMP )
	    {
	        formattedValue = value.toString();
	        try
	        {
	            DateFormat dateFormatForDisplay = DBTableCell.getDateFormat();
	            Date date = dateFormatForDisplay.parse( value.toString() );
	            DateFormat dateFormatForUpdate = new SimpleDateFormat( MYSQL_DATE_FORMAT_STRING );
	            formattedValue = "'" + dateFormatForUpdate.format(date) + "'";
	        }
	        catch(ParseException exc)
	        {
	            throw new DBEngineException( exc.getMessage() );
	        }
		}
	    else
	    {
	    	formattedValue = super.formatValue( value, dataType );
	    }
	    
	    return formattedValue;        
	}
	
	

}
