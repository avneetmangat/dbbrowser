package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.View;

/**
 * DBQuery Engine which gets data from a MySQL DBMS
 */
public class MySQLDBQueryEngine extends AbstractDBQueryEngine implements DBQueryEngine
{
	/**
	 * Constructer
	 * @param statement
	 */
	public MySQLDBQueryEngine(Statement statement)
	{
		super(statement);
	}
	
    /**
	 * Returns a list of schemas in the Database.
	 * @return - a list of Strings.  It is empty if there are no table spaces for the database.
	 * @throws DBEngineException
	 */
	public List listSchemas() throws DBEngineException
	{
		Log.getInstance().infoMessage("Listing database instances in MySQL...", this.getClass().getName());
		
		List listOfDatabaseInstances = new ArrayList();
		try
		{
			String sql = "show databases";
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
	    	ResultSet rs = this.getStatement().executeQuery( sql );
			while( rs.next() )
			{
				String tablename = rs.getString( "Database" );
				listOfDatabaseInstances.add( tablename );
			}
			
			Log.getInstance().debugMessage("Finished listing schemas in MySQLDBQueryEngine", this.getClass().getName());
			rs.close();			
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}
		
		Log.getInstance().infoMessage("Found " + listOfDatabaseInstances.size() + " database instances in MySQL", this.getClass().getName());

		return listOfDatabaseInstances;
	}	
	
	/**
	 * MySQL 5 does not support views.  Views are same as tables
	 * @return - a list of View objects
	 * @throws DBEngineException
	 */
	public List listViews() 
		throws DBEngineException
	{
		Log.getInstance().infoMessage("Listing views in MySQL...", this.getClass().getName());
		
    	List views = new ArrayList();
		try
		{
			//Execute the statement to get the data in the table
			String sql = "select table_schema, table_name from information_schema.views";
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
			ResultSet rs = this.getStatement().executeQuery( sql );
			
	    	//Get the data from the result set and build a DBTable
	    	while( rs.next() )
	    	{
	    		String schemaName = rs.getString("table_schema");
	    		String viewName = rs.getString("table_name");
	    		View view = new View(schemaName, viewName, null);
	    		views.add( view );
	    	}
			Log.getInstance().debugMessage("Found" + views.size() + " views", this.getClass().getName());
	    	rs.close();			
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}
		
		return views;		
	}
	
	/**
	 * Returns the SQL used to create a view
	 * @return - a String
	 * @throws DBEngineException
	 */
	public String getSQLForView(View view) 
		throws DBEngineException
	{
		Log.getInstance().infoMessage("Getting SQL for " + view.getViewName() + " in MySQL...", this.getClass().getName());
		
    	String viewDefinition = "";
		try
		{
			//Execute the statement to get the data in the table
			String sql = "select view_definition from information_schema.views where table_name = '" + view.getViewName() + "' and table_schema = '" + view.getSchemaName() + "'";
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
			ResultSet rs = this.getStatement().executeQuery( sql );
			
	    	//Get the data from the result set and build a DBTable
	    	if( rs.next() )
	    	{
	    		viewDefinition = rs.getString("view_definition");
	    	}
			Log.getInstance().debugMessage("View definition for " + view.getViewName() + " is: \n" + viewDefinition, this.getClass().getName());
	    	rs.close();			
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}
		
		return viewDefinition;
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
		Log.getInstance().infoMessage("Listing all data in " + schemaName + "." + tableName + "...", this.getClass().getName());
		
		List listOfColumnsInATable = listColumnsInATable(schemaName, tableName);
		
        //Get the number of rows in the table
        Integer rowCount = this.getRowCount(schemaName, tableName);
        
		DBTable dbTable = null;
    	List rows = new ArrayList();
		try
		{
			
			//Change the schema
			String sql = "use " + schemaName;
			this.getStatement().execute( sql );
			
			//Execute the statement to get the data in the table
			sql = "select * from " + tableName;
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
			ResultSet rs = this.getStatement().executeQuery( sql );
			
	    	//Get the data from the result set and build a DBTable
	    	while( rs.next() )
	    	{
	    		//Get the data for every column
	    		List listOfRowData = new ArrayList();
		    	for(int i=0; i<listOfColumnsInATable.size(); i++)
			    {
			    	ColumnInfo columnInfo = (ColumnInfo)listOfColumnsInATable.get( i );
			    	String columnName = columnInfo.getColumnName();
			    	Object o = rs.getObject(columnName);
			    	DBTableCell dbTableCell = null;
			    	
			    	dbTableCell = new DBTableCell(columnInfo, o, Boolean.FALSE);

			    	listOfRowData.add( dbTableCell );
			    }
		    	DBRow dbRow = new DBRow( listOfRowData );
		    	rows.add( dbRow );
	    	}
	    	
	    	
	    	if( rows.isEmpty() )
	    	{
                dbTable = new DBTable(schemaName, tableName, rows, offset, numberOfRowsToReturn, listOfColumnsInATable);
	    	}
	    	else
	    	{
	    		dbTable = new DBTable(schemaName, tableName, rows, offset, new Integer(offset.intValue() + rows.size()), rowCount);
	    	}
	    	
			Log.getInstance().debugMessage("Returning all data for " + schemaName + "." + tableName + " in MySQLDBQueryEngine", this.getClass().getName());
	    	
	    	rs.close();			
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}
		
		Log.getInstance().infoMessage("Found " + rows.size() + " rows in " + schemaName + "." + tableName, this.getClass().getName());
		
		return dbTable;
	}
	
