package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

public class ExportConfirmationWizardPanel extends AbstractWizardPanel
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-confirmation-message", null);;
	private JPanel panel = new JPanel();
	
	/**
	 * Constrcuter
	 * @param wizard
	 */
    public ExportConfirmationWizardPanel()
    {
        super(PANEL_TITLE);
    }
    
    public void setupPanel()
    {
    	panel.setBorder( BorderFactory.createEtchedBorder() );
    	panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
        
        Iterator i = WizardState.getInstance().getWizardState().keySet().iterator();
        while(i.hasNext())
        {
        	Object key = i.next();
        	Object value = WizardState.getInstance().getState( key );
        	JLabel label = new JLabel(key.toString() + ": " + value.toString());
        	panel.add( label );
        }
        
    	this.add( panel, BorderLayout.CENTER );
    } 
    
    public void repaint()
    {
    	if( panel != null )
    	{
    		panel.removeAll();
    		setupPanel();
    	}
    }
}
