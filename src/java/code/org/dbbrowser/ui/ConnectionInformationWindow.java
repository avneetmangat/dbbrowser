package org.dbbrowser.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import infrastructure.propertymanager.PropertyManagementException;
import javax.swing.*;
import org.dbbrowser.DBBrowser;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.ConnectionInfoSerializer;
import org.dbbrowser.drivermanager.ConnectionInfoSorter;
import org.dbbrowser.drivermanager.ConnectionInfos;
import org.dbbrowser.drivermanager.DriverManagerException;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.ui.helper.ConnectionInfoFileFilter;
import org.dbbrowser.ui.panel.connectioninformationwindow.KnownConnectionsPanel;
import org.dbbrowser.ui.panel.connectioninformationwindow.NewConnectionDetailsPanel;
import org.dbbrowser.ui.panel.connectioninformationwindow.PasswordInputWindow;
import org.dbbrowser.help.HelpManager;

public class ConnectionInformationWindow implements ActionListener
{
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    private static final String CONNECT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-connect-button-label", null);
    private static final String EDIT_CONNECTION_INFO_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-edit-connection-info-button-label", null);
    private static final String DELETE_CONNECTION_INFO_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-delete-connection-info-button-label", null);
    private static final String SAVE_CHANGES_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-save-changes-button-label", null);
    private static final String TEST_CONNECTION_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-test-connection-button-label", null);

    //Labels for menu
    private static final String FILE_MENU_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-connection-info-file-menu-label", null);
    private static final String FILE_OPEN_MENU_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-connection-info-file-open-menu-label", null);
    private static final String HELP_MENU_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-label", null);
    private static final String HELP_MENU_HELP_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-help-label", null);
    private static final String HELP_MENU_COMMENTS_FEEDBACK_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-comments-feedback-label", null);
    private static final String HELP_MENU_ABOUT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-about-label", null);
    private static final String MASTER_PASSWORD_INPUT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-enter-master-password-message", null);

    private static final String fileOpenIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-file-open-icon");
    private static final String helpIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-help-icon");
    private static final String COMMENTS_FEEDBACK_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-comments-feedback-menu-icon");
    private static final String aboutIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-about-menu-icon");
    private static final String windowIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-icon-filename");
    private static final String USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-use-master-password");

    private Image windowIcon = (new ImageIcon(windowIconFileName)).getImage();
    
    private JFrame connectionInformationWindow = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private KnownConnectionsPanel knownConnectionsPanel = null;
    private NewConnectionDetailsPanel newConnectionDetailsPanel = null;
    private ConnectionInfos connectionInfos = null;
    private DBBrowserWindow dbBrowserWindow = null;

    public ConnectionInformationWindow()
    {
    	loadConnectionInfo();
        newConnectionDetailsPanel = new NewConnectionDetailsPanel( this );
        initializeWindow();
        addMenuBar();
    }

    public ConnectionInformationWindow(DBBrowserWindow dbBrowserWindow)
    {
        this.dbBrowserWindow = dbBrowserWindow;
    	loadConnectionInfo();
        newConnectionDetailsPanel = new NewConnectionDetailsPanel( this );
        initializeWindow();
        addMenuBar();
    }

    private void addMenuBar()
    {
        //Setup help menu
    	JMenu fileMenu = new JMenu(FILE_MENU_LABEL);
        JMenuItem fileOpenMenuHelpItem = new JMenuItem(FILE_OPEN_MENU_LABEL, new ImageIcon(fileOpenIconFileName));
        fileOpenMenuHelpItem.addActionListener( this );
        fileMenu.add(fileOpenMenuHelpItem);
        JMenu helpMenu = new JMenu(HELP_MENU_LABEL);
        JMenuItem helpMenuHelpItem = new JMenuItem(HELP_MENU_HELP_LABEL, new ImageIcon(helpIconFileName));
        helpMenuHelpItem.addActionListener( HelpManager.getInstance().getActionListenerForHelpEvents() );
        helpMenu.add(helpMenuHelpItem);
        JMenuItem helpMenuCommentsFeedbackItem = new JMenuItem(HELP_MENU_COMMENTS_FEEDBACK_LABEL, new ImageIcon(COMMENTS_FEEDBACK_ICON_FILENAME));
        helpMenuCommentsFeedbackItem.addActionListener( this );
        helpMenu.add(helpMenuCommentsFeedbackItem);
        JMenuItem helpMenuAboutItem = new JMenuItem(HELP_MENU_ABOUT_LABEL, new ImageIcon(aboutIconFileName));
        helpMenuAboutItem.addActionListener(this);
        helpMenu.add(helpMenuAboutItem);

        //Setup the menu bar for the frame
        this.menuBar.add( fileMenu );
        this.menuBar.add( helpMenu );
        this.connectionInformationWindow.setJMenuBar( this.menuBar );
    }

