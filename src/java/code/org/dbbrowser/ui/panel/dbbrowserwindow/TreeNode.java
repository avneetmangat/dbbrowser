package org.dbbrowser.ui.panel.dbbrowserwindow;

/**
 * An instance of this type represents a node of a JTree
 * @author amangat
 *
 */
public class TreeNode
{
	public static String ROOT_VIEW_TYPE = "ROOT_VIEW_TYPE";
	public static String SCHEMAS_TYPE = "SCHEMAS_TYPE";
	public static String SCHEMA_TYPE = "SCHEMA_TYPE";
	public static String TABLE_TYPE = "TABLE_TYPE";

	public static String VIEWS_TYPE = "VIEWS_TYPE";
	public static String VIEW_TYPE = "VIEW_TYPE";
	
	private String name = null;
	private String type = null;
	private Object userObject = null;
	
	/**
	 * Constrcuter
	 * @param name
	 * @param type
	 */
	public TreeNode(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Constrcuter
	 * @param name
	 * @param type
	 * @param userObject
	 */
	public TreeNode(String name, String type, Object userObject)
	{
		this.name = name;
		this.type = type;
		this.userObject = userObject;
	}	
	
	/**
	 * Returns the name
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the type
	 * @return
	 */
	public String getType()
	{
		return this.type;
	}
	
	/**
	 * Returns the user object
	 * @return
	 */
	public Object getUserObject()
	{
		return this.userObject;
	}
	
	/**
	 * toString - used by JTree for displaying
	 * @return 
	 */
	public String toString()
	{
		return this.name;
	}
}
