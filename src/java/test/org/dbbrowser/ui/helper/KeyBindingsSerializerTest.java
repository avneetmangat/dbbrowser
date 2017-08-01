package org.dbbrowser.ui.helper;

import infrastructure.logging.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dbbrowser.ui.KeyBinding;
import junit.framework.TestCase;

public class KeyBindingsSerializerTest extends TestCase
{
	private List listOfKeyBindings = new ArrayList();
	
	public KeyBindingsSerializerTest(String name)
	{
	    super(name);
	    
	    KeyBinding keyBinding1 = new KeyBinding("Test1", new Integer(10), new Integer(0), KeyBinding.HELP);
	    KeyBinding keyBinding2 = new KeyBinding("Test2", new Integer(11), new Integer(1), KeyBinding.FILE_CONNECT);
	    KeyBinding keyBinding3 = new KeyBinding("Test3", new Integer(12), new Integer(2), KeyBinding.EDIT_PREFERENCES);
	    KeyBinding keyBinding4 = new KeyBinding("Test4", new Integer(13), new Integer(3), KeyBinding.FILE_OPEN_SQL_SCRIPT_FILE);
	    
	    listOfKeyBindings.add(keyBinding1);
	    listOfKeyBindings.add(keyBinding2);
	    listOfKeyBindings.add(keyBinding3);
	    listOfKeyBindings.add(keyBinding4);
	}
	
	public void testKeyBindingsSerializer()
	{
		try
		{
			Log.getInstance().debugMessage("*** Serializing key bindings *** ", this.getClass().getName());			
			KeyBindingsSerializer.serializeKeyBindings( listOfKeyBindings );
		}
		catch(IOException exc)
		{
			fail( exc.getMessage() );
		}
	}

	public void testKeyBindingsDeSerializer()
	{
		try
		{
			Log.getInstance().debugMessage("*** DeSerializing key bindings *** ", this.getClass().getName());			
			
			List listOfDeserializedKeyBindings = KeyBindingsSerializer.deserializeKeyBindings();
			
			assertEquals("KeyBindingsSerializerTest.testKeyBindingsDeSerializer failed - sizes of list is different", listOfDeserializedKeyBindings.size(), listOfKeyBindings.size());
			
			assertEquals("KeyBindingsSerializerTest.testKeyBindingsDeSerializer failed - lists are different", listOfDeserializedKeyBindings, listOfKeyBindings);
		}
		catch(IOException exc)
		{
			fail( exc.getMessage() );
		}		
	}
}
