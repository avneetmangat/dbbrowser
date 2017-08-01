package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import org.dbbrowser.ui.helper.exporthelper.wizard.panel.PageSetupWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class PageSetupPanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "PAGE_SETUP_PANEL";
    
    public PageSetupPanelDescriptor()
    {
        super(IDENTIFIER, new PageSetupWizardPanel());
    }
    
    public Object getNextPanelDescriptor() 
    {
        return ExportConfirmationPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return HeaderFooterPanelDescriptor.IDENTIFIER;
    }  
    
    public void aboutToHidePanel() 
    {
    	String paperOrientation = ((PageSetupWizardPanel)getPanelComponent()).getPaperOrientation();
    	WizardState.getInstance().setState("Paper Orientation", paperOrientation);
    }      
}
