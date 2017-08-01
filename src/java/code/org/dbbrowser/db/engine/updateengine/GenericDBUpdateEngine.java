package org.dbbrowser.db.engine.updateengine;

import infrastructure.logging.Log;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.SQLLog;

/**
 * Updates the database
 */
public class GenericDBUpdateEngine implements DBUpdateEngine
{
    private Statement statement = null;

    /**
     * Constructer
     * @param statement
     */
    public GenericDBUpdateEngine(Statement statement)
    {
        this.statement = statement;
    }

    /**
     * Returns the DB statement used to update the database
     * @return
     */
    public Statement getStatement()
    {
        return this.statement;
    }
    
    /**
     * Update the view definition for the view
     * @param view
     * @throws DBEngineException
     */
    public void updateViewDefinition(View view)
    	throws DBEngineException
    {
        Log.getInstance().infoMessage("Updating view definition for view: " + view.getViewName() + "...", this.getClass().getName());

        String sql = "";
        
        if(view.getSchemaName() == null)
        {
        	sql = "create or replace view " + view.getViewName() + " as " + view.getViewDefinition();
        }
        else
        {
        	sql = "create or replace view " + view.getSchemaName() + "." + view.getViewName() + " as " + view.getViewDefinition();        	
        }

        Log.getInstance().debugMessage("SQL for updating view is: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        try
        {
            this.getStatement().execute( sql );
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Finished update to view definition", this.getClass().getName());    	
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
        Log.getInstance().infoMessage("Adding new column into " + tableName + "...", this.getClass().getName());

        //Decide the nullable nature
        String nullableNature = "";
        if( ColumnInfo.COLUMN_NOT_NULLABLE.equals(columnInfo.getNullableNature()) )
        {
            nullableNature = " not null ";
        }

        String sql = "";
        //If it is a varchar, then build the SQL
        if( ColumnInfo.COLUMN_TYPE_VARCHAR.equals(columnInfo.getColumnTypeName()) )
        {
            sql = "alter table " + tableName + " add " + columnInfo.getColumnName() +
                " " + columnInfo.getColumnTypeName() + "(" + columnInfo.getColumnDisplaySize().intValue() + ")" +
                nullableNature;
        }
        else if( ColumnInfo.COLUMN_TYPE_NUMBER.equals(columnInfo.getColumnTypeName()) )
        {
            sql = "alter table " + tableName + " add " + columnInfo.getColumnName() +
                " " + columnInfo.getColumnTypeName() + "(" + columnInfo.getColumnDisplaySize().intValue() + ")" +
                nullableNature;
        }
        else if( ColumnInfo.COLUMN_TYPE_DATE.equals(columnInfo.getColumnTypeName()) || ColumnInfo.COLUMN_TYPE_DATE_TIME.equals(columnInfo.getColumnTypeName()))
        {
            sql = "alter table " + tableName + " add " + columnInfo.getColumnName() +
                " " + columnInfo.getColumnTypeName() + nullableNature;
        }
        else
        {
            throw new UnsupportedOperationException("*** Invalid data type: " + columnInfo.getColumnTypeName() + " ***");
        }

        Log.getInstance().debugMessage("SQL for adding new column is: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        try
        {
            this.getStatement().execute( sql );
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Add new complete complete in " + tableName, this.getClass().getName());
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
        Log.getInstance().infoMessage("Dropping column " + columnInfo.getColumnName() + " from " + tableName + "...", this.getClass().getName());

        String sql = "alter table " + tableName + " drop column " + columnInfo.getColumnName();

        Log.getInstance().debugMessage("SQL for dropping column is: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        try
        {
            this.getStatement().execute( sql );
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Column " + columnInfo.getColumnName() + " dropped from " + tableName, this.getClass().getName());
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
        Log.getInstance().infoMessage("Adding new record into " + tableName + "...", this.getClass().getName());

        //Get the list of cells which need to be added
        List listOfDBTableCellsWhichNeedToBeAdded = dbRow.getListOFDBTableCells();

        //Add the row only if there are some values
        if( listOfDBTableCellsWhichNeedToBeAdded != null && (!listOfDBTableCellsWhichNeedToBeAdded.isEmpty()) )
        {
            //Build the sql using the values which have changed
            StringBuffer buffer = new StringBuffer();
            buffer.append( "insert into " + tableName + " ( " );
            Iterator i = listOfDBTableCellsWhichNeedToBeAdded.iterator();
            while(i.hasNext())
            {
                DBTableCell dbTableCell = (DBTableCell)i.next();

                //Add the column name to the list
                String columnName = dbTableCell.getColumnInfo().getColumnName();

                //Add to buffer
                buffer.append( columnName );

                //if there are more, add the comma
                if( i.hasNext() )
                {
                    buffer.append( ", " );
                }
            }

            buffer.append( ")values(" );

            i = listOfDBTableCellsWhichNeedToBeAdded.iterator();
            while(i.hasNext())
            {
                DBTableCell dbTableCell = (DBTableCell)i.next();

                //Format the value for update
                String formattedValue = this.formatValue( dbTableCell.getValue(), dbTableCell.getColumnInfo().getColumnType().intValue() );

                //Build the sql
                buffer.append( formattedValue );

                if(i.hasNext())
                {
                    buffer.append( ", " );
                }
            }

            buffer.append( ")" );

            String sql = buffer.toString();
            Log.getInstance().debugMessage("SQL for adding new row is: " + sql, this.getClass().getName());

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            try
            {
                this.getStatement().execute( sql );
            }
            catch(SQLException exc)
            {
                throw new DBEngineException(exc.getMessage());
            }
        }

        Log.getInstance().infoMessage("Add new row complete in " + tableName, this.getClass().getName());

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
        Log.getInstance().infoMessage("Deleting record from " + tableName + "...", this.getClass().getName());

        //Get the list of primary key columns
         List listOfPrimaryKeycolumns = dbRow.getListOfPrimaryKeyDBTableCells();

        //If there are no primary key columns, throw an exception
        if( listOfPrimaryKeycolumns.isEmpty() )
        {
            throw new DBEngineException("Cannot delete rows from table which has no primary key columns");
        }

        //Build SQL statement
        String sql = "delete from " + tableName + " where ";
        Iterator i = listOfPrimaryKeycolumns.iterator();
        while(i.hasNext())
        {
            DBTableCell dbTableCell = (DBTableCell)i.next();

            //If it is a string, put the ' commas around the value
            if( dbTableCell.getColumnInfo().getColumnType().intValue() == Types.VARCHAR )
            {
                sql = sql + dbTableCell.getColumnInfo().getColumnName() + " = '" + dbTableCell.getFormattedValue() + "'";
            }
            else
            {
                sql = sql + dbTableCell.getColumnInfo().getColumnName() + " = " + dbTableCell.getFormattedValue();
            }

            if(i.hasNext())
            {
                sql = sql + " and ";
            }
        }

        Log.getInstance().debugMessage("SQL for delete is: " + sql, this.getClass().getName());

        //Log the SQL
        SQLLog.getInstance().logSQLStatement( sql );

        try
        {
            this.getStatement().executeUpdate( sql );
        }
        catch(SQLException exc)
        {
            throw new DBEngineException(exc.getMessage());
        }

        Log.getInstance().infoMessage("Deleted one record from " + tableName, this.getClass().getName());
    }

    /**
     * Update the database using the data from the DBRow
     * @param schemaName
     * @param tableName
     * @param dbRow
     */
    public void update(String schemaName, String tableName, DBRow dbRow)
        throws DBEngineException
    {
        Log.getInstance().infoMessage("Updating record from " + tableName + "...", this.getClass().getName());

        //Get the list of primary key columns
        List listOfPrimaryKeyDBTableCells = dbRow.getListOfPrimaryKeyDBTableCells();

        //If there are no primary keys, raise and exception
        if( listOfPrimaryKeyDBTableCells == null || listOfPrimaryKeyDBTableCells.isEmpty() )
        {
            throw new DBEngineException("Cannot update a table which does not have a unique primary key column");
        }

        //Get the list of cells which have changed
        List listOfDBTableCellsWhichHaveChanged = dbRow.getListOfChangedNonPrimaryKeyCells();

        //If there are cells which have changed, then update them
        if( listOfDBTableCellsWhichHaveChanged != null && (!listOfDBTableCellsWhichHaveChanged.isEmpty()) )
        {
            //Build the sql using the values which have changed
            StringBuffer buffer = new StringBuffer();
            buffer.append( "Update " + tableName + " set " );
            Iterator i = listOfDBTableCellsWhichHaveChanged.iterator();
            while(i.hasNext())
            {
                DBTableCell dbTableCell = (DBTableCell)i.next();

                //Format the value for update
                String formattedValue = this.formatValue( dbTableCell.getValue(), dbTableCell.getColumnInfo().getColumnType().intValue() );

                //Build the sql
                buffer.append( dbTableCell.getColumnInfo().getColumnName() + "=" + formattedValue );

                if(i.hasNext())
                {
                    buffer.append( ", " );
                }
            }

            //Build the sql using the primary key columns
            buffer.append( " where " );
            i = listOfPrimaryKeyDBTableCells.iterator();
            while(i.hasNext())
            {
                DBTableCell dbTableCell = (DBTableCell)i.next();

                //Format the value for update
                String formattedValue = this.formatValue( dbTableCell.getValue(), dbTableCell.getColumnInfo().getColumnType().intValue() );

                //Build the sql
                buffer.append( dbTableCell.getColumnInfo().getColumnName() + "=" + formattedValue );

                if(i.hasNext())
                {
                    buffer.append( " and " );
                }
            }

            String sql = buffer.toString();
            Log.getInstance().debugMessage("SQL for update is: " + sql, this.getClass().getName());

            //Log the SQL
            SQLLog.getInstance().logSQLStatement( sql );

            try
            {
                this.getStatement().executeUpdate( sql );
            }
            catch(SQLException exc)
            {
                throw new DBEngineException(exc.getMessage());
            }
        }

        Log.getInstance().infoMessage("Update complete in " + tableName, this.getClass().getName());
    }

    protected String formatValue(Object value, int dataType) throws DBEngineException
    {
        String formattedValue = "";

        if( dataType == Types.VARCHAR )
        {
            formattedValue = "'" + value.toString() + "'";
        }
        else if( dataType == Types.INTEGER )
        {
            formattedValue = value.toString();
            try
            {
                int valueToUpdate = Integer.parseInt(formattedValue);
                formattedValue = valueToUpdate + "";
            }
            catch(NumberFormatException exc)
            {
                throw new DBEngineException( exc.getMessage() );
            }
        }
        else if( dataType == Types.NUMERIC )
        {
            formattedValue = value.toString();
            try
            {
                BigDecimal bd = new BigDecimal(formattedValue);
                formattedValue = bd.toString() + "";
            }
            catch(NumberFormatException exc)
            {
                throw new DBEngineException( exc.getMessage() );
            }
        }
        else if( dataType == Types.DATE || dataType == Types.TIMESTAMP )
        {
            formattedValue = value.toString();
            try
            {
                DateFormat dateFormatForDisplay = DBTableCell.getDateFormat();
                Date date = dateFormatForDisplay.parse( value.toString() );
                DateFormat dateFormatForUpdate = new SimpleDateFormat( DBTableCell.DATE_FORMAT_STRING );
                formattedValue = "'" + dateFormatForUpdate.format(date) + "'";
            }
            catch(ParseException exc)
            {
                throw new DBEngineException( exc.getMessage() );
            }
		} else if (dataType == Types.BIGINT) {
			try {
				long val = Long.parseLong(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
	
		} else if (dataType == Types.BIT) {
			int val = 0;
			try {
				val = Integer.parseInt(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				if (value.toString().equals(true + "")) {
					val = 1;
				} else if (value.toString().equals(false + "")) {
					val = 0;
				} else {
					throw new DBEngineException(exc.getMessage());
				}
			}
	
		} else if (dataType == Types.CHAR) {
			formattedValue = "'" + value.toString() + "'";
	
		} else if (dataType == Types.DECIMAL) {
			try {
				BigDecimal val = new BigDecimal(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
	
		} else if (dataType == Types.FLOAT) {
			try {
				double val = Double.parseDouble(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
	
		} else if (dataType == Types.CLOB) {
	
			formattedValue = "'" + value.toString() + "'";
	
		} else if (dataType == Types.REAL) {
			try {
				float val = Float.parseFloat(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
	
		} else if (dataType == Types.SMALLINT) {
			try {
				int val = Integer.parseInt(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
	
		} else if (dataType == Types.TINYINT) {
			try {
				int val = Integer.parseInt(value.toString());
				formattedValue = val + "";
			} catch (NumberFormatException exc) {
				
				throw new DBEngineException(exc.getMessage());
			}
		}        
        else
        {
            throw new DBEngineException("Cannot format " + value.toString() + " into " + dataType);
        }
        return formattedValue;
    }
}
