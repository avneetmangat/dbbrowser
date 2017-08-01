package org.dbbrowser.drivermanager;

import infrastructure.propertymanager.PropertyManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.thoughtworks.xstream.XStream;

/**
 * Used to serialize the connection to a stream
 */
public class ConnectionInfoSerializer
{		
    /**
     * Serialize(save) the connection information
     * @param listOfConnectionInfo
     * @throws IOException
     */
    public static void serialize(List listOfConnectionInfo) throws IOException
	{
		//Serialize the object
		XStream xstream = new XStream();
		xstream.alias("ConnectionInfo", ConnectionInfo.class);
		String xml = xstream.toXML(listOfConnectionInfo);
		
		//Write key bindings to file
		FileWriter fw = new FileWriter( PropertyManager.getInstance().getProperty( "dbbrowser.connectioninfo.serialize.location" ) );
		BufferedWriter writer = new BufferedWriter( fw );
		
		writer.write( xml );
		writer.flush();
		writer.close();		
	} 
    
    /**
     * Deserialize(load) the connection info
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List deserialize() throws ClassNotFoundException, IOException
	{
		//Load the connection info
		StringBuffer xml = new StringBuffer();
		FileReader fr = new FileReader( PropertyManager.getInstance().getProperty( "dbbrowser.connectioninfo.serialize.location" ) );
		BufferedReader reader = new BufferedReader( fr );
		
		String line = reader.readLine();
		
		while( line != null )
		{
			xml.append(line);
			line = reader.readLine();
		}

		XStream xstream = new XStream();
		xstream.alias("ConnectionInfo", ConnectionInfo.class);
		return (List)xstream.fromXML(xml.toString());    	
	}
}
