package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.helper.ColumnInfoComparator;

public class AddNewRecordWindow implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    private String ADD_NEW_RECORD_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-add-new-record-window-add-record-button-label", null);
    private static final String COMMIT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-commit-button-label", null);
    private static final String ROLLBACK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-rollback-button-label", null);

    private static final String ADD_NEW_RECORD_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-add-new-record-icon");
    private static final String COMMIT_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-commit-button-icon");
    private static final String ROLLBACK_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-rollback-button-icon");

    private JPanel mainPanel = new JPanel();
    private JDialog dialog = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    private DBTable dbTable = null;
    private JPanel panelForData = new JPanel();
    private JPanel panelForScrollPane = new JPanel();
    private Map mapOfTextFieldToColumnInfos = new HashMap();

    public AddNewRecordWindow(UIControllerForUpdates uiControllerForUpdates, DBTable dbTable)
    {
        this.uiControllerForUpdates = uiControllerForUpdates;
        this.dbTable = dbTable;
        initialize();
    }

    private void initialize()
    {
        //Setup the dialog
        this.dialog = new JDialog();
        this.dialog.setTitle( TITLE );
        this.dialog.setModal( true );

        //Setup the main panel
        this.panelForScrollPane.setLayout( new BoxLayout(this.panelForScrollPane, BoxLayout.PAGE_AXIS) );
        this.panelForScrollPane.setBorder( BorderFactory.createTitledBorder(this.dbTable.getTableName()) );
        this.mainPanel.setLayout( new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS) );
        JScrollPane pane = new JScrollPane(this.panelForData);
        this.panelForScrollPane.add( pane );
        this.mainPanel.add( this.panelForScrollPane );

        //Setup the frame
        this.dialog.setSize(700, 500);
        this.dialog.setLocationRelativeTo( null );
        this.dialog.getContentPane().add( this.mainPanel );

        //Get the list of column infos
        List listOfColumnInfos = this.dbTable.getListOfColumnInfos();

        //Sort the list of DBTable Cells
        Collections.sort(listOfColumnInfos, new ColumnInfoComparator());

        //Iterate through the list of table cells
        Iterator i = listOfColumnInfos.iterator();
        while(i.hasNext())
        {
            ColumnInfo columnInfo = (ColumnInfo)i.next();
            String labelText = columnInfo.getColumnName();
            JTextField textFieldForData = new JTextField( 20 );

            //if it is not editable, disable it
            if( !columnInfo.isEditable().booleanValue() )
            {
                textFieldForData.setEnabled( false );
            }

            //if it is primary key, mark it as such
            if(columnInfo.isPrimaryKeyColumn().booleanValue())
            {
                labelText = labelText + "(PK)";
            }

            JLabel labelForData = new JLabel(labelText);
            JLabel labelForDataType = null;
            
            if( ColumnInfo.COLUMN_TYPE_DATE.equals( columnInfo.getColumnTypeName() ) || ColumnInfo.COLUMN_TYPE_DATE_TIME.equals( columnInfo.getColumnTypeName() ) )
            {
            	labelForDataType = new JLabel( columnInfo.getColumnTypeName() + " - " + columnInfo.getNullableNature() + " - " + DBTableCell.DATE_FORMAT_STRING );
            }
            else
            {
            	labelForDataType = new JLabel( columnInfo.getColumnTypeName() + " - " + columnInfo.getNullableNature() );
            }

            //Add to map
            this.mapOfTextFieldToColumnInfos.put(textFieldForData, columnInfo);

            //Add the label to the panel for labels
            this.panelForData.add( labelForData );
            this.panelForData.add( labelForDataType );
            this.panelForData.add( textFieldForData );
        }

        //Set the layout on the panel
        this.panelForData.setLayout( new GridLayout( listOfColumnInfos.size(), 3) );

        //Setup the panel for the add new record button
        List listOfButtonsInAddNewRecordPanel = new ArrayList();
        Button addNewRecordButton = new Button(ADD_NEW_RECORD_BUTTON_LABEL, this, ADD_NEW_RECORD_BUTTON_LABEL, new ImageIcon(ADD_NEW_RECORD_ICON_FILENAME), Boolean.TRUE);
        listOfButtonsInAddNewRecordPanel.add( addNewRecordButton );
        Button commitButton = new Button(COMMIT_BUTTON_LABEL, this, COMMIT_BUTTON_LABEL, new ImageIcon(COMMIT_BUTTON_ICON_FILENAME), Boolean.TRUE);
        listOfButtonsInAddNewRecordPanel.add( commitButton );
        Button rollbackButton = new Button(ROLLBACK_BUTTON_LABEL, this, ROLLBACK_BUTTON_LABEL, new ImageIcon(ROLLBACK_BUTTON_ICON_FILENAME), Boolean.TRUE);
        listOfButtonsInAddNewRecordPanel.add( rollbackButton );

        ButtonsPanel addNewRecordButtonPanel = new ButtonsPanel( listOfButtonsInAddNewRecordPanel );

        //If autocommit is on, disable all buttons
        String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
        if("true".equals(autoCommitFlag))
        {
            //Disable the commit and rollback buttons
            addNewRecordButtonPanel.enableButton(COMMIT_BUTTON_LABEL, Boolean.FALSE);
            addNewRecordButtonPanel.enableButton(ROLLBACK_BUTTON_LABEL, Boolean.FALSE);
        }

        //Add the panel to the main panel
        this.mainPanel.add( addNewRecordButtonPanel );
    }

    public void show()
    {
        this.dialog.setVisible( true );
    }

    public void actionPerformed(ActionEvent e)
    {
        if(ADD_NEW_RECORD_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            //Get all the text fields
            Iterator i = this.mapOfTextFieldToColumnInfos.keySet().iterator();
            List listOfNewValues = new ArrayList();

            while(i.hasNext())
            {
                JTextField textField = (JTextField)i.next();

                //Get the new value typed by the user
                String valueToAdd = textField.getText();

                //If the user has typed a value, then build a table cell
                if( (valueToAdd != null) && (!valueToAdd.equals("")) )
                {
                    //Get the column info from the map
                    ColumnInfo ci = (ColumnInfo)this.mapOfTextFieldToColumnInfos.get( textField );
                    //Set the new value
                    DBTableCell newTableCell = new DBTableCell(ci, valueToAdd, Boolean.TRUE);
                    listOfNewValues.add( newTableCell );
                }
            }

            //Build the dbrow which needs to be updated
            DBRow updatedDBRow = new DBRow(listOfNewValues);

            //Add the DBRow
            try
            {
                if( this.uiControllerForUpdates != null )
                {
                    //Set the auto commit flag
                    String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                    if( "false".equals(autoCommitFlag) )
                    {
                        this.uiControllerForUpdates.setAutoCommit(false);
                    }

                    //Add the row
                    this.uiControllerForUpdates.addNewRow(this.dbTable.getSchemaName(), this.dbTable.getTableName(), updatedDBRow);
                }
            }
            catch(DBEngineException exc)
            {
                Log.getInstance().fatalMessage(exc.getMessage(), AddNewRecordWindow.class.getName());
                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        if(COMMIT_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            try
            {
                this.uiControllerForUpdates.commit();
                this.dialog.pack();
                this.dialog.dispose();
                this.dialog = null;
            }
            catch(DBEngineException exc)
            {
                Log.getInstance().fatalMessage(exc.getMessage(), AddNewRecordWindow.class.getName());
                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        if(ROLLBACK_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            try
            {
                this.uiControllerForUpdates.rollback();
                this.dialog.pack();
                this.dialog.dispose();
                this.dialog = null;
            }
            catch(DBEngineException exc)
            {
                Log.getInstance().fatalMessage(exc.getMessage(), AddNewRecordWindow.class.getName());
                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        //Update the main panel
        SwingUtilities.updateComponentTreeUI( this.mainPanel );
    }
}
