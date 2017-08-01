package org.dbbrowser.ui.panel.dbbrowserwindow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForRawSQL;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.*;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.help.HelpManager;

public class RunSQLPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private static final String SELECT_PREVIOUSLY_RUN_SQL_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-select-previously-run-sql", null);
    private static final String RUN_SQL_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-run-sql-button-label", null);;
    private static final String COMMIT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-commit-button-label", null);;
    private static final String ROLLBACK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-rollback-button-label", null);;

    private static final String POP_UP_MENU_COPY_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-copy-menu-item-label", null);;
    private static final String POP_UP_MENU_EXPORT_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-tools-menu-copy-label", null);;
    private static final String POP_UP_MENU_WHATS_THIS_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-whats-this-menu-item-label", null);;
    private static final String POP_UP_MENU_HELP_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-help-menu-item-label", null);;

    private static final String COMMIT_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-commit-button-icon");
    private static final String ROLLBACK_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-rollback-button-icon");

    private static final String COPY_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-popup-menu-copy-icon-filename");
    private static final String EXPORT_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-export-icon");
    private static final String WHATS_THIS_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-popup-menu-whats-this-icon-filename");
    private static final String HELP_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-help-icon");

    private UIControllerForQueries uicontroller = null;
    private UIControllerForRawSQL uiControllerForRawSQL = null;
    private String initialContents = null;

    private JComboBox comboBoxForHistoryOfSQLStatements = new JComboBox();
    private ButtonsPanel buttonsPanel = null;
    private SyntaxHighlighterPanel syntaxHighlighterPanel = null;
    private JPanel resultsPanel = new JPanel();
    private Table resultsTable = null;
    private JPanel panelForSplitPane = new JPanel();
    private JSplitPane splitPane = null;
    private SQLHistoryComboBoxModel sqlHistoryComboBoxModel = null;

    public RunSQLPanel(UIControllerForQueries uicontroller, UIControllerForRawSQL uiControllerForRawSQL)
    {
        this.uicontroller = uicontroller;
        this.uiControllerForRawSQL = uiControllerForRawSQL;
        initialize();
        
        //Add key listener for CTRL-Enter
        this.syntaxHighlighterPanel.getTextPane().addKeyListener( new RunSQLKeyBinding() );
    }

    public RunSQLPanel(UIControllerForQueries uicontroller, UIControllerForRawSQL uiControllerForRawSQL, String initialContents)
    {
        this.uicontroller = uicontroller;
        this.uiControllerForRawSQL = uiControllerForRawSQL;
        this.initialContents = initialContents;
        initialize();
        
        //Add key listener for CTRL-Enter
        this.syntaxHighlighterPanel.getTextPane().addKeyListener( new RunSQLKeyBinding() );
    }
    
    private class RunSQLKeyBinding extends KeyAdapter
    {
    	public void keyPressed(KeyEvent e)
    	{
    		//if CTRL-Enter is pressed, run SQL
    		if( e.getKeyCode() == 10 && e.getModifiers() == 2)
    		{
    			runSQL();
    		}
    	}
    }

    private void initialize()
    {
        //Set the layout
        this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );

        //Setup the combo box
        sqlHistoryComboBoxModel = new SQLHistoryComboBoxModel();
        sqlHistoryComboBoxModel.addElement( SELECT_PREVIOUSLY_RUN_SQL_LABEL );
        this.comboBoxForHistoryOfSQLStatements.setModel( sqlHistoryComboBoxModel );

        //Set the size of the drop down box
        Dimension dd = new Dimension( 500, this.comboBoxForHistoryOfSQLStatements.getPreferredSize().height );
        this.comboBoxForHistoryOfSQLStatements.setPreferredSize( dd );

        //Add the listener for the combo box
        this.comboBoxForHistoryOfSQLStatements.addActionListener( this );

        //Set the action command for the drop down box
        this.comboBoxForHistoryOfSQLStatements.setActionCommand( "ComboBoxForSQLHistory" );

        //Add the drop down box
        this.add( this.comboBoxForHistoryOfSQLStatements );

        //Setup the text area
        this.syntaxHighlighterPanel = new SyntaxHighlighterPanel();
        
        //Set the initial contents if it is not null
        if( this.initialContents != null && (!"".equals(this.initialContents) ) )
        {
        	this.syntaxHighlighterPanel.setText( this.initialContents );
        }
        
        Dimension minimumSizeOfTheTextArea = new Dimension( 2000, 100 );
        this.syntaxHighlighterPanel.setMinimumSize( minimumSizeOfTheTextArea );

        //Add the split pane
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.syntaxHighlighterPanel, resultsPanel);
        this.splitPane.setOneTouchExpandable(true);
        this.splitPane.setDividerLocation(250);
        this.panelForSplitPane.setLayout( new BorderLayout() );
        this.panelForSplitPane.add( this.splitPane );
        this.add( this.panelForSplitPane );

        //Add the buttons
        String runSQLIconFilename = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-run-sql-icon");
        Button runSQLButton = new Button(RUN_SQL_BUTTON_LABEL, this, RUN_SQL_BUTTON_LABEL, new ImageIcon(runSQLIconFilename), Boolean.FALSE);
        Button commitButton = new Button(COMMIT_BUTTON_LABEL, this, COMMIT_BUTTON_LABEL, new ImageIcon(COMMIT_BUTTON_ICON_FILENAME), Boolean.FALSE);
        Button rollbackButton = new Button(ROLLBACK_BUTTON_LABEL, this, ROLLBACK_BUTTON_LABEL, new ImageIcon(ROLLBACK_BUTTON_ICON_FILENAME), Boolean.FALSE);

        List listOFButtons = new ArrayList();
        listOFButtons.add( runSQLButton );
        listOFButtons.add( commitButton );
        listOFButtons.add( rollbackButton );

        //Setup the buttons panel
        this.buttonsPanel = new ButtonsPanel( listOFButtons );

        //If autocommit is on, disable all buttons
        String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
        if("true".equals(autoCommitFlag))
        {
            //Disable the commit and rollback buttons
            this.buttonsPanel.enableButton(COMMIT_BUTTON_LABEL, Boolean.FALSE);
            this.buttonsPanel.enableButton(ROLLBACK_BUTTON_LABEL, Boolean.FALSE);
        }

        this.add( this.buttonsPanel );

        //Add the pop up menu
        JPopupMenu popupMenuForRunSQLTable = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem(POP_UP_MENU_COPY_MENU_ITEM_LABEL, new ImageIcon(COPY_POPUP_ITEM_ICON_FILENAME));
        //copyMenuItem.addActionListener(this);
        popupMenuForRunSQLTable.add(copyMenuItem);
        popupMenuForRunSQLTable.addSeparator();
        JMenuItem exportMenuItem = new JMenuItem(POP_UP_MENU_EXPORT_MENU_ITEM_LABEL, new ImageIcon(EXPORT_POPUP_ITEM_ICON_FILENAME));
        copyMenuItem.addActionListener(this);
        popupMenuForRunSQLTable.add(exportMenuItem);
        popupMenuForRunSQLTable.addSeparator();        
        JMenuItem whatsThisMenuItem = new JMenuItem(POP_UP_MENU_WHATS_THIS_MENU_ITEM_LABEL, new ImageIcon(WHATS_THIS_POPUP_ITEM_ICON_FILENAME));
        popupMenuForRunSQLTable.add(whatsThisMenuItem);
        JMenuItem helpMenuItem = new JMenuItem(POP_UP_MENU_HELP_MENU_ITEM_LABEL, new ImageIcon(HELP_POPUP_ITEM_ICON_FILENAME));
        helpMenuItem.addActionListener( HelpManager.getInstance().getActionListenerForHelpEvents() );
        popupMenuForRunSQLTable.add(helpMenuItem);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListenerForResultsTable = new BasicPopupListener( popupMenuForRunSQLTable );
        this.resultsPanel.addMouseListener( popupListenerForResultsTable );

        //Setup CSH
        HelpManager.getInstance().registerCSH(whatsThisMenuItem, this, "sql_tab");
    }

    public void actionPerformed(ActionEvent e)
    {
        if( RUN_SQL_BUTTON_LABEL.equals( e.getActionCommand() ) )
        {
        	runSQL();
        }
        
        //If the combox box value is selected, append it to the ext area
        if( COMMIT_BUTTON_LABEL.equals(e.getActionCommand()) )
        {
        	try
        	{
                //Rollback if autocommit if off
                String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                if( "false".equals(autoCommitFlag) )
                {
                    this.uiControllerForRawSQL.commit();
                }
            }
        	catch(DBEngineException exc)
        	{
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);        		
        	}
        }  
        
        //If the combox box value is selected, append it to the ext area
        if( ROLLBACK_BUTTON_LABEL.equals(e.getActionCommand()) )
        {
        	try
        	{
                //Rollback if autocommit if off
                String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
                if( "false".equals(autoCommitFlag) )
                {
                    this.uiControllerForRawSQL.rollback();
                }
        	}
        	catch(DBEngineException exc)
        	{
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);        		
        	}        	
        }         

        //If the combox box value is selected, append it to the ext area
        if( "ComboBoxForSQLHistory".equals(e.getActionCommand()) )
        {
            //append the value if it is not the first value
            if( this.comboBoxForHistoryOfSQLStatements.getSelectedIndex() != 0 )
            {
                this.syntaxHighlighterPanel.append( this.comboBoxForHistoryOfSQLStatements.getSelectedItem().toString() );
            }
        }

        //If the user wants to copy something
        if( POP_UP_MENU_EXPORT_MENU_ITEM_LABEL.equals(e.getActionCommand()) )
        {

        }
    }
    
    private void runSQL()
    {
        //Get the text from the text area
        String sql = this.syntaxHighlighterPanel.getText();

        //Run the sql and get the results as DBTable
        try
        {
            //Set the auto commit flag
            String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
            if( "false".equals(autoCommitFlag) )
            {
                this.uiControllerForRawSQL.setAutoCommit(false);
            }

            //Run the SQL
            DBTable dbTable = this.uiControllerForRawSQL.runRawSQL( sql );

            //if a value is returned
            if( dbTable != null )
            {
                DBTableDataTableModel dbTableDataTableModel = new DBTableDataTableModel( dbTable );

                //Remove the existing results table from the results panel if table is not null
                if( this.resultsTable != null )
                {
                    this.resultsPanel.remove( this.resultsTable );
                }

                //Display the results as a table
                this.resultsTable = new Table(this.uicontroller, null);

                this.resultsPanel.add( this.resultsTable );
                this.resultsPanel.setLayout( new BoxLayout(this.resultsPanel, BoxLayout.PAGE_AXIS) );
                Object[] messageParameters = new Object[]{"0", dbTable.getNumberOfRowsInTable(), dbTable.getNumberOfRowsInTable()};
                String title = InternationalizationManager.getInstance().getMessage("dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-panel-title", messageParameters);
                this.resultsPanel.setBorder( BorderFactory.createTitledBorder( title ) );
                this.resultsTable.initializeTable( dbTableDataTableModel );

                //Add the sql to the combo box if it is not a multiline statement
                boolean b = doesStringOccurMoreThanOnceInAnotherString(sql, ";");
                if( b == false )
                {
                    sqlHistoryComboBoxModel.addElement( sql );
                }
            }

            //Update the UI
            this.updateUI();
        }
        catch(DBEngineException exc)
        {
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
            JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }

        //Update the UI
        this.updateUI();    	
    }
    
    private boolean doesStringOccurMoreThanOnceInAnotherString(String containerString, String stringToLookFor)
    {
    	boolean occursMoreThanOnce = false;
    	int firstLocation = containerString.indexOf(stringToLookFor);
    	
    	if( firstLocation != -1 )
    	{
    		int secondLocation = containerString.indexOf(stringToLookFor, firstLocation + stringToLookFor.length());
    		if(secondLocation != -1)
    		{
    			occursMoreThanOnce = true;
    		}
    	}
    	return occursMoreThanOnce;
    }

    private class SQLHistoryComboBoxModel extends DefaultComboBoxModel
    {
        public void addElement(Object anObject)
        {
            //Add the element if it does not already exist
            if( this.getIndexOf(anObject) == -1 )
            {
                super.addElement(anObject);
            }
        }
    }
}