	/**
	 * Returns a list of tables in the schema
	 * @param schemaName
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listTablesInSchema(String databaseInstanceName) throws DBEngineException
	{
		Log.getInstance().infoMessage("Listing tables in MySQL database instance " + databaseInstanceName + "...", this.getClass().getName());

		List listOfTables = new ArrayList();
		try
		{
			//Change the tablespace
			String sql = "use " + databaseInstanceName;
			this.getStatement().execute( sql );
			
			//Select the list of tables
			sql = "show tables";
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
			ResultSet rs = this.getStatement().executeQuery( sql );
			while( rs.next() )
			{
				String tablename = rs.getString( "tables_in_" + databaseInstanceName );
				listOfTables.add( tablename );
			}
			
			Log.getInstance().debugMessage("Finished listing all schemas for " + databaseInstanceName + " in MySQLDBQueryEngine", this.getClass().getName());
			
			rs.close();			
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}
		
		Log.getInstance().infoMessage("Found " + listOfTables.size() + " tables in MySQL database instance " + databaseInstanceName, this.getClass().getName());
		
		return listOfTables;		
	}
	
	/**
	 * Returns a list of columns in the table
	 * @param databaseInstanceName 
	 * @param tableName
	 * @return - a list of ColumnInfo objects
	 * @throws DBEngineException
	 */
	public List listColumnsInATable(String databaseInstanceName, String tableName)
		throws DBEngineException
	{
		Log.getInstance().infoMessage("Listing columns in table " + databaseInstanceName + "." + tableName + "...", this.getClass().getName());		
		
		//Get the list of primary key column names
		List primaryKeyColumnNames = this.getPrimaryKeyColumnNames(databaseInstanceName, tableName);
		
		List listOfColumns = new ArrayList();
		try
		{
			String sql = "select * from " + databaseInstanceName + "." + tableName;
			
			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
			
	    	ResultSet rs = this.getStatement().executeQuery( sql );
	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	
	    	int numberOfColumns = rsmd.getColumnCount();
	    	for(int i=1; i<numberOfColumns+1; i++)
	    	{
	    		//Get the values from the ResultSetMetaData
	    		String columnName = rsmd.getColumnName( i );
	    		String equivalentJavaClass = rsmd.getColumnClassName( i );
	    		String columnTypeName = rsmd.getColumnTypeName( i );
	    		Integer columnDisplaysize = new Integer( rsmd.getColumnDisplaySize( i ) );
	    		int nullable = rsmd.isNullable( i );
	    		
	    		//Check if this column is a primary key column
	    		boolean isPrimaryKeyColumn = primaryKeyColumnNames.contains( columnName );
	    		Boolean isPrimaryKeyColumnBoolean = new Boolean( isPrimaryKeyColumn );
	    		
	    		//Build the column info
	    		String nullableNature = ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN;
	    		if(nullable == ResultSetMetaData.columnNullable)
	    		{
	    			nullableNature = ColumnInfo.COLUMN_NULLABLE;
	    		}
	    		
	    		if(nullable == ResultSetMetaData.columnNoNulls)
	    		{
	    			nullableNature = ColumnInfo.COLUMN_NOT_NULLABLE;
	    		}
	    		
	    		//Check if this column is editable/writable
	    		//boolean isEditable = rsmd.isDefinitelyWritable( i );
	    		//Boolean isEditableBoolean = new Boolean( isEditable );	 
	    		
	    		int columnType = rsmd.getColumnType( i );
	    		
	    		Boolean isAutoIncrement = new Boolean( rsmd.isAutoIncrement( i ) );
	    		ColumnInfo columnInfo = new ColumnInfo(columnName, columnTypeName, equivalentJavaClass, columnDisplaysize, nullableNature, isAutoIncrement, isPrimaryKeyColumnBoolean, Boolean.TRUE, new Integer( columnType ));
	    		listOfColumns.add( columnInfo );
	    	}
	    	
			Log.getInstance().debugMessage("Finished listing all columns in for " + databaseInstanceName + "." + tableName + " in MySQLDBQueryEngine", this.getClass().getName());
	    	
			rs.close();	
		}
		catch(SQLException exc)
		{
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage("Found " + listOfColumns.size() + " columns in table " + databaseInstanceName + "." + tableName, this.getClass().getName());		
		
		return listOfColumns;		
	}
	
    /**
     * Returns the list of column names which are primary keys for the table
     * @param schemaName
     * @param tableName
     * @return a list of strings - names of primary key columns
     * @throws DBEngineException
     */
    public List getPrimaryKeyColumnNames(String schemaName, String tableName) throws DBEngineException
    {
        Log.getInstance().infoMessage("Listing primary key columns...", this.getClass().getName());

        List listOfPrimaryKeyColumnNames = new ArrayList();
        try
        {
            DatabaseMetaData databaseMetaData = this.getStatement().getConnection().getMetaData();
            ResultSet rs = databaseMetaData.getPrimaryKeys(schemaName, null, tableName);

            while(rs.next())
            {
                String primaryKeyColumnNameForTable = rs.getString("COLUMN_NAME");
                String tableNameOfPrimaryKey = rs.getString("TABLE_NAME");
                String catalogNameForTable = rs.getString("TABLE_CAT");
                
                //if the primary key is for this table, add it
                if( tableName.equals( tableNameOfPrimaryKey ) && schemaName.equals( catalogNameForTable) )
                {
                	listOfPrimaryKeyColumnNames.add( primaryKeyColumnNameForTable );
                }
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + listOfPrimaryKeyColumnNames.size() + " primary key columns", this.getClass().getName());

        return listOfPrimaryKeyColumnNames;
    }	
    
	/**
	 * Lists the indexes
	 * @return
	 * @throws DBEngineException
	 */
    public DBTable listIndexes()
    	throws DBEngineException
    {
    	DBTable dbTableForIndexes = null;
    	List listOfColumnInfos = null;
    	
    	//ColumnInfo for first column
        ColumnInfo firstColumnInfo = new ColumnInfo("Database name", "VARCHAR2", "java.lang.String", new Integer(20), ColumnInfo.COLUMN_NOT_NULLABLE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, new Integer( Types.VARCHAR ));    	
    	
    	try
    	{
	    	//Get the list of all schemas/databases in MySQL
	    	List listOfSchemas = listSchemas();
	    	List listOfDBRows = new ArrayList();
	    	
	    	//For each schemas, find all the tables
	    	Iterator iteratorForSchemas = listOfSchemas.iterator();
	    	while( iteratorForSchemas.hasNext() )
	    	{
	    		String schemaName = (String)iteratorForSchemas.next();
	    		
	    		//Find all the tables in the schema
	    		List listOfTablesInSchema = listTablesInSchema( schemaName );
	    		
	    		//Find all the indexes in the table
	        	Iterator iteratorForListOfTablesInSchemas = listOfTablesInSchema.iterator();
	        	while( iteratorForListOfTablesInSchemas.hasNext() ) 
	        	{
	        		String tableName = (String)iteratorForListOfTablesInSchemas.next();
	        		
	        		//Get the indexes for the table
	    			String sql = "show index in " + schemaName + "." + tableName;
	    			Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());
	
	    			//Run the sql
	    	    	ResultSet rs = this.getStatement().executeQuery( sql );
	    	    	
	                //Build the column infos only if they have not been built yet
	                if( listOfColumnInfos == null )
	                {
		                //Get the resultset metadata
		                ResultSetMetaData rsmd = rs.getMetaData();
	                	
		                //Build the list of column infos
		                listOfColumnInfos = getListOfColumnInfosForIndexes( rsmd );
		                
		                //The first column info is the schema name
		                listOfColumnInfos.add( 0, firstColumnInfo );
	                }
	        		
	    	    	//Get all the indexes for the table
	                while(rs.next())
	                {
	                	List listOfDBTableCells = new ArrayList();
	                	//Build a DBTableCell for each data
	                	for(int i=0; i<listOfColumnInfos.size(); i++)
	                	{
	                		//if this is the first column, then insert data for database name first
	                		if( i == 0 )
	                		{
		                		DBTableCell cell = new DBTableCell( firstColumnInfo, schemaName, Boolean.FALSE );
		                		listOfDBTableCells.add( cell );	                			                			
	                		}
	                		else
	                		{
		                		ColumnInfo ci = (ColumnInfo)listOfColumnInfos.get(i);
		                		Object value = rs.getObject( ci.getColumnName() );
		                		DBTableCell cell = new DBTableCell( ci, value, Boolean.FALSE );
		                		listOfDBTableCells.add( cell );	                		
	                		}
	                	}
	                	
	                	DBRow dbRow = new DBRow(listOfDBTableCells);
	                	listOfDBRows.add( dbRow );
	                }	    	    	
	        	}
	    	}
	    	dbTableForIndexes = new DBTable(null, null, listOfDBRows, new Integer(0), new Integer(0), new Integer(0));
    	}
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }
    	
    	
    	return dbTableForIndexes;
    }
    
