package org.dbbrowser.ui.helper.exporthelper.wizard.panel;

import infrastructure.internationalization.InternationalizationManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;

/**
 * Panel to select columns to include in the report
 * @author amangat
 */
public class IncludeTableColumnsWizardPanel extends AbstractWizardPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;		
	private static final String PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-select-columns-to-include-panel-title", null);;
	private static final String SELECT_ALL_COLUMNS_BUTTONS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-select-columns-to-include-panel-select-all-button-label", null);;
	private static final String DESELECT_ALL_COLUMNS_BUTTONS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-export-wizard", "dbbrowser-ui-export-wizard-select-columns-to-include-panel-deselect-all-button-label", null);;
	
	private AbstractTableModel abstractTableModel = null;
	private List listOfCheckBoxForColumns = new ArrayList();

	/**
	 * Constrcuter
	 */
    public IncludeTableColumnsWizardPanel(AbstractTableModel abstractTableModel)
    {
        super(PANEL_TITLE);
    	this.abstractTableModel = abstractTableModel;
    	setupPanel();
    }
    
    public List getListOfColumnsToIncludeInExport()
    {
    	//Iterate through each checkbox and add it to list if it is checked
    	List listOfColumnsToIncludeInExport = new ArrayList();
    	Iterator i = listOfCheckBoxForColumns.iterator();
    	while( i.hasNext() )
    	{
    		JCheckBox box = (JCheckBox)i.next();
    		
    		//if box is checked, add it to list
    		if( box.isSelected() )
    		{
    			listOfColumnsToIncludeInExport.add( box.getText() );
    		}
    	}
    	return listOfColumnsToIncludeInExport;
    }
    
    public void actionPerformed(ActionEvent e) 
    {
    	if( SELECT_ALL_COLUMNS_BUTTONS_LABEL.equals( e.getActionCommand() ) )
    	{
    		Iterator i = listOfCheckBoxForColumns.iterator();
    		while( i.hasNext() )
    		{
    			JCheckBox checkBox = (JCheckBox)i.next();
    			checkBox.setSelected( true );
    		}
    	}
    	
    	if( DESELECT_ALL_COLUMNS_BUTTONS_LABEL.equals( e.getActionCommand() ) )
    	{
    		Iterator i = listOfCheckBoxForColumns.iterator();
    		while( i.hasNext() )
    		{
    			JCheckBox checkBox = (JCheckBox)i.next();
    			checkBox.setSelected( false );
    		}
    	}        	
    }
    
    private void setupPanel()
    {
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createEtchedBorder() );
		
    	//Set the layout
		panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
    	
    	//Add the widgets
    	Button buttonToSelectAllColumns = new Button(SELECT_ALL_COLUMNS_BUTTONS_LABEL, this, SELECT_ALL_COLUMNS_BUTTONS_LABEL, null, Boolean.FALSE);
    	Button buttonToDeSelectAllColumns = new Button(DESELECT_ALL_COLUMNS_BUTTONS_LABEL, this, DESELECT_ALL_COLUMNS_BUTTONS_LABEL, null, Boolean.FALSE);
    	List listOfButtons = new ArrayList();
    	listOfButtons.add( buttonToSelectAllColumns );
    	listOfButtons.add( buttonToDeSelectAllColumns );
    	ButtonsPanel buttonsPanel = new ButtonsPanel(listOfButtons);
    	panel.add( buttonsPanel );
    	
    	//Panel for checkboxes for columns
    	JPanel panelForCheckboxes = new JPanel();
    	panelForCheckboxes.setLayout( new BoxLayout(panelForCheckboxes, BoxLayout.Y_AXIS) );
    	JScrollPane pane = new JScrollPane(panelForCheckboxes);
    	
        //Add a checkbox for each column in the table - start from 1 as the first(0) column is column header
    	if(abstractTableModel != null)
    	{
	        for(int i=1; i<abstractTableModel.getColumnCount(); i++)
	        {
	        	String columnName = abstractTableModel.getColumnName( i );
	        	JCheckBox checkBox = new JCheckBox(columnName);
	        	checkBox.setSelected( true );
	        	panelForCheckboxes.add( checkBox );
	        	listOfCheckBoxForColumns.add( checkBox );
	        }
    	}
    	
    	panel.add( pane );
        
    	this.add( panel, BorderLayout.CENTER );
    }
}
