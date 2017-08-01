package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.dbbrowser.ui.UIControllerForQueries;

public class AbstractWizardPanel extends JPanel
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	private static final String FILENAME_FOR_ICON = PropertyManager.getInstance().getProperty( "dbbrowser-export-wizard-image");
	private String panelTitle = "";
	private JLabel labelForImage = null;
	private JLabel title = null;
	
	/**
	 * Constructer
	 *
	 */
	public AbstractWizardPanel(String panelTitle)
	{
		this.panelTitle = panelTitle;
		labelForImage = new JLabel(new ImageIcon(FILENAME_FOR_ICON));
		title = new JLabel(this.panelTitle, JLabel.CENTER);
		title.setBorder( BorderFactory.createEtchedBorder() );
		
		this.setLayout(new BorderLayout(20, 20));

		//Add splash screen image on left
        this.add(labelForImage, BorderLayout.WEST);
        
        //Add title on top
        this.add( title, BorderLayout.NORTH );	
        
        this.setSize( new Dimension(600, 400) );
	}
}
