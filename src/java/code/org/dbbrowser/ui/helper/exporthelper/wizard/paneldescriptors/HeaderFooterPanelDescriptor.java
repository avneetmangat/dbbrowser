package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.panel.HeaderFooterWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class HeaderFooterPanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "HEADER_FOOTER_PANEL";
    
    public HeaderFooterPanelDescriptor()
    {
        super(IDENTIFIER, new HeaderFooterWizardPanel());
    }
    
    public Object getNextPanelDescriptor() 
    {
        return PageSetupPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return IncludeTableColumnsPanelDescriptor.IDENTIFIER;
    }  
}
