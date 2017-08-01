package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

public class HeaderFooterWizardPanel extends AbstractWizardPanel implements KeyListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-header-footer-panel-title", null);
	private static final String HEADER_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-header-label", null);
	private static final String FOOTER_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-footer-label", null);
	private JTextField fieldForHeader = new JTextField("");
	private JTextField fieldForFooter = new JTextField("");
	
	/**
	 * Constrcuter
	 */
    public HeaderFooterWizardPanel()
    {
        super(PANEL_TITLE);
        this.add( setupPanel(), BorderLayout.CENTER );			
    }
    
    public void keyTyped(KeyEvent e)
    {       	
    }   
    
    public void keyPressed(KeyEvent e)
    {
    }
    
    public void keyReleased(KeyEvent e)
    {
    	WizardState.getInstance().setState("Header", fieldForHeader.getText());
    	WizardState.getInstance().setState("Footer", fieldForFooter.getText());
    }
    
    private JPanel setupPanel()
    {
    	JPanel headerFooterPanel = new JPanel();
    	headerFooterPanel.setBorder( BorderFactory.createEtchedBorder() );
        
        headerFooterPanel.setLayout(new GridLayout(10,2));
        JLabel labelForHeader = new JLabel(HEADER_LABEL);
        headerFooterPanel.add( labelForHeader );
        headerFooterPanel.add( fieldForHeader );
        JLabel labelForFooter = new JLabel(FOOTER_LABEL);
        headerFooterPanel.add( labelForFooter ); 
        headerFooterPanel.add( fieldForFooter );
        
        //Add key listeners to set value for header and footer as the step listener does not work
        fieldForHeader.addKeyListener( this );
        fieldForFooter.addKeyListener( this );
        
        for(int i=0; i<12; i++)
        {
            JLabel dummyLabel = new JLabel("");
            headerFooterPanel.add( dummyLabel );            	
        }
        
        return headerFooterPanel;
    }    
}
