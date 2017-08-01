package org.dbbrowser.ui;

import infrastructure.logging.Log;

import java.sql.Connection;
import java.sql.SQLException;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.updateengine.DBUpdateEngine;
import org.dbbrowser.db.engine.updateengine.GenericDBUpdateEngine;
import org.dbbrowser.db.engine.updateengine.MySQLDBUpdateEngine;
import org.dbbrowser.db.engine.updateengine.OracleDBUpdateEngine;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;

/**
 * UI Controller for updating the database
 */
public class UIControllerForUpdates
{
	public static final String ORACLE_DBMS = "Oracle";
	public static final String MYSQL_DBMS = "MySQL";
	public static final String MSSQL_DBMS = "MS-SQL";
	
	private ConnectionInfo connectionInfo = null;
	private DBUpdateEngine dbUpdateEngine = null;
	
	/**
	 * Connects to the database using the supplied connection info.  DirverManager caches the connection.  Start a DBQueryEngine
	 * @param connectionInfo
	 * @param masterPassword
	 */
	public void setup(ConnectionInfo connectionInfo, String masterPassword) throws DriverManagerException, DBEngineException
	{
		this.connectionInfo = connectionInfo;
		Connection connection = DBBrowserDriverManager.getInstance().getConnection( connectionInfo, masterPassword );
		
		if( ORACLE_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbUpdateEngine = new OracleDBUpdateEngine( connection.createStatement() );
			}
			catch(SQLException exc)
			{
				throw new DBEngineException(exc.getMessage());
			}
		}
		
		if( MYSQL_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbUpdateEngine = new MySQLDBUpdateEngine( connection.createStatement() );
			}
			catch(SQLException exc)
			{
				throw new DBEngineException(exc.getMessage());
			}			
		}
		
		if( MSSQL_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbUpdateEngine = new GenericDBUpdateEngine( connection.createStatement() );
			}
			catch(SQLException exc)
			{
				throw new DBEngineException(exc.getMessage());
			}			
		}			
	}

    /**
     * Set autocommit
     * @param autoCommitFlag
     * @throws DBEngineException
     */
    public void setAutoCommit(boolean autoCommitFlag) throws DBEngineException
	{
		try
		{
			this.dbUpdateEngine.getStatement().getConnection().setAutoCommit( autoCommitFlag );
			Log.getInstance().debugMessage("Autocommit " + autoCommitFlag, this.getClass().getName());
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}		
	}

    /**
     * Commit
     * @throws DBEngineException
     */
    public void commit() throws DBEngineException
	{
		try
		{
			this.dbUpdateEngine.getStatement().getConnection().commit();
			Log.getInstance().debugMessage("Commit complete", this.getClass().getName());
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}		
	}

    /**
     * Rollback
     * @throws DBEngineException
     */
    public void rollback() throws DBEngineException
	{
		try
		{
			this.dbUpdateEngine.getStatement().getConnection().rollback();
			Log.getInstance().debugMessage("Rollback complete", this.getClass().getName());
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}		
	}	
	
	/**
	 * Return the connection info representing the connection
	 * @return
	 */
	public ConnectionInfo getConnectionInfo()
	{
		return this.connectionInfo;
	}
	
    /**
     * Update the view definition for the view
     * @param view
     * @throws DBEngineException
     */
    public void updateViewDefinition(View view)
    	throws DBEngineException
	{
    	this.dbUpdateEngine.updateViewDefinition(view);
	}

    /**
     * Add new column to the table
     * @param schemaName
     * @param tableName
     * @param columnInfo
     * @throws DBEngineException
     */
    public void addNewColumn(String schemaName, String tableName, ColumnInfo columnInfo)
		throws DBEngineException
	{
		this.dbUpdateEngine.addNewColumn(schemaName, tableName, columnInfo);
	}

    /**
     * Drop the column from the table
     * @param schemaName
     * @param tableName
     * @param columnInfo
     * @throws DBEngineException
     */
    public void dropColumn(String schemaName, String tableName, ColumnInfo columnInfo)
		throws DBEngineException
	{
		this.dbUpdateEngine.dropColumn(schemaName, tableName, columnInfo);
	}
	
    /**
     * Add a new row to the database
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
	public void addNewRow(String schemaName, String tableName, DBRow dbRow)
       throws DBEngineException
   {
		this.dbUpdateEngine.addNewRow( schemaName, tableName, dbRow);
   }
	
    /**
     * Update the database using the data from the DBRow
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
	public void update(String schemaName, String tableName, DBRow dbRow)
		throws DBEngineException
	{
		this.dbUpdateEngine.update( schemaName, tableName, dbRow);
	}

    /**
     * Delete the row
     * @param schemaName
     * @param tableName
     * @param dbRow
     * @throws DBEngineException
     */
    public void deleteRow(String schemaName, String tableName, DBRow dbRow)
		throws DBEngineException
    {
        this.dbUpdateEngine.deleteRow(schemaName, tableName, dbRow);
    }
}
