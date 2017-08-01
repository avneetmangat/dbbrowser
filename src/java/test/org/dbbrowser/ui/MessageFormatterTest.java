package org.dbbrowser.ui;

import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBTableCell;
import junit.framework.TestCase;

/**
 * Tests message formatter.  The formatter is used to parse user input and creates a java object from the parsed string
 * @author amangat
 *
 */
public class MessageFormatterTest extends TestCase
{
	private Map mapOfDataTypeNameToFormatter = new HashMap(); 
	
    public MessageFormatterTest(String name)
    {
        super(name);  
    	mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_VARCHAR, new MessageFormat("{0}"));			//Format varchar datatype using the message formatter
    	mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_CHAR, new MessageFormat("{0}"));			//Format char datatype using the message formatter
    	mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_DATE, DBTableCell.getDateFormat());			//Format date datatype using the message formatter
    	mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_NUMBER, DBTableCell.getDecimalFormat());			//Format date datatype using the message formatter
    }
    
    public void testVarChar()
    {
    	String stringToParse = "a string";
    	Format formatter = (Format)mapOfDataTypeNameToFormatter.get( ColumnInfo.COLUMN_TYPE_VARCHAR );
    	
    	try
    	{
    		Object o = formatter.parseObject( stringToParse );
    		
    		Object stringObject = (((Object[])o)[0]).toString();
    		
    		//Test
    		assertTrue("Object should be of type String", stringObject.getClass().getName().equals("java.lang.String"));
    		assertTrue("Formatting of string failed", ((String)stringObject).equals(stringToParse));
    	}
    	catch(ParseException exc)
    	{
    		fail(exc.getMessage());
    	}
    }
    
    public void testChar()
    {
    	String charToParse = "a";
    	Format formatter = (Format)mapOfDataTypeNameToFormatter.get( ColumnInfo.COLUMN_TYPE_CHAR );
    	
    	try
    	{
    		Object o = formatter.parseObject( charToParse );
    		
    		Object stringObject = (((Object[])o)[0]).toString();
    		
    		//Test
    		assertTrue("Object should be of type String", stringObject.getClass().getName().equals("java.lang.String"));
    		assertTrue("Formatting of char failed", ((String)stringObject).equals(charToParse));
    	}
    	catch(ParseException exc)
    	{
    		fail(exc.getMessage());
    	}
    }   
    
    public void testDate()
    {
    	String dateToParse = "26/12/2005";
    	Format formatter = (Format)mapOfDataTypeNameToFormatter.get( ColumnInfo.COLUMN_TYPE_DATE );
    	
    	try
    	{
    		Object o = formatter.parseObject( dateToParse );
    		
    		//Test
    		assertTrue("Object should be of type Date", o.getClass().getName().equals("java.util.Date"));
    		String d = DBTableCell.getDateFormat().format((Date)o);
    		assertTrue("Formatting of Date failed", d.equals(dateToParse));
    	}
    	catch(ParseException exc)
    	{
    		fail(exc.getMessage());
    	}
    }
    
    public void testNumber()
    {
    	String numberToParse = "100";
    	Format formatter = (Format)mapOfDataTypeNameToFormatter.get( ColumnInfo.COLUMN_TYPE_NUMBER );
    	
    	try
    	{
    		Object o = formatter.parseObject( numberToParse );
    		
    		//Test
    		assertTrue("Object should be of type Number", o instanceof Number);
    	}
    	catch(ParseException exc)
    	{
    		fail(exc.getMessage());
    	}
    }    
}
