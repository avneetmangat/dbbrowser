package org.dbbrowser.db.engine.rawsqlengine;

import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.SQLLog;

/**
 * Runs raw SQL statement entered by the user
 */
public class GenericRawSQLEngine implements DBRawSQLEngine
{
    private static final String SELECT = "SELECT";
    private static final String DESC = "DESC";
    private static final String DESCRIBE = "DESCRIBE";

    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";
    private static final String INSERT = "INSERT";
    private static final String DROP = "DROP";

    private Statement statement = null;

    /**
     * Constructer
     * @param statement
     */
    public GenericRawSQLEngine(Statement statement)
    {
        this.statement = statement;
    }

    /**
     * Returns the statement used to run the SQL statement
     * @return
     */
    public Statement getStatement()
    {
        return this.statement;
    }

    /**
     * Run the SQL statement and return the result if any.
     * @param sql
     * @return - DBTable if there is any result or null
     * @throws DBEngineException
     */
    public DBTable runRawSQL(String sql)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Running raw SQL: " + sql, this.getClass().getName());

        //Tokenize the string using the sql delimiter
        String delimiter = PropertyManager.getInstance().getProperty("dbbrowser-sql-statement-terminator");
        DBTable dbTable = null;
        StringTokenizer st = new StringTokenizer(sql, delimiter);

