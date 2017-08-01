package org.dbbrowser.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import infrastructure.internationalization.InternationalizationManager;

public class AddNewColumnWindow implements ActionListener
{
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
	
	private String ADD_NEW_COLUMN_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-add-new-record-window-add-label", null);
	private UIControllerForUpdates uiControllerForUpdates = null;
	private DBTable dbTable = null;	
	
	private JPanel mainPanel = new JPanel();
	private JDialog dialog = null;
	private ButtonsPanel buttonsPanel = null;
	
	private JPanel panelForData = new JPanel();
	private JTextField fieldForColumnName = new JTextField();
	private JTextField fieldForColumnSize = new JTextField();
	private JComboBox comboBoxForDataType = null;
	private JCheckBox checkBoxForNullableNature = new JCheckBox();

	public AddNewColumnWindow(UIControllerForUpdates uiControllerForUpdates, DBTable dbTable)
	{
		this.uiControllerForUpdates = uiControllerForUpdates;
		this.dbTable = dbTable;
		
		initialize();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( ADD_NEW_COLUMN_LABEL.equals(e.getActionCommand()) )
		{
			//Get the column type name
			String columnTypeName = this.comboBoxForDataType.getSelectedItem().toString();
						
			//Validate the values entered by the user
			String columnName = this.fieldForColumnName.getText();
			if(columnName == null || columnName.length() == 0)
			{
				String COLUMN_NAME_INCORRECT_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-add-new-record-window-wrong-column-name-error-message-label", null);				
				JOptionPane.showMessageDialog(null, COLUMN_NAME_INCORRECT_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				String columnDisplaySize = this.fieldForColumnSize.getText();
				if(columnDisplaySize == null || columnDisplaySize.length() == 0)
				{
					String COLUMN_SIZE_INCORRECT_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-add-new-record-window-wrong-column-size-error-message-label", null);				
					JOptionPane.showMessageDialog(null, COLUMN_SIZE_INCORRECT_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					try
					{
						int size = Integer.parseInt(columnDisplaySize);
						
						//Validation complete
						
						//Get nullable nature
						boolean nullable = this.checkBoxForNullableNature.isSelected();
						String nullableString = ColumnInfo.COLUMN_NOT_NULLABLE;
						if( nullable )
						{
							nullableString = ColumnInfo.COLUMN_NULLABLE;
						}
						
						//Build the column info
						ColumnInfo ci = new ColumnInfo(columnName, columnTypeName, null, new Integer(size), nullableString, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, null);
						
						//Add the column
						try
						{
							this.uiControllerForUpdates.addNewColumn( this.dbTable.getSchemaName(), this.dbTable.getTableName(), ci);
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
					catch(NumberFormatException exc)
					{
						String COLUMN_SIZE_INCORRECT_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-add-new-record-window-wrong-column-size-error-message-label", null);				
						JOptionPane.showMessageDialog(null, COLUMN_SIZE_INCORRECT_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	
	public void show()
	{
		this.dialog.setVisible( true );
	}	
	
	private void initialize()
	{
		//Setup the dialog
		this.dialog = new JDialog();
		this.dialog.setTitle( TITLE );
		this.dialog.setModal( true );
		
		//Setup the frame
		this.dialog.setSize(500, 200);
		this.dialog.setLocationRelativeTo( null );
		this.dialog.getContentPane().add( this.mainPanel );
		
		//Build the list of buttons
		List listOfButtons = new ArrayList();
		Button addNewColumnButton = new Button(ADD_NEW_COLUMN_LABEL, this, ADD_NEW_COLUMN_LABEL, null, Boolean.FALSE);
		listOfButtons.add( addNewColumnButton );
		
		//Setup the navigation panel
		buttonsPanel = new ButtonsPanel( listOfButtons );
		
		//Set up the textfields and labels
		this.panelForData.setLayout( new GridLayout( 4, 2) );
		
		String COLUMN_NAME_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-column-name", null);
		String COLUMN_SIZE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-size", null);
		String COLUMN_TYPE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-data-type", null);
		String COLUMN__NULLABLE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-column-details-panel-nullable", null);
		
		JLabel labelForColumnName = new JLabel(COLUMN_NAME_LABEL);
		JLabel labelForColumnSize = new JLabel(COLUMN_SIZE_LABEL);
		JLabel labelForColumnType = new JLabel(COLUMN_TYPE_LABEL);
		JLabel labelForNullableNature = new JLabel(COLUMN__NULLABLE_LABEL);
		
		//Build the list of column types
		String[] dataTypes = new String[]{ColumnInfo.COLUMN_TYPE_VARCHAR, ColumnInfo.COLUMN_TYPE_DATE, ColumnInfo.COLUMN_TYPE_DATE_TIME, ColumnInfo.COLUMN_TYPE_NUMBER};
		this.comboBoxForDataType = new JComboBox(dataTypes);
		this.comboBoxForDataType.setEditable( false );
		
		this.panelForData.add(labelForColumnName);
		this.panelForData.add(fieldForColumnName);
		this.panelForData.add(labelForColumnSize);
		this.panelForData.add(fieldForColumnSize);
		this.panelForData.add(labelForColumnType);
		this.panelForData.add(comboBoxForDataType);
		this.panelForData.add(labelForNullableNature);
		this.panelForData.add(checkBoxForNullableNature);
		
		//Add the buttons panel
		this.mainPanel.setLayout( new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS) );		
		this.mainPanel.add( this.panelForData );
		this.mainPanel.add( this.buttonsPanel );
	}
}
