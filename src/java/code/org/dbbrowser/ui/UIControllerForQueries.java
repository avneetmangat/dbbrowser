package org.dbbrowser.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.model.filter.Filter;
import org.dbbrowser.db.engine.queryengine.DBQueryEngine;
import org.dbbrowser.db.engine.queryengine.MsSQLDBQueryEngine;
import org.dbbrowser.db.engine.queryengine.MySQLDBQueryEngine;
import org.dbbrowser.db.engine.queryengine.Oracle9iDBQueryEngine;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.DBBrowserDriverManager;
import org.dbbrowser.drivermanager.DriverManagerException;

/**
 * Controller for the UI layer.  Delegates to other classes
 * @author amangat
 *
 */
public class UIControllerForQueries
{
	public static final String ORACLE_DBMS = "Oracle";
	public static final String MYSQL_DBMS = "MySQL";
	public static final String MSSQL_DBMS = "MS-SQL";
	
	public static final long version = 1l;
	private ConnectionInfo connectionInfo = null;
	private DBQueryEngine dbQueryEngine = null;
	
	/**
	 * Connects to the database using the supplied connection info.  DirverManager caches the connection.  Start a DBQueryEngine
	 * @param connectionInfo
	 * @param masterPassword
	 * @author amangat
	 */
	public void setup(ConnectionInfo connectionInfo, String masterPassword) throws DriverManagerException, DBEngineException
	{
		this.connectionInfo = connectionInfo;
		Connection connection = DBBrowserDriverManager.getInstance().getConnection( connectionInfo, masterPassword );
		
		if( ORACLE_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbQueryEngine = new Oracle9iDBQueryEngine( connection.createStatement() );				
			}
			catch(SQLException exc)
			{
				throw new DBEngineException( exc.getMessage() );
			}
		}
		
		if( MYSQL_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbQueryEngine = new MySQLDBQueryEngine( connection.createStatement() );				
			}
			catch(SQLException exc)
			{
				throw new DBEngineException( exc.getMessage() );
			}			
		}
		
		if( MSSQL_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			try
			{
				this.dbQueryEngine = new MsSQLDBQueryEngine( connection.createStatement() );				
			}
			catch(SQLException exc)
			{
				throw new DBEngineException( exc.getMessage() );
			}			
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
	 * Returns a list of table spaces in the Database.  It is empty if there are no table spaces for the database.  
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listSchemas()
		throws DBEngineException
	{
		return this.dbQueryEngine.listSchemas();
	}
	
	/**
	 * Lists the indexes in the schema
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listIndexes()
		throws DBEngineException
	{
		return this.dbQueryEngine.listIndexes();
	}	
	
	/**
	 * Lists the constraints in the schema
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listConstraints()
		throws DBEngineException
	{
		return this.dbQueryEngine.listConstraints();
	}		
	
	/**
	 * Returns a list of views accessible to the user.  It is empty if there are no views for the user.
	 * @return - a list of View objects
	 * @throws DBEngineException
	 */
	public List listViews() 
		throws DBEngineException
	{
		return this.dbQueryEngine.listViews();
	}
	
	/**
	 * Returns the SQL used to create a view
	 * @return - a String
	 * @throws DBEngineException
	 */
	public String getSQLForView(View view) 
		throws DBEngineException
	{
		return this.dbQueryEngine.getSQLForView( view );
	}
	
	
	/**
	 * Returns a list of tables in the table space
	 * @param schemaName
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listTablesInSchema(String schemaName)
		throws DBEngineException
	{
		return this.dbQueryEngine.listTablesInSchema( schemaName );
	}
	
	/**
	 * Returns a list of columns in the table
	 * @param schemaName
	 * @param tableName
	 * @return - a list of ColumnInfo objects
	 * @throws DBEngineException
	 */
	public List listColumnsInATable(String schemaName, String tableName)
		throws DBEngineException
	{
		return this.dbQueryEngine.listColumnsInATable( schemaName, tableName );
	}	
	
	/**
	 * Get all the data in a table
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable getAllDataInATable(String schemaName, String tableName, Integer offset, Integer numberOfRowsToReturn)
		throws DBEngineException
	{
		return this.dbQueryEngine.getAllDataInATable( schemaName, tableName, offset, numberOfRowsToReturn );
	}
	
	/**
	 * Get all the data in a table
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws DBEngineException
	 */	
	public DBTable getFilteredDataInATable(String schemaName, String tableName, Filter filter)
		throws DBEngineException
	{
		return this.dbQueryEngine.getFilteredDataInATable( schemaName, tableName, filter );
	}
	
	/**
	 * Lists the sequence in an Oracle database.  For other databases, throws an UnsupportedOperationException
	 * @return - results as a dbtable
	 * @throws DBEngineException
	 */
	public DBTable listSequences()
		throws DBEngineException
	{
		//Only oracle has sequences
		if( ! ORACLE_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			throw new UnsupportedOperationException("*** MySQL does not support sequences.  Sequences cant be listed for MySQL - UIController.listSequences ***");
		}
		
		//If it is oracle dbms, list sequences
		Oracle9iDBQueryEngine oracle9iDBQueryEngine = (Oracle9iDBQueryEngine)this.dbQueryEngine;
		return oracle9iDBQueryEngine.listSequences();
	}
}
