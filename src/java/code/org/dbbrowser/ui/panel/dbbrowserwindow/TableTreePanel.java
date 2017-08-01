package org.dbbrowser.ui.panel.dbbrowserwindow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForRawSQL;

public class TableTreePanel extends JPanel
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private String DROP_TABLE_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-drop-table-icon");
    private String DELETE_ALL_RECORDS_IN_TABLE_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-delete-all-rows-in-table-icon");
    private String REFRESH_TABLE_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-refresh-table-icon");
    private Icon iconForDropTable = new ImageIcon( DROP_TABLE_ICON_FILE_NAME );
    private Icon iconForDeleteAllRecordsInTable = new ImageIcon( DELETE_ALL_RECORDS_IN_TABLE_ICON_FILE_NAME );
    private Icon iconForRefreshTable = new ImageIcon( REFRESH_TABLE_ICON_FILE_NAME );

	//Lables for file menu
	private UIControllerForQueries uicontroller = null;
	private UIControllerForRawSQL uiControllerForRawSQL = null;
	private JTree tree = null;
    private DefaultTreeModel treeModel = null;
	private TreeSelectionListener treeSelectionListener = null;

	public TableTreePanel(UIControllerForQueries uicontroller,  UIControllerForRawSQL uiControllerForRawSQL, TreeSelectionListener treeSelectionListener)
	{
		this.uicontroller = uicontroller;
		this.uiControllerForRawSQL = uiControllerForRawSQL;
		this.treeSelectionListener = treeSelectionListener;
		initialize();
	}
	
	public DefaultMutableTreeNode getLastSelectedPathComponent()	
	{
		return (DefaultMutableTreeNode)this.tree.getLastSelectedPathComponent();
	}
	
	public void add(DefaultMutableTreeNode parent, TreeNode child)
	{
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
	}
	
	private class PopupDecorator extends MouseAdapter
	{
	    public void mouseReleased(MouseEvent me) 
	    { 
		    if (me.isPopupTrigger())
		    {
				JPopupMenu jPopupMenu = new JPopupMenu();
		    	
				DefaultMutableTreeNode node = getLastSelectedPathComponent();
			    TreePath path = tree.getPathForLocation(me.getX(), me.getY());
	            
	            //If a user object has been set(it wont be for root node)
	            if( node != null )
	            {
		            Object selectedUserObject = node.getUserObject();	            	
		            TreeNode treeNode = (TreeNode)selectedUserObject;
				    
		            //if the popup is on the tree and a table has been selected
				    if(path != null && TreeNode.TABLE_TYPE.equals(treeNode.getType()) )
				    {
				    	Object[] params = new Object[]{treeNode.getName()};
				    	
						//Add menu item to refresh table
						String REFRESH_TABLE_MENU_ITEM = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-refresh-table-menu-item-label", params);					
						JMenuItem refreshTableMenuItem = new JMenuItem( REFRESH_TABLE_MENU_ITEM, iconForRefreshTable );
						refreshTableMenuItem.setActionCommand("Refresh");
						refreshTableMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(refreshTableMenuItem);	
				  	    
						//Add menu item to delete all records in table
						String deleteAllRecordsInTableMenuItemLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-delete-all-records-in-table-menu-item-label", params);			    	
						JMenuItem deleteAllRecordsInTableMenuItem = new JMenuItem( deleteAllRecordsInTableMenuItemLabel, iconForDeleteAllRecordsInTable );
						deleteAllRecordsInTableMenuItem.setActionCommand( "Delete all records in table" );
						deleteAllRecordsInTableMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(deleteAllRecordsInTableMenuItem);	
				  	    
						//Add menu item for drop table
						String dropTableMenuItemLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-drop-table-menu-item-label", params);			    	
						JMenuItem dropTableMenuItem = new JMenuItem( dropTableMenuItemLabel, iconForDropTable );
						dropTableMenuItem.setActionCommand( "Drop table" );
						dropTableMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(dropTableMenuItem);	
				  	    
						jPopupMenu.show(tree,me.getX(),me.getY());
				    }
				    else if(path != null && TreeNode.SCHEMA_TYPE.equals(treeNode.getType()) )
				    {
				    	//Refreshing all tables in the schema
				    	Object[] params = new Object[]{treeNode.getName()};
				  	    
						//Add menu item to refresh schema
						String REFRESH_SCHEMA_MENU_ITEM = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-refresh-table-menu-item-label", params);					
						JMenuItem refreshSchemaMenuItem = new JMenuItem( REFRESH_SCHEMA_MENU_ITEM, iconForRefreshTable );
						refreshSchemaMenuItem.setActionCommand("Refresh");
						refreshSchemaMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(refreshSchemaMenuItem);	
				  	    
						jPopupMenu.show(tree,me.getX(),me.getY());				    	
				    }
				    else if(path != null && TreeNode.VIEWS_TYPE.equals(treeNode.getType()) )
				    {
				    	//Refreshing all tables in the schema
				    	Object[] params = new Object[]{treeNode.getName()};
				  	    
						//Add menu item to refresh schema
						String REFRESH_SCHEMA_MENU_ITEM = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-refresh-table-menu-item-label", params);					
						JMenuItem refreshSchemaMenuItem = new JMenuItem( REFRESH_SCHEMA_MENU_ITEM, iconForRefreshTable );
						refreshSchemaMenuItem.setActionCommand("Refresh");
						refreshSchemaMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(refreshSchemaMenuItem);	
				  	    
						jPopupMenu.show(tree,me.getX(),me.getY());				    	
				    }
				    else if(path != null && TreeNode.VIEW_TYPE.equals(treeNode.getType()) )
				    {
				    	Object[] params = new Object[]{treeNode.getName()};
				    	
						//Add menu item to refresh table
						String REFRESH_TABLE_MENU_ITEM = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-refresh-table-menu-item-label", params);					
						JMenuItem refreshTableMenuItem = new JMenuItem( REFRESH_TABLE_MENU_ITEM, iconForRefreshTable );
						refreshTableMenuItem.setActionCommand("Refresh");
						refreshTableMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(refreshTableMenuItem);		
				  	    
						//Add menu item for drop table
						String dropViewMenuItemLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-drop-view-menu-item-label", params);			    	
						JMenuItem dropTableMenuItem = new JMenuItem( dropViewMenuItemLabel, iconForDropTable );
						dropTableMenuItem.setActionCommand( "Drop view" );
						dropTableMenuItem.addActionListener( new MouseActionListener() );
						jPopupMenu.add(dropTableMenuItem);	
				  	    
						jPopupMenu.show(tree,me.getX(),me.getY());
				    }				    
	            }
		    }
	    }
	}
	
	private class MouseActionListener implements ActionListener
	{	
		public void actionPerformed(ActionEvent e)
		{
			if("Refresh".equals(e.getActionCommand()))
			{
				TreeSelectionEvent event = new TreeSelectionEvent(tree, tree.getSelectionPath(), false, null, null);
				treeSelectionListener.valueChanged( event );
			}
			else if( "Drop table".equals(e.getActionCommand()) )
			{
				//Get last selected table name
				DefaultMutableTreeNode node = getLastSelectedPathComponent();
	            Object selectedUserObject = node.getUserObject();	            	
	            TreeNode treeNode = (TreeNode)selectedUserObject;
				
				//Confirm drop table
				Object[] params = new Object[]{treeNode.getName()};
				String DROP_TABLE_CONFIRMATION_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-drop-table-confirmation-message", params);					
				
				int ans = JOptionPane.showConfirmDialog(null, DROP_TABLE_CONFIRMATION_MESSAGE, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if( JOptionPane.YES_OPTION == ans )
				{
					try
					{
						uiControllerForRawSQL.runRawSQL("drop table " + treeNode.getName());
						
						//Update the UI
						DefaultMutableTreeNode parent = ((DefaultMutableTreeNode)node.getParent());
						parent.remove(node);
						//Refresh the parent schema node
						refresh( parent );
						
						TreeSelectionEvent event = new TreeSelectionEvent(tree, tree.getSelectionPath(), false, null, null);
						treeSelectionListener.valueChanged( event );												
					}
		            catch(DBEngineException exc)
		            {
		                Log.getInstance().fatalMessage(exc.getMessage(), TableTreePanel.class.getName());
		                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
		                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
		            }
				}
			}
			else if( "Drop view".equals(e.getActionCommand()) )
			{
				//Get last selected table name
				DefaultMutableTreeNode node = getLastSelectedPathComponent();
	            Object selectedUserObject = node.getUserObject();	            	
	            TreeNode treeNode = (TreeNode)selectedUserObject;
				
				//Confirm drop table
				Object[] params = new Object[]{treeNode.getName()};
				String DROP_TABLE_CONFIRMATION_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-drop-table-confirmation-message", params);					
				
				int ans = JOptionPane.showConfirmDialog(null, DROP_TABLE_CONFIRMATION_MESSAGE, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if( JOptionPane.YES_OPTION == ans )
				{
					try
					{
						View view = (View)treeNode.getUserObject();
						if( view != null && view.getSchemaName() != null )
						{
							uiControllerForRawSQL.runRawSQL("drop view " + view.getSchemaName() + "." + treeNode.getName());
						}
						else
						{
							uiControllerForRawSQL.runRawSQL("drop view " + treeNode.getName());							
						}
						
						//Update the UI
						DefaultMutableTreeNode parent = ((DefaultMutableTreeNode)node.getParent());
						parent.remove(node);
						//Refresh the parent schema node
						refresh( parent );
						
						TreeSelectionEvent event = new TreeSelectionEvent(tree, tree.getSelectionPath(), false, null, null);
						treeSelectionListener.valueChanged( event );												
					}
		            catch(DBEngineException exc)
		            {
		                Log.getInstance().fatalMessage(exc.getMessage(), TableTreePanel.class.getName());
		                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
		                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
		            }
				}
			}			
			else if( "Delete all records in table".equals(e.getActionCommand()) )
			{
				//Get last selected table name
				DefaultMutableTreeNode node = getLastSelectedPathComponent();
	            Object selectedUserObject = node.getUserObject();	            	
	            TreeNode treeNode = (TreeNode)selectedUserObject;
				
				//Confirm drop table
				Object[] params = new Object[]{treeNode.getName()};
				String DROP_TABLE_CONFIRMATION_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-browser-panel-delete-all-records-in-table-menu-item-label", params);					
				
				int ans = JOptionPane.showConfirmDialog(null, DROP_TABLE_CONFIRMATION_MESSAGE, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if( JOptionPane.YES_OPTION == ans )
				{
					try
					{
						uiControllerForRawSQL.runRawSQL("delete from " + treeNode.getName());
						
						//Update the UI
						refresh( node );
						
						TreeSelectionEvent event = new TreeSelectionEvent(tree, tree.getSelectionPath(), false, null, null);
						treeSelectionListener.valueChanged( event );						
					}
		            catch(DBEngineException exc)
		            {
		                Log.getInstance().fatalMessage(exc.getMessage(), TableTreePanel.class.getName());
		                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
		                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
		            }
				}
			}
			
		}
	}
	
	public void refresh(DefaultMutableTreeNode parent)
	{
		this.treeModel.reload( parent );
		this.tree.expandPath( new TreePath( parent ) );		
	}
	
	public void clear(DefaultMutableTreeNode parent)
	{
		parent.removeAllChildren();
	}

    private void initialize()
	{
		//Set up the tree for the list of tablespaces
		this.setLayout(new BorderLayout());

        try
		{
            //Setup the tree
            this.populateTree();
        }
		catch(DBEngineException exc)
		{
	    	Log.getInstance().fatalMessage(exc.getMessage(), TableTreePanel.class.getName());
			String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
			JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
		}		
	}
		
	private void populateTree()
		throws DBEngineException
	{
		//Setup the JTree
		TreeNode rootTreeNode = new TreeNode( this.uicontroller.getConnectionInfo().getName(), TreeNode.ROOT_VIEW_TYPE );
		DefaultMutableTreeNode database = new DefaultMutableTreeNode( rootTreeNode );
		this.treeModel = new DefaultTreeModel(database);
		this.tree = new JTree(treeModel);
		this.tree.setShowsRootHandles(true);
		this.tree.addMouseListener( new PopupDecorator() );
		this.tree.addTreeSelectionListener( this.treeSelectionListener );

        //Set the tree cell renderer
        JScrollPane pane = new JScrollPane( this.tree );
		Dimension minimumSize = new Dimension(200, 100);
		pane.setMinimumSize( minimumSize );
		this.add( pane );
		
		//Setup tables and schemas
		TreeNode schemasNode = new TreeNode( "Schemas", TreeNode.SCHEMAS_TYPE );		
        DefaultMutableTreeNode schemasTreeNode = new DefaultMutableTreeNode(schemasNode);
        database.add(schemasTreeNode); 
		
		//Setup views
		TreeNode viewsNode = new TreeNode( "Views", TreeNode.VIEWS_TYPE );				
        DefaultMutableTreeNode viewsTreeNode = new DefaultMutableTreeNode(viewsNode);
        database.add(viewsTreeNode);        
	}
}
