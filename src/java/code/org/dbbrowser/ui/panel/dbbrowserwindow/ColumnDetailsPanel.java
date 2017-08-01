package org.dbbrowser.ui.panel.dbbrowserwindow;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.ui.AddNewColumnWindow;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForUpdates;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.widget.Table;

public class ColumnDetailsPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;

    private String addNewRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-add-new-record-icon");
    private String deleteRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-delete-record-icon");

    private Icon iconForAddNewRecordButton = new ImageIcon( addNewRecordIconFileName );
    private Icon iconForDeleteRecordButton = new ImageIcon( deleteRecordIconFileName );

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
	private static final String ADD_NEW_COLUMN_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-add-new-column-button-label", null);;
	private static final String REMOVE_COLUMN_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-remove-column-button-label", null);;
	
	private UIControllerForQueries uiControllerForQueries = null;
	private UIControllerForUpdates uiControllerForUpdates = null;
	
	private List listOfColumnInfos = null;
	private Table tableForColumnInfos = null;
	private ButtonsPanel buttonsPanel = null;
	
	private DBTable dbTableOfColumnDetails = null;

	public ColumnDetailsPanel(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates)
	{
		this.uiControllerForQueries  = uiControllerForQueries;
		this.uiControllerForUpdates = uiControllerForUpdates;
	}
	
	private void initialize()
	{
		//Set the table for data
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Add the table
		this.tableForColumnInfos = new Table( this.uiControllerForQueries, this.uiControllerForUpdates );
		this.add( tableForColumnInfos );
		
		//Set the buttons panel
		Button addNewColumnButton = new Button(ADD_NEW_COLUMN_BUTTON_LABEL,this, ADD_NEW_COLUMN_BUTTON_LABEL, iconForAddNewRecordButton, Boolean.TRUE);
		Button removeColumnButton = new Button(REMOVE_COLUMN_BUTTON_LABEL,this, REMOVE_COLUMN_BUTTON_LABEL, iconForDeleteRecordButton, Boolean.FALSE);
		List listOfButtons = new ArrayList();
		listOfButtons.add( addNewColumnButton );
		listOfButtons.add( removeColumnButton );
		buttonsPanel = new ButtonsPanel( listOfButtons );
		
		this.add( buttonsPanel );
	}
	
	public void initializeTable(DBTable dbTableOfColumnDetails)
	{
        //If the buttons panel has not been set, set it up
        if( this.tableForColumnInfos == null )
        {
            initialize();
        }

        //Initialize table
        this.dbTableOfColumnDetails = dbTableOfColumnDetails;
		
		//Set the title of the panel
		this.tableForColumnInfos.setBorder( BorderFactory.createTitledBorder(dbTableOfColumnDetails.getTableName()) );
		
		//Build the table
		this.listOfColumnInfos = dbTableOfColumnDetails.getListOfColumnInfos();
		this.tableForColumnInfos.initializeTable( new ColumnInfoTableModel() );
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//Add a new column
		if( ADD_NEW_COLUMN_BUTTON_LABEL.equals(e.getActionCommand()))
		{
			AddNewColumnWindow addNewColumnWindow = new AddNewColumnWindow(this.uiControllerForUpdates, this.dbTableOfColumnDetails);
			addNewColumnWindow.show();
        }
		
		//Remove the selected column
		if( REMOVE_COLUMN_BUTTON_LABEL.equals(e.getActionCommand()))
		{
			if( (this.tableForColumnInfos.getSelectedRow() != null) && (this.tableForColumnInfos.getSelectedRow().intValue() != -1) )
			{
				String removeColumnConfirmationMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-remove-column-confirmation-message", null);;
				int ans = JOptionPane.showConfirmDialog(null, removeColumnConfirmationMessage, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				if( ans == 0)
				{
					try
					{
						//Get the selected column
						int selectedRow = this.tableForColumnInfos.getSelectedRow().intValue();
						
						if( selectedRow != -1 )
						{
							//Drop the column
							ColumnInfo ci = (ColumnInfo)this.listOfColumnInfos.get(selectedRow);
							this.uiControllerForUpdates.dropColumn(dbTableOfColumnDetails.getSchemaName(), dbTableOfColumnDetails.getTableName(), ci);
	
							//Update the UI
							this.listOfColumnInfos.remove( this.tableForColumnInfos.getSelectedRow() );
							this.tableForColumnInfos.update();
						}
					}
					catch(DBEngineException exc)
					{
						String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
						JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);				
					}
				}
			}
            else
            {
                //No column has been selected
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-delete-column-no-column-selected-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
	}
	
	/**
	 * Private class representing the model for columns in the table
	 * @author amangat
	 */
	private class ColumnInfoTableModel extends AbstractTableModel 
	{
		private static final long serialVersionUID = 1l;
		
		private String COLUMN_NULLABLE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-nullable-true", null);;
		private String COLUMN_NOT_NULLABLE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-nullable-false", null);;
		private String COLUMN_NULLABLE_NATURE_UNKNOWN = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-nullable-unknown", null);;
		
		private String COLUMN_NAME_STRING_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-column-name", null);; 
		private String DATA_TYPE_STRING_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-data-type", null);; 
		private String COLUMN_SIZE_STRING_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-size", null);; 
		private String NULLABLE_STRING_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-nullable", null);; 

		private String[] listOfColumnInfoHeaders = new String[]{"    ", COLUMN_NAME_STRING_LABEL, DATA_TYPE_STRING_LABEL, COLUMN_SIZE_STRING_LABEL, NULLABLE_STRING_LABEL};
		
	    public String getColumnName(int col) 
	    {
	        return listOfColumnInfoHeaders[col];
	    }
	    
	    public int getRowCount() 
	    { 
	    	return listOfColumnInfos.size(); 
	    }
	    
	    public int getColumnCount() 
	    { 
	    	return listOfColumnInfoHeaders.length; 
	    }
	    
	    public Object getValueAt(int row, int col) 
	    {
	    	String cellValue = null;

	    	if( col==0 )
	    	{
	    		return "" + (row + 1);
	    	}
	    	else
	    	{
	    		ColumnInfo columnInfo = (ColumnInfo)listOfColumnInfos.get( row );
	    		
	    		switch (col) 
	    		{
	    			case 1:  
	    			{
	    				cellValue = columnInfo.getColumnName();
	    				break;
	    			}
	    			case 2:  
	    			{
	    				cellValue = columnInfo.getColumnTypeName();
	    				break;
	    			}
	    			case 3:  
	    			{
	    				cellValue = columnInfo.getColumnDisplaySize().intValue() + "";
	    				break;
	    			}
	    			case 4:  
	    			{
	    				String nullableNature = columnInfo.getNullableNature();
	    				if(ColumnInfo.COLUMN_NULLABLE.equals( nullableNature ))
	    				{
	    					cellValue = COLUMN_NULLABLE_LABEL;
	    				}
	    				if(ColumnInfo.COLUMN_NOT_NULLABLE.equals( nullableNature ))
	    				{
	    					cellValue = COLUMN_NOT_NULLABLE_LABEL;
	    				}
	    				if(ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN.equals( nullableNature ))
	    				{
	    					cellValue = COLUMN_NULLABLE_NATURE_UNKNOWN;
	    				}
	    				
	    				break;
	    			}
	    		}
	    	}

	        return cellValue;
	    }
	}	
	
}
