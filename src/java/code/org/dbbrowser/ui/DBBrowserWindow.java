package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.lookandfeel.LookAndFeelHandler;
import infrastructure.propertymanager.PropertyManagementException;
import infrastructure.propertymanager.PropertyManager;
import infrastructure.logging.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DriverManagerException;
import org.dbbrowser.ui.helper.KeyBindingsSerializer;
import org.dbbrowser.ui.panel.dbbrowserwindow.DBBrowserConnectionInstancePanel;
import org.dbbrowser.ui.widget.MemoryMonitor;
import org.dbbrowser.help.HelpManager;

public class DBBrowserWindow implements ActionListener, ItemListener, ChangeListener, Observer
{
    private JFrame dbBrowserFrame = new JFrame();
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);
    private JMenuBar menuBar = new JMenuBar();
    private JToolBar toolBar = new JToolBar();

    //Labels for file menu
    private static final String fileMenuLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-label", null);
    private static final String fileMenuConnectLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-connect-label", null);
    private static final String fileMenuDropConnectionLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-drop-connection-label", null);
    private static final String fileMenuBatchRunLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-batch-run-label", null);
    private static final String fileMenuSetLookandFeelLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-lookandfeel-label", null);
    private static final String fileMenuExitLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-file-menu-exit-label", null);

    //Labels for Edit menu
    private String editMenuLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-edit-menu-label", null);
    private String editMenuPreferencesLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-edit-menu-preferences-label", null);

    //Labels for Tools menu
    private static final String toolsMenuLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-tools-menu-label", null);
    private static final String toolsMenuReverseEngineerLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-tools-menu-paste-label", null);
    private static final String toolsMenuCommitLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-commit-button-label", null);
    private static final String toolsMenuRollbackLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-view-record-window-rollback-button-label", null);

    //Labels for View menu
    private static final String viewMenuLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-view-menu-label", null);
    private static final String viewMenuRefreshLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-view-menu-refresh-label", null);
    private static final String viewMenuShowToolbarLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-view-menu-showtoolbar-label", null);
    private static final String viewMenuShowSQLLogLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-show-sql-log-label", null);
    private static final String viewMenuShowMemoryMonitorLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-view-menu-show-memory-monitor-label", null);

    //Labels for Help  menu
    private static final String helpMenuLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-label", null);
    private static final String helpMenuAboutLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-about-label", null);
    private static final String helpMenuCommentsFeedbackLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-comments-feedback-label", null);
    private static final String helpMenuHelpLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-help-label", null);

    private static final String connectIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-test-connection-icon");
    private static final String dropConnectionIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-remove-sql-tab-button-icon");
    private static final String exitIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-file-menu-exit-icon");
    private static final String lookAndFeelIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-lookandfeel-icon");
    private static final String importIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-runbatchfile-icon");
    private static final String refreshIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-refresh-icon");
    private static final String aboutIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-about-menu-icon");
    private static final String viewToolbarIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-view-menu-toolbar-icon");
    private static final String viewSQLLogIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-view-menu-show-sql-log-icon");
    private static final String viewMemoryMonitorIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-good-signal-icon");
    private static final String preferencesIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-preferences-icon");
    private static final String reverseEngineerIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-reverse-engineer-icon");
    private static final String commitIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-commit-button-icon");
    private static final String rollbackIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-rollback-button-icon");
    private static final String helpIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-help-icon");
    private static final String commentsFeedbackIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-comments-feedback-menu-icon");
    private static final String windowIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-icon-filename");
    private Image windowIcon = (new ImageIcon(windowIconFileName)).getImage();

    private static final String connectionInstanceTabIconFilename = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-connection-instance-tab-icon");
    private static final String USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-use-master-password");

    private JTabbedPane tabbedPaneForConnectionInstance = new JTabbedPane();
    private DBBrowserConnectionInstancePanel dbBrowserConnectionInstancePanel = null;
    private SQLLogUI sqlLogUI = new SQLLogUI();
    private Map mapOfActionNameToAction = new HashMap();
    private PreferencesWindow preferencesWindow = null;

    public DBBrowserWindow()
    {
    	this.tabbedPaneForConnectionInstance.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
        initialize();
        addMenuBar();
        addToolBar();
        this.dbBrowserFrame.getContentPane().add( this.tabbedPaneForConnectionInstance );
        setupActionMaps();
    }
    
    private void setupActionMaps()
    {
    	//Get all the possible actions and add them to the map
    	List listOfActions = KeyBinding.getAllActions();
    	Iterator i = listOfActions.iterator();
    	while( i.hasNext() )
    	{
    		String anActionString = (String)i.next();
    		mapOfActionNameToAction.put(anActionString, new DBBrowserKeyAction());
    	}
    }
    
    public void update(Observable o, Object args)
    {
    	initializeKeyBindings();
    }
    
    private void initializeKeyBindings()
    {
		//Load the key bindings
		try
		{
			List listOfKeyBindings = KeyBindingsSerializer.deserializeKeyBindings();
			
			Iterator i = listOfKeyBindings.iterator();
			while( i.hasNext() )
			{
				KeyBinding keyBinding = (KeyBinding)i.next();
				
				//Get the action
				Action anAction = (Action)mapOfActionNameToAction.get( keyBinding.getAction() );
				
				//Set the action command to tell this action apart from other actions
				anAction.putValue( "ACTION", keyBinding.getAction() );
				
				this.dbBrowserConnectionInstancePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyBinding.getKeyCode().intValue(), keyBinding.getModifierCode().intValue()), keyBinding.getDescription());
		        this.dbBrowserConnectionInstancePanel.getActionMap().put(keyBinding.getDescription(), anAction);				
			}
		}
		catch(FileNotFoundException exc)
		{
			//Ignore if key bindings have not been set
		}
		catch(IOException exc)
		{
			String[] params = new String[]{exc.getMessage()};
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-save-error-message", params);
            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);										
		}    	
    }
    
    private class DBBrowserKeyAction extends AbstractAction
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		//Get the discriminator
    		String action = (String)super.getValue("ACTION");
    		Log.getInstance().debugMessage("Pressed key for action: " + action, DBBrowserWindow.class.getName());
    		
    		if( KeyBinding.HELP.equals(action) )
    		{
    			HelpManager.getInstance().getActionListenerForHelpEvents().actionPerformed( e );
    		}
    		
    		if( KeyBinding.FILE_CONNECT.equals(action) )
    		{
    			connect();
    		}
    		
    		if( KeyBinding.EDIT_PREFERENCES.equals(action) )
    		{
    			editPreferences();
    		}

    		if( KeyBinding.FILE_DROP_CONNECTION.equals(action) )
    		{
    			dropConnection();
    		}

    		if( KeyBinding.FILE_EXIT.equals(action) )
    		{
    			exit();
    		}

    		if( KeyBinding.FILE_OPEN_SQL_SCRIPT_FILE.equals(action) )
    		{
    			batchRun();
    		}

    		if( KeyBinding.TOOLS_COMMIT.equals(action) )
    		{
    			commit();
    		}

    		if( KeyBinding.TOOLS_ROLLBACK.equals(action) )
    		{
    			rollback();
    		}
    	}
    }    
    
    public void	stateChanged(ChangeEvent e)
    {
    	if( e.getSource() instanceof JTabbedPane )
    	{
    		//Get the tabbed pane which generated this change event
    		JTabbedPane tp = (JTabbedPane)e.getSource();
    		
    		//Get the title of the selected tab
    		int selectedIndex = tp.getSelectedIndex();
    		if( selectedIndex != -1 )
    		{
    			String title = tp.getTitleAt( selectedIndex );
    		
	    		//Set the title
	    		this.dbBrowserFrame.setTitle( title + " - " + TITLE);
    		}
    		else
    		{
    			//Tabbed Pane has no tab
	    		//Set the title
	    		this.dbBrowserFrame.setTitle(TITLE);
    		}
    	}
    }

    private void initialize()
    {
        this.dbBrowserFrame.setTitle(TITLE);
        this.dbBrowserFrame.setSize(1200, 600);
        this.dbBrowserFrame.setLocationRelativeTo(null);
        this.dbBrowserFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.dbBrowserFrame.setIconImage( windowIcon );
    }

    private void addMenuBar()
    {
        //Setup file menu
        JMenu fileMenu = new JMenu(fileMenuLabel);

        JMenuItem fileConnectMenuItem = new JMenuItem(fileMenuConnectLabel, new ImageIcon(connectIconFileName));
        fileConnectMenuItem.addActionListener(this);
        fileMenu.add(fileConnectMenuItem);
        JMenuItem fileDropConnectionMenuItem = new JMenuItem(fileMenuDropConnectionLabel, new ImageIcon(dropConnectionIconFileName));
        fileDropConnectionMenuItem.addActionListener(this);
        fileMenu.add(fileDropConnectionMenuItem);
        fileMenu.addSeparator();

        JMenuItem fileBatchRunMenuItem = new JMenuItem(fileMenuBatchRunLabel, new ImageIcon(importIconFileName));
        fileBatchRunMenuItem.addActionListener(this);
        fileMenu.add(fileBatchRunMenuItem);
        fileMenu.addSeparator();
        JMenuItem fileExitMenuItem = new JMenuItem(fileMenuExitLabel, new ImageIcon(exitIconFileName));
        fileExitMenuItem.addActionListener(this);
        fileMenu.add(fileExitMenuItem);

        //Setup edit menu
        JMenu editMenu = new JMenu(editMenuLabel);
        JMenuItem editMenuPreferencesItem = new JMenuItem(editMenuPreferencesLabel, new ImageIcon(preferencesIconFileName));
        editMenuPreferencesItem.addActionListener(this);
        editMenu.add(editMenuPreferencesItem);

        //Setup tools menu
        JMenu toolsMenu = new JMenu(toolsMenuLabel);
        JMenuItem toolsMenuReverseEngineerItem = new JMenuItem(toolsMenuReverseEngineerLabel, new ImageIcon(reverseEngineerIconFileName));
        toolsMenuReverseEngineerItem.addActionListener(this);
        toolsMenu.add(toolsMenuReverseEngineerItem);
        toolsMenu.addSeparator();
        JMenuItem toolsMenuCommitItem = new JMenuItem(toolsMenuCommitLabel, new ImageIcon(commitIconFileName));
        toolsMenuCommitItem.addActionListener(this);
        toolsMenu.add(toolsMenuCommitItem);
        JMenuItem toolsMenuRollbackItem = new JMenuItem(toolsMenuRollbackLabel, new ImageIcon(rollbackIconFileName));
        toolsMenuRollbackItem.addActionListener(this);
        toolsMenu.add(toolsMenuRollbackItem);

        //Setup view menu
        JMenu viewMenu = new JMenu(viewMenuLabel);
        JMenuItem viewMenuRefreshItem = new JMenuItem(viewMenuRefreshLabel, new ImageIcon(refreshIconFileName));
        viewMenuRefreshItem.addActionListener(this);
        viewMenu.add(viewMenuRefreshItem);
        viewMenu.addSeparator();        
        JMenuItem fileSetLookAndFeelMenuItem = new JMenuItem(fileMenuSetLookandFeelLabel, new ImageIcon(lookAndFeelIconFileName));
        fileSetLookAndFeelMenuItem.addActionListener(this);
        viewMenu.add(fileSetLookAndFeelMenuItem);
        viewMenu.addSeparator();        
        JCheckBoxMenuItem viewMenuShowToolbarItem = new JCheckBoxMenuItem(viewMenuShowToolbarLabel, new ImageIcon(viewToolbarIconFileName));
        viewMenuShowToolbarItem.addItemListener( this );
        viewMenuShowToolbarItem.setActionCommand( viewMenuShowToolbarLabel );

        //Menu item is selected if it is selected in properties
        //Add the toolbar to the frame if it is set in the properties
        String showToolbar = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-show-toolbar");
        if("true".equals( showToolbar ))
        {
            viewMenuShowToolbarItem.setSelected( true );
        }
        viewMenu.add(viewMenuShowToolbarItem);

        //Add menu item for toggle SQL on or off
        JCheckBoxMenuItem viewMenuShowSQLLogItem = null;
        String showSQLLog = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-show-sql-log");
        if("true".equals( showSQLLog ))
        {
            viewMenuShowSQLLogItem = new JCheckBoxMenuItem(viewMenuShowSQLLogLabel, new ImageIcon(viewSQLLogIconFileName), true);
            viewMenuShowSQLLogItem.setSelected( true );
        }
        else
        {
            viewMenuShowSQLLogItem = new JCheckBoxMenuItem(viewMenuShowSQLLogLabel, new ImageIcon(viewSQLLogIconFileName));
            viewMenuShowSQLLogItem.setSelected( false );
        }
        
        viewMenuShowSQLLogItem.addItemListener( this );
        viewMenuShowSQLLogItem.setActionCommand( viewMenuShowSQLLogLabel );

        viewMenu.add(viewMenuShowSQLLogItem);
        
        //Add the view memory monitor label
        JMenuItem viewMenuShowMemoryMonitorMenuItem = new JMenuItem(viewMenuShowMemoryMonitorLabel, new ImageIcon(viewMemoryMonitorIconFileName));
        viewMenuShowMemoryMonitorMenuItem.addActionListener( this );
        viewMenuShowMemoryMonitorMenuItem.setActionCommand( viewMenuShowMemoryMonitorLabel );        
        viewMenu.add(viewMenuShowMemoryMonitorMenuItem);
        
        //Setup help menu
        JMenu helpMenu = new JMenu(helpMenuLabel);
        JMenuItem helpMenuHelpItem = new JMenuItem(helpMenuHelpLabel, new ImageIcon(helpIconFileName));
        helpMenuHelpItem.addActionListener( HelpManager.getInstance().getActionListenerForHelpEvents() );
        helpMenu.add(helpMenuHelpItem);
        JMenuItem helpMenuCommentsFeedbackItem = new JMenuItem(helpMenuCommentsFeedbackLabel, new ImageIcon(commentsFeedbackIconFileName));
        helpMenuCommentsFeedbackItem.addActionListener( this );
        helpMenu.add(helpMenuCommentsFeedbackItem);
        JMenuItem helpMenuAboutItem = new JMenuItem(helpMenuAboutLabel, new ImageIcon(aboutIconFileName));
        helpMenuAboutItem.addActionListener(this);
        helpMenu.add(helpMenuAboutItem);

        //Setup the menu bar for the frame
        this.menuBar.add( fileMenu );
        this.menuBar.add( editMenu );
        this.menuBar.add( toolsMenu );
        this.menuBar.add( viewMenu );
        this.menuBar.add( helpMenu );
        this.dbBrowserFrame.setJMenuBar( this.menuBar );
    }

    private void addToolBar()
    {
        //Set the connect button on the toolbar
        Icon iconForConnect = new ImageIcon( connectIconFileName );
        JButton buttonToConnect = new JButton( fileMenuConnectLabel, iconForConnect );
        buttonToConnect.addActionListener( this );
        this.toolBar.add( buttonToConnect );

        //Set the connect button on the toolbar
        Icon iconToDropConnection = new ImageIcon( dropConnectionIconFileName );
        JButton buttonToDropConnection = new JButton( fileMenuDropConnectionLabel, iconToDropConnection );
        buttonToDropConnection.addActionListener( this );
        this.toolBar.add( buttonToDropConnection );

        //Set the look and feel button on the toolbar
        Icon iconToSetLookAndFeel = new ImageIcon( lookAndFeelIconFileName );
        JButton buttonToSetLookAndFeel = new JButton( fileMenuSetLookandFeelLabel, iconToSetLookAndFeel );
        buttonToSetLookAndFeel.addActionListener( this );
        //this.toolBar.add( buttonToSetLookAndFeel );

        //Set the import button on the toolbar
        Icon iconForImport = new ImageIcon( importIconFileName );
        JButton importButton = new JButton( fileMenuBatchRunLabel, iconForImport );
        importButton.addActionListener( this );
        this.toolBar.add( importButton );

        //Set the Reverse enginner button on the toolbar
//        String reverseEngineerIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-reverse-engineer-icon");
//        Icon iconForReverseEngineer = new ImageIcon( reverseEngineerIconFileName );
//        JButton reverseEngineerButton = new JButton( toolsMenuReverseEngineerLabel, iconForReverseEngineer );
//        reverseEngineerButton.addActionListener( this );
//        this.toolBar.add( reverseEngineerButton );
        
        //Commit button
	    Icon iconForCommitButton = new ImageIcon( commitIconFileName );
	    JButton commitButton = new JButton( toolsMenuCommitLabel, iconForCommitButton );
	    commitButton.addActionListener( this );
	    this.toolBar.add( commitButton ); 
	    
	    //Rollback button
	    Icon iconForRollbackButton = new ImageIcon( rollbackIconFileName );
	    JButton rollbackButton = new JButton( toolsMenuRollbackLabel, iconForRollbackButton );
	    rollbackButton.addActionListener( this );
	    this.toolBar.add( rollbackButton );	    

        //Set the refresh button on the toolbar
        Icon iconForRefresh = new ImageIcon( refreshIconFileName );
        JButton refereshButton = new JButton( viewMenuRefreshLabel, iconForRefresh );
        refereshButton.addActionListener( this );
        this.toolBar.add( refereshButton );

        //Add the toolbar to the frame if it is set in the properties
        String showToolbar = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-show-toolbar");
        if("true".equals( showToolbar ))
        {
            this.dbBrowserFrame.getContentPane().add( this.toolBar, BorderLayout.PAGE_START);
        }
    }

    public void setupTabbedPane(ConnectionInfo connectionInfo, String masterPassword)
        throws DriverManagerException, DBEngineException
    {
        //If the connection is already in use, drop it and reconnect
        boolean connectionAlreadyinUse = false;
        Component[] tabs = this.tabbedPaneForConnectionInstance.getComponents();
        for(int i=0; i<tabs.length; i++)
        {
        	Object o = tabs[ i ];
        	if( o != null && o instanceof DBBrowserConnectionInstancePanel)
        	{
	            DBBrowserConnectionInstancePanel dbBrowserConnectionInstancePanel = (DBBrowserConnectionInstancePanel)tabs[ i ];
	            String connectionInfoName = dbBrowserConnectionInstancePanel.getConnectionInfoName();
	            if( connectionInfo.getName().equals( connectionInfoName ) )
	            {
	                connectionAlreadyinUse = true;
	                break;
	            }
        	}
        }

        if( connectionAlreadyinUse )
        {
            this.tabbedPaneForConnectionInstance.remove( this.dbBrowserConnectionInstancePanel );
        }
        
        //Start the ui controller
        UIControllerForQueries uiControllerForQueries = new UIControllerForQueries();
        uiControllerForQueries.setup( connectionInfo, masterPassword );
        UIControllerForUpdates uiControllerForUpdates = new UIControllerForUpdates();
        uiControllerForUpdates.setup( connectionInfo, masterPassword );
        UIControllerForRawSQL uiControllerForRawSQL = new UIControllerForRawSQL();
        uiControllerForRawSQL.setup( connectionInfo, masterPassword );

        this.dbBrowserConnectionInstancePanel = new DBBrowserConnectionInstancePanel(uiControllerForQueries, uiControllerForUpdates, uiControllerForRawSQL);
        this.tabbedPaneForConnectionInstance.addTab(connectionInfo.getName(), new ImageIcon(connectionInstanceTabIconFilename), this.dbBrowserConnectionInstancePanel, connectionInfo.getName());
        
        //Set the title of the dbbrowser window
		this.dbBrowserFrame.setTitle( connectionInfo.getName() + " - " + TITLE);
		
		//Update the UI
        this.tabbedPaneForConnectionInstance.updateUI();
        
        //Add the change listener which is used to change the title of dbbroweser window
        this.tabbedPaneForConnectionInstance.addChangeListener( this );
        
        //Setup key bindings
        this.initializeKeyBindings();
    }

    public void showSQLLog()
    {
        //If SQL Log is required, show it
        String showSQLLog = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-show-sql-log");
        if( "true".equals( showSQLLog ) )
        {
            this.sqlLogUI.show();
            Log.getInstance().debugMessage("SQL Logger UI has been started", this.getClass().getName());
        }
        
        this.dbBrowserFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        //Set the look and feel if look and feel button pressed
        if( fileMenuSetLookandFeelLabel.equals( e.getActionCommand() ))
        {
            setLookAndFeel();
        }

        //Refresh data in a table
        if( viewMenuRefreshLabel.equals( e.getActionCommand() ))
        {
        	refresh();
        }

        //Exit
        if( fileMenuExitLabel.equals( e.getActionCommand() ))
        {
            //Exit
        	exit();
        }

        //Batch run
        if( fileMenuBatchRunLabel.equals( e.getActionCommand() ))
        {
        	batchRun();
        }

        //Reverse Engineer
        if( toolsMenuReverseEngineerLabel.equals( e.getActionCommand() ))
        {
            String UNIMPLEMENTED_FEATURE_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-unimplemented-feature", null);
            JOptionPane.showMessageDialog(null, UNIMPLEMENTED_FEATURE_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
        }

        //Change preferences
        if( editMenuPreferencesLabel.equals( e.getActionCommand() ))
        {
        	editPreferences();
        }

        //Setup another connection
        if( fileMenuConnectLabel.equals( e.getActionCommand() ) )
        {
        	connect();
        }

        //Show about window
        if( helpMenuAboutLabel.equals( e.getActionCommand() ) )
        {
            AboutWindow aboutWindow = new AboutWindow();
            aboutWindow.show();    	        	
        }

        //Show about window
        if( helpMenuCommentsFeedbackLabel.equals( e.getActionCommand() ) )
        {
            CommentsFeedbackWindow commentsFeedbackWindow = new CommentsFeedbackWindow();
            commentsFeedbackWindow.show();
        }

        //Close the connection tab
        if( fileMenuDropConnectionLabel.equals( e.getActionCommand() ) )
        {      
        	dropConnection();
        }
        
        //Commit
        if( toolsMenuCommitLabel.equals( e.getActionCommand() ) )
        {
        	commit();
        } 
        
        //Rollback
        if( toolsMenuRollbackLabel.equals( e.getActionCommand() ) )
        {
        	rollback();
        }  
        
        //Show memory monitor
        if( viewMenuShowMemoryMonitorLabel.equals( e.getActionCommand() ) )
        {
        	showMemoryMonitor();
        }         
    }
    
    private void showMemoryMonitor()
    {
    	MemoryMonitor memoryMonitor = new MemoryMonitor();
    	memoryMonitor.show();    	
    }

    public void itemStateChanged(ItemEvent e)
    {
        Object o = e.getItem();
        JMenuItem mi = (JMenuItem)o;
        String actionCommand = mi.getActionCommand();

        if(viewMenuShowToolbarLabel.equals( actionCommand ))
        {
            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
                this.dbBrowserFrame.remove( this.toolBar );
                //Persist the new property
                try
                {
                    PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-toolbar", "false");
                }
                catch(PropertyManagementException exc)
                {
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                    JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                this.dbBrowserFrame.getContentPane().add( this.toolBar, BorderLayout.PAGE_START);
                //Persist the new property
                try
                {
                    PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-toolbar", "true");
                }
                catch(PropertyManagementException exc)
                {
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                    JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }

            //Get the top level ancestor and ask it to update itself
            Component c = SwingUtilities.getRoot(this.dbBrowserFrame);
            if( c instanceof JFrame)
            {
                SwingUtilities.updateComponentTreeUI(c);
            }
        }

        if(viewMenuShowSQLLogLabel.equals( actionCommand ))
        {
            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
                //Persist the new property
                try
                {
                    PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-sql-log", "false");
                    this.sqlLogUI.hide();
                    Log.getInstance().debugMessage("SQL Logger UI has been closed", this.getClass().getName());
                }
                catch(PropertyManagementException exc)
                {
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                    JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                //Persist the new property
                try
                {
                    PropertyManager.getInstance().setProperty("dbbrowser-ui-dbbrowser-window-show-sql-log", "true");
                    this.sqlLogUI.show();
                    Log.getInstance().debugMessage("SQL Logger UI has been started", this.getClass().getName());
                }
                catch(PropertyManagementException exc)
                {
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                    JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }       
    }
    
    private void refresh()
    {
        this.dbBrowserConnectionInstancePanel = (DBBrowserConnectionInstancePanel)this.tabbedPaneForConnectionInstance.getSelectedComponent();
        
        //if there is a connection panel, then refresh it
        if( this.dbBrowserConnectionInstancePanel != null )
        {
        	this.dbBrowserConnectionInstancePanel.refresh();
        }
    }
    
    private void exit()
    {
    	System.exit(0);
    }
    
    private void batchRun()
    {
        //Get the file to run as batch file
        JFileChooser jfc = new JFileChooser();
        jfc.setMultiSelectionEnabled( false );
        int ans = jfc.showOpenDialog(null);

        if( JFileChooser.APPROVE_OPTION == ans )
        {
            File fileToOpen = jfc.getSelectedFile();
            this.dbBrowserConnectionInstancePanel = (DBBrowserConnectionInstancePanel)this.tabbedPaneForConnectionInstance.getSelectedComponent();
            
            //if there is a connection panel, then refresh it
            if( this.dbBrowserConnectionInstancePanel != null )
            {
            	this.dbBrowserConnectionInstancePanel.runBatchFile( fileToOpen );
            }
        }    	
    }
    
    private void editPreferences()
    {
    	this.preferencesWindow = new PreferencesWindow( this );
    	this.preferencesWindow.show();    	
    }
    
    private void connect()
    {
    	ConnectionInformationWindow connectionInformationWindow = new ConnectionInformationWindow( this );
        connectionInformationWindow.show();    	
    }
    
    private void dropConnection()
    {
        String dropConnectionConfirmMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-drop-connection-confirm-message", null);
        int ans = JOptionPane.showConfirmDialog(null, dropConnectionConfirmMessage, TITLE, JOptionPane.OK_CANCEL_OPTION);
        
        if(ans == JOptionPane.OK_OPTION)
        {
            int selectedConnectionInstanceTabIndex = this.tabbedPaneForConnectionInstance.getSelectedIndex();
            if( selectedConnectionInstanceTabIndex != -1 )
            {
                this.tabbedPaneForConnectionInstance.removeTabAt( selectedConnectionInstanceTabIndex );
            }
        }    	
    }
    
    private void commit()
    {
    	DBBrowserConnectionInstancePanel dbBrowserConnectionInstancePanel = (DBBrowserConnectionInstancePanel)this.tabbedPaneForConnectionInstance.getSelectedComponent();
        
    	//if there is a connection panel, then refresh it
        if( this.dbBrowserConnectionInstancePanel != null )
        {        	
        	dbBrowserConnectionInstancePanel.commit();
        }    	
    }
    
    private void rollback()
    {
    	DBBrowserConnectionInstancePanel dbBrowserConnectionInstancePanel = (DBBrowserConnectionInstancePanel)this.tabbedPaneForConnectionInstance.getSelectedComponent();
    	
        //if there is a connection panel, then refresh it
        if( this.dbBrowserConnectionInstancePanel != null )
        {        	
        	dbBrowserConnectionInstancePanel.rollback();
        }    	
    }

    private void setLookAndFeel()
    {
        String chooseLookAndFeelLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-choose-lookandfeel-message", null);
        Object[] installedLookAndFeels = LookAndFeelHandler.getInstance().getAllInstalledLookAndFeels();
        String currentLookandFeel = LookAndFeelHandler.getInstance().getCurrentLookAndFeel();
        Object response = JOptionPane.showInputDialog(this.dbBrowserFrame, chooseLookAndFeelLabel, TITLE, JOptionPane.PLAIN_MESSAGE, null, installedLookAndFeels, currentLookandFeel);

        if(response != null)
        {
            String newLookAndFeel = (String)response;
            LookAndFeelHandler.getInstance().setLookAndFeel( newLookAndFeel );
            //Get the top level ancestor and ask it to update itself
            Component c = SwingUtilities.getRoot(this.dbBrowserFrame);
            if( c instanceof JFrame)
            {
                SwingUtilities.updateComponentTreeUI(c);
            }

            //Persist the changes
            try
            {
                PropertyManager.getInstance().setProperty("dbbrowser.ui.lookandfeel", newLookAndFeel);
            }
            catch(PropertyManagementException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
