package org.dbbrowser.db.engine.updateengine;

import java.sql.Statement;

import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.View;

/**
 * Methods which must be implemented by a Update Engine.  The UI uses these methods to update the database
 */
public interface DBUpdateEngine
{
    /**
     * Update the view definition for the view
     * @param view
     * @throws DBEngineException
     */
    public void updateViewDefinition(View view)
    	throws DBEngineException;
    
    /**
     * Add new column to the table
     * @param schemaName
     * @param tableName
     * @param columnInfo
     * @throws DBEngineException
     */
    public void addNewColumn(String schemaName, String tableName, ColumnInfo columnInfo)
    	throws DBEngineException;

    /**
     * Drop the column from the table
     * @param schemaName
     * @param tableName
     * @param columnInfo
     * @throws DBEngineException
     */
    public void dropColumn(String schemaName, String tableName, ColumnInfo columnInfo)
		throws DBEngineException;

    /**
     * Add a new row to the database
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
    public void addNewRow(String schemaName, String tableName, DBRow dbRow)
       throws DBEngineException;

    /**
     * Update the database using the data from the DBRow
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
	public void update(String schemaName, String tableName, DBRow dbRow)
       throws DBEngineException;

    /**
     * Delete the row
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
    public void deleteRow(String schemaName, String tableName, DBRow dbRow)
		throws DBEngineException;

    /**
     * Return the statement used to update the database
     * @return
     */
    public Statement getStatement();
}
