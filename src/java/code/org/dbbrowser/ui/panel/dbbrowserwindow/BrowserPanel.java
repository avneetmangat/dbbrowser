package org.dbbrowser.ui.panel.dbbrowserwindow;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForUpdates;
import org.dbbrowser.ui.UIControllerForRawSQL;

public class BrowserPanel extends JPanel implements TreeSelectionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private String tableDataTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-tab-title", null);
    private String columnDetailsTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-columndetails-tab-title", null);
    private String dbTableSequencesTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-db-table-sequences-tab-title", null);
    private String dividerLocation = PropertyManager.getInstance().getProperty( "dbbrowser-ui-dbbrowser-window-browser-panel-splitpane-divider-location");
    
    private TableDataPanel tableDataPanel = null;
    private ColumnDetailsPanel columnDetailsPanel = null;
    private JSplitPane splitPane = null;
    private JTabbedPane tabbedPane = null;
    private UIControllerForQueries uiControllerForQueries = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    private UIControllerForRawSQL uiControllerForRawSQL = null;
    private TableTreePanel tableTreePanel = null;

    private Integer pagingSize = null;

    public BrowserPanel(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates, UIControllerForRawSQL uiControllerForRawSQL)
    {
        this.uiControllerForQueries = uiControllerForQueries;
        this.uiControllerForUpdates = uiControllerForUpdates;
        this.uiControllerForRawSQL = uiControllerForRawSQL;

        pagingSize = new Integer( Integer.parseInt( PropertyManager.getInstance().getProperty("dbbrowser-ui-browser-window-paging-size") ) );
        initialize();
    }

    private void initialize()
    {
        this.setLayout(new BorderLayout());
        //Dimension minimumSize = new Dimension(200, 100);
        tableTreePanel = new TableTreePanel( this.uiControllerForQueries, this.uiControllerForRawSQL, this );
        //JScrollPane scrollPane1 = new JScrollPane( tableTreePanel );
        //scrollPane1.setMinimumSize( minimumSize );

        tabbedPane = new JTabbedPane();
        this.tableDataPanel = new TableDataPanel( this.uiControllerForQueries, this.uiControllerForUpdates );
        tabbedPane.addTab(tableDataTitle, null, this.tableDataPanel, tableDataTitle);

        this.columnDetailsPanel = new ColumnDetailsPanel( this.uiControllerForQueries, this.uiControllerForUpdates );
        tabbedPane.addTab(columnDetailsTitle, null, this.columnDetailsPanel, columnDetailsTitle);

        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableTreePanel, tabbedPane);
        this.splitPane.setOneTouchExpandable(true);
        
        this.splitPane.setDividerLocation( Integer.parseInt(dividerLocation) );
        this.add( this.splitPane );
    }

    public void refresh()
    {
        DefaultMutableTreeNode node = this.tableTreePanel.getLastSelectedPathComponent();

        //if user has selected something, then refresh it
        if( node != null )
        {
            Object selectedUserObject = node.getUserObject();
            TreeNode treeNode = (TreeNode)selectedUserObject;

            //Set the right component to be the tabel tree panel
            this.splitPane.setRightComponent( this.tabbedPane );

            if( TreeNode.ROOT_VIEW_TYPE.equals( treeNode.getType() ) )	//This is the 'root' - it has been already built
            {
                //Dont do anything
            }
            else if( TreeNode.SCHEMAS_TYPE.equals( treeNode.getType() ) ) //User has selected 'schemas', dont do anything as it is already built
            {
                //Dont do anything

                try
                {
                    //Get the list of schemas
                    List listOfSchemas = this.uiControllerForQueries.listSchemas();
                    
                    //Sort the list of tablespaces
                    Collections.sort(listOfSchemas);
                    
                    //Clear existing list of schemas
                    this.tableTreePanel.clear(node);

                    //Add a sub tree for every schema user has access to
                    for(int i=0; i<listOfSchemas.size(); i++)
                    {
                        String schemaName = (String)listOfSchemas.get(i);
                        TreeNode tableTreeNode = new TreeNode(schemaName, TreeNode.SCHEMA_TYPE);
                        //DefaultMutableTreeNode schema = new DefaultMutableTreeNode(tn);
                        //node.add(schema);
                        this.tableTreePanel.add(node, tableTreeNode);
                    }
                }
                catch(DBEngineException exc)
                {
                    Log.getInstance().fatalMessage(exc.getMessage(), BrowserPanel.class.getName());
                    String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                    JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
            else if( TreeNode.SCHEMA_TYPE.equals( treeNode.getType() ) ) //User has selected a schema, show the tables in that table
            {
                String schemaName = treeNode.getName();

                //Get the tables in the tablespace
                try
                {
                    //Get the tables in the tablespace
                    List listOfTablesInSchema = this.uiControllerForQueries.listTablesInSchema( schemaName );

                    //Sort the list of tables
                    Collections.sort( listOfTablesInSchema );
                    
                    //Clear the existing data in node                    
                    this.tableTreePanel.clear(node);

                    //Add a sub tree for every table
                    for(int i=0; i<listOfTablesInSchema.size(); i++)
                    {
                        String tableName = (String)listOfTablesInSchema.get(i);
                        TreeNode tableTreeNode = new TreeNode(tableName, TreeNode.TABLE_TYPE);
                        this.tableTreePanel.add(node, tableTreeNode);
                    }
                }
                catch(DBEngineException exc)
                {
                    Log.getInstance().fatalMessage(exc.getMessage(), BrowserPanel.class.getName());
                    String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                    JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
            else if( TreeNode.TABLE_TYPE.equals( treeNode.getType() ) )	//User has selected a table
            {
                //Get the schema and table name
                TreeNode schemaTreeNode = (TreeNode) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
                String schemaName = schemaTreeNode.getName();
                String tableName = treeNode.getName();;

                //Show the column details and data in the table
                showColumnDetailsAndData(schemaName, tableName);
            }
            else if( TreeNode.VIEWS_TYPE.equals( treeNode.getType() ) )
            {
                try
                {
                    //Get the list of views
                    List listOfViews = this.uiControllerForQueries.listViews();
                    
                    //Clear the existing data in node
                    this.tableTreePanel.clear(node);

                    //Add a sub tree for every view
                    for(int i=0; i<listOfViews.size(); i++)
                    {
                    	View view = (View)listOfViews.get(i);
                        String viewName = view.getViewName();
                        TreeNode tn = new TreeNode(viewName, TreeNode.VIEW_TYPE, view);
                        DefaultMutableTreeNode nodeForView = new DefaultMutableTreeNode(tn);
                        node.add(nodeForView);
                    }
                }
                catch(DBEngineException exc)
                {
                    Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
                    String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                    JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
            else if( TreeNode.VIEW_TYPE.equals( treeNode.getType() ) )	//Show details about a view
            {
                //Get the details for a view
            	View view = (View)treeNode.getUserObject();

                //Get the SQL for the view
                try
                {
                    String viewDefinition = this.uiControllerForQueries.getSQLForView( view );

                    //Run the sql for view and get the results
                    DBTable dbTable = this.uiControllerForRawSQL.runRawSQL( viewDefinition );

                    //Show the results of the view if the results are not null
                    if( dbTable != null )
                    {
                    	View newView = new View(view.getSchemaName(), view.getViewName(), viewDefinition);
                        ViewPanel vp = new ViewPanel( newView, dbTable, this.uiControllerForUpdates );
                        vp.updateUI();
                        this.splitPane.setRightComponent( vp );
                        vp.initialize();
                    }
                }
                catch(DBEngineException exc)
                {
                    Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
                    String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                    JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }

            //Set the right component to be the table if views are not selected
            if( ! TreeNode.VIEW_TYPE.equals( treeNode.getType() ) )
            {
                this.splitPane.setRightComponent( this.tabbedPane );
            }

            this.splitPane.setDividerLocation( Integer.parseInt(dividerLocation) );
            
            //Update the UI
            this.tableTreePanel.refresh( node );
            this.updateUI();
        }
    }

    public void valueChanged(TreeSelectionEvent e)
    {
    	//Store the cursor type
    	Cursor defaultCursor = Cursor.getDefaultCursor();

    	Cursor waitCursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
		JFrame rootFrame = (JFrame)SwingUtilities.getWindowAncestor( this );
    	
    	//Change the cursor to hourglass
    	try
    	{
    		rootFrame.setCursor( waitCursor );
    		refresh();
    	}
        finally
        {
        	//Change the cursor to normal cursor
        	rootFrame.setCursor( defaultCursor );
        }
    }

    private void showColumnDetailsAndData(String schemaName, String tableName)
    {
        //Get the data in the table
        try
        {
            DBTable dbTable = null;

            //if filter has been set, use it, else get all records
            if( this.tableDataPanel.getFilter(tableName) != null )
            {
                //Filter has been set, use it
                dbTable = this.uiControllerForQueries.getFilteredDataInATable(schemaName, tableName, this.tableDataPanel.getFilter(tableName));
            }
            else
            {
                //Get the results from the database and show the data in the table
                dbTable = this.uiControllerForQueries.getAllDataInATable(schemaName, tableName, new Integer(0), this.pagingSize);
            }

            //Show the results
            this.tableDataPanel.initializeTable(dbTable);

            //Show the details of the columns
            this.columnDetailsPanel.initializeTable( dbTable );
        }
        catch(DBEngineException exc)
        {
            Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
            String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
            JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
}
