package org.dbbrowser.ui.helper.exporthelper;

import java.io.File;

import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

public interface ExportHelper
{
	/**
	 * Export the data in the table to the specified file as a PDF file
	 * @param abstractTableModel
	 * @param changeListener
	 * @param fileToExportTo
	 * @throws ExportHelperException
	 */
	public void export(AbstractTableModel abstractTableModel, ChangeListener changeListener, File fileToExportTo) 
		throws ExportHelperException;
	
	/**
	 * Call this to stop the export.  This is required as it may be a long process and it runs in a seperate thread
	 */
	public void stop();
}
