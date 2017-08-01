package org.dbbrowser.ui.panel.dbbrowserwindow;

import java.awt.BorderLayout;
import java.io.File;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForRawSQL;
import org.dbbrowser.ui.UIControllerForUpdates;

public class DBBrowserConnectionInstancePanel extends JPanel
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private UIControllerForQueries uiControllerForQueries = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    private UIControllerForRawSQL uiControllerForRawSQL = null;
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    //Tabbed panes
    private BrowserPanel browserPanel = null;
    private SQLPanel sqlPanel = null;
    private DBTableSequencesPanel dbTableSequencesPanel = null;
    private DBIndexesPanel dbIndexesPanel = null;
    private DBConstraintsPanel dbConstraintsPanel = null;
    private JTabbedPane tabbedPane = null;

    public DBBrowserConnectionInstancePanel(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates, UIControllerForRawSQL uiControllerForRawSQL)
    {
        this.uiControllerForUpdates = uiControllerForUpdates;
        this.uiControllerForQueries = uiControllerForQueries;
        this.uiControllerForRawSQL = uiControllerForRawSQL;
        initialize();

        //Setup the tabbed pane
        setupTabbedPane();
    }

    public void refresh()
    {
        this.browserPanel.refresh();
        this.dbTableSequencesPanel.refresh();
        this.dbIndexesPanel.refresh();
    }
    
    public void commit()
    {
    	try
    	{
            //Commit if autocommit if off
            String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
            if( "false".equals(autoCommitFlag) )
            {
            	this.uiControllerForUpdates.commit();
                this.uiControllerForRawSQL.commit();
            }
        }
    	catch(DBEngineException exc)
    	{
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
            JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);        		
    	}    	
    }
    
    public void rollback()
    {
    	try
    	{
            //Rollback if autocommit if off
            String autoCommitFlag = PropertyManager.getInstance().getProperty("dbbrowser-auto-commit");
            if( "false".equals(autoCommitFlag) )
            {
            	this.uiControllerForUpdates.rollback();
                this.uiControllerForRawSQL.rollback();
            }
        }
    	catch(DBEngineException exc)
    	{
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
            JOptionPane.showMessageDialog(null, errorMessage + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);        		
    	}
    }

    public void runBatchFile(File file)
    {
        this.sqlPanel.runBatchFile( file );
        this.tabbedPane.setSelectedComponent( this.sqlPanel );
    }

    public String getConnectionInfoName()
    {
        return this.uiControllerForQueries.getConnectionInfo().getName();
    }

    private void initialize()
    {
        //Set the layout
        this.setLayout( new BorderLayout() );

        //Add a border
        this.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    private void setupTabbedPane()
    {
        tabbedPane = new JTabbedPane();
        Icon browserTabIcon = new ImageIcon(PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-tab-browser-icon"));
        String browserTabbedPaneTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-title", null);
        this.browserPanel = new BrowserPanel( this.uiControllerForQueries, this.uiControllerForUpdates, this.uiControllerForRawSQL );
        tabbedPane.addTab(browserTabbedPaneTitle, browserTabIcon, this.browserPanel, browserTabbedPaneTitle);

        Icon sqlTabIcon = new ImageIcon(PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-tab-sql-icon"));
        String sqlTabbedPaneTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-sql-tab-title", null);
        this.sqlPanel = new SQLPanel( this.uiControllerForQueries, this.uiControllerForRawSQL );
        tabbedPane.addTab(sqlTabbedPaneTitle, sqlTabIcon, this.sqlPanel, sqlTabbedPaneTitle);

        Icon sequencesTabIcon = new ImageIcon(PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sequences-tab-icon"));
        String dbTableSequencesTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-db-table-sequences-tab-title", null);
        this.dbTableSequencesPanel = new DBTableSequencesPanel( this.uiControllerForQueries, this.uiControllerForUpdates );
        tabbedPane.addTab(dbTableSequencesTitle, sequencesTabIcon, this.dbTableSequencesPanel, dbTableSequencesTitle);
        
        Icon indexTabIcon = new ImageIcon(PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-index-tab-icon"));
        String dbTableIndexTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-db-table-indexes-tab-title", null);
        this.dbIndexesPanel = new DBIndexesPanel( this.uiControllerForQueries, this.uiControllerForUpdates );
        tabbedPane.addTab(dbTableIndexTitle, indexTabIcon, this.dbIndexesPanel, dbTableIndexTitle);

        Icon constraintsTabIcon = new ImageIcon(PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-constraints-tab-icon"));
        String dbTableConstraintsTabbedPaneTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-db-table-constraints-tab-title", null);
        this.dbConstraintsPanel = new DBConstraintsPanel( this.uiControllerForQueries, this.uiControllerForUpdates );
        tabbedPane.addTab(dbTableConstraintsTabbedPaneTitle, constraintsTabIcon, this.dbConstraintsPanel, dbTableConstraintsTabbedPaneTitle);

        this.add( tabbedPane );

        //Add the change listener for tab.  This is invoked when the user clicks on the tab.  Lazy loads the list of sequences for a tablespace
        this.tabbedPane.addChangeListener( this.dbTableSequencesPanel );
        this.tabbedPane.addChangeListener( this.dbIndexesPanel );
        this.tabbedPane.addChangeListener( this.dbConstraintsPanel );
    }
}
