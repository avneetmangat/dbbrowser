package org.dbbrowser.ui.helper.exporthelper;

import infrastructure.logging.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

public class ExporterForSQLFile implements ExportHelper
{
	private boolean stop = false;
	private DateFormat dateFormat = null;
	
	/**
	 * Export the data in the table to the specified file as SQL insert statements
	 * @param abstractTableModel
	 * @param changeListener
	 * @param fileToExportTo
	 * @throws ExportHelperException
	 */
	public void export(AbstractTableModel abstractTableModel, ChangeListener changeListener, File fileToExportTo) 
		throws ExportHelperException
    {
		this.stop = false;
		Log.getInstance().infoMessage("Starting SQL Export...", ExporterForCSVFile.class.getName());
	
		//Set the date format
		String dateFormatString = (String)WizardState.getInstance().getState("Date format");
		this.dateFormat = new SimpleDateFormat( dateFormatString );
		
		//String buffer to hold sql
        StringBuffer buffer = new StringBuffer();
        
        //Get the SQL prefix e.g. 'insert into <tablename> ('
        String sqlInsertPrefix = this.getSQLInsertPrefix(abstractTableModel);
        
        //Get the list of columns to include
        List listOfColumns = (List)WizardState.getInstance().getState("List of columns to include");

        //Count of number of rows and columns
        int rowCount = abstractTableModel.getRowCount();
        int columnCount = abstractTableModel.getColumnCount();
        
        BufferedWriter writer = null;
        try
        {
	        writer = new BufferedWriter( new FileWriter(fileToExportTo) );

	        //Process each row
	        for( int rowIndex=0; rowIndex<rowCount; rowIndex++ )
	        {
	        	//If stopped, exit and export incomplete table
	        	if( stop == true )
	        	{
	        		break;
	        	}
	        	
	        	buffer = new StringBuffer();
	        	buffer.append(sqlInsertPrefix);
	        	
	    		Log.getInstance().debugMessage("Exporting row " + (rowIndex+1) + " of " + rowCount + " as SQL...", ExporterForCSVFile.class.getName());        
	        	
	           //Process each column in the row - start from 1 as the first column is the row number
	           for( int columnIndex=1; columnIndex<columnCount; columnIndex++ )
	           {
	               String columnName = abstractTableModel.getColumnName( columnIndex );               
	               //If the user wants this columns, then include it
	               if( listOfColumns.contains( columnName ) )
	               {   
	            	   //Get the type of column
	            	   DBTableDataTableModel dbTableDataModel = (DBTableDataTableModel)abstractTableModel;
	            	   ColumnInfo ci = (ColumnInfo)dbTableDataModel.getDBTable().getListOfColumnInfos().get(columnIndex - 1);
	            	   
	            	   //Format the value and add to buffer
	                   Object cell = dbTableDataModel.getObjectValueAt( rowIndex, columnIndex );
	            	   String formattedValue = formatValueForSQL(cell, ci.getColumnTypeName());
	            	   buffer.append(formattedValue);
	               }
	               
	               //if it is the last column, add the new line character, else add a comma
	               if( columnIndex+1 == columnCount )
	               {
	            	   buffer.append(");\n");
	               }
	               else
	               {
	            	   buffer.append(", ");
	               }	               
	           }
	           
	           String rowString = buffer.toString();
	           writer.write(rowString, 0, rowString.length());
	           
	           //Inform the change listener
	           changeListener.stateChanged( new ChangeEvent(new Integer(rowIndex) ));
	        }
        }
        catch(IOException exc)
        {
        	throw new ExportHelperException( exc.getMessage() );
        }
        finally
        {
        	try
        	{
		        writer.flush();
		        writer.close();
        	}
        	catch(IOException exc)
        	{
        		//Cant do anything
        	}
        } 	        
        
		Log.getInstance().infoMessage("Finished SQL export", ExporterForCSVFile.class.getName());        
    }
	
	/**
	 * Formats the cell object depending on its type
	 * @param cell
	 * @param columnnTypeName
	 * @return
	 * @throws ExportHelperException
	 */
	private String formatValueForSQL(Object cell, String columnnTypeName)
		throws ExportHelperException
	{
		StringBuffer buffer = new StringBuffer();
 	   //if it is a varchar, add it as a string
 	   if( ColumnInfo.COLUMN_TYPE_VARCHAR.equals( columnnTypeName ) || ColumnInfo.COLUMN_TYPE_VARCHAR2.equals(columnnTypeName) )
 	   {
     	   if( cell != null )
            {
                String value = cell.toString();
                buffer.append("'" + value + "'");
            }
            else
            {
                buffer.append("''");
            }            		   
 	   }
 	   
 	   //if it is a number
 	   if( ColumnInfo.COLUMN_TYPE_NUMBER.equals( columnnTypeName ) )
 	   {
     	   if( cell != null )
            {
                String value = cell.toString();
                buffer.append(value);
            }
            else
            {
                buffer.append("0");
            }            		   
 	   }    
 	   
 	   //if it is a char
 	   if( ColumnInfo.COLUMN_TYPE_CHAR.equals( columnnTypeName ) )
 	   {
     	   if( cell != null )
            {
                String value = cell.toString();
                buffer.append("'" + value + "'");
            }
            else
            {
                buffer.append("''");
            }            		   
 	   }  
 	   
 	   //if it is a date
 	   if( ColumnInfo.COLUMN_TYPE_DATE.equals( columnnTypeName ) )
 	   {
     	   if( cell != null )
            {
     		  String dateFormatString = (String)WizardState.getInstance().getState("Date format");
   			   buffer.append("to_date('" + this.dateFormat.format((Date)cell) + "', '" + dateFormatString + "')");
            }
            else
            {
                buffer.append("''");
            }            		   
 	   } 	   
		
 	   return buffer.toString();
	}
	
	/**
	 * Returns the SQL prefix e.g. 'insert into <tablename> (<columnname>)values('
	 * @param abstractTableModel
	 * @return
	 */
	private String getSQLInsertPrefix(AbstractTableModel abstractTableModel)
	{
        DBTableDataTableModel dbTableDataModel = (DBTableDataTableModel)abstractTableModel;
        StringBuffer buffer = new StringBuffer();
        List listOfColumns = (List)WizardState.getInstance().getState("List of columns to include");
        int columnCount = abstractTableModel.getColumnCount();
        buffer.append("insert into " + dbTableDataModel.getDBTable().getTableName() + "(");

       //Processing column names - start from 1 as the first column is the row number
       for( int columnIndex=1; columnIndex<columnCount; columnIndex++ )
       {
           String columnName = abstractTableModel.getColumnName( columnIndex );
           
           //If the user wants this columns, then include it
           if( listOfColumns.contains( columnName ) )
           {
               //Strip the (PK) from the primary key column name
               if( columnName.endsWith("(PK)") )
               {
            	   columnName = columnName.substring(0, columnName.length()-4);
               }
               
        	   buffer.append( columnName );
           }
           
           //if it is the last column, add the new line character, else add a comma
           if( columnIndex+1 == columnCount )
           {
        	   buffer.append(") values (");
           }
           else
           {
        	   buffer.append(", ");
           }           
       }
       
       return buffer.toString();
	}
	
	/**
	 * Call this to stop the export.  This is required as it may be a long process and it runs in a seperate thread
	 */
	public void stop()
	{
		this.stop = true;
	}
}
