package org.dbbrowser.db.engine.model;

/**
 * A view class holds information about a view
 * @author amangat
 *
 */
public class View
{
	private String schemaName = null;
	private String viewName = null;
	private String viewDefinition = null;
	
	/**
	 * Constructer
	 * @param schemaName
	 * @param viewName
	 * @param viewDefinition
	 */
	public View(String schemaName, String viewName, String viewDefinition)
	{
		this.schemaName = schemaName;
		this.viewName = viewName;
		this.viewDefinition = viewDefinition;
	}
	
	/**
	 * Returns the schema name
	 * @return
	 */
	public String getSchemaName()
	{
		return this.schemaName;
	}
	
	/**
	 * Returns the view name
	 * @return
	 */
	public String getViewName()
	{
		return this.viewName;
	}
	
	/**
	 * Returns the SQL used to create the view
	 * @return
	 */
	public String getViewDefinition()
	{
		return this.viewDefinition;
	}
}
