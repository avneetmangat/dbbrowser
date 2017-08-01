package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import org.dbbrowser.ui.helper.exporthelper.wizard.panel.ExportConfirmationWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class ExportConfirmationPanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "EXPORT_CONFIRMATION_PANEL";
    private static ExportConfirmationWizardPanel exportConfirmationWizardPanel = new ExportConfirmationWizardPanel();
    
    public ExportConfirmationPanelDescriptor()
    {
        super(IDENTIFIER, exportConfirmationWizardPanel);
    }
    
    public Object getNextPanelDescriptor() 
    {
        return ExportProgressPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
    	//Get the export type
    	String selectedExportType = (String)WizardState.getInstance().getState("Export type");
    	
    	//if CSV file is selected, then 
    	if("csv".equals( selectedExportType ))
    	{
    		return IncludeTableColumnsPanelDescriptor.IDENTIFIER;
    	}    	
        return PageSetupPanelDescriptor.IDENTIFIER;
    }  
    
    public void aboutToDisplayPanel() 
    {
    	exportConfirmationWizardPanel.setupPanel();
    }    
}
