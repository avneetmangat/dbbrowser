package org.dbbrowser.ui;

import infrastructure.logging.Log;

import java.sql.Connection;
import java.sql.SQLException;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.rawsqlengine.DBRawSQLEngine;
import org.dbbrowser.db.engine.rawsqlengine.GenericRawSQLEngine;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;

/**
 * UI Controller for running raw SQL entered by the user
 */
public class UIControllerForRawSQL
{
	private ConnectionInfo connectionInfo = null;
	private DBRawSQLEngine dbRawSQLEngine = null;
	
	/**
	 * Connects to the database using the supplied connection info.  DirverManager caches the connection.  Start a DBQueryEngine
	 * @param connectionInfo
	 * @param masterPassword
	 */
	public void setup(ConnectionInfo connectionInfo, String masterPassword) throws DriverManagerException, DBEngineException
	{
		this.connectionInfo = connectionInfo;
		Connection connection = DBBrowserDriverManager.getInstance().getConnection( connectionInfo, masterPassword );
		
		try
		{
			this.dbRawSQLEngine = new GenericRawSQLEngine( connection.createStatement() );
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
     * Set auto commit
     * @param autoCommitFlag
     * @throws DBEngineException
     */
    public void setAutoCommit(boolean autoCommitFlag) throws DBEngineException
	{
		try
		{
			this.dbRawSQLEngine.getStatement().getConnection().setAutoCommit( autoCommitFlag );
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
			this.dbRawSQLEngine.getStatement().getConnection().commit();
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
			this.dbRawSQLEngine.getStatement().getConnection().rollback();
			Log.getInstance().debugMessage("Rollback complete", this.getClass().getName());
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}		
	}

    /**
     * Run the raw SQL statement.  Returns a DBTable if the query returns a result(SELECT queries)
     * @param sql
     * @return - DBTable or null if the query does not return anything
     * @throws DBEngineException
     */
    public DBTable runRawSQL(String sql)
		throws DBEngineException
	{	
		return this.dbRawSQLEngine.runRawSQL( sql );
	}
}
