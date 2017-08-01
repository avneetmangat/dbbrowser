package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.queryengine.MySQLDBQueryEngine;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.ConnectionInfoSerializer;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;
import org.dbbrowser.ui.UIControllerForQueries;

public class MySQLDBQueryEngineTest	extends TestCase
{
	private MySQLDBQueryEngine mySQLDBQueryEngine = null;
	private static final String SCHEMA_NAME = "hww";
	private static final String TABLE_NAME = "customer";
	
	public MySQLDBQueryEngineTest(String name)
	{
	    super(name);    
	}
	
	public void setUp()
	{
		try
		{
			Log.getInstance().debugMessage("*** Setting up connection for mySQL database *** ", this.getClass().getName());
			
	    	//Get the connection info
	    	List listOfConnectionInfo = ConnectionInfoSerializer.deserialize(); 
	    	
	    	//Get the first mysql connection info
	    	Iterator i = listOfConnectionInfo.iterator();
	    	ConnectionInfo ci = null;
	    	while(i.hasNext())
	    	{
	    		ConnectionInfo connectionInfo = (ConnectionInfo)i.next();
	    		if( connectionInfo.getDBMSType().equals(UIControllerForQueries.MYSQL_DBMS))
	    		{
	    			ci = connectionInfo;
	    			break;
	    		}
	    	}
	    	
	    	//Get the connection if connection info is not null
	    	if( ci != null )
	    	{
	    		Connection conn = DBBrowserDriverManager.getInstance().getConnection( ci, null );
		    	
	    		//Setup the query engine
		    	mySQLDBQueryEngine = new MySQLDBQueryEngine( conn.createStatement() );	    		
	    	}
	    	else
	    	{
		        Log.getInstance().fatalMessage("*** No Connection info found for MySQL database *** ", this.getClass().getName());
		        fail("*** No Connection info found for mySQL database *** ");        		    		
	    	}
		}
		catch(ClassNotFoundException exc)
		{
	        Log.getInstance().fatalMessage("*** ClassNotFoundException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	
		}
		catch(IOException exc)
		{
	        Log.getInstance().fatalMessage("*** IOException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}
		catch(DriverManagerException exc)
		{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		} 
		catch(SQLException exc)
		{
	        Log.getInstance().fatalMessage("*** SQLException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		} 		
	}
	
	public void testListSchemas()
	{
		try
		{
			Log.getInstance().debugMessage("*** testListSchemas mySQL database *** ", this.getClass().getName());
			
	    	//Get the list of table spaces
	    	List listOfSchemas = mySQLDBQueryEngine.listSchemas();
			
	    	assertTrue("Schema " + SCHEMA_NAME + " found", listOfSchemas.contains(SCHEMA_NAME));
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}     	
	}
	
	public void testListIndexes()
	{
		try
		{
			Log.getInstance().debugMessage("*** testListIndexes mySQL database *** ", this.getClass().getName());
			
	    	//Get the list of table spaces
	    	DBTable tableForIndexes = mySQLDBQueryEngine.listIndexes();
	    	
	    	assertTrue("MySQL database has " + tableForIndexes.getListOfColumnInfos().size() + " columns, should be 13", tableForIndexes.getListOfColumnInfos().size() == 13);	    	
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}     	
	}	
	
	public void testListTablesInSchema()
	{
		try
		{
			Log.getInstance().debugMessage("*** testListTablesInSchema for mySQL database *** ", this.getClass().getName());
			
	    	//Get the list of tables
	    	List listOfTables = mySQLDBQueryEngine.listTablesInSchema(SCHEMA_NAME);
			
	    	assertTrue("Table " + TABLE_NAME + " found in schema " + SCHEMA_NAME, listOfTables.contains(TABLE_NAME));
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}    	
	}
	
	public void testGetAllDataInATable()
	{
		try
		{
			Log.getInstance().debugMessage("*** testGetAllDataInATable for mySQL database *** ", this.getClass().getName());
			
	    	//Get the data in the form of a dbtable
	    	DBTable dbTable = mySQLDBQueryEngine.getAllDataInATable(SCHEMA_NAME, TABLE_NAME, new Integer(0), new Integer(100));
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}    	
	}    
	
	public void testGetPrimaryKeyColumnNames()
	{
		try
		{
			Log.getInstance().debugMessage("*** testGetPrimaryKeyColumnNames for MySQL DBMS *** ", this.getClass().getName());
			
	    	//Get the primary keys for the table
	    	List listOfPrimaryKeyColumnNames = mySQLDBQueryEngine.getPrimaryKeyColumnNames(SCHEMA_NAME, TABLE_NAME);
	    	assertTrue("List of primary key in table " + TABLE_NAME + "should be 1", listOfPrimaryKeyColumnNames.size() == 1);
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}    	
	}  
	
	public void testListColumnsInATable()
	{
		try
		{
			Log.getInstance().debugMessage("*** testListColumnsInATable for MySQL DBMS *** ", this.getClass().getName());
			
	    	//Get the list of columns in the table
	    	List listColumnInfos = mySQLDBQueryEngine.listColumnsInATable(SCHEMA_NAME, TABLE_NAME);
	    	assertTrue("List of columns in table " + TABLE_NAME + "should be 13", listColumnInfos.size() == 13);
		}
		catch(DBEngineException exc)
		{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
		}    	
	}
}
