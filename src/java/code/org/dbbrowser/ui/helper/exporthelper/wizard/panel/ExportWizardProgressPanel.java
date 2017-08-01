package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.ui.UIControllerForQueries;

public class ExportWizardProgressPanel extends AbstractWizardPanel implements ChangeListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-progress-panel-title-message", null);;
	private JProgressBar progressBar = null;
	
	/**
	 * Constrcuter
	 * @param wizard
	 */
    public ExportWizardProgressPanel()
    {
        super(PANEL_TITLE);
    }
    
    public void setupPanel()
    {
    	JPanel panel = new JPanel();
    	panel.setBorder( BorderFactory.createEtchedBorder() );
    	panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
    	
    	progressBar = new JProgressBar();
    	progressBar.setMinimum(0);
    	progressBar.setValue(0);
    	progressBar.setStringPainted( true );
    	panel.add( progressBar );
        
        this.add( panel, BorderLayout.CENTER );			
    }
    
    public void setMaximumValue(int max)
    {
    	progressBar.setMaximum( max );
    }
    
    public void stateChanged(ChangeEvent e)
    {
    	int value = ((Integer)e.getSource()).intValue();
    	progressBar.setValue( value );
    }
}
