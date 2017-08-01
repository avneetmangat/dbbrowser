package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.dbbrowser.ui.UIControllerForQueries;

public class DateFormatWizardPanel extends AbstractWizardPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;	
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-date-format-panel-title", null);;
    private JComboBox comboBoxForDateFormats = null;
    private static final ExportWizardSimpleDateFormat[] dateFormats = new ExportWizardSimpleDateFormat[]{
    			new ExportWizardSimpleDateFormat("dd/MM/yyyy HH:mm a"),
    			new ExportWizardSimpleDateFormat("yyyy-MM-dd"),
    			new ExportWizardSimpleDateFormat("dd-MM-yyyy"),
    			new ExportWizardSimpleDateFormat("dd/MM/yyyy"),
    			new ExportWizardSimpleDateFormat("MM/dd/yyyy"),
    			new ExportWizardSimpleDateFormat("MM-dd-yyyy"),
    			new ExportWizardSimpleDateFormat("MM/dd/yyyy HH:mm a"),    			
    			new ExportWizardSimpleDateFormat("MM-dd-yyyy HH:mm a")
    			};
    private String dateFormatPattern = "";

    /**
	 * Constrcuter
	 * @param wizard
	 */
    public DateFormatWizardPanel()
    {
        super(PANEL_TITLE);
        this.add( setupPanel(), BorderLayout.CENTER );			
    }
    
    private JPanel setupPanel()
    {
    	JPanel panel = new JPanel();
    	panel.setBorder( BorderFactory.createEtchedBorder() );
    	panel.setLayout( new BorderLayout() );
    	
        //Setup the combo box
        comboBoxForDateFormats = new JComboBox( dateFormats );
        comboBoxForDateFormats.setEditable( false );
        comboBoxForDateFormats.addActionListener( this );
        
        //Store the first date format as default
        this.dateFormatPattern = dateFormats[0].toString();
        
        //Add the combo box
        panel.add( comboBoxForDateFormats, BorderLayout.NORTH );    	

        return panel;
    } 

    public void actionPerformed(ActionEvent e)
    {
    	this.dateFormatPattern = dateFormats[this.comboBoxForDateFormats.getSelectedIndex()].toString();
    }
    
    public String getDateFormatPattern()
    {
    	return this.dateFormatPattern;
    }
    
    /**
     * Private class to represent a date format - overrides toString method to return the pattern used to format/parse dates
     * @author amangat
     */
    private static class ExportWizardSimpleDateFormat
    {
    	private String pattern = null;
    	private DateFormat dateFormat = null;
    	
    	public ExportWizardSimpleDateFormat(String pattern)
    	{
    		this.pattern = pattern;
    		this.dateFormat = new SimpleDateFormat(pattern);
    	}
    	
    	public String toString()
    	{
    		return this.pattern;
    	}
    	
    	public DateFormat getSimpleDateFormat()
    	{
    		return this.dateFormat;
    	}
    }
}
