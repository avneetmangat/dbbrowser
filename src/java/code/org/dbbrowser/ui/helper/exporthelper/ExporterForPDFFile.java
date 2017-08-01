package org.dbbrowser.ui.helper.exporthelper;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import infrastructure.logging.Log;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.List;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

/**
 * Class used to export a table to PDF file
 */
public class ExporterForPDFFile implements ExportHelper
{
	private boolean stop = false;
	
	/**
	 * Export the data in the table to the specified file as a PDF file
	 * @param abstractTableModel
	 * @param changeListener
	 * @throws ExportHelperException
	 */
	public void export(AbstractTableModel abstractTableModel, ChangeListener changeListener, File fileToExportTo) 
		throws ExportHelperException
    {
		this.stop = false;
		
		Log.getInstance().infoMessage("Starting PDF Export...", ExporterForPDFFile.class.getName());
		Document pdfDocument = setupPDFDocument(abstractTableModel, fileToExportTo);
        List listOfColumnsToInclude = (List)WizardState.getInstance().getState("List of columns to include");

        //Open the pdf document
        pdfDocument.open();

        int rowCount = abstractTableModel.getRowCount();
        int columnCount = abstractTableModel.getColumnCount();

        //Setup the table
        PdfPTable table = null;
        table = new PdfPTable( listOfColumnsToInclude.size() );
        table.setHeaderRows( 1 );

        table.setWidthPercentage(100.0f);
        table.getDefaultCell().setGrayFill(0.8f);

        //Set to 0 if you want to include row number
        int startPosition = 0;

       //Processing column names - start from 1 as the first column is the row number
        for( int columnIndex=startPosition; columnIndex<columnCount; columnIndex++ )
        {
            String columnName = abstractTableModel.getColumnName( columnIndex );
            if(listOfColumnsToInclude.contains(columnName))
            {
            	Phrase tableCell = new Phrase(columnName);
            	table.addCell( tableCell );
            }
        }

        table.getDefaultCell().setGrayFill(0.0f);

        //Process each row
        for( int rowIndex=startPosition; rowIndex<rowCount; rowIndex++ )
        {
        	//If stopped, exit and export incomplete table
        	if( stop == true )
        	{
        		break;
        	}
        	
    		Log.getInstance().infoMessage("Exporting row " + (rowIndex+1) + " of " + rowCount + " as PDF file...", ExporterForPDFFile.class.getName());
        	
           //Process each column in the row - start from 1 as the first column is the row number
           for( int columnIndex=startPosition; columnIndex<columnCount; columnIndex++ )
           {
               String columnName = abstractTableModel.getColumnName( columnIndex );
               if(listOfColumnsToInclude.contains(columnName))
               {
	               Object cell = abstractTableModel.getValueAt( rowIndex, columnIndex );
	               if( cell != null )
	               {
	                   String value = cell.toString();
	                   Phrase tableCell = new Phrase(value);
	                   table.addCell( tableCell );
	               }
	               else
	               {
	                   Phrase tableCell = new Phrase("");
	                   table.addCell( tableCell );
	               }
               }
           }
           
           //Inform the change listener
           changeListener.stateChanged( new ChangeEvent(new Integer(rowIndex) ));           
        }

        try
        {
            pdfDocument.add( table );
        }
        catch(DocumentException exc)
        {
            throw new ExportHelperException(exc.getMessage());
        }

        //Close and flush the document
        pdfDocument.close();

        //Write the document
		Log.getInstance().infoMessage("finished PDF Export", ExporterForPDFFile.class.getName());
    }
	
	/**
	 * Call this to stop the export.  This is required as it may be a long process and it runs in a seperate thread
	 */
	public void stop()
	{
		this.stop = true;
	}

    private static Document setupPDFDocument(AbstractTableModel abstractTableModel, File fileToExportTo)
            throws ExportHelperException
    {
        //Build a document
    	Document pdfDocument = null;
    	String orientation = (String)WizardState.getInstance().getState("Paper Orientation");
    	if( "PORTRAIT".equals(orientation) )
    	{
    		pdfDocument = new Document(PageSize.A4);
    	}
    	else
    	{
    		pdfDocument = new Document(PageSize.A4.rotate());    		
    	}

        try
        {
        	PdfWriter.getInstance( pdfDocument, new FileOutputStream(fileToExportTo) );
        }
        catch(DocumentException exc)
        {
            throw new ExportHelperException(exc.getMessage());
        }
        catch(FileNotFoundException exc)
        {
            throw new ExportHelperException(exc.getMessage());
        }

        //Add document metadata
        pdfDocument.addTitle("Results");
        pdfDocument.addAuthor("DBBrowser");
        pdfDocument.addSubject("Data results");
        pdfDocument.addCreator("DBBrowser");

        //Add header
        String headerString = (String)WizardState.getInstance().getState("Header");
        Phrase headerPhrase = new Phrase( headerString );
        HeaderFooter header = new HeaderFooter(headerPhrase, false);
        header.setAlignment( HeaderFooter.ALIGN_CENTER );
        pdfDocument.setHeader( header );

        //Set the footer
        Object footerString = WizardState.getInstance().getState("Footer");
        Phrase footerPhrase = null;
        
        if(footerString != null)
        {
        	footerPhrase = new Phrase((String)footerString + " - Page - ");
        }
        else
        {
        	footerPhrase = new Phrase("Page - ");        	
        }
        
        HeaderFooter footer = new HeaderFooter(footerPhrase, true);
        footer.setAlignment( HeaderFooter.ALIGN_CENTER );
        pdfDocument.setFooter( footer );

        return pdfDocument;
    }
}