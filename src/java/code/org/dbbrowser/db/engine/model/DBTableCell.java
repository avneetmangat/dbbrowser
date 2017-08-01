package org.dbbrowser.db.engine.model;

import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a cell in a database table
 */
public class DBTableCell
{
	//public static String DATE_FORMAT_STRING = "dd/MM/yyyy hh:mm:ss";
    public static String DATE_FORMAT_STRING = "dd/MM/yyyy hh:mm a";
	private ColumnInfo columnInfo = null;
	private Object value = null;
	private Boolean isChangedByUser = null;
	
	private static final DecimalFormat doubleFormatter = new DecimalFormat("0");
	private static final DateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT_STRING );
	
	/**
	 * Build a object representing a value in the table cell
	 * @param columnInfo
	 * @param value
	 * @param isChangedByUser
	 */
	public DBTableCell(ColumnInfo columnInfo, Object value, Boolean isChangedByUser)
	{
		this.columnInfo = columnInfo;
		this.value = value;
		this.isChangedByUser = isChangedByUser;
	}
	
	/**
	 * Returns the value of the Object. 
	 * @return
	 */
	public Object getValue()
	{
		return this.value;
	}
	
	/**
	 * Return the value formatted as per the format of the data type.  If the value is null, returns null
	 * @return
	 */
	public String getFormattedValue()
	{
		String formattedValue = null;
        if( this.getValue() != null )
        {
            if( this.getColumnInfo().getColumnType() == null )
            {
                formattedValue = getValue().toString();
            }
            else
            {
                int columnType = this.getColumnInfo().getColumnType().intValue();

                if(this.value != null)
                {
                    if( columnType == Types.DOUBLE )
                    {
                        formattedValue = doubleFormatter.format( ((Double)this.getValue()).doubleValue() );
                    }
                    else if( columnType == Types.DATE || columnType == Types.TIMESTAMP )
                    {
                        formattedValue = dateFormat.format( (Date)this.getValue() );
                    }
                    else
                    {
                        //Handles Strings, numbers, clobs, blobs etc
                        formattedValue = this.value.toString();
                    }
                }
            }
        }
        return formattedValue;
	}
	
	/**
	 * Returns the column info describing this object
	 * @return
	 */
	public ColumnInfo getColumnInfo()
	{
		return this.columnInfo;
	}
	
	/**
	 * Returns true if this has been changed by the user and so this cell should be updated
	 * @return
	 */
	public Boolean isChangedByUser()
	{
		return this.isChangedByUser;
	}

    /**
     * Returns the date format for all dates
     * @return
     */
    public static DateFormat getDateFormat()
    {
        return dateFormat;
    }
    
    /**
     * Returns the double format for formatting numbers
     * @return
     */
    public static DecimalFormat getDecimalFormat()
    {
        return doubleFormatter;
    }    

    /**
	 * toString  - for debugging only
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("DBTableCell:\n");
		buffer.append("Value: " + this.getValue() + "\n");
		buffer.append("Is changed by user: " + this.isChangedByUser() + "\n");
		
		return buffer.toString();
	}
}
