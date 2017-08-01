package org.dbbrowser.ui.panel.dbbrowserwindow;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import infrastructure.logging.Log;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.filter.Filter;
import org.dbbrowser.ui.*;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.widget.Table;

public class TableDataPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);

    private String addNewRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-add-new-record-icon");
    private String deleteRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-delete-record-icon");
    private String addEditFilterIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-add-edit-filter-icon");

    private Icon iconForAddNewRecordButton = new ImageIcon( addNewRecordIconFileName );
    private Icon iconForDeleteRecordButton = new ImageIcon( deleteRecordIconFileName );
    private Icon iconForAddEditFilterButton = new ImageIcon( addEditFilterIconFileName );

    private String firstRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-first-record-icon");
    private String lastRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-last-record-icon");
    private String previousRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-previous-record-icon");
    private String nextRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-next-record-icon");

    private Icon iconForFirstPageButton = new ImageIcon( firstRecordIconFileName );
    private Icon iconForLastPageButton = new ImageIcon( lastRecordIconFileName );
    private Icon iconForPreviousPageButton = new ImageIcon( previousRecordIconFileName );
    private Icon iconForNextPageButton = new ImageIcon( nextRecordIconFileName );

    private static final String ADD_NEW_RECORD_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-add-new-record-button-label", null);
	private static final String DELETE_RECORD_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-delete-record-button-label", null);
	private static final String ADD_FILTER_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-add-new-filter-button-label", null);

	private static Integer pagingSize = new Integer( Integer.parseInt( PropertyManager.getInstance().getProperty("dbbrowser-ui-browser-window-paging-size") ) );	
    private static final String FIRST_PAGE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-first-page-button-label", new Integer[]{pagingSize});
    private static final String LAST_PAGE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-last-page-button-label", new Integer[]{pagingSize});
	private static final String PREVIOUS_PAGE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-previous-page-button-label", new Integer[]{pagingSize});
	private static final String NEXT_PAGE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-next-page-button-label", new Integer[]{pagingSize});
    private static final String DELETE_RECORD_CONFIRMATION_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-delete-record-confirm-message", null);

    private UIControllerForQueries uiControllerForQueries = null;
	private UIControllerForUpdates uiControllerForUpdates = null;
	
	private Table queryResultsTablePanel = null;
	private ButtonsPanel pagingButtonsPanel = null;
	private ButtonsPanel buttonsPanel = null;
	private JPanel panelForFilterSetMessage = null;

    private DBTable dbTable = null;
    //private Filter filter = null;
    
    private Map mapOfTableNameToFilter = new HashMap();
	
	public TableDataPanel(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates)
	{
		this.uiControllerForQueries = uiControllerForQueries;
		this.uiControllerForUpdates = uiControllerForUpdates;
	}
	
	public Filter getFilter(String tableName)
	{
		Object o = this.mapOfTableNameToFilter.get(tableName);
		if( o != null )
		{
			return (Filter)o;
		}
		return null;
	}
	
	public void initializeTable(DBTable dbTable)
	{
        //If the panels have not been set, set them up
        if(this.pagingButtonsPanel == null)
        {
            this.initialize();
        }

        this.dbTable = dbTable;
		DBTableDataTableModel dbTableDataTableModel = new DBTableDataTableModel(dbTable);
		this.queryResultsTablePanel.initializeTable(dbTableDataTableModel);
		
		Integer offset = this.dbTable.getOffset();
		Integer numberOfPagesToReturn = new Integer( this.dbTable.getNumberOfRowsToReturn().intValue() + offset.intValue() );
        Integer numberOfRowsInTable = this.dbTable.getNumberOfRowsInTable();

        //if the numberOfPagesToReturn greater than number of rows, then display the number of rows in the table
        Integer[] i = null;
        if( numberOfPagesToReturn.intValue() > numberOfRowsInTable.intValue() )
        {
            i = new Integer[]{offset, numberOfRowsInTable, numberOfRowsInTable};
        }
        else
        {
            i = new Integer[]{offset, numberOfPagesToReturn, numberOfRowsInTable};
        }

        String TABLE_PANEL_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-panel-title", i);

		this.queryResultsTablePanel.setBorder( BorderFactory.createTitledBorder(this.dbTable.getTableName() + " - " + TABLE_PANEL_LABEL) );
		
		//Rebuild the buttons panel
    	Object o = this.mapOfTableNameToFilter.get(this.dbTable.getTableName());
    	if( o != null )
    	{
        	if( this.panelForFilterSetMessage == null )
        	{
            	this.panelForFilterSetMessage = new JPanel();
            	JLabel labelForFilterSetMessage = new JLabel("Data has been filtered");
            	labelForFilterSetMessage.setIcon( iconForAddEditFilterButton );
            	this.panelForFilterSetMessage.add( labelForFilterSetMessage );
            	this.add( this.panelForFilterSetMessage, 1 ); 
            	this.remove( this.pagingButtonsPanel );
        	}    		
    	}
    	else
    	{
	    	if( this.panelForFilterSetMessage != null )
	    	{
	    		this.add( this.pagingButtonsPanel, 1 );
	    		this.remove( this.panelForFilterSetMessage );
	    		this.panelForFilterSetMessage = null;
	    	}    		
    	}	
    	this.updateUI();
	}
	
	private void initialize()
	{
		//Set the layout
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Add the buttons for paging to the button panel
        Button firstPageButton = new Button(FIRST_PAGE_BUTTON_LABEL, this, FIRST_PAGE_BUTTON_LABEL, iconForFirstPageButton, Boolean.FALSE);
        Button lastPageButton = new Button(LAST_PAGE_BUTTON_LABEL, this, LAST_PAGE_BUTTON_LABEL, iconForLastPageButton, Boolean.FALSE);
		Button previousPageButton = new Button(PREVIOUS_PAGE_BUTTON_LABEL, this, PREVIOUS_PAGE_BUTTON_LABEL, iconForPreviousPageButton, Boolean.FALSE);
		Button nextPageButton = new Button(NEXT_PAGE_BUTTON_LABEL, this, NEXT_PAGE_BUTTON_LABEL, iconForNextPageButton, Boolean.FALSE);
		ArrayList listOfButtons = new ArrayList();
        listOfButtons.add(firstPageButton);
		listOfButtons.add(previousPageButton);
		listOfButtons.add(nextPageButton);
        listOfButtons.add(lastPageButton);
		this.pagingButtonsPanel = new ButtonsPanel(listOfButtons);
		
		//Add the buttons to the button panel
		Button addNewRecordButton = new Button(ADD_NEW_RECORD_BUTTON_LABEL, this, ADD_NEW_RECORD_BUTTON_LABEL, iconForAddNewRecordButton, Boolean.FALSE);
		Button deleteRecordButton = new Button(DELETE_RECORD_BUTTON_LABEL, this, DELETE_RECORD_BUTTON_LABEL, iconForDeleteRecordButton, Boolean.FALSE);
		Button addFilterButton = new Button(ADD_FILTER_BUTTON_LABEL, this, ADD_FILTER_BUTTON_LABEL, iconForAddEditFilterButton, Boolean.FALSE);
		listOfButtons = new ArrayList();
		listOfButtons.add(addNewRecordButton);
		listOfButtons.add(deleteRecordButton);
		listOfButtons.add(addFilterButton);
		this.buttonsPanel = new ButtonsPanel(listOfButtons);
		
		//Setup the query results panel
		queryResultsTablePanel = new Table(this.uiControllerForQueries, this.uiControllerForUpdates);
		
		//Add the panels to this panel
		this.add( this.queryResultsTablePanel );
		this.add( this.pagingButtonsPanel );
		this.add( this.buttonsPanel );
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( FIRST_PAGE_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            try
            {
                DBTable dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), new Integer( 0 ), this.dbTable.getNumberOfRowsToReturn());
                this.initializeTable(dbTable);
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
		}

		if( LAST_PAGE_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            try
            {
                int offset = this.dbTable.getNumberOfRowsInTable().intValue() - this.dbTable.getNumberOfRowsToReturn().intValue();

                DBTable dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), new Integer( offset ), this.dbTable.getNumberOfRowsToReturn());
                this.initializeTable(dbTable);
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
		}

        if( NEXT_PAGE_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            int currentRecordNumber = this.dbTable.getOffset().intValue() + this.dbTable.getNumberOfRowsToReturn().intValue();

            //If there are more rows to return
            if( currentRecordNumber < this.dbTable.getNumberOfRowsInTable().intValue() )
			{
				int offset = this.dbTable.getOffset().intValue() + this.dbTable.getNumberOfRowsToReturn().intValue();
				try
				{
					DBTable dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), new Integer( offset ), this.dbTable.getNumberOfRowsToReturn());
					this.initializeTable(dbTable);
				}
				catch(DBEngineException exc)
				{
					String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
					JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

        if( PREVIOUS_PAGE_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            int currentRecordNumber = this.dbTable.getOffset().intValue() + this.dbTable.getNumberOfRowsToReturn().intValue();

            //If this is the first row
            if( this.dbTable.getOffset().intValue() != 0 )
			{
                int offset = this.dbTable.getOffset().intValue() - pagingSize.intValue();

                //if offset is less than 0, make it zero
                if( offset < 0 )
                {
                    offset =0;
                }

                try
				{
					DBTable dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), new Integer( offset ), this.dbTable.getNumberOfRowsToReturn() );
					this.initializeTable(dbTable);
				}
				catch(DBEngineException exc)
				{
					String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
					JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

        //Delete the selected record
        if( DELETE_RECORD_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            Integer selectedRowInteger = this.queryResultsTablePanel.getSelectedRow();
            if( selectedRowInteger == null )
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-delete-record-no-row-selected-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                int selectedRow = selectedRowInteger.intValue();
                if(selectedRow != -1)
                {
                    int ans = JOptionPane.showConfirmDialog(null, DELETE_RECORD_CONFIRMATION_MESSAGE, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if( ans == 0)
                    {
                        DBRow dbRow = (DBRow)this.dbTable.getListOfRows().get( selectedRow );
                        try
                        {
                            //Delete the row
                            this.uiControllerForUpdates.deleteRow(this.dbTable.getSchemaName(), this.dbTable.getTableName(), dbRow);
                            
                            //Get the auto commit flag.  If autocommit if off, commit
                            String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                            if( "false".equals(autoCommitFlag) )
                            {
                                this.uiControllerForUpdates.commit();
                            }

                            //remove the selected row from the list of rows
                            this.dbTable.getListOfRows().remove( selectedRow );

                            //Update the UI
                            this.queryResultsTablePanel.update();
                            this.queryResultsTablePanel.updateUI();
                        }
                        catch(DBEngineException exc)
                        {
                            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                            JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        //Add filter
        if( ADD_FILTER_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
        	SetFilterWindow setFilterWindow = null;
        	Object o = this.mapOfTableNameToFilter.get(this.dbTable.getTableName());
        	if( o != null )
        	{
            	Filter cachedFilter = (Filter)o;
        		setFilterWindow = new SetFilterWindow( this.dbTable.getListOfColumnInfos(), cachedFilter );	
        	}
        	else
        	{
        		setFilterWindow = new SetFilterWindow( this.dbTable.getListOfColumnInfos(), null );	
        	}
            
            setFilterWindow.show();
            
            //Get the filter
            Filter filter = setFilterWindow.getFilter();
            
            //if filter has been set, use it
            if( filter != null )
            {
            	Log.getInstance().debugMessage( "Filter is: " + filter.getSQLString(), this.getClass().getName() );
            	
                //Get the data from the UI Controller
                try
                {
                	this.dbTable = this.uiControllerForQueries.getFilteredDataInATable( this.dbTable.getSchemaName(), this.dbTable.getTableName(), filter);
                	
                	if( this.panelForFilterSetMessage == null )
                	{
	                	this.panelForFilterSetMessage = new JPanel();
	                	JLabel labelForFilterSetMessage = new JLabel("Data has been filtered");
	                	labelForFilterSetMessage.setIcon( iconForAddEditFilterButton );
	                	this.panelForFilterSetMessage.add( labelForFilterSetMessage );
	                	this.add( this.panelForFilterSetMessage, 1 ); 
	                	this.remove( this.pagingButtonsPanel );
                	}
    	        }
    		    catch(DBEngineException exc)
    		    {
    		    	Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
    				String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
    				JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);		    	
    		    }            	
            }
            else
            {
            	Log.getInstance().debugMessage( "Filter has been cleared", this.getClass().getName() );
            	
                //Get the data from the UI Controller
                try
                {
    		    	//Get the results from the database and show the data in the table
    		    	dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), new Integer(0), pagingSize);
    		    	
    		    	if( this.panelForFilterSetMessage != null )
    		    	{
    		    		this.add( this.pagingButtonsPanel, 1 );
    		    		this.remove( this.panelForFilterSetMessage );
    		    		this.panelForFilterSetMessage = null;
    		    	}
    	        }
    		    catch(DBEngineException exc)
    		    {
    		    	Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
    				String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
    				JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);		    	
    		    }              	
            }
            
            //Store the filter
            this.mapOfTableNameToFilter.put( this.dbTable.getTableName(), filter );            
            
            //Display the table
        	this.initializeTable( this.dbTable );            
		}

        //Add new record
        if( ADD_NEW_RECORD_BUTTON_LABEL.equals(e.getActionCommand()) )
		{
            AddNewRecordWindow addNewRecordWindow = new AddNewRecordWindow(this.uiControllerForUpdates, this.dbTable);
            addNewRecordWindow.show();
            
            //Refresh the data
            try
            {
		    	//Get the results from the database and show the data in the table
		    	this.dbTable = this.uiControllerForQueries.getAllDataInATable(this.dbTable.getSchemaName(), this.dbTable.getTableName(), this.dbTable.getOffset(), this.dbTable.getNumberOfRowsToReturn());
		    	
		    	//Show the results
		    	this.initializeTable(this.dbTable);	    
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);            
            }
        }
    }
}
