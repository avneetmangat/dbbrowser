package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManagementException;
import infrastructure.propertymanager.PropertyManager;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;

public class PreferencesPanel extends JPanel implements ActionListener
{
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);
	
	private static final String RECORDS_PER_PAGE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-records-per-page-label", null);
	private static final String AUTO_COMMIT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-auto-commit-label", null);
	private static final String SHOW_SQL_LOG_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-show-sql-log-label", null);
	private static final String SORT_COLUMNS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-sort-columns", null);
	private static final String SAVE_CHANGES_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-save-changes-button-label", null);
	private static final String SAVE_CHANGES_ICON_FILENAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-view-record-window-update-record-icon" );
	private static final String SQL_STATEMENT_TERMINATOR = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-sql-statement-terminator-label", null);
	
	private Icon iconForSaveChanges = new ImageIcon(SAVE_CHANGES_ICON_FILENAME);
	private JTextField textFieldForRecordsPerPage = new JTextField("");
	private JTextField textFieldForSQLStatementTerminator = new JTextField("");
	private JCheckBox checkBoxForAutoCommit = new JCheckBox();
	private JCheckBox checkBoxForShowSQL = new JCheckBox();
	private JCheckBox checkBoxForSortColumns = new JCheckBox();
	
	public PreferencesPanel()
	{
		initialize();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String recordsPerPageString = this.textFieldForRecordsPerPage.getText();
		
		try
		{
			int recordsPerPage = Integer.parseInt(recordsPerPageString);
			PropertyManager.getInstance().setProperty("dbbrowser-ui-browser-window-paging-size", "" + recordsPerPage);
			PropertyManager.getInstance().setProperty("dbbrowser-sql-statement-terminator", this.textFieldForSQLStatementTerminator.getText());
			
			if(checkBoxForAutoCommit.isSelected())
			{
				PropertyManager.getInstance().setProperty("dbbrowser-auto-commit", "true");				
			}
			else
			{
				PropertyManager.getInstance().setProperty("dbbrowser-auto-commit", "false");								
			}
			
			if(checkBoxForShowSQL.isSelected())
			{
				PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-sql-log", "true");				
			}
			else
			{
				PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-sql-log", "false");								
			}
			
			if(checkBoxForSortColumns.isSelected())
			{
				PropertyManager.getInstance().setProperty("dbbrowser-ui-sort-columns-in-table", "true");				
			}
			else
			{
				PropertyManager.getInstance().setProperty("dbbrowser-ui-sort-columns-in-table", "false");								
			}			
		}
		catch(NumberFormatException exc)
		{
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-not-a-number-error-message-label", null);
            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);			
		}
		catch(PropertyManagementException exc)
		{
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);			
		}		
		
		//Close this window
		Window window = SwingUtilities.getWindowAncestor( this );
		if( window != null )
		{
			window.dispose();
			window.hide();
		}
	}
	
	private void initialize()
	{
		//Setup the dialog
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Build the widgets
		JLabel labelForRecordsPerPage = new JLabel(RECORDS_PER_PAGE_LABEL);
		JLabel labelForAutoCommit = new JLabel(AUTO_COMMIT_LABEL);
		JLabel labelForShowSQLInLog = new JLabel(SHOW_SQL_LOG_LABEL);
		JLabel labelForSortColumns = new JLabel(SORT_COLUMNS_LABEL);		
		JLabel labelForSQLStatementTerminator = new JLabel(SQL_STATEMENT_TERMINATOR);
		
		JPanel panel = new JPanel();
		String panelTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-preferences-panel-title", null);
		panel.setBorder( BorderFactory.createTitledBorder(panelTitle) );
		panel.setLayout( new GridLayout(5,2) );
		panel.add(labelForRecordsPerPage);
		panel.add(textFieldForRecordsPerPage);
		panel.add(labelForAutoCommit);
		panel.add(checkBoxForAutoCommit);
		panel.add(labelForShowSQLInLog);
		panel.add(checkBoxForShowSQL);
		panel.add( labelForSortColumns );
		panel.add( checkBoxForSortColumns );
		panel.add( labelForSQLStatementTerminator );
		panel.add( textFieldForSQLStatementTerminator );
		
		//Set the values from the properties file
		textFieldForRecordsPerPage.setText( PropertyManager.getInstance().getProperty("dbbrowser-ui-browser-window-paging-size") );
		textFieldForSQLStatementTerminator.setText( PropertyManager.getInstance().getProperty("dbbrowser-sql-statement-terminator") );
		
		String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
		if( "true".equals(autoCommitFlag) )
		{
			checkBoxForAutoCommit.setSelected( true );
		}
		
		String showSQLLog = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-show-sql-log");
		if( "true".equals(showSQLLog) )
		{
			checkBoxForShowSQL.setSelected( true );
		}
		
		String sortColumns = PropertyManager.getInstance().getProperty("dbbrowser-ui-sort-columns-in-table");
		if( "true".equals(sortColumns) )
		{
			checkBoxForSortColumns.setSelected( true );
		}		
		
		//Add the panel
		this.add(panel);
		
		//Build the list of buttons for the navigation buttons panel
		List listOfButtons = new ArrayList();
		Button firstRecordButton = new Button(SAVE_CHANGES_BUTTON_LABEL, this, SAVE_CHANGES_BUTTON_LABEL, iconForSaveChanges, Boolean.FALSE);
		listOfButtons.add( firstRecordButton );
		
		//Setup the navigation panel
		ButtonsPanel butonsPanel = new ButtonsPanel( listOfButtons );
		this.add( butonsPanel );
	}
}