    private List getListOfColumnInfosForIndexes(ResultSetMetaData rsmd)
    	throws SQLException
    {
    	List listOfColumnInfos = new ArrayList();
    	
    	//Build the column info for each column
    	for(int i=0; i<rsmd.getColumnCount(); i++)
    	{
            //Build the column info object
            String columnName = rsmd.getColumnName( i+1 );
            String equivalentJavaClass = rsmd.getColumnClassName( i+1 );
            String columnTypeName = rsmd.getColumnTypeName( i+1 );                          //e.g. NUMBER, VARCHAR2
            Integer columnDisplaysize = new Integer( rsmd.getColumnDisplaySize( i+1 ) );
            int columnType = rsmd.getColumnType( i+1 );                                     //From java.sql.Types
            
            //Decide the nullable nature
            int nullable = rsmd.isNullable(i+1);
            String nullableNature = ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN;
            if(nullable == ResultSetMetaData.columnNullable)
            {
                nullableNature = ColumnInfo.COLUMN_NULLABLE;
            }

            if(nullable == ResultSetMetaData.columnNoNulls)
            {
                nullableNature = ColumnInfo.COLUMN_NOT_NULLABLE;
            }

            ColumnInfo columnInfo = new ColumnInfo(columnName, columnTypeName, equivalentJavaClass, columnDisplaysize, nullableNature, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, new Integer( columnType ));
            listOfColumnInfos.add( columnInfo );
    	}

        return listOfColumnInfos;
    }
    
	/**
	 * Lists the constraints
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listConstraints()
		throws DBEngineException
	{
    	throw new UnsupportedOperationException();	
	}
}
