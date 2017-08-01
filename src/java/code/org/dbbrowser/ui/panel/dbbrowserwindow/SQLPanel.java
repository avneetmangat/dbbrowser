package org.dbbrowser.ui.panel.dbbrowserwindow;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForRawSQL;

public class SQLPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;	
	private static final String OPEN_NEW_SQL_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-open-new-tab-button-label", null);
	private static final String REMOVE_SQL_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-remove-tab-button-label", null);
	private static final String SELECT_NAME_FOR_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-select-name-for-tab", null);
	
    private static final String OPEN_NEW_SQL_TAB_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-open-new-sql-tab-button-icon");
    private static final String REMOVE_SQL_TAB_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-remove-sql-tab-button-icon");

	private UIControllerForQueries uicontroller = null;
	private UIControllerForRawSQL uiControllerForRawSQL = null;
	
	private JPanel topRowPanel = new JPanel();
	private JTabbedPane tabbedPanesForSQLStatements = new JTabbedPane();
	
	private RunSQLPanel sqlPanel = null;

	public SQLPanel(UIControllerForQueries uicontroller, UIControllerForRawSQL uiControllerForRawSQL)
	{
		this.uicontroller = uicontroller;
		this.uiControllerForRawSQL = uiControllerForRawSQL;
		initialize();
	}
	
	public void runBatchFile(File file)
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader( new FileReader(file) );
			String line = reader.readLine();
			while( line != null )
			{
				buffer.append(line + "\n");
				line = reader.readLine();
			}
			
			reader.close();
			
			//Create a new tab with the contents
			sqlPanel = new RunSQLPanel(this.uicontroller, this.uiControllerForRawSQL, buffer.toString());
			tabbedPanesForSQLStatements.addTab(file.getName(), null, sqlPanel, file.getAbsolutePath());
			tabbedPanesForSQLStatements.setSelectedComponent( sqlPanel );
		}
		catch(FileNotFoundException exc)
		{
			String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-open-batch-file-failed", null);
			JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);					
		}
		catch(IOException exc)
		{
			String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-open-batch-file-failed", null);
			JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);					
		}
	}
	
	private void initialize()
	{
    	this.tabbedPanesForSQLStatements.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

		//Set the layout
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Setup the button to add new tab
		JButton buttonToAddNewSQLTab = new JButton( OPEN_NEW_SQL_TAB_LABEL, new ImageIcon(OPEN_NEW_SQL_TAB_ICON_FILENAME) );
		this.topRowPanel.add( buttonToAddNewSQLTab );
		buttonToAddNewSQLTab.addActionListener( this );
		
		//Setup the button to remove current tab
		JButton buttonToRemoveSQLTab = new JButton( REMOVE_SQL_TAB_LABEL,  new ImageIcon(REMOVE_SQL_TAB_ICON_FILENAME) );
		this.topRowPanel.add( buttonToRemoveSQLTab );
		buttonToRemoveSQLTab.addActionListener( this );		
		
		//Set the size of the top row panel
		Dimension d = new Dimension( this.topRowPanel.getMaximumSize().width, 50);
		this.topRowPanel.setMaximumSize(d);
		
		//Add a tab
		sqlPanel = new RunSQLPanel(this.uicontroller, this.uiControllerForRawSQL, "");
		tabbedPanesForSQLStatements.addTab("SQL", sqlPanel);
		
		//Add the panels
		this.add( this.topRowPanel );
		this.add( this.tabbedPanesForSQLStatements );
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( e.getActionCommand().equals( OPEN_NEW_SQL_TAB_LABEL ) )
		{
			String tabTitle = (String)JOptionPane.showInputDialog(null, SELECT_NAME_FOR_TAB_LABEL, TITLE, JOptionPane.QUESTION_MESSAGE, null, null, "SQL");
			
			if( tabTitle != null )
			{
				//Add a tab
				sqlPanel = new RunSQLPanel(this.uicontroller, this.uiControllerForRawSQL);
				tabbedPanesForSQLStatements.addTab(tabTitle, sqlPanel);
				tabbedPanesForSQLStatements.setSelectedComponent( sqlPanel );
			}
		}
		
		if( e.getActionCommand().equals( REMOVE_SQL_TAB_LABEL ) )
		{
			//Add a tab
			int selectedTabIndex = tabbedPanesForSQLStatements.getSelectedIndex();
			if( selectedTabIndex > -1 )
			{
				tabbedPanesForSQLStatements.remove( selectedTabIndex );
			}
		}		
	}
}
