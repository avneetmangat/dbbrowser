package org.dbbrowser.ui.helper.exporthelper;

import infrastructure.logging.Log;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Class used to export a table to CSV file
 */
public class ExporterForCSVFile implements ExportHelper
{
	private boolean stop = false;
	
	/**
	 * Export the data in the table to the specified file as a PDF file
	 * @param abstractTableModel
	 * @param changeListener
	 * @param fileToExportTo
	 * @throws ExportHelperException
	 */
	public void export(AbstractTableModel abstractTableModel, ChangeListener changeListener, File fileToExportTo) 
		throws ExportHelperException
    {
		this.stop = false;
		Log.getInstance().infoMessage("Starting CSV Export...", ExporterForCSVFile.class.getName());
		
        StringBuffer buffer = new StringBuffer();
        int rowCount = abstractTableModel.getRowCount();
        int columnCount = abstractTableModel.getColumnCount();
        List listOfColumns = (List)WizardState.getInstance().getState("List of columns to include");

       //Processing column names - start from 1 as the first column is the row number
       for( int columnIndex=1; columnIndex<columnCount; columnIndex++ )
       {
           String columnName = abstractTableModel.getColumnName( columnIndex );
           
           //If the user wants this columns, then include it
           if( listOfColumns.contains( columnName ) )
           {
               //Strip the (PK) from the column name
               if( columnName.endsWith("(PK)") )
               {
            	   columnName = columnName.substring(0, columnName.length()-4);
               }
               
        	   buffer.append( columnName );

	           //if it is the last column, add the new line character, else add a comma
	           if( columnIndex+1 == columnCount )
	           {
	               buffer.append("\n");
	           }
	           else
	           {
	               buffer.append(",");
	           }
           }
       }
       
       String rowHeaderString = buffer.toString();
       
       //Write the contents of the header
       BufferedWriter writer = null;
       try
       {
	        writer = new BufferedWriter( new FileWriter(fileToExportTo) );
	        
	        //Write the header string into the stream
	        writer.write(rowHeaderString, 0, rowHeaderString.length());
	        
	        //Process each row
	        for( int rowIndex=0; rowIndex<rowCount; rowIndex++ )
	        {
	        	//If stopped, exit and export incomplete table
	        	if( stop == true )
	        	{
	        		break;
	        	}
	        	
	    		Log.getInstance().debugMessage("Exporting row " + (rowIndex+1) + " of " + rowCount + " as CSV...", ExporterForCSVFile.class.getName());        
	        	
		        buffer = new StringBuffer();

	           //Process each column in the row - start from 1 as the first column is the row number
	           for( int columnIndex=1; columnIndex<columnCount; columnIndex++ )
	           {
	               Object cell = abstractTableModel.getValueAt( rowIndex, columnIndex );
	               String columnName = abstractTableModel.getColumnName( columnIndex );               
	               //If the user wants this columns, then include it
	               if( listOfColumns.contains( columnName ) )
	               {               
	            	   if( cell != null )
		               {
		                   buffer.append("\"" + cell.toString() + "\"");
		               }
		               else
		               {
		                   buffer.append("\"\"");
		               }
	
		               //if it is the last column, add the new line character, else add a comma
		               if( columnIndex+1 == columnCount )
		               {
		            	   buffer.append("\n");
		               }
		               else
		               {
		            	   buffer.append(",");
		               }
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
       Log.getInstance().infoMessage("Finished CSV export", ExporterForCSVFile.class.getName());        
    }
	
	/**
	 * Call this to stop the export.  This is required as it may be a long process and it runs in a seperate thread
	 */
	public void stop()
	{
		this.stop = true;
	}
}
