package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import org.dbbrowser.ui.helper.exporthelper.wizard.panel.DateFormatWizardPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class DateFormatPanelDescriptor extends WizardPanelDescriptor 
{
    public static final String IDENTIFIER = "DATE_FORMAT_PANEL";
    
    public DateFormatPanelDescriptor()
    {
        super(IDENTIFIER, new DateFormatWizardPanel());
    }
    
    public Object getNextPanelDescriptor() 
    {
        return IncludeTableColumnsPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() 
    {
        return ExportTypePanelDescriptor.IDENTIFIER;
    }  
    
    public void aboutToHidePanel() 
    {
    	String dateFormatPattern = ((DateFormatWizardPanel)getPanelComponent()).getDateFormatPattern();
    	WizardState.getInstance().setState("Date format", dateFormatPattern);
    }    
}
