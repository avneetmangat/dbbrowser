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

public class OracleGenericRawSQLEngineAutoCommitOnTest extends TestCase
{
    private GenericRawSQLEngine genericRawSQLEngine = null;

    public OracleGenericRawSQLEngineAutoCommitOnTest(String name)
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
                if( connectionInfo.getName().equals("Oracle PPDB") )
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

    public void testRunRawSelectSQLStatement()
    {
        try
        {
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawSelectSQLStatement *** ", this.getClass().getName());

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
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawInsertSQLStatement *** ", this.getClass().getName());

            String sql = "insert into CLAIM_EXPERTS (ID, CLAIM_ID, EXPERT_ID) " +
                    "values ( CLAIM_EXPERTS_SEQ.nextval, 4, 15)";
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
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawUpdateSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("update CLAIM_EXPERTS set individual_name = 'Updated by JUnit' where CLAIM_ID=534");
            assertTrue("GenericRawSQLEngineTest.testRunRawUpdateSQLStatement should be null", dbTable == null);
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
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawDeleteSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("delete from CLAIM_EXPERTS where CLAIM_ID=534");
            assertTrue("GenericRawSQLEngineTest.testRunRawDeleteSQLStatement should be null", dbTable == null);
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
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawAddColumnSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("alter table CLAIM_EXPERTS add test_column varchar(10)");
            assertTrue("GenericRawSQLEngineTest.testRunRawAlterTableSQLStatement should be null", dbTable == null);
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
            Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testRunRawDropColumnSQLStatement *** ", this.getClass().getName());

            DBTable dbTable = this.genericRawSQLEngine.runRawSQL("alter table CLAIM_EXPERTS drop column test_column");
            assertTrue("GenericRawSQLEngineTest.testRunRawAlterTableSQLStatement should be null", dbTable == null);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }
    }
    
    public void testStripComments()
    {
        Log.getInstance().debugMessage("*** GenericRawSQLEngineTest.testStripComments *** ", this.getClass().getName());

        String sql = this.genericRawSQLEngine.stripComments("alter table CLAIM_EXPERTS /*Test comments*/drop column test_column");
        assertTrue("GenericRawSQLEngineTest.testStripComments sql is incorrect", "alter table CLAIM_EXPERTS drop column test_column".equals(sql) );
    }    
}

