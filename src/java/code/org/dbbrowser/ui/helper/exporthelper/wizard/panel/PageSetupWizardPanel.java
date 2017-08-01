package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

public class PageSetupWizardPanel extends AbstractWizardPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-page-setup-format-panel-title", null);;
    private static final String LANDSCAPE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage("dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-landscape-button-label", null);
    private static final String PORTRAIT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage("dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-portrait-button-label", null);
    private static final String PORTRAIT = "PORTRAIT";
    private static final String LANDSCAPE = "LANDSCAPE";
    private String paperOrientation = PORTRAIT;
	
    /**
	 * Constrcuter
	 * @param wizard
	 */
    public PageSetupWizardPanel()
    {
        super(PANEL_TITLE);
        this.add( setupPanel(), BorderLayout.CENTER );			
    }
    
    private JPanel setupPanel()
    {
    	JPanel panel = new JPanel();
    	panel.setBorder( BorderFactory.createEtchedBorder() );
    	panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
    	
        //Setup the radio button for page format
    	JRadioButton buttonForLandscapeFormat = new JRadioButton(LANDSCAPE_BUTTON_LABEL);
    	buttonForLandscapeFormat.setActionCommand( LANDSCAPE );
    	JRadioButton buttonForPortraitFormat = new JRadioButton(PORTRAIT_BUTTON_LABEL);
    	buttonForPortraitFormat.setActionCommand( PORTRAIT );
    	panel.add(buttonForLandscapeFormat);
    	panel.add(buttonForPortraitFormat);
    	ButtonGroup buttonGroup = new ButtonGroup();
    	buttonGroup.add( buttonForLandscapeFormat );
    	buttonGroup.add( buttonForPortraitFormat );
    	buttonForLandscapeFormat.addActionListener( this );
    	buttonForPortraitFormat.addActionListener( this );
    	
    	//Set the default paper orientation to be portrait
    	buttonForPortraitFormat.setSelected( true );

        return panel;
    } 
    
    public String getPaperOrientation()
    {
    	return this.paperOrientation;
    }

    public void actionPerformed(ActionEvent e)
    {
    	WizardState.getInstance().setState("Paper Orientation", e.getActionCommand());
    	paperOrientation = e.getActionCommand();
    }
}
