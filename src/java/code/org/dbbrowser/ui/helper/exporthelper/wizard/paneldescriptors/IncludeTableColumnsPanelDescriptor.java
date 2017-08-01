package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import org.dbbrowser.ui.helper.exporthelper.wizard.panel.IncludeTableColumnsWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class IncludeTableColumnsPanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "INCLUDE_TABLE_COLUMNS_PANEL";
    
    public IncludeTableColumnsPanelDescriptor(AbstractTableModel abstractTableModel)
    {
        super(IDENTIFIER, new IncludeTableColumnsWizardPanel(abstractTableModel));
    }
    
    public Object getNextPanelDescriptor() 
    {
    	//Get the export type
    	String selectedExportType = (String)WizardState.getInstance().getState("Export type");
    	
    	//if CSV file is selected, then 
    	if("pdf".equals( selectedExportType ))
    	{
            return HeaderFooterPanelDescriptor.IDENTIFIER;
    	}    	
		return ExportConfirmationPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return DateFormatPanelDescriptor.IDENTIFIER;
    }
    
    public void aboutToHidePanel()
    {
    	List listOfColumnsToIncludeInExport = ((IncludeTableColumnsWizardPanel)getPanelComponent()).getListOfColumnsToIncludeInExport();
    	WizardState.getInstance().setState("List of columns to include", listOfColumnsToIncludeInExport);
    }
}
