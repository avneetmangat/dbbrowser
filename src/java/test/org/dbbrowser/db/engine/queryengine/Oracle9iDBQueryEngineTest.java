package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.ConnectionInfoSerializer;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;
import junit.framework.TestCase;

public class Oracle9iDBQueryEngineTest extends TestCase
{
	private Oracle9iDBQueryEngine oracle9iDBQueryEngine = null;
	private static final String SCHEMA_NAME = "XCSSYS";
	private static final String TABLE_NAME = "CLAIM";
	
    public Oracle9iDBQueryEngineTest(String name)
    {
        super(name);    
    }
    
    public void setUp()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** Setting up connection for Oracle database *** ", this.getClass().getName());
    		
	    	//Get the connection info
	    	List listOfConnectionInfo = ConnectionInfoSerializer.deserialize(); 
	    	
	    	//Get the Oracle connection info
	    	Iterator i = listOfConnectionInfo.iterator();
	    	ConnectionInfo ci = null;
	    	while(i.hasNext())
	    	{
	    		ConnectionInfo connectionInfo = (ConnectionInfo)i.next();
	    		if( connectionInfo.getName().equals("XCSSYS for Data Migration"))
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
	    		oracle9iDBQueryEngine = new Oracle9iDBQueryEngine( conn.createStatement() );	    		
	    	}
	    	else
	    	{
		        Log.getInstance().fatalMessage("*** No Connection info found for Oracle database *** ", this.getClass().getName());
		        fail("*** No Connection info found for Oracle database *** ");        		    		
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

    public void testGetAllDataInATable()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testCountNumberOfRows for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the number of rows in a table
	    	Integer rowCount = oracle9iDBQueryEngine.getRowCount(SCHEMA_NAME, TABLE_NAME);

	    	assertTrue("Number of rows in " + TABLE_NAME + " is " + rowCount.intValue(), rowCount.intValue() == 663);
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
    	}
    }
    
    public void testListIndexes()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testListIndexes for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the number of rows in a table
	    	DBTable table = oracle9iDBQueryEngine.listIndexes();

	    	assertTrue("Number of indexes is " + table.getListOfRows().size(), table.getListOfRows().size() == 52);
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
    	}
    }    

    public void testCountNumberOfRows()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testCountNumberOfRows for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the number of rows in a table
	    	Integer rowCount = oracle9iDBQueryEngine.getRowCount(SCHEMA_NAME, TABLE_NAME);

	    	assertTrue("Number of rows in " + TABLE_NAME + " is " + rowCount.intValue(), rowCount.intValue() == 663);
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
    	}
    }
    
    public void testListSchemas()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testListSchemas for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the list of table spaces
	    	List listOfSchemas = oracle9iDBQueryEngine.listSchemas();
    		
	    	assertTrue("Schema " + SCHEMA_NAME + " found", listOfSchemas.contains(SCHEMA_NAME));
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
    	}     	
    }
    
    public void testListConstraints()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testListConstraints for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the list of table spaces
	    	DBTable tableForConstraints = oracle9iDBQueryEngine.listConstraints();
    		
	    	assertTrue("Number of constraints in " + SCHEMA_NAME + " is " + tableForConstraints.getListOfRows().size(), tableForConstraints.getListOfRows().size() == 203);
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
    	}     	
    }    
    
    public void testListTablesInSchema()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testListTablesInSchema for Oracle DBMS *** ", this.getClass().getName());

	    	//Get the list of tables
	    	List listOfTables = oracle9iDBQueryEngine.listTablesInSchema(SCHEMA_NAME);
    		
	    	assertTrue("Table " + TABLE_NAME + " found in schema " + SCHEMA_NAME, listOfTables.contains(TABLE_NAME));
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
    	}    	
    }
    
    /*public void testGetAllFilteredDataInATable()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testGetAllFilteredDataInATable for Oracle DBMS *** ", this.getClass().getName());

            //Build the filters
            ColumnInfo ci1 = new ColumnInfo("UCR", ColumnInfo.COLUMN_TYPE_VARCHAR, "java.lang.String", new Integer(17), ColumnInfo.COLUMN_NULLABLE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, null);
            DBTableCell dbTableCell1 = new DBTableCell(ci1, "B0423LINKED1", Boolean.FALSE);
            ColumnInfo ci2 = new ColumnInfo("client_name", ColumnInfo.COLUMN_TYPE_VARCHAR, "java.lang.String", new Integer(17), ColumnInfo.COLUMN_NULLABLE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, null);
            DBTableCell dbTableCell2 = new DBTableCell(ci2, "Ascot", Boolean.FALSE);

            List listOfFilters = new ArrayList();
            listOfFilters.add( dbTableCell1 );
            listOfFilters.add( dbTableCell2 );

            //Get the data in the form of a dbtable
	    	DBTable dbTable = oracle9iDBQueryEngine.testGetAllFilteredDataInATable(SCHEMA_NAME, TABLE_NAME, listOfFilters);
            Log.getInstance().debugMessage("List of rows size is: " + dbTable.getListOfRows().size(), this.getClass().getName());

            assertTrue("Should return 1 record", dbTable.getListOfRows().size() == 1);
        }
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
    	}    	
    }*/    
    
    public void testGetPrimaryKeyColumnNames()
    {
    	try
    	{
    		Log.getInstance().debugMessage("*** testGetPrimaryKeyColumnNames for Oracle DBMS *** ", this.getClass().getName());
    		
	    	//Get the primary keys for the table
	    	List listOfPrimaryKeyColumnNames = oracle9iDBQueryEngine.getPrimaryKeyColumnNames(SCHEMA_NAME, TABLE_NAME);
	    	assertTrue("List of primary key in table " + TABLE_NAME + " should be 1", listOfPrimaryKeyColumnNames.size() == 1);
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
    		Log.getInstance().debugMessage("*** testListColumnsInATable for Oracle DBMS *** ", this.getClass().getName());
    		
	    	//Get the list of columns in the table
	    	List listColumnInfos = oracle9iDBQueryEngine.listColumnsInATable(SCHEMA_NAME, TABLE_NAME);
	    	assertTrue("List of columns in table " + TABLE_NAME + " should be 52", listColumnInfos.size() == 55);
    	}
    	catch(DBEngineException exc)
    	{
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	    		
    	}    	
    }
}
