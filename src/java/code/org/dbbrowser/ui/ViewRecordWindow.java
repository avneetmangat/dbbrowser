package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.helper.DBTableCellComparator;

public class ViewRecordWindow implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    private String firstRecordNavigationButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-navigate-first-button-label", null);
    private String previousRecordNavigationButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-navigate-previous-button-label", null);
    private String nextRecordNavigationButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-navigate-next-button-label", null);
    private String lastRecordNavigationButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-navigate-last-button-label", null);
    private String UPDATE_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-update-button-label", null);
    private String COMMIT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-commit-button-label", null);
    private String ROLLBACK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-rollback-button-label", null);

    private String firstRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-first-record-icon");
    private String lastRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-last-record-icon");
    private String previousRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-previous-record-icon");
    private String nextRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-next-record-icon");

    private Icon iconForFirstRecordButton = new ImageIcon( firstRecordIconFileName );
    private Icon iconForLastRecordButton = new ImageIcon( lastRecordIconFileName );
    private Icon iconForPreviousRecordButton = new ImageIcon( previousRecordIconFileName );
    private Icon iconForNextRecordButton = new ImageIcon( nextRecordIconFileName );

    private static final String UPDATE_RECORD_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");
    private static final String COMMIT_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-commit-button-icon");
    private static final String ROLLBACK_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-rollback-button-icon");

    private JPanel mainPanel = new JPanel();
    private JDialog dialog = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    private DBTable dbTable = null;
    private int counterForRowNumber = 0;
    private WindowListener listenerForWindowClosing = null;
    private JPanel panelForData = new JPanel();
    private JPanel panelForScrollPane = new JPanel();
    private ButtonsPanel navigationButonsPanel = null;
    private ButtonsPanel updateButtonPanel = null;
    private Map mapOfTextFieldsToOriginalValue = null;
    private List listOfTextFieldsWhichHaveChanged = null;

    public ViewRecordWindow(UIControllerForUpdates uiControllerForUpdates, DBTable dbTable, Integer rowNumber)
    {
        this.uiControllerForUpdates = uiControllerForUpdates;
        this.dbTable = dbTable;
        this.counterForRowNumber = rowNumber.intValue();
        initialize();
    }

    private void initialize()
    {
        //Setup the dialog
        this.dialog = new JDialog();
        this.dialog.setTitle( TITLE );
        this.dialog.setModal( true );

        //Setup the main panel
        panelForScrollPane.setLayout( new BoxLayout(this.panelForScrollPane, BoxLayout.PAGE_AXIS) );
        this.mainPanel.setLayout( new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS) );
        JScrollPane pane = new JScrollPane(this.panelForData);
        this.panelForScrollPane.add( pane );
        this.mainPanel.add( this.panelForScrollPane );

        //Setup the frame
        this.dialog.setSize(700, 500);
        this.dialog.setLocationRelativeTo( null );
        this.dialog.getContentPane().add( this.mainPanel );

        //Refresh the table
        refresh();

        //Build the list of buttons for the navigation buttons panel
        List listOfNavigationButtons = new ArrayList();
        Button firstRecordButton = new Button(firstRecordNavigationButtonLabel, this, firstRecordNavigationButtonLabel, this.iconForFirstRecordButton, Boolean.FALSE);
        listOfNavigationButtons.add( firstRecordButton );
        Button previousRecordButton = new Button(previousRecordNavigationButtonLabel, this, previousRecordNavigationButtonLabel, this.iconForPreviousRecordButton, Boolean.FALSE);
        listOfNavigationButtons.add( previousRecordButton );
        Button nextRecordButton = new Button(nextRecordNavigationButtonLabel, this, nextRecordNavigationButtonLabel, this.iconForNextRecordButton, Boolean.TRUE);
        listOfNavigationButtons.add( nextRecordButton );
        Button lastRecordButton = new Button(lastRecordNavigationButtonLabel, this, lastRecordNavigationButtonLabel, this.iconForLastRecordButton, Boolean.FALSE);
        listOfNavigationButtons.add( lastRecordButton );

        //Setup the navigation panel
        navigationButonsPanel = new ButtonsPanel( listOfNavigationButtons );
        this.mainPanel.add( navigationButonsPanel );

        //Setup the panel for the update button if the uiControllerForUpdates is not null
        if( this.uiControllerForUpdates != null )
        {
            List listOfButtonsInUpdatePanel = new ArrayList();
            Button updateButton = new Button(UPDATE_BUTTON_LABEL, this, UPDATE_BUTTON_LABEL, new ImageIcon(UPDATE_RECORD_ICON_FILENAME), Boolean.FALSE);
            listOfButtonsInUpdatePanel.add( updateButton );
            Button commitButton = new Button(COMMIT_BUTTON_LABEL, this, COMMIT_BUTTON_LABEL, new ImageIcon(COMMIT_BUTTON_ICON_FILENAME), Boolean.FALSE);
            listOfButtonsInUpdatePanel.add( commitButton );
            Button rollbackButton = new Button(ROLLBACK_BUTTON_LABEL, this, ROLLBACK_BUTTON_LABEL, new ImageIcon(ROLLBACK_BUTTON_ICON_FILENAME), Boolean.FALSE);
            listOfButtonsInUpdatePanel.add( rollbackButton );

            this.updateButtonPanel = new ButtonsPanel( listOfButtonsInUpdatePanel );

            //If autocommit is on, disable all buttons
            String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
            if("true".equals(autoCommitFlag))
            {
                //Disable the commit and rollback buttons
                updateButtonPanel.enableButton(COMMIT_BUTTON_LABEL, Boolean.FALSE);
                updateButtonPanel.enableButton(ROLLBACK_BUTTON_LABEL, Boolean.FALSE);
            }

            //Add the buttons to the main panel
            this.mainPanel.add( this.updateButtonPanel );
        }
    }

    private void refresh()
    {
        //Get the selected row
        DBRow dbRow = (DBRow)this.dbTable.getListOfRows().get( this.counterForRowNumber );

        //Get the selected table cells
        List listOfTableCells = dbRow.getListOFDBTableCells();

        //Get the internationalized string for displaying 'Record 1 of 20'
        Integer[] values = new Integer[]{new Integer(counterForRowNumber + 1), new Integer(this.dbTable.getListOfRows().size())};
        String message = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-records-panel-title", values);

        //if the table name is not null, show it in the title
        if( this.dbTable.getTableName() != null )
        {
            message = this.dbTable.getTableName() + " : " + message;
        }

        this.panelForScrollPane.setBorder( BorderFactory.createTitledBorder(message) );

        //Remove all componenets from the panel
        this.panelForData.removeAll();

        //Build a new map
        this.mapOfTextFieldsToOriginalValue = new HashMap();
        this.listOfTextFieldsWhichHaveChanged = new ArrayList();

    	//if sorting enabled, sort the data in table and sort the columns
    	String sortColumnsFlag = PropertyManager.getInstance().getProperty("dbbrowser-ui-sort-columns-in-table");
    	if("true".equals( sortColumnsFlag ))
    	{
    		Collections.sort(listOfTableCells, new DBTableCellComparator());
    	}

        //Iterate through the list of table cells
        Iterator i = listOfTableCells.iterator();
        while(i.hasNext())
        {
            DBTableCell dbTableCell = (DBTableCell)i.next();
            ColumnInfo columnInfo = dbTableCell.getColumnInfo();
            String formattedValue = dbTableCell.getFormattedValue();
            String labelText = "";
            JTextField textFieldForData = null;

            //if it is a primary key column or it is not editable, disable it
            if(columnInfo.isPrimaryKeyColumn().booleanValue() || ( !columnInfo.isEditable().booleanValue() ) )
            {
                //if it is a primary key column, append the (PK) label to the end
                if(columnInfo.isPrimaryKeyColumn().booleanValue() )
                {
                    labelText = columnInfo.getColumnName() + "(PK)";
                }
                else
                {
                    labelText = columnInfo.getColumnName();
                }
                textFieldForData = new JTextField( 20 );
                textFieldForData.setText( formattedValue );
                textFieldForData.setEnabled( false );
            }
            else
            {
                //If it is not a primary key column, show the name of column
                labelText = columnInfo.getColumnName();
                textFieldForData = new JTextField( 20 );
                textFieldForData.setText( formattedValue );

                //Add the listener to detect changes
                textFieldForData.getDocument().addDocumentListener( new DocumentListenerForDetectingChangeInTextField(textFieldForData) );

                //Add the textfield to the map
                this.mapOfTextFieldsToOriginalValue.put( textFieldForData, dbTableCell );
            }
            JLabel labelForData = new JLabel(labelText);

            JLabel labelForDataType = null;
            if(ColumnInfo.COLUMN_TYPE_DATE.equals( columnInfo.getColumnTypeName() ) || ColumnInfo.COLUMN_TYPE_DATE_TIME.equals( columnInfo.getColumnTypeName() ))
            {
            	labelForDataType = new JLabel( columnInfo.getColumnTypeName() + "(" + columnInfo.getColumnDisplaySize().intValue() + ")" + " - " + columnInfo.getNullableNature() + " - " + DBTableCell.DATE_FORMAT_STRING);
            }
            else
            {
            	labelForDataType = new JLabel( columnInfo.getColumnTypeName() + "(" + columnInfo.getColumnDisplaySize().intValue() + ")" + " - " + columnInfo.getNullableNature() );            	
            }

            //Add the label to the panel for labels
            this.panelForData.add( labelForData );
            this.panelForData.add( labelForDataType );
            this.panelForData.add( textFieldForData );
        }

        //Set the layout on the panel
        this.panelForData.setLayout( new GridLayout( listOfTableCells.size(), 3) );
    }

    public void show()
    {
        this.dialog.setVisible( true );
    }

    public void actionPerformed(ActionEvent e)
    {
        if(firstRecordNavigationButtonLabel.equals( e.getActionCommand() ))
        {
            counterForRowNumber = 0;
            this.refresh();
        }

        if(previousRecordNavigationButtonLabel.equals( e.getActionCommand() ))
        {
            if(counterForRowNumber != 0)
            {
                counterForRowNumber = counterForRowNumber - 1;
                this.refresh();
            }
        }

        if(nextRecordNavigationButtonLabel.equals( e.getActionCommand() ))
        {
            if( (counterForRowNumber+1) < this.dbTable.getListOfRows().size())
            {
                counterForRowNumber = counterForRowNumber + 1;
                this.refresh();
            }
        }

        if(lastRecordNavigationButtonLabel.equals( e.getActionCommand() ))
        {
            if( (counterForRowNumber+1) < this.dbTable.getListOfRows().size())
            {
                counterForRowNumber = this.dbTable.getListOfRows().size() - 1;
                this.refresh();
            }
        }

        if(UPDATE_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            //Get the list of values which have been changed by the user
            Iterator i = this.listOfTextFieldsWhichHaveChanged.iterator();
            List listOfChangedCells = new ArrayList();

            while(i.hasNext())
            {
                JTextField textField = (JTextField)i.next();

                //Get the original value from the map
                DBTableCell tableCell = (DBTableCell)this.mapOfTextFieldsToOriginalValue.get( textField );

                //Get the new value typed by the user
                String newValue = textField.getText();

                //Set the new value
                DBTableCell newTableCell = new DBTableCell(tableCell.getColumnInfo(), newValue, Boolean.TRUE);
                listOfChangedCells.add( newTableCell );
            }

            //Add the primary key columns
            DBRow dbRow = (DBRow)this.dbTable.getListOfRows().get( this.counterForRowNumber );
            List listOfPrimaryKeyCells = dbRow.getListOfPrimaryKeyDBTableCells();
            listOfChangedCells.addAll( listOfPrimaryKeyCells );

            //Build the dbrow which needs to be updated
            DBRow updatedDBRow = new DBRow(listOfChangedCells);

            //Update the DBRow
            try
            {
                //Set the auto commit flag
                String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                if( "false".equals(autoCommitFlag) )
                {
                    this.uiControllerForUpdates.setAutoCommit(false);
                }

                //Update
                if( this.uiControllerForUpdates != null )
                {
                    this.uiControllerForUpdates.update(this.dbTable.getSchemaName(), this.dbTable.getTableName(), updatedDBRow);
                }
            }
            catch(DBEngineException exc)
            {
                Log.getInstance().fatalMessage(exc.getMessage(), ViewRecordWindow.class.getName());
                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        if(COMMIT_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            try
            {
                //Commit only if autocommit if off
                String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                if( "false".equals(autoCommitFlag) )
                {
                    this.uiControllerForUpdates.commit();
                }

                //Close the window
                this.dialog.pack();
                this.dialog.dispose();
                this.dialog = null;
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        if(ROLLBACK_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            try
            {
                //Rollback only if autocommit if off
                String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                if( "false".equals(autoCommitFlag) )
                {
                    this.uiControllerForUpdates.rollback();
                }
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        //Update the main panel
        SwingUtilities.updateComponentTreeUI( this.mainPanel );
    }

    private class DocumentListenerForDetectingChangeInTextField implements DocumentListener
    {
        private JTextField textFieldWhoseValueMayBeChangedByUser = null;

        public DocumentListenerForDetectingChangeInTextField(JTextField textField)
        {
            textFieldWhoseValueMayBeChangedByUser = textField;
        }

        public void insertUpdate(DocumentEvent e)
        {
            updateLog(e);
        }

        public void removeUpdate(DocumentEvent e)
        {
            updateLog(e);
        }

        public void changedUpdate(DocumentEvent e)
        {
            //Not used
        }

        private void updateLog(DocumentEvent e)
        {
            //if the list does not contain this field, add it
            if( ! listOfTextFieldsWhichHaveChanged.contains(textFieldWhoseValueMayBeChangedByUser) )
            {
                listOfTextFieldsWhichHaveChanged.add( textFieldWhoseValueMayBeChangedByUser );
            }
        }
    }
}
