package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.dbbrowser.ui.UIControllerForQueries;

/**
 * Overview panel in a wizard.  
 * @author Amangat
 */
public class OverviewPanelWizardPanel extends AbstractWizardPanel
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-overview-panel-title", null);;
	
	/**
	 * Constructer
	 */
	public OverviewPanelWizardPanel()
	{
		super(PANEL_TITLE);
        this.add( setupPanel(), BorderLayout.CENTER );			
	}

	private JPanel setupPanel()
    {
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createEtchedBorder() );
		JEditorPane editorPane = null;
		
        String locationOfOverviewHTMLFile = PropertyManager.getInstance().getProperty("dbbrowser-export-wizard-overview-html-file-location");
        try
        {
        	//Set the page on a JEditorPane - this can be an HTML page
        	editorPane = new JEditorPane( (new File( locationOfOverviewHTMLFile ) ).toURL() );
        	editorPane.setEditable( false );
        }
        catch(MalformedURLException exc)
        {
        	editorPane.setText( exc.getMessage() );
        }
        catch(IOException exc)
        {
        	editorPane.setText( exc.getMessage() );
        }
        
        //Add to panel
		JScrollPane pane = new JScrollPane(editorPane);
        panel.setLayout( new BorderLayout() );
        panel.add( pane, BorderLayout.CENTER );
        
        return panel;
    }     
}
