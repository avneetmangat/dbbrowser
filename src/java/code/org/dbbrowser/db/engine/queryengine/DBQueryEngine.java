package org.dbbrowser.db.engine.queryengine;

import java.util.List;

import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.model.filter.Filter;

/**
 * The interface for the engine which runs the SQL queries
 * @author amangat
 *
 */
public interface DBQueryEngine
{
	/**
	 * Returns a list of schemas in the Database.  It is empty if there are no schemas for the database.
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listSchemas() throws DBEngineException;	
	
	/**
	 * Returns a list of views accessible to the user.  It is empty if there are no views for the user.
	 * @return - a list of View objects
	 * @throws DBEngineException
	 */
	public List listViews() throws DBEngineException;
	
	/**
	 * Lists the indexes
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listIndexes()throws DBEngineException;
	
	/**
	 * Lists the constraints
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listConstraints()throws DBEngineException;	
	
	/**
	 * Returns the SQL used to create a view
	 * @return - a String
	 * @throws DBEngineException
	 */
	public String getSQLForView(View view) throws DBEngineException;	
	
	/**
	 * Returns a list of tables in the schema
	 * @param schemaName
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listTablesInSchema(String schemaName) throws DBEngineException;
	
	/**
	 * Returns a list of columns in the table
	 * @param schemaName
	 * @param tableName
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listColumnsInATable(String schemaName, String tableName) throws DBEngineException;
	
	/**
	 * Get all the data in a table
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable getAllDataInATable(String schemaName, String tableName, Integer offset, Integer numberOfRowsToReturn)
		throws DBEngineException;	
	
	/**
	 * Returns the filtered data in the table
	 * @param schemaName
	 * @param tableName
	 * @param filter
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable getFilteredDataInATable(String schemaName, String tableName, Filter filter)
		throws DBEngineException;	
	
	/**
	 * Returns the list of column names which are primary keys for the table
	 * @param schemaName
	 * @param tableName
	 * @return a list of strings - names of primary key columns
	 * @throws DBEngineException
	 */
	public List getPrimaryKeyColumnNames(String schemaName, String tableName) throws DBEngineException;

    /**
     * Return the number of rows in a table
     * @param schemaName
     * @param tableName
     * @return
     * @throws DBEngineException
     */
    public Integer getRowCount(String schemaName, String tableName) throws DBEngineException;
}
