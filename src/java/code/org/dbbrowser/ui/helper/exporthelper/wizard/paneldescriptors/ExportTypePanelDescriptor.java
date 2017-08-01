package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.panel.ExportTypeWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class ExportTypePanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "EXPORT_TYPE_PANEL";
    
    public ExportTypePanelDescriptor()
    {
        super(IDENTIFIER, new ExportTypeWizardPanel());
    }
    
    public Object getNextPanelDescriptor() 
    {
        return DateFormatPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return OverviewPanelDescriptor.IDENTIFIER;
    } 
}