    private void initializeWindow()
    {
        //Set window properties
        String formattedMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-connection-info-title", null);
        this.connectionInformationWindow.setTitle(formattedMessage);
        this.connectionInformationWindow.setSize(1200, 600);
        this.connectionInformationWindow.setLocationRelativeTo(null);
        this.connectionInformationWindow.setIconImage( windowIcon );

        //if there is no dbBrowserWindow window open, then exit on close
        if( this.dbBrowserWindow == null )
        {
            this.connectionInformationWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        }

        //Add the panels
        this.connectionInformationWindow.getContentPane().setLayout( new GridLayout(2,0) );
        this.connectionInformationWindow.getContentPane().add(this.newConnectionDetailsPanel);
    }

    public void show()
    {
        //Show the first time message if it has not been shown
        String firstRunCompleted = PropertyManager.getInstance().getProperty( "dbbrowser-first-run-completed" );
        if( "false".equals(firstRunCompleted) )
        {
            //If the fist run was never run, run it and set it to true so it is not shown ever again
            FirstRunMessageDialog firstRunMessageDialog = new FirstRunMessageDialog();
            firstRunMessageDialog.show();
            try
            {
                PropertyManager.getInstance().setProperty("dbbrowser-first-run-completed", "true");
            }
            catch(PropertyManagementException exc)
            {
                //Ignore it
            }
        }
        this.connectionInformationWindow.setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
    	if( FILE_OPEN_MENU_LABEL.equals(e.getActionCommand()) )
    	{
    	    //Present a dialog to choose connection info file
    	    JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter( new ConnectionInfoFileFilter() );
            jfc.setFileSelectionMode( JFileChooser.FILES_ONLY );
            jfc.setMultiSelectionEnabled( false );
            jfc.showOpenDialog(null);
            File selectedFile = jfc.getSelectedFile();
            
            //if the user has selected a file, load the connection info
            if( selectedFile != null )
            {
	            //Set the property
	            try
	            {
	            	PropertyManager.getInstance().setProperty("dbbrowser.connectioninfo.serialize.location", selectedFile.getAbsolutePath());
	            }
	            catch(PropertyManagementException exc)
	            {
	                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
	                JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
	            }
	            
	            //Load the connection info
	            loadConnectionInfo();
            }
    	}
    	
        if( CONNECT_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
        	connect();
        }
        
        if( EDIT_CONNECTION_INFO_LABEL.equals( e.getActionCommand() ))
        {   
            Integer selectedRow = this.knownConnectionsPanel.getSelectedRow();

            //if the source is a row in the table
            if( selectedRow != null )
            {
                //Get the master password from user and set it
                useMasterPassword();                    
            	
                ConnectionInfo ci = (ConnectionInfo)this.connectionInfos.getListOfConnectionInfos().get( selectedRow.intValue() );
                newConnectionDetailsPanel.setConnectionInfo( ci, this.connectionInfos.getMasterPassword() );
            }
            else
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-no-connection-selected-error-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);
            }            
        }

        if( DELETE_CONNECTION_INFO_LABEL.equals( e.getActionCommand() ))
        {
            Integer selectedConnectionInfo = this.knownConnectionsPanel.getSelectedRow();

            if(selectedConnectionInfo != null)
            {
                String connectionInfoDeleteConfirmMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-delete-connection-info-confirm-message", null);
                int ans = JOptionPane.showConfirmDialog(null, connectionInfoDeleteConfirmMessage, TITLE, JOptionPane.OK_CANCEL_OPTION);

                if( ans==0 )
                {
                    try
                    {
                    	this.connectionInfos.getListOfConnectionInfos().remove(selectedConnectionInfo.intValue());
                        ConnectionInfoSerializer.serialize( this.connectionInfos.getListOfConnectionInfos() );
                        this.knownConnectionsPanel.update();
                    }
                    catch(IOException exc)
                    {
                        String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-exception-connectioninfo-update-failed", null);
                        JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-no-connection-selected-error-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);
            }
        }

        if( TEST_CONNECTION_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            //Get the master password from user and set it
            useMasterPassword();                    

            //Build a connection info
            ConnectionInfo connectionInfo = this.newConnectionDetailsPanel.getConnectionInfo(this.connectionInfos.getMasterPassword());
            if( connectionInfo==null )
            {
                String errorMessageForMissingData = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-missing-data-message", null);
                JOptionPane.showMessageDialog(null, errorMessageForMissingData, TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                //Store the default cursor and show the wait cursor
            	Cursor defaultCursor = Cursor.getDefaultCursor();
            	Cursor waitCursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
                try
                {
                	//Change the cursor to hourglass
               		this.connectionInformationWindow.setCursor( waitCursor );
                    //Call the Driver Manager to test the connection
                    DBBrowserDriverManager.getInstance().getConnection(connectionInfo, this.connectionInfos.getMasterPassword());

                    String connectionSuccessfulMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-test-connection-success-message", null);
                    JOptionPane.showMessageDialog(null, connectionSuccessfulMessage, TITLE, JOptionPane.INFORMATION_MESSAGE);
                }
                catch(DriverManagerException exc)
                {
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-exception-connect-failed", null);
                    JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
                finally
                {
                	//Change the cursor to normal cursor
                	this.connectionInformationWindow.setCursor( defaultCursor );
                }                 	
            }
        }

        if( SAVE_CHANGES_BUTTON_LABEL.equals( e.getActionCommand() ))
        {
            //Get the master password from user and set it
            useMasterPassword();                    
        	
            ConnectionInfo connectionInfo = this.newConnectionDetailsPanel.getConnectionInfo(this.connectionInfos.getMasterPassword());
            if( connectionInfo==null )
            {
                String errorMessageForMissingData = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-missing-data-message", null);
                JOptionPane.showMessageDialog(null, errorMessageForMissingData, TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                try
                {
                    //If there is a connection info with the same name, overwrite it.  ConnectionInfo class overrides equals method and uses name to do the comparision
                    int connectionInfoLocation = this.connectionInfos.getListOfConnectionInfos().indexOf( connectionInfo );
                    if( connectionInfoLocation != -1 )
                    {
                    	this.connectionInfos.getListOfConnectionInfos().remove( connectionInfoLocation );
                    	this.connectionInfos.getListOfConnectionInfos().add( connectionInfo );
                    }
                    else
                    {
                    	this.connectionInfos.getListOfConnectionInfos().add(connectionInfo);
                    }

                    //Update the UI and store the settings
                    ConnectionInfoSerializer.serialize( this.connectionInfos.getListOfConnectionInfos() );
                    this.knownConnectionsPanel.update();
                    this.newConnectionDetailsPanel.clearFields();
                }
                catch(IOException exc)
                {
                    String[] s = new String[]{exc.getMessage()};
                    String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-new-connections-panel-exception-connectioninfo-update-failed", s);
                    JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);

                    //Select file to save to
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode( JFileChooser.FILES_ONLY );
                    jfc.setMultiSelectionEnabled( false );
                    jfc.showSaveDialog(null);
                    File file = jfc.getSelectedFile();

                    //Set the property
                    try
                    {
                        PropertyManager.getInstance().setProperty("dbbrowser.connectioninfo.serialize.location", file.getAbsolutePath());
                        Log.getInstance().debugMessage("*** Set dbbrowser.connectioninfo.serialize.location to " + file.getAbsolutePath() + " ***", ConnectionInformationWindow.class.getName());
                    }
                    catch(PropertyManagementException pme)
                    {
                        String errorMessage2 = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-save-property-failed-message", null);
                        JOptionPane.showMessageDialog(null ,errorMessage2, TITLE, JOptionPane.ERROR_MESSAGE);
                    }

                    //Update the UI and store the settings
                    try
                    {
                        ConnectionInfoSerializer.serialize( this.connectionInfos.getListOfConnectionInfos() );
                        this.knownConnectionsPanel.update();
                        this.newConnectionDetailsPanel.clearFields();
                    }
                    catch(IOException ioexc)
                    {
                        String[] s2 = new String[]{exc.getMessage()};
                        String errorMessage2 = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-new-connections-panel-exception-connectioninfo-update-failed", s2);
                        JOptionPane.showMessageDialog(null, errorMessage2, TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        if( HELP_MENU_ABOUT_LABEL.equals( e.getActionCommand() ))
        {
            AboutWindow aboutWindow = new AboutWindow();
            aboutWindow.show();            
        }

        //Show about window
        if( HELP_MENU_COMMENTS_FEEDBACK_LABEL.equals( e.getActionCommand() ) )
        {
            CommentsFeedbackWindow commentsFeedbackWindow = new CommentsFeedbackWindow();
            commentsFeedbackWindow.show();
        }
    }
    
    private void useMasterPassword()
    {
        //Get the master password from the user if a master password is to be used and the password has not been set
		if( "true".equals( USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO ) &&  (!this.connectionInfos.isPasswordSetByUser().booleanValue()) )
		{
			//String masterPassword = (String)JOptionPane.showInputDialog(null,MASTER_PASSWORD_INPUT_LABEL, TITLE, JOptionPane.PLAIN_MESSAGE, null, null, null);
			PasswordInputWindow piw = new PasswordInputWindow(Boolean.FALSE);
			piw.show();
			String masterPassword = piw.getPassword();
			if( masterPassword != null && (!"".equals(masterPassword)) )
			{
				this.connectionInfos.setMasterPassword( masterPassword );
			}
		}		
    }
    
    private void loadConnectionInfo()
    {
    	List listOfConnectionInfo = new ArrayList();
        //Get the connection info
        try
        {
            listOfConnectionInfo = ConnectionInfoSerializer.deserialize();
            Log.getInstance().debugMessage("Found " + listOfConnectionInfo.size() + " connection info objects" , DBBrowser.class.getName());

            //Sort the listOfConnectionInfo by 'last used' column
            Collections.sort(listOfConnectionInfo, new ConnectionInfoSorter());
        }
        catch(ClassNotFoundException exc)
        {
            Log.getInstance().debugMessage("No connection info found", DBBrowser.class.getName());
        }
        catch(IOException exc)
        {
            Log.getInstance().debugMessage("No connection info found", DBBrowser.class.getName());
        }
        
		this.connectionInfos = new ConnectionInfos(listOfConnectionInfo, null);
        
        //Remove the current known connections panel
        if(this.knownConnectionsPanel != null)
        {
        	this.connectionInformationWindow.getContentPane().remove( this.knownConnectionsPanel );
        }
        
        //Add the new known connections panel
        knownConnectionsPanel = new KnownConnectionsPanel(this.connectionInfos, this, new TableMouseListener());
        this.connectionInformationWindow.getContentPane().add( this.knownConnectionsPanel, 0 );
        this.knownConnectionsPanel.updateUI();
    }
    
    private void connect()
    {
        Integer selectedConnectionInfo = this.knownConnectionsPanel.getSelectedRow();

        if(selectedConnectionInfo != null)
        {
            ConnectionInfo connectionInfo = (ConnectionInfo)this.connectionInfos.getListOfConnectionInfos().get( selectedConnectionInfo.intValue() );

            try
            {
                //Store the default cursor and show the wait cursor
            	Cursor defaultCursor = Cursor.getDefaultCursor();
            	Cursor waitCursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
            	
            	//Change the cursor to hourglass
            	try
            	{
            		this.connectionInformationWindow.setCursor( waitCursor );

            		//Update the connection info
                    connectionInfo.setLastUsed( new Date() );
                    ConnectionInfoSerializer.serialize( this.connectionInfos.getListOfConnectionInfos() );
                    
                    //Show the DBBrowser window
                    if( this.dbBrowserWindow == null )
                    {
                        dbBrowserWindow = new DBBrowserWindow();
                    }
                    
                    //Get the master password from user and set it
                    useMasterPassword();                    
                    
                    dbBrowserWindow.setupTabbedPane(connectionInfo, this.connectionInfos.getMasterPassword());
            	}
                finally
                {
                	//Change the cursor to normal cursor
                	this.connectionInformationWindow.setCursor( defaultCursor );
                }                
                
                dbBrowserWindow.showSQLLog();

                //Close the connection information window - this window
                this.connectionInformationWindow.dispose();
                this.connectionInformationWindow = null;
            }
            catch(DriverManagerException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-exception-connect-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
            catch(DBEngineException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-exception-connect-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
            catch(IOException exc)
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-exception-connect-failed", null);
                JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-no-connection-selected-error-message", null);
            JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);
        }    	
    }


    /**
     * Listener for double clicks
     * @author amangat
     */
    private class TableMouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent evt)
        {
            if (evt.getClickCount() == 2)
            { 
                Component sourceComponent = evt.getComponent();

                //if the source is a jtabel, get the selected row
                if( sourceComponent instanceof JTable )
                {            	
                	connect();
                }
            }
        }
    }
}
