package org.dbbrowser.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents a key binding.  A key binding object contains information
 * about a key press which will lead to an action.  A Key Binding is uniquely identified by its description.
 * The method 'getAllActions' lists all the possible actions.
 * @author amangat
 *
 */
public class KeyBinding
{
	public static String HELP = "Help -> Help";
	public static String FILE_CONNECT = "File -> Connect";
	public static String FILE_DROP_CONNECTION = "File -> Drop connection";
	public static String FILE_OPEN_SQL_SCRIPT_FILE = "File -> Open SQL Script";
	public static String FILE_EXIT = "File -> Exit";
	public static String EDIT_PREFERENCES = "Edit -> Preferences";
	public static String TOOLS_COMMIT = "Tools -> Commit";
	public static String TOOLS_ROLLBACK = "Tools -> Rollback";
	
	private String description = null;
	private Integer keyCode = null;
	private Integer modifierCode = null;
	private String action = null;
	
	/**
	 * Constructer
	 * @param description
	 * @param keyCode
	 * @param modifierCode
	 * @param action
	 */
	public KeyBinding(String description, Integer keyCode, Integer modifierCode, String action)
	{
		this.description = description;
		this.keyCode = keyCode;
		this.modifierCode = modifierCode;
		this.action = action;
	}
	
	/**
	 * Returns all the possible actions
	 * @return - a list of strings
	 */
	public static List getAllActions()
	{
		List listOfActions = new ArrayList();
		listOfActions.add(HELP);
		listOfActions.add(FILE_CONNECT);
		listOfActions.add(FILE_DROP_CONNECTION);
		listOfActions.add(FILE_OPEN_SQL_SCRIPT_FILE);
		listOfActions.add(FILE_EXIT);
		listOfActions.add(EDIT_PREFERENCES);
		listOfActions.add(TOOLS_COMMIT);
		listOfActions.add(TOOLS_ROLLBACK);
		
		return listOfActions;
	}
	
	/**
	 * Returns a descriptions which uniquely identifies this key binding
	 * @return
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Returns the key code for the key
	 * @return
	 */
	public Integer getKeyCode()
	{
		return this.keyCode;
	}
	
	/**
	 * Returns the modifier code - such as CTRL or ALT key
	 * @return
	 */
	public Integer getModifierCode()
	{
		return this.modifierCode;
	}
	
	/**
	 * Returns the action to be performed when this key is pressed
	 * @return
	 */
	public String getAction()
	{
		return this.action;
	}
	
	public int hashCode()
	{
		return this.getDescription().hashCode();
	}
	
	/**
	 * Two key bindings are equal if they have the same description
	 * @return - true of the otherObject is an instance of KeyBinding and it has the same description as this object
	 */
	public boolean equals(Object otherObject)
	{
		boolean equal = false;
		if( otherObject instanceof KeyBinding )
		{
			KeyBinding otherKeyBinding = (KeyBinding)otherObject;
			String otherObjectsDescription = otherKeyBinding.getDescription();
			
			//if the 2 descriptions are equal, then they are equal
			if( otherObjectsDescription.equals( this.getDescription() ))
			{
				equal = true;
			}
		}
		return equal;
	}
}
