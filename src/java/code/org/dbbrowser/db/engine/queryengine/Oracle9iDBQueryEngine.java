package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.SQLLog;

/**
 * The engine which runs the SQL queries on an Oracle DBMS
 * @author amangat
 *
 */
public class Oracle9iDBQueryEngine extends AbstractDBQueryEngine
{
    /**
     * Constructer
     * @param statement
     */
    public Oracle9iDBQueryEngine(Statement statement)
    {
        super( statement );
    }

    public List listSchemas()
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Listing schemas in Oracle database", this.getClass().getName());

        List listOfSchemas = new ArrayList();
        try
        {
            //To see tables in this schema
            //String sql = "select TABLESPACE_NAME from user_tables";

            //SQL to get the tables in the user tablespaces
            //String sql = "select distinct tablespace_name from all_tables";

            String sql = "select distinct owner from all_tables";

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            ResultSet rs = this.getStatement().executeQuery( sql );
            while( rs.next() )
            {
                String schema = rs.getString( "owner" );

                if(schema != null)
                {
                    listOfSchemas.add( schema );
                }
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + listOfSchemas.size() + " schemas in Oracle database", this.getClass().getName());
        return listOfSchemas;
    }
    
	/**
	 * Returns a list of views accessible to the user.  It is empty if there are no views for the user.
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listViews() 
		throws DBEngineException
	{
        Log.getInstance().infoMessage("Listing views in Oracle database", this.getClass().getName());

        List listOfViews = new ArrayList();
        try
        {
            String sql = "select view_name from user_views";

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            ResultSet rs = this.getStatement().executeQuery( sql );
            while( rs.next() )
            {
                String viewName = rs.getString( "view_name" );

                if(viewName != null)
                {
                	View view = new View(null, viewName, null);
                	listOfViews.add( view );
                }
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + listOfViews.size() + " views in Oracle database", this.getClass().getName());
        return listOfViews;		
	}
	
	/**
	 * Returns the SQL used to create a view
	 * @return - a String
	 * @throws DBEngineException
	 */
	public String getSQLForView(View view) 
		throws DBEngineException
	{
        Log.getInstance().infoMessage("Getting SQL for " + view.getViewName() + " in Oracle database", this.getClass().getName());

        String sqlForView = null;
        try
        {
            String sql = "select text from user_views where view_name='" + view.getViewName() + "'";

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            ResultSet rs = this.getStatement().executeQuery( sql );
            if( rs.next() )
            {
            	sqlForView = rs.getString( "text" );
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("SQL for view " + view.getViewName() + " is " + sqlForView + " in Oracle database", this.getClass().getName());
        return sqlForView;		
	}

    /**
     * Returns a list of tables in the table space
     * @param schemaName
     * @return - a list of Strings
     * @throws DBEngineException
     */
    public List listTablesInSchema(String schemaName) throws DBEngineException
    {
        Log.getInstance().infoMessage("Listing tables in " + schemaName + "...", this.getClass().getName());

        List listOfTables = new ArrayList();
        try
        {
            String sql = "select distinct table_name from all_tables where owner = '" + schemaName + "'";

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            ResultSet rs = this.getStatement().executeQuery( sql );
            while( rs.next() )
            {
                String tablename = rs.getString( "table_name" );
                listOfTables.add( tablename );
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + listOfTables.size() + " tables in " + schemaName, this.getClass().getName());

        return listOfTables;
    }

    /**
     * Get all the data in a table
     * @param schemaName
     * @param tableName
     * @param offset
     * @param numberOfRowsToReturn
     * @return
     * @throws DBEngineException
     */
    public DBTable getAllDataInATable(String schemaName, String tableName, Integer offset, Integer numberOfRowsToReturn)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Getting all data in " + schemaName + "." + tableName + "...", this.getClass().getName());

        //Get the number of rows in the table
        Integer rowCount = this.getRowCount(schemaName, tableName);

        //Get the columns in the table
        List listOfColumnsInATable = listColumnsInATable(schemaName, tableName);

        //Get the primary key columns in the table
        List listOfPrimaryKeyColumns = this.getPrimaryKeyColumnNames(schemaName, tableName);

        String column = "";
        //if there no primary keys, use the first column in the table
        if( listOfPrimaryKeyColumns.isEmpty() )
        {
            ColumnInfo ci = (ColumnInfo)listOfColumnsInATable.get( 0 );
            column = ci.getColumnName();
        }
        else
        {
            //Use the first primary key
            column = (String)listOfPrimaryKeyColumns.get( 0 );
        }

        //select *  from legacy_ctr_claim where  rownum < 9  and  claim_id  in ( SELECT claim_id FROM legacy_ctr_claim   group  by claim_id ) minus
        //select *  from legacy_ctr_claim where  rownum < 7  and  claim_id  in ( SELECT claim_id FROM legacy_ctr_claim   group  by claim_id )

        //Set the table data
        String sql = "select * ";
        /*for(int i=0; i<listOfColumnsInATable.size(); i++)
          {
              ColumnInfo columnInfo = (ColumnInfo)listOfColumnsInATable.get(i);
              sql = sql + " " + columnInfo.getColumnName();
              if( (i+1) < listOfColumnsInATable.size())
              {
                  sql = sql + ", ";
              }
          }*/

        //sql = sql + " from " + tableName;
        sql = sql + " from " + schemaName + "." + tableName;

        //Add the clause which allows us to restrict the number of rows
        sql = sql + " where rownum <= " + (numberOfRowsToReturn.intValue() + offset.intValue()) + " and " + column + " in " +
            " ( select " + column + " from " + schemaName + "." + tableName + " group by " + column + " ) " +
            " minus " +
            " select * from " + schemaName + "." + tableName + " where rownum < " + (offset.intValue()+1) + " and " + column + " in " +
            " ( select " + column + " from " + schemaName + "." + tableName + " group by " + column + " ) ";

        Log.getInstance().debugMessage("SQL is: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        ResultSet rs = null;
        DBTable dbTable = null;
        List rows = new ArrayList();
        try
        {
            rs = this.getStatement().executeQuery( sql );

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
                    
                    //if it is a date, get the date as a timestamp as date sometimes does not have hours and minutes data
                    if( (o != null) && (o instanceof Date) )
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
                dbTable = new DBTable(schemaName, tableName, rows, offset, numberOfRowsToReturn, listOfColumnsInATable);
            }
            else
            {
                dbTable = new DBTable(schemaName, tableName, rows, offset, numberOfRowsToReturn, rowCount);
            }

            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + rowCount + " rows in " + schemaName + "." + tableName, this.getClass().getName());

        return dbTable;
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
        Log.getInstance().infoMessage("Listing columns in " + schemaName + "." + tableName + "...", this.getClass().getName());

        //Build the list of column infos
        List listOfColumns = new ArrayList();

        //Get the list of primary key column names
        List listOfPrimaryKeyColumnNames = getPrimaryKeyColumnNames(schemaName, tableName);

        ResultSet rs = null;
        try
        {
            String sql = "select * from " + schemaName + "." + tableName + " where rownum < 1";
            //String sql = "select * from " + tableName;

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            rs = this.getStatement().executeQuery( sql );
            ResultSetMetaData rsmd = rs.getMetaData();

            int numberOfColumns = rsmd.getColumnCount();
            for(int i=1; i<numberOfColumns+1; i++)
            {
                String columnName = rsmd.getColumnName( i );
                String equivalentJavaClass = rsmd.getColumnClassName( i );
                String columnTypeName = rsmd.getColumnTypeName(i);
                Integer columnDisplaysize = new Integer( rsmd.getColumnDisplaySize( i ) );

                //Decide the nullable nature
                int nullable = rsmd.isNullable(i);
                String nullableNature = ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN;
                if(nullable == ResultSetMetaData.columnNullable)
                {
                    nullableNature = ColumnInfo.COLUMN_NULLABLE;
                }

                if(nullable == ResultSetMetaData.columnNoNulls)
                {
                    nullableNature = ColumnInfo.COLUMN_NOT_NULLABLE;
                }

                Boolean isAutoIncrement = new Boolean( rsmd.isAutoIncrement( i ));

                //Check if this column is primary key column
                boolean isPrimaryKeyColumn = listOfPrimaryKeyColumnNames.contains( columnName );
                Boolean isPrimaryKeyColumnBoolean = new Boolean( isPrimaryKeyColumn );

                //Check if this column is editable/writable
                //boolean isEditable = rsmd.isDefinitelyWritable( i );
                //Boolean isEditableBoolean = new Boolean( isEditable );

                int columnType = rsmd.getColumnType( i );

                ColumnInfo columnInfo = new ColumnInfo(columnName, columnTypeName, equivalentJavaClass, columnDisplaysize, nullableNature, isAutoIncrement, isPrimaryKeyColumnBoolean, Boolean.TRUE, new Integer( columnType ));
                listOfColumns.add( columnInfo );
            }
            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + listOfColumns.size() + " columns in " + schemaName + "." + tableName, this.getClass().getName());

        return listOfColumns;
    }

    /**
     * Lists the sequence in a database.
     * @return - results as a dbtable
     * @throws DBEngineException
     */
    public DBTable listSequences()
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Listing sequences in Oracle Database...", this.getClass().getName());

        //Set the table data
        String sql = "select sequence_name, min_value, max_value, increment_by, cycle_flag, last_number from user_sequences";

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        DBTable dbTable = null;
        List rows = new ArrayList();
        ResultSet rs = null;
        try
        {
            rs = this.getStatement().executeQuery( sql );

            //Build the ColumnInfo objects
            ColumnInfo columnInfoForSequenceName = new ColumnInfo("Sequence name", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForMinValue = new ColumnInfo("Min value", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForMaxValue = new ColumnInfo("Max value", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForIncrementBy = new ColumnInfo("Increment by", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForCycleFlag = new ColumnInfo("Cycle", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForLastNumber = new ColumnInfo("Last number", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);

            //Get the data from the result set and build a DBTable
            while( rs.next() )
            {
                String sequenceName = rs.getString("sequence_name");
                int minValue = rs.getInt("min_value");
                double maxValue = rs.getDouble("max_value");
                int incrementBy = rs.getInt("increment_by");
                String cycle = rs.getString("cycle_flag");
                double lastNumber = rs.getDouble("last_number");

                DBTableCell dbTableCellForSequenceName = new DBTableCell(columnInfoForSequenceName, sequenceName, Boolean.FALSE);
                DBTableCell dbTableCellForMinValue = new DBTableCell(columnInfoForMinValue, new Integer(minValue), Boolean.FALSE);
                DBTableCell dbTableCellForMaxValue = new DBTableCell(columnInfoForMaxValue, new Double(maxValue), Boolean.FALSE);
                DBTableCell dbTableCellForIncrementBy = new DBTableCell(columnInfoForIncrementBy, new Integer(incrementBy), Boolean.FALSE);
                DBTableCell dbTableCellForCycleFlag = new DBTableCell(columnInfoForCycleFlag, new Boolean(cycle), Boolean.FALSE);
                DBTableCell dbTableCellForLastNumber = new DBTableCell(columnInfoForLastNumber, new Double(lastNumber), Boolean.FALSE);

                List listOfRowData = new ArrayList();
                listOfRowData.add( dbTableCellForSequenceName );
                listOfRowData.add( dbTableCellForMinValue );
                listOfRowData.add( dbTableCellForMaxValue );
                listOfRowData.add( dbTableCellForIncrementBy );
                listOfRowData.add( dbTableCellForCycleFlag );
                listOfRowData.add( dbTableCellForLastNumber );

                DBRow dbRow = new DBRow( listOfRowData );
                rows.add( dbRow );
            }

            dbTable = new DBTable(null, null, rows, null, null, new Integer(0));

            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + rows.size() + " sequences in Oracle database", this.getClass().getName());

        return dbTable;
    }
    
	/**
	 * Lists the indexes
	 * @return
	 * @throws DBEngineException
	 */
    public DBTable listIndexes()
    	throws DBEngineException
    {
        Log.getInstance().infoMessage("Listing indexes in Oracle Database...", this.getClass().getName());

        //Set the table data
        String sql = "select index_name, table_name, uniqueness, table_owner from user_indexes";

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        DBTable dbTable = null;
        List rows = new ArrayList();
        ResultSet rs = null;
        try
        {
            rs = this.getStatement().executeQuery( sql );

            //Build the ColumnInfo objects
            ColumnInfo columnInfoForIndexName = new ColumnInfo("Index name", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForTableName = new ColumnInfo("Table name", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForUniqueness = new ColumnInfo("Uniqueness", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForTableOwner = new ColumnInfo("Table owner", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);

            //Get the data from the result set and build a DBTable
            while( rs.next() )
            {
                String indexName = rs.getString("index_name");
                String tableName = rs.getString("table_name");
                String uniqueness = rs.getString("uniqueness");
                String tableOwner = rs.getString("table_owner");

                DBTableCell dbTableCellForIndexName = new DBTableCell(columnInfoForIndexName, indexName, Boolean.FALSE);
                DBTableCell dbTableCellForTableName = new DBTableCell(columnInfoForTableName, tableName, Boolean.FALSE);
                DBTableCell dbTableCellForUniqueness = new DBTableCell(columnInfoForUniqueness, uniqueness, Boolean.FALSE);
                DBTableCell dbTableCellForTableOwner = new DBTableCell(columnInfoForTableOwner, tableOwner, Boolean.FALSE);

                List listOfRowData = new ArrayList();
                listOfRowData.add( dbTableCellForIndexName );
                listOfRowData.add( dbTableCellForTableName );
                listOfRowData.add( dbTableCellForUniqueness );
                listOfRowData.add( dbTableCellForTableOwner );

                DBRow dbRow = new DBRow( listOfRowData );
                rows.add( dbRow );
            }

            dbTable = new DBTable(null, null, rows, null, null, new Integer(0));

            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + rows.size() + " indexes in Oracle database", this.getClass().getName());

        return dbTable;    	
    }
    
	/**
	 * Lists the constraints
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable listConstraints()
		throws DBEngineException
	{
        Log.getInstance().infoMessage("Listing constraints in Oracle Database...", this.getClass().getName());

        //Set the table data
        String sql = "select constraint_name, table_name, search_condition, status, owner from user_constraints";

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        DBTable dbTable = null;
        List rows = new ArrayList();
        ResultSet rs = null;
        try
        {
            rs = this.getStatement().executeQuery( sql );

            //Build the ColumnInfo objects
            ColumnInfo columnInfoForConstraintName = new ColumnInfo("Constraint name", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForTableName = new ColumnInfo("Table name", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForSearchCondition = new ColumnInfo("Search condition", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForStatus = new ColumnInfo("Status", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);
            ColumnInfo columnInfoForOwner = new ColumnInfo("Owner", null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);

            //Get the data from the result set and build a DBTable
            while( rs.next() )
            {
                String constraintName = rs.getString("constraint_name");
                String tableName = rs.getString("table_name");
                String searchCondition = rs.getString("search_condition");
                String status = rs.getString("status");
                String owner = rs.getString("owner");

                DBTableCell dbTableCellForConstraintName = new DBTableCell(columnInfoForConstraintName, constraintName, Boolean.FALSE);
                DBTableCell dbTableCellForTableName = new DBTableCell(columnInfoForTableName, tableName, Boolean.FALSE);
                DBTableCell dbTableCellForSearchCondition = new DBTableCell(columnInfoForSearchCondition, searchCondition, Boolean.FALSE);
                DBTableCell dbTableCellForStatus = new DBTableCell(columnInfoForStatus, status, Boolean.FALSE);
                DBTableCell dbTableCellForOwner = new DBTableCell(columnInfoForOwner, owner, Boolean.FALSE);

                List listOfRowData = new ArrayList();
                listOfRowData.add( dbTableCellForConstraintName );
                listOfRowData.add( dbTableCellForTableName );
                listOfRowData.add( dbTableCellForSearchCondition );
                listOfRowData.add( dbTableCellForStatus );
                listOfRowData.add( dbTableCellForOwner );

                DBRow dbRow = new DBRow( listOfRowData );
                rows.add( dbRow );
            }

            dbTable = new DBTable(null, null, rows, null, null, new Integer(0));

            rs.close();
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Found " + rows.size() + " constraints in Oracle database", this.getClass().getName());

        return dbTable;    	
		
	}
}
