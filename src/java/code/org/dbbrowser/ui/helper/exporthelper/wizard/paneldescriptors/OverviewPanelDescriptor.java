package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.panel.OverviewPanelWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class OverviewPanelDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "OVERVIEW_PANEL";
    
    public OverviewPanelDescriptor()
    {
        super(IDENTIFIER, new OverviewPanelWizardPanel());
    }
    
    public Object getNextPanelDescriptor() 
    {
        return ExportTypePanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return null;
    }  
    
}
