package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.helper.exporthelper.wizard.WizardState;

/**
 * Panel to get the export type from the user
 * @author amangat
 *
 */
public class ExportTypeWizardPanel extends AbstractWizardPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
    
	public static final String PDF = "pdf";
	public static final String CSV = "csv";
	public static final String EXCEL = "xls"; 
	public static final String SQL = "sql"; 
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-export-panel-title", null);;
	private static final String SAVE_AS_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-save-as-button-label", null);;
	private String locationOfFileToExportTo = "export";
	private JTextField fieldForFilename = null;
	private static final String SAVE_AS_BUTTON_ACTION_COMMAND = "SAVE_AS";
	
	/**
	 * Constructer
	 */
	public ExportTypeWizardPanel()
	{
		super(PANEL_TITLE);
		
		//Set the default export type to be CSV
		WizardState.getInstance().setState("Export type", CSV);
		WizardState.getInstance().setState("Save as", locationOfFileToExportTo + "." + CSV);
		
        this.add( setupPanel(), BorderLayout.CENTER );					
	}	
    
    public void actionPerformed(ActionEvent e)
    {
    	if( SAVE_AS_BUTTON_ACTION_COMMAND.equals( e.getActionCommand() ))
    	{
    		JFileChooser jfc = new JFileChooser();
    		int ans = jfc.showSaveDialog(null);
    		if( JFileChooser.APPROVE_OPTION == ans )
    		{
    			File selectedFile = jfc.getSelectedFile();
    			this.fieldForFilename.setText(selectedFile.getAbsolutePath());
    			WizardState.getInstance().setState("Save as", selectedFile.getAbsolutePath()); 
    		}
    	}
    	else
    	{
	    	//Store the state in the WizardState
	    	WizardState.getInstance().setState("Export type", e.getActionCommand());
			WizardState.getInstance().setState("Save as", locationOfFileToExportTo + "." + e.getActionCommand()); 
	        File f = new File(locationOfFileToExportTo + "." + e.getActionCommand());
	        this.fieldForFilename.setText(f.getAbsolutePath());
    	}
    }

    private JPanel setupPanel()
    {
    	JPanel panel = new JPanel();
    	panel.setBorder( BorderFactory.createEtchedBorder() );
    	panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
    	
        //Add the radio button for export type
        JRadioButton csvRadioButton = new JRadioButton(CSV); 
        JRadioButton pdfRadioButton = new JRadioButton(PDF); 
        JRadioButton excelRadioButton = new JRadioButton(EXCEL); 
        JRadioButton sqlRadioButton = new JRadioButton(SQL); 
        buttonGroup.add( pdfRadioButton );
        buttonGroup.add( csvRadioButton );
        buttonGroup.add( excelRadioButton );
        buttonGroup.add( sqlRadioButton );
        pdfRadioButton.addActionListener( this );
        csvRadioButton.addActionListener( this );
        excelRadioButton.addActionListener( this );
        sqlRadioButton.addActionListener( this );
        csvRadioButton.setSelected( true );
        
        //Add the buttons
        panel.add( csvRadioButton );
        panel.add( pdfRadioButton );
        panel.add( excelRadioButton );
        panel.add( sqlRadioButton );
                
        //Add the textfield and button for location of file
        File f = new File(locationOfFileToExportTo + "." + CSV);
        this.fieldForFilename = new JTextField(f.getAbsolutePath());
        JButton buttonForFile = new JButton(SAVE_AS_BUTTON_LABEL);
        buttonForFile.setActionCommand(SAVE_AS_BUTTON_ACTION_COMMAND);
        buttonForFile.addActionListener( this );
        JPanel panelForFileLocation = new JPanel();
        panelForFileLocation.setLayout( new BoxLayout(panelForFileLocation, BoxLayout.X_AXIS) );
        panelForFileLocation.add( fieldForFilename );
        panelForFileLocation.add( buttonForFile );
        panelForFileLocation.setMaximumSize(new Dimension(panelForFileLocation.getMaximumSize().width, 20));
        panel.add( panelForFileLocation );
        
        return panel;
    }    
}
