package org.dbbrowser.db.engine.rawsqlengine;

import junit.framework.TestCase;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.rawsqlengine.GenericRawSQLEngine;
import org.dbbrowser.drivermanager.ConnectionInfoSerializer;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;
import infrastructure.logging.Log;
import java.util.List;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class MySQLGenericRawSQLEngineAutoCommitOnTest extends TestCase
{
    private GenericRawSQLEngine genericRawSQLEngine = null;
    private Connection conn = null;

    public MySQLGenericRawSQLEngineAutoCommitOnTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        try
        {
            Log.getInstance().debugMessage("*** Setting up connection for MySQL database *** ", this.getClass().getName());

            //Get the connection info
            List listOfConnectionInfo = ConnectionInfoSerializer.deserialize();

            //Get the Oracle connection info
            Iterator i = listOfConnectionInfo.iterator();
            ConnectionInfo ci = null;
            while(i.hasNext())
            {
                ConnectionInfo connectionInfo = (ConnectionInfo)i.next();
                if( connectionInfo.getName().equals("HWW on MySQL") )
                {
                    ci = connectionInfo;
                    break;
                }
            }

            //Get the connection if connection info is not null
            if( ci != null )
            {
                Connection conn = DBBrowserDriverManager.getInstance().getConnection( ci, null );

                //Setup the update engine
                genericRawSQLEngine = new GenericRawSQLEngine( conn.createStatement() );
            }
            else
            {
                Log.getInstance().fatalMessage("*** No Connection info found for MySQL database *** ", this.getClass().getName());
                fail("*** No Connection info found for MySQL database *** ");
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

    public void testRunRawSelectSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOnTest.testRunRawSelectSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("select count(*) from claim_experts");
            assertTrue("Number of rows should be 1", dbTable.getNumberOfRowsInTable().intValue() == 1);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }
    }

    public void testRunRawInsertSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawInsertSQLStatement *** ", this.getClass().getName());

            String sql = "insert into hww.customer (pk_customerid, last_updated_at, last_updated_by " +
                    ") values ( 3, '2005-10-21', 'JUnit')";
            DBTable dbTable = this.genericRawSQLEngine.runRawSQL(sql);
            assertTrue("DBTable should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }       
    }

    public void testRunRawUpdateSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawUpdateSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("update hww.customer set firstname = 'JUnit' where pk_customerid=3");
            assertTrue("MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawUpdateSQLStatement should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }        
    }

    public void testRunRawDeleteSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawDeleteSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("delete from hww.customer where pk_customerid=3");
            assertTrue("MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawDeleteSQLStatement should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }        
    }

    public void testRunRawAddColumnSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawAddColumnSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("alter table hww.customer add test_column varchar(10)");
            assertTrue("MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawAlterTableSQLStatement should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }      
    }

    public void testRunRawDropColumnSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawDropColumnSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("alter table hww.customer drop column test_column");
            assertTrue("MySQLGenericRawSQLEngineAutoCommitOffTest.testRunRawAlterTableSQLStatement should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }     
    }
    
    public void testStripComments()
    {
        Log.getInstance().debugMessage("*** MySQLGenericRawSQLEngineAutoCommitOffTest.testStripComments *** ", this.getClass().getName());

        String sql = this.genericRawSQLEngine.stripComments("alter table CLAIM_EXPERTS /*Test comments*/drop column test_column");
        assertTrue("GenericRawSQLEngineTest.testStripComments sql is incorrect", "alter table CLAIM_EXPERTS drop column test_column".equals(sql) );
    }   
}

