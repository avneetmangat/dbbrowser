package org.dbbrowser.ui.helper;

import infrastructure.propertymanager.PropertyManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.dbbrowser.ui.KeyBinding;
import com.thoughtworks.xstream.XStream;

/**
 * Serializes the key bindings using XStream.  serializes the list of key bindings as XML
 * @author amangat
 *
 */
public class KeyBindingsSerializer
{
    private static final String KEY_BINDING_FILE_NAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-key-bindings-filename" );			
	
    /**
     * Converts XML to a list of KeyBindings
     * @return - java.util.List - a list of KeyBinding classes
     * @throws FileNotFoundException
     * @throws IOException
     */
	public static List deserializeKeyBindings()
		throws FileNotFoundException, IOException
	{
		//Load the key bindings
		StringBuffer xml = new StringBuffer();
		FileReader fr = new FileReader( KEY_BINDING_FILE_NAME );
		BufferedReader reader = new BufferedReader( fr );
		
		String line = reader.readLine();
		
		while( line != null )
		{
			xml.append(line);
			line = reader.readLine();
		}

		XStream xstream = new XStream();
		xstream.alias("keyBinding", KeyBinding.class);
		return (List)xstream.fromXML(xml.toString());
	}
	
	/**
	 * Stores the list of KeyBindings as XML
	 * @param keyBindings
	 * @throws IOException
	 */
	public static void serializeKeyBindings(List keyBindings)
		throws IOException
	{
		//Save the changes to XML file
		XStream xstream = new XStream();
		xstream.alias("keyBinding", KeyBinding.class);
		String xml = xstream.toXML(keyBindings);
		
		//Write key bindings to file
		FileWriter fw = new FileWriter(KEY_BINDING_FILE_NAME);
		BufferedWriter writer = new BufferedWriter( fw );
		
		writer.write( xml );
		writer.flush();
		writer.close();
	}
}
