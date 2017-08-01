package org.dbbrowser.db.engine.updateengine;

import junit.framework.TestCase;
import org.dbbrowser.db.engine.queryengine.Oracle9iDBQueryEngine;
import org.dbbrowser.db.engine.updateengine.GenericDBUpdateEngine;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.drivermanager.ConnectionInfoSerializer;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;
import infrastructure.logging.Log;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.io.IOException;

public class GenericDBUpdateEngineTest extends TestCase
{
    private GenericDBUpdateEngine genericDBUpdateEngine = null;

    public GenericDBUpdateEngineTest(String name)
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
                if( connectionInfo.getName().equals("Oracle PPDB"))
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
                genericDBUpdateEngine = new GenericDBUpdateEngine( conn.createStatement() );
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
            Log.getInstance().fatalMessage("*** DriverManagerException *** " + exc.getMessage(), this.getClass().getName());
            fail(exc.getMessage());
        }        
    }

    public void testUpdate()
    {
        try
        {
            ColumnInfo ci1 = new ColumnInfo("ID", "", "", new Integer(10), null, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, new Integer(Types.NUMERIC));
            DBTableCell dbTableCell1 = new DBTableCell(ci1, new Integer(61), Boolean.FALSE);
            ColumnInfo ci2 = new ColumnInfo("INDIVIDUAL_NAME", "", "", new Integer(10), null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, new Integer(Types.VARCHAR));
            DBTableCell dbTableCell2 = new DBTableCell(ci2, "Updated by JUnit", Boolean.TRUE);

            List listOfTableCells = new ArrayList();
            listOfTableCells.add( dbTableCell1 );
            listOfTableCells.add( dbTableCell2 );

            DBRow dbRow = new DBRow(listOfTableCells);
            this.genericDBUpdateEngine.update("", "CLAIM_EXPERTS", dbRow);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }
    }

    public void testDeleteRow()
    {
        try
        {
            ColumnInfo ci1 = new ColumnInfo("ID", "", "", new Integer(10), null, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, new Integer(Types.NUMERIC));
            DBTableCell dbTableCell1 = new DBTableCell(ci1, new Integer(100), Boolean.FALSE);

            List listOfTableCells = new ArrayList();
            listOfTableCells.add( dbTableCell1 );

            DBRow dbRow = new DBRow(listOfTableCells);
            this.genericDBUpdateEngine.deleteRow("", "CLAIM_EXPERTS", dbRow);
        }
        catch(DBEngineException exc)
        {
	        Log.getInstance().fatalMessage("*** DBEngineException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }
    }
}

