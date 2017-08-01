package org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import infrastructure.internationalization.InternationalizationManager;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.helper.exporthelper.ExportHelper;
import org.dbbrowser.ui.helper.exporthelper.ExportHelperFactory;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;
import org.dbbrowser.ui.helper.exporthelper.wizard.panel.ExportWizardProgressPanel;
import com.nexes.wizard.WizardPanelDescriptor;

public class ExportProgressPanelDescriptor extends WizardPanelDescriptor implements Runnable 
{
    public static final String IDENTIFIER = "EXPORT_PROGRESS_PANEL";
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private AbstractTableModel abstractTableModel = null;
    private ExportHelper exportHelper = null;
    
    public ExportProgressPanelDescriptor(AbstractTableModel abstractTableModel)
    {
        super(IDENTIFIER, new ExportWizardProgressPanel());
        this.abstractTableModel = abstractTableModel;
    }
    
    public Object getNextPanelDescriptor() 
    {
        return WizardPanelDescriptor.FINISH;
    }
    
    public Object getBackPanelDescriptor() 
    { 	
        return ExportConfirmationPanelDescriptor.IDENTIFIER;
    }  
    
    public void aboutToDisplayPanel() 
    {
    	((ExportWizardProgressPanel)getPanelComponent()).setupPanel();
    	getWizard().setNextFinishButtonEnabled( false );
    	getWizard().setBackButtonEnabled( false );
    }   
    
    public void displayingPanel() 
    {
    	//Set the maximum value
    	((ExportWizardProgressPanel)getPanelComponent()).setMaximumValue(abstractTableModel.getRowCount() - 1);
    	
    	//Start a new thread to do the export
    	Thread t = new Thread(this);
    	t.start();
    }
    
    public void run()
    {
    	try
    	{
    		//Add this as a listener for window closing event.  Stop thread when window is closed
    		this.getWizard().getDialog().addWindowListener( new WindowCloseListener() );
    		
    		//Start the export
    		this.exportHelper = ExportHelperFactory.getExportHelper((String)WizardState.getInstance().getState("Export type"));
    		this.exportHelper.export(this.abstractTableModel, ((ExportWizardProgressPanel)getPanelComponent()), new File( (String)WizardState.getInstance().getState("Save as") ));

    		//Enable Finish button
    		getWizard().setNextFinishButtonEnabled( true );
    		getWizard().setCancelButtonEnabled( false );
    	}
    	catch(Exception exc)
    	{
    		Object[] params = new Object[]{exc.getMessage()};
    		String ERROR_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-export-failed-message", params);;
    		JOptionPane.showMessageDialog(null, ERROR_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE, null);
    		
    		//Enable Cancel and previous button
    		getWizard().setBackButtonEnabled( true );
    	}
    } 
    
    private class WindowCloseListener extends WindowAdapter
    {
    	public void windowClosed(WindowEvent e)
    	{
    		if( exportHelper != null )
    		{
    			exportHelper.stop();
    		}
    	}
    }
}
