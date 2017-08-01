package org.dbbrowser.drivermanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import infrastructure.logging.Log;
import junit.framework.TestCase;

public class ConnectionInfoTest extends TestCase
{
    public ConnectionInfoTest(String name)
    {
        super(name);    
    }

    public void testSerializeConnectionInfo()
    {
        Log.getInstance().debugMessage("Test serialize Connection Info", this.getClass().getName());
     
        ConnectionInfo connectionInfo = new ConnectionInfo(
        		"oracle.jdbc.OracleDriver",
        		new File("C:/projects/DBBrowser/lib/ojdbc14.jar"),
        		"Oracle XCS Charging connection",
        		"Oracle", 
        		"jdbc:oracle:thin:@localhost:1521:xcs",
        		"system", 
        		"qwerty",
        		null,
        		null
        		);
        connectionInfo.setLastUsed( new Date() );
        ArrayList listOfconnectionInfos = new ArrayList();
        listOfconnectionInfos.add(connectionInfo);

        //Serialize the object
        /*try
        {
            ConnectionInfoSerializer.serialize( listOfconnectionInfos );
        }
        catch(IOException exc)
        {
	        Log.getInstance().fatalMessage("*** IOException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());
        }*/
    }
    
    public void testDeSerializeConnectionInfo()
    {
        Log.getInstance().debugMessage("Test de-serialize Connection Info", this.getClass().getName());
        
        ConnectionInfo connectionInfo1 = new ConnectionInfo(
        		"oracle.jdbc.OracleDriver",
        		new File("C:/projects/DBBrowser/lib/ojdbc14.jar"),
        		"Oracle XCS Charging connection",
        		"Oracle",
        		"jdbc:oracle:thin:@localhost:1521:xcs",
        		"system", 
        		"qwerty",
        		null,
        		null); 
        
        //Serialize the object
        try
        {
        	List listOfconnectionInfos = ConnectionInfoSerializer.deserialize();
        	
        	ConnectionInfo connectionInfo2 = (ConnectionInfo)listOfconnectionInfos.get(0);
        	Log.getInstance().debugMessage(connectionInfo2.toString(), this.getClass().getName());
        
        	//asserts
        	assertEquals(connectionInfo1.getName(), connectionInfo2.getName());
        	assertEquals(connectionInfo1.getDatabaseURL(), connectionInfo2.getDatabaseURL());
        	assertEquals(connectionInfo1.getDriverClassName(), connectionInfo2.getDriverClassName());
        	assertEquals(connectionInfo1.getJdbcDriverJarFileLocation(), connectionInfo2.getJdbcDriverJarFileLocation());
        	assertEquals(connectionInfo1.getUsername(), connectionInfo2.getUsername());
        	assertEquals(connectionInfo1.getPassword(), connectionInfo2.getPassword());
        }
        catch(IOException exc)
        {
	        Log.getInstance().fatalMessage("*** IOException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	
        }
        catch(ClassNotFoundException exc)
        {
	        Log.getInstance().fatalMessage("*** IOException *** " + exc.getMessage(), this.getClass().getName());
	        fail(exc.getMessage());        	
        }        
    }    
}
