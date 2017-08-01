package org.dbbrowser.ui.helper.exporthelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Class used to export a table to Excel file
 */
public class ExporterForXLSFile implements ExportHelper 
{
	private static WritableWorkbook xlsWorkBook = null;
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

		xlsWorkBook = setupXLSWorkBook(abstractTableModel, fileToExportTo);
		WritableSheet workSheet = xlsWorkBook.createSheet("data sheet", 1);

		int rowCount = abstractTableModel.getRowCount();
		int columnCount = abstractTableModel.getColumnCount();
		// keeps info of the max character count for each column
		int[] maxCharacterCount = new int[columnCount];

		// Set to 0 if you want to include row number
		int startPosition = 0;

		// Processing column names - start from 1 as the first column is the row
		// number
		WritableFont verdanaFont = new WritableFont(WritableFont.TAHOMA, 12);
		WritableCellFormat coloredFormat = new WritableCellFormat();
		WritableCellFormat simpleFormat = new WritableCellFormat();
		coloredFormat.setFont(verdanaFont);
		simpleFormat.setFont(verdanaFont);
		
		try
		{
			simpleFormat.setAlignment(Alignment.CENTRE);
			coloredFormat.setAlignment(Alignment.CENTRE);
			coloredFormat.setBackground(Colour.GRAY_25);
	        //Process each row
			for (int rowIndex = startPosition; rowIndex < rowCount; rowIndex++) 
			{
	        	//If stopped, exit and export incomplete table
	        	if( stop == true )
	        	{
	        		break;
	        	}
				
				// Process each column in the row - start from 1 as the first column
				// is the row number
				for (int columnIndex = startPosition; columnIndex < columnCount; columnIndex++) {
					Object cell = abstractTableModel.getValueAt(rowIndex,
							columnIndex);
	
					// Change put in for demo only - please remove
					Class aClass = abstractTableModel.getColumnClass(columnIndex);
					if (aClass.getName().equals("java.lang.Number")) {
						if (cell != null) {
							String value = cell.toString();
							Number tableCell = new Number(columnIndex,
									rowIndex + 2, Double.parseDouble(value));
							tableCell.setCellFormat(simpleFormat);
							workSheet.addCell(tableCell);
							maxCharacterCount[columnIndex] = maxCharacterCount[columnIndex] <= value
									.length() ? value.length()
									: maxCharacterCount[columnIndex];
						} else {
							Label tableCell = new Label(columnIndex, rowIndex + 2,
									"");
							tableCell.setCellFormat(simpleFormat);
							workSheet.addCell(tableCell);
						}
					} else {
						if (aClass.getName().equals("java.util.Date")) {
							if (cell != null) {
								String value = cell.toString();
								DateTime tableCell = new DateTime(columnIndex,
										rowIndex + 2, new Date(value));
								tableCell.setCellFormat(simpleFormat);
								workSheet.addCell(tableCell);
								maxCharacterCount[columnIndex] = maxCharacterCount[columnIndex] <= value
										.length() ? value.length()
										: maxCharacterCount[columnIndex];
							} else {
								Label tableCell = new Label(columnIndex,
										rowIndex + 2, "");
								tableCell.setCellFormat(simpleFormat);
								workSheet.addCell(tableCell);
							}
						} else {
							if (cell != null) {
	
								String value = cell.toString();
								Label tableCell = new Label(columnIndex,
										rowIndex + 2, value);
								tableCell.setCellFormat(simpleFormat);
								workSheet.addCell(tableCell);
								maxCharacterCount[columnIndex] = maxCharacterCount[columnIndex] <= value
										.length() ? value.length()
										: maxCharacterCount[columnIndex];
							} else {
								Label tableCell = new Label(columnIndex,
										rowIndex + 2, "");
								tableCell.setCellFormat(simpleFormat);
								workSheet.addCell(tableCell);
							}
						}
					}
					// End of change
	
				}
				
	           //Inform the change listener
	           changeListener.stateChanged( new ChangeEvent(new Integer(rowIndex) ));				
			}
	
			for (int columnIndex = startPosition; columnIndex < columnCount; columnIndex++) {
				String columnName = abstractTableModel.getColumnName(columnIndex);
				Label tableCell = new Label(columnIndex, 1, columnName);
	
				tableCell.setCellFormat(coloredFormat);
	
				// workSheet.setColumnView(columnIndex,fMetrics.stringWidth(columnName));
				workSheet.addCell(tableCell);
				maxCharacterCount[columnIndex] = maxCharacterCount[columnIndex] <= columnName
						.length() ? columnName.length()
						: maxCharacterCount[columnIndex];
	
				workSheet.setColumnView(columnIndex,
						maxCharacterCount[columnIndex] + 10);
	
			}
			
			// write and flush the workbook			
			xlsWorkBook.write();
			xlsWorkBook.close();			
		}
		catch(WriteException exc)
		{
			throw new ExportHelperException( exc.getMessage() );
		}
		catch(IOException exc)
		{
			throw new ExportHelperException( exc.getMessage() );
		}		
	}
	
	/**
	 * Call this to stop the export.  This is required as it may be a long process and it runs in a seperate thread
	 */
	public void stop()
	{
		this.stop = true;		
	}	

	private static WritableWorkbook setupXLSWorkBook(
			AbstractTableModel abstractTableModel, File fileToExportTo)
			throws ExportHelperException 
			{
		WritableWorkbook xlsWrkBook = null;
		try
		{
			// Build a workbook
			xlsWrkBook = Workbook.createWorkbook(fileToExportTo);
		}
		catch(IOException exc)
		{
			throw new ExportHelperException( exc.getMessage() );
		}
		return xlsWrkBook;
	}
}
