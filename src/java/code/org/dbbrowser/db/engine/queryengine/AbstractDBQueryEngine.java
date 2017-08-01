package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.filter.Filter;
import org.dbbrowser.db.engine.SQLLog;

/**
 * A DB Query engine which handles some of the common/generic behaviour
 */
public abstract class AbstractDBQueryEngine implements DBQueryEngine
{
    private Statement statement = null;

    /**
     * Constructer
     * @param statement
     */
    public AbstractDBQueryEngine(Statement statement)
    {
        this.statement = statement;
    }

    /**
     * Returns the statememnt used to run the queries
     * @return
     */
    public Statement getStatement()
    {
        return this.statement;
    }

    /**
     * Return the number of rows in a table
     * @param schemaName
     * @param tableName
     * @return
     * @throws DBEngineException
     */
    public Integer getRowCount(String schemaName, String tableName)
            throws DBEngineException
    {
        Log.getInstance().infoMessage("Getting row count for " + schemaName + "." + tableName + "...", this.getClass().getName());

        Integer rowCount = null;
        try
        {
            //String sql = "select count(*) from " + tableName;
            String sql = "select count(*) from " + schemaName + "." + tableName;

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );
            
            //Run the SQL
            ResultSet rs = this.statement.executeQuery( sql );
            if( rs.next() )
            {
                rowCount = new Integer( rs.getInt( "count(*)" ) );
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Row count for " + schemaName + "." + tableName + " is " + rowCount, this.getClass().getName());
        return rowCount;
    }

    /**
     * Returns a list of table spaces in the Database.  It is empty if there are no table spaces for the database.
     * @return - a list of Strings
     * @throws DBEngineException
     */
    public abstract List listSchemas() throws DBEngineException;
    
	/**
	 * Returns a list of views accessible to the user.  It is empty if there are no views for the user.
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public abstract List listViews() throws DBEngineException;  
	
    /**
     * Returns a list of tables in the table space
     * @param schemaName
     * @return - a list of Strings
     * @throws DBEngineException
     */
    public abstract List listTablesInSchema(String schemaName) throws DBEngineException;

    /**
     * Returns a list of columns in the table
     * @param schemaName
     * @param tableName
     * @return - a list of ColumnInfo objects
     * @throws DBEngineException
     */
    public abstract List listColumnsInATable(String schemaName, String tableName) throws DBEngineException;

	/**
	 * Lists the indexes in the schema
	 * @return
	 * @throws DBEngineException
	 */
    public abstract DBTable listIndexes()
    	throws DBEngineException;
    
	/**
	 * Lists the constraints
	 * @return
	 * @throws DBEngineException
	 */
	public abstract DBTable listConstraints()
		throws DBEngineException;    
    	
    /**
     * Get all the data in a table
     * @param schemaName
     * @param tableName
     * @return
     * @throws DBEngineException
     */
    public abstract DBTable getAllDataInATable(String schemaName, String tableName, Integer offset, Integer numberOfRowsToReturn)
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
		throws DBEngineException
	{
        Log.getInstance().infoMessage("Getting filtered data for " + schemaName + "." + tableName + "...", this.getClass().getName());
        Log.getInstance().infoMessage("Filter: " + filter.getSQLString(), this.getClass().getName());
        DBTable dbTable = null;
        List rows = new ArrayList(); 
        
        //Get the columns in the table
        List listOfColumnsInATable = listColumnsInATable(schemaName, tableName);

        try
        {
            //String sql = "select count(*) from " + tableName;
            String sql = "select * from " + tableName + " where " + filter.getSQLString();

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );
            Log.getInstance().infoMessage("SQL to get filtered data is: " + sql, this.getClass().getName());
            
            //Run the SQL
            ResultSet rs = this.statement.executeQuery( sql );
            
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

                    //if it is an oracle timestamp, get the timestamp
                    if( (o != null) && ("oracle.sql.TIMESTAMP".equals( o.getClass().getName() )) )
                    {
                        o = rs.getTimestamp(columnName);
                    }

                    DBTableCell dbTableCell = new DBTableCell(columnInfo, o, Boolean.FALSE);

                    listOfRowData.add( dbTableCell );
                }
                DBRow dbRow = new DBRow( listOfRowData );
                rows.add( dbRow );
            }

            if( rows.isEmpty() )
            {
                dbTable = new DBTable(schemaName, tableName, rows, new Integer(0), new Integer(rows.size()), listOfColumnsInATable);
            }
            else
            {
                dbTable = new DBTable(schemaName, tableName, rows, new Integer(0), new Integer(rows.size()), new Integer(rows.size()));
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Row count for " + schemaName + "." + tableName + " is " + rows.size(), this.getClass().getName());
        return dbTable;
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
            ResultSet rs = databaseMetaData.getPrimaryKeys(null, null, tableName);

            while(rs.next())
            {
                String primaryKeyColumnNameForTable = rs.getString("COLUMN_NAME");
                String tableNameOfPrimaryKey = rs.getString("TABLE_NAME");
                String schemaNameForTable = rs.getString("TABLE_SCHEM");
                
                //if the primary key is for this table, add it
                if( tableName.equals( tableNameOfPrimaryKey ) && schemaName.equals( schemaNameForTable) )
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
}
