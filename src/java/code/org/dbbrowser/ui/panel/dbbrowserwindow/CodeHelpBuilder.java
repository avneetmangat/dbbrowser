package org.dbbrowser.ui.panel.dbbrowserwindow;

import infrastructure.logging.Log;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.ui.UIControllerForQueries;

public class CodeHelpBuilder extends Thread
{
	private UIControllerForQueries uiController = null;
	private ActionListener actionListenerForPopupMenu = null;
	private ChangeListener changeListener = null;
	private ChangeListener processCompletedListener = null;
	
	private JPopupMenu codeHelp = null;
	private int counter = 0;
	
	public CodeHelpBuilder(UIControllerForQueries uiController, ActionListener actionListenerForPopupMenu, ChangeListener changeListener, ChangeListener processCompletedListener)
	{
		this.uiController = uiController;
		this.actionListenerForPopupMenu = actionListenerForPopupMenu;
		this.changeListener = changeListener;
		this.processCompletedListener = processCompletedListener;
	}
	
	public JPopupMenu getJPopupMenu()
	{
		return codeHelp;
	}
	
	public void run()
	{
		
		Map mapOfSchemaNamesToListOfTableNames = null;
		Map mapOfTableNamesToListOfColumnInfos = null;
		
        //Get the map of tablespaceNames to list of tablenames
        try
        {
            mapOfSchemaNamesToListOfTableNames = buildMapOfSchemaNamesToListOfTableNames();
            mapOfTableNamesToListOfColumnInfos = buildMapOfTableNamesToListOfColumnInfos( mapOfSchemaNamesToListOfTableNames.keySet() );
        }
        catch(DBEngineException exc)
        {
            Log.getInstance().fatalMessage("*** Serious error while retrieving list of schema names *** - " + exc.getMessage(), this.getClass().getName());
            //Dont do anything, use null values
        }
        
        if( mapOfSchemaNamesToListOfTableNames != null )
        {
            //Iterate through the map and build the pop up menu
            codeHelp = new JPopupMenu();
            Iterator i = mapOfSchemaNamesToListOfTableNames.keySet().iterator();
            while( i.hasNext() )
            {
                String schemaName = (String)i.next();
                List listOfTableNames = (List)mapOfSchemaNamesToListOfTableNames.get( schemaName );

                //Build the popup menu
                JMenu jMenuForSchemaName = new JMenu( schemaName );
                JMenuItem jMenuItemForSchemName = new JMenuItem(" - " + schemaName + " - ");
                jMenuItemForSchemName.setActionCommand(schemaName);
                jMenuItemForSchemName.addActionListener( actionListenerForPopupMenu );
                jMenuForSchemaName.add(jMenuItemForSchemName);

                //Build a jmenu item for each table in the tablspace
                Iterator iteratorForListOfTableNames = listOfTableNames.iterator();
                while( iteratorForListOfTableNames.hasNext() )
                {
                    String tablename = (String)iteratorForListOfTableNames.next();
                    JMenu jMenuForTableName = new JMenu( tablename );
                    jMenuForTableName.addActionListener( actionListenerForPopupMenu );
                    JMenuItem jMenuItemForTableName = new JMenuItem(" - " + tablename + " - ");
                    jMenuItemForTableName.setActionCommand(tablename);
                    jMenuItemForTableName.addActionListener( actionListenerForPopupMenu );
                    jMenuForTableName.add( jMenuItemForTableName );
                    jMenuForSchemaName.add( jMenuForTableName );

                    //Build a jmenu item for each column in the table
                    Object o = mapOfTableNamesToListOfColumnInfos.get( tablename );
                    if( o!= null )
                    {
                        List listOfColumnInfos = (List)o;
                        Iterator iteratorForListOfColumnInfos = listOfColumnInfos.iterator();
                        while( iteratorForListOfColumnInfos.hasNext() )
                        {
                            ColumnInfo ci = (ColumnInfo)iteratorForListOfColumnInfos.next();
                            String columnInfoString = ci.getColumnName() + " - " + ci.getColumnTypeName() + " - " + ci.getColumnDisplaySize().intValue();
                            JMenuItem jmenuItemForColumnName = new JMenuItem( columnInfoString );
                            jmenuItemForColumnName.setActionCommand(ci.getColumnName());
                            jmenuItemForColumnName.addActionListener( actionListenerForPopupMenu );
                            jMenuForTableName.add( jmenuItemForColumnName );
                            
                	        counter++;
                	        this.changeListener.stateChanged(new ChangeEvent(new Integer(counter)));                            
                        }
                    }
                }

                codeHelp.add(jMenuForSchemaName);
            }
        }

        this.processCompletedListener.stateChanged(new ChangeEvent("Process complete"));
	}
	
	private Map buildMapOfSchemaNamesToListOfTableNames()
	    throws DBEngineException
	{
	    Map mapOfSchemaNamesToListOfTableNames = new TreeMap();
	    List listOfSchemas = this.uiController.listSchemas();
	
	    Iterator i = listOfSchemas.iterator();
	    while( i.hasNext() )
	    {
	        String schemaName = (String)i.next();
	
	        //Get all the tables in the tablespace
	        List listOfTablesInSchema = this.uiController.listTablesInSchema( schemaName );
	
	        //Add to map
	        mapOfSchemaNamesToListOfTableNames.put( schemaName, listOfTablesInSchema );
	        
	        counter++;
	        this.changeListener.stateChanged(new ChangeEvent(new Integer(counter)));
	    }

        return mapOfSchemaNamesToListOfTableNames;
	}

	private Map buildMapOfTableNamesToListOfColumnInfos(Set setOfSchemas)
	    throws DBEngineException
	{
	    Map mapOfTableNamesToListOfColumnInfos = new TreeMap();
	    Iterator i = setOfSchemas.iterator();
	    while( i.hasNext() )
	    {
	        String schemaName = (String)i.next();
	
	        //Get all the tables in the tablespace
	        List listOfTablesInSchema = this.uiController.listTablesInSchema( schemaName );
	
	        //Get the list of columns in each table
	        Iterator ii = listOfTablesInSchema.iterator();
	        while( ii.hasNext() )
	        {
	        	String tableName = (String)ii.next();
	
	            try
	            {
	                List listOfColumnInfos = this.uiController.listColumnsInATable(schemaName, tableName);
	        	    mapOfTableNamesToListOfColumnInfos.put( tableName, listOfColumnInfos);
	            }
	            catch(DBEngineException exc)
	            {
	                Log.getInstance().infoMessage(exc.getMessage() + " - " + schemaName + "." + tableName, this.getClass().getName());
	            }
	            
		        counter++;
		        this.changeListener.stateChanged(new ChangeEvent(new Integer(counter)));
	        }
	    }
	
	    return mapOfTableNamesToListOfColumnInfos;
	}	
}