        //If the sql has only one token, run the sql
        if( st.countTokens() == 1 )
        {
            dbTable = runSingleSQLStatement( sql );
        }
        else
        {
            while( st.hasMoreTokens() )
            {
                String token = st.nextToken();
                runSingleSQLStatement( token );
            }
        }
        return dbTable;
    }

    /**
     * Strips the comments from an SQL statement.
     * @param sql
     * @return
     */
    public String stripComments(String sql)
    {
        Log.getInstance().infoMessage("Stripping comments from SQL: " + sql, this.getClass().getName());

        //Are there any comments
        int commentsStartPosition = sql.indexOf("/*");

        //if there are any comments
        if(commentsStartPosition != -1)
        {
            int commentsEndPosition = sql.indexOf("*/", commentsStartPosition);

            //if comments have been ended
            if( commentsEndPosition != -1 )
            {
                //Strip the comments
                String sql1 = sql.substring(0, commentsStartPosition);
                String sql2 = sql.substring(commentsEndPosition+2, sql.length());

                sql = sql1 + sql2;

                //Recursively call this method to strip all strings
                sql = stripComments( sql );
            }
        }

        return sql;
    }

    private DBTable runSingleSQLStatement(String sql)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Running SQL: " + sql, this.getClass().getName());

        DBTable dbTable = null;
        StringTokenizer st = new StringTokenizer(sql, " ");
        if( st.hasMoreTokens() )
        {
            String firstToken = st.nextToken();

            //Remove the ; token
            sql = sql.replaceAll(PropertyManager.getInstance().getProperty("dbbrowser-sql-statement-terminator"), "");

            if(SELECT.equalsIgnoreCase( firstToken ) || DESC.equalsIgnoreCase( firstToken ) || DESCRIBE.equalsIgnoreCase( firstToken ))
            {
                dbTable = runRawSelectStatement( sql );
            }
            else if(DELETE.equalsIgnoreCase( firstToken ) || UPDATE.equalsIgnoreCase( firstToken ) || INSERT.equalsIgnoreCase( firstToken ) || DROP.equalsIgnoreCase( firstToken ) )
            {
                runRawUpdateStatement( sql );
            }
            else
            {
                //Run unknown SQL statement
                dbTable = runRawUnknownSQLStatement( sql );
            }
        }
        else
        {
        	//if the sql is not a blank string, then it is an invalid sql
        	sql = sql.trim();
        	if( !"".equals(sql) )
        	{
        		throw new DBEngineException("Invalid SQL");
        	}
        }

        return dbTable;
    }


    private DBTable runRawSelectStatement(String sql)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Running select/describe statement: " + sql, this.getClass().getName());

        //Split the ';' from the sql query
        sql = sql.replaceAll(PropertyManager.getInstance().getProperty("dbbrowser-sql-statement-terminator"), "");

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        ResultSet rs = null;
        DBTable dbTable = null;
        try
        {
            rs = this.statement.executeQuery( sql );

            //Get the resultset metadata
            ResultSetMetaData rsmd = rs.getMetaData();

            //Get the data from the result set and build a DBTable
            List rows = new ArrayList();
            List listOfColumnInfos = new ArrayList();
            while( rs.next() )
            {
                //Get the data for every column
                List listOfRowData = new ArrayList();
                int columnCount = rsmd.getColumnCount();
                for(int i=0; i<columnCount; i++)
                {
                    //Build the column info object
                    String columnName = rsmd.getColumnName( i+1 );
                    String equivalentJavaClass = rsmd.getColumnClassName( i+1 );
                    String columnTypeName = rsmd.getColumnTypeName( i+1 );                          //e.g. NUMBER, VARCHAR2
                    Integer columnDisplaysize = new Integer( rsmd.getColumnDisplaySize( i+1 ) );
                    int columnType = rsmd.getColumnType( i+1 );                                     //From java.sql.Types
                    ColumnInfo columnInfo = new ColumnInfo(columnName, columnTypeName, equivalentJavaClass, columnDisplaysize, ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, new Integer( columnType ));
                    listOfColumnInfos.add( columnInfo );
                    
                    //Get the data
                    Object o = rs.getObject(i+1);

                    //if it is an oracle timestamp, get the timestamp
                    if( (o != null) && ("oracle.sql.TIMESTAMP".equals( o.getClass().getName() )) )
                    {
                        o = rs.getTimestamp(i+1);
                    }

                    DBTableCell dbTableCell = new DBTableCell(columnInfo, o, Boolean.FALSE);

                    listOfRowData.add( dbTableCell );
                }
                DBRow dbRow = new DBRow( listOfRowData );
                rows.add( dbRow );
            }

            dbTable = new DBTable(null, null, rows, null, new Integer(rows.size()), new Integer(rows.size()));
            rs.close();
        }
        catch(SQLException exc)
        {
            Log.getInstance().warnMessage("Exception while running select/describe statement: " + sql, this.getClass().getName());
            throw new DBEngineException(exc.getMessage());
        }
        return dbTable;
    }

    private void runRawUpdateStatement(String sql)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Running raw update/delete/insert/alter SQL: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        //Run the SQL
        try
        {
            this.statement.executeUpdate( sql );
        }
        catch(SQLException exc)
        {
            Log.getInstance().warnMessage("Exception while running update/delete/insert/alter SQL: " + sql, this.getClass().getName());
            throw new DBEngineException(exc.getMessage());
        }
    }

    private DBTable runRawUnknownSQLStatement(String sql)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Running raw unknown SQL: " + sql, this.getClass().getName());

        DBTable dbTable = null;

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        try
        {
            //Run the SQL
            boolean areThereResults = this.statement.execute( sql );

            //if there are any results
            if( areThereResults )
            {
                ResultSet rs = this.statement.getResultSet();

                //Get the resultset metadata
                ResultSetMetaData rsmd = rs.getMetaData();

                //Get the data from the result set and build a DBTable
                List rows = new ArrayList();
                List listOfColumnInfos = new ArrayList();
                while( rs.next() )
                {
                    //Get the data for every column
                    List listOfRowData = new ArrayList();
                    int columnCount = rsmd.getColumnCount();
                    for(int i=0; i<columnCount; i++)
                    {
                        //Build the column info object
                        String columnName = rsmd.getColumnName( i+1 );
                        String equivalentJavaClass = rsmd.getColumnClassName( i+1 );
                        String columnTypeName = rsmd.getColumnTypeName( i+1 );                          //e.g. NUMBER, VARCHAR2
                        Integer columnDisplaysize = new Integer( rsmd.getColumnDisplaySize( i+1 ) );
                        int columnType = rsmd.getColumnType( i+1 );                                     //From java.sql.Types
                        ColumnInfo columnInfo = new ColumnInfo(columnName, columnTypeName, equivalentJavaClass, columnDisplaysize, ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, new Integer( columnType ));
                        listOfColumnInfos.add( columnInfo );

                        //Get the data
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

                dbTable = new DBTable(null, null, rows, new Integer(0), new Integer(rows.size()), new Integer(rows.size()));
                rs.close();
            }
        }
        catch(SQLException exc)
        {
            Log.getInstance().warnMessage("Exception while running unknown SQL: " + sql, this.getClass().getName());
            throw new DBEngineException(exc.getMessage());
        }

        return dbTable;
    }
}
