package org.dbbrowser.drivermanager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;
import org.dbbrowser.ui.UIControllerForQueries;
import infrastructure.logging.Log;
import junit.framework.TestCase;

public class DBBrowserDriverManagerTest extends TestCase
{
    public DBBrowserDriverManagerTest(String name)
    {
        super(name);    
    }

    public void testDriverManager()
    {
        Log.getInstance().debugMessage("Test DriverManager", this.getClass().getName());
        
        try
        {
            Log.getInstance().debugMessage("--------------------------------------------------", this.getClass().getName());
            ConnectionInfo connectionInfo = new ConnectionInfo(
            		"oracle.jdbc.OracleDriver",
            		new File("C:/projects/DBBrowser/lib/ojdbc14.jar"),
            		"Component Link Testing User",
            		UIControllerForQueries.ORACLE_DBMS, 
            		"jdbc:oracle:thin:@localhost:1521:live",
            		"xcssys", 
            		"qwerty",
            		null,
            		null); 
        	Connection conn = DBBrowserDriverManager.getInstance().getConnection( connectionInfo, null );
            
            
    		Statement stmt = conn.createStatement();
    		String sql = "select *  from XCSSYS.CLIENT where rownum <= 100 and NAME in  ( select NAME from XCSSYS.CLIENT group by NAME )  minus  select * from XCSSYS.CLIENT where rownum < 1 and NAME in  ( select NAME from XCSSYS.CLIENT group by NAME )  ";
    		
    		ResultSet rs = stmt.executeQuery(sql);
           while(rs.next())
           {
        	   Log.getInstance().debugMessage("*** Row returned from ***", this.getClass().getName());
           }
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
}
