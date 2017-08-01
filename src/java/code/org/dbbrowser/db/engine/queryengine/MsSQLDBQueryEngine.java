package org.dbbrowser.db.engine.queryengine;

import infrastructure.logging.Log;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.dbbrowser.db.engine.SQLLog;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.db.engine.model.filter.Filter;

/**
 * DBQuery Engine which gets data from a MsSQL DBMS
 */
public class MsSQLDBQueryEngine extends AbstractDBQueryEngine implements
		DBQueryEngine {
	/**
	 * Constructer
	 * 
	 * @param statement
	 */
	public MsSQLDBQueryEngine(Statement statement) {
		super(statement);
	}

	/**
	 * Returns a list of schemas in the Database.
	 * 
	 * @return - a list of Strings. It is empty if there are no table spaces for
	 *         the database.
	 * @throws DBEngineException
	 */
	public List listSchemas() throws DBEngineException {
		Log.getInstance().infoMessage("Listing database instances in MsSQL...",
				this.getClass().getName());

		List listOfDatabaseInstances = new ArrayList();
		try {
			String sql = "exec sp_databases";

			Log.getInstance().debugMessage("SQL is: " + sql,
					this.getClass().getName());

			ResultSet rs = this.getStatement().executeQuery(sql);

			while (rs.next()) {
				String tablename = rs.getString(1);
				listOfDatabaseInstances.add(tablename);
			}

			Log.getInstance().debugMessage(
					"Finished listing schemas in MsSQLDBQueryEngine",
					this.getClass().getName());
			rs.close();
		} catch (SQLException exc) {
			
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Found " + listOfDatabaseInstances.size()
						+ " database instances in MsSQL",
				this.getClass().getName());

		return listOfDatabaseInstances;
	}

	/**
	 * Returns a list of views accessible to the user. It is empty if there are
	 * no views for the user.
	 * 
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listViews() throws DBEngineException {
		return new ArrayList();
	}

	/**
	 * Returns the SQL used to create a view
	 * 
	 * @return - a String
	 * @throws DBEngineException
	 */
	public String getSQLForView(View view) throws DBEngineException 
	{
		return "";
	}

	/**
	 * Get all the data in a table
	 * 
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws DBEngineException
	 */
	public DBTable getAllDataInATable(String schemaName, String tableName,
			Integer offset, Integer numberOfRowsToReturn)
			throws DBEngineException {
		Log.getInstance().infoMessage(
				"Listing all data in " + schemaName + "." + tableName + "...",
				this.getClass().getName());

		// Get the number of rows in the table
		Integer rowCount = this.getRowCount(schemaName, tableName);

		List listOfColumnsInATable = listColumnsInATable(schemaName, tableName);
		DBTable dbTable = null;
		List rows = new ArrayList();
		try {

			// Change the schema
			String sql = "use " + schemaName;
			// this.getStatement().execute(sql);

			// Execute the statement to get the data in the table
			sql = "select * from " + tableName;

			Log.getInstance().debugMessage("SQL is: " + sql,
					this.getClass().getName());

			ResultSet rs = this.getStatement().executeQuery(sql);

			// Get the data from the result set and build a DBTable
			while (rs.next()) {
				// Get the data for every column
				List listOfRowData = new ArrayList();
				for (int i = 0; i < listOfColumnsInATable.size(); i++) {
					ColumnInfo columnInfo = (ColumnInfo) listOfColumnsInATable
							.get(i);
					String columnName = columnInfo.getColumnName();
					Object o = rs.getObject(columnName);
					DBTableCell dbTableCell = null;

					dbTableCell = new DBTableCell(columnInfo, o, Boolean.FALSE);

					listOfRowData.add(dbTableCell);
				}
				DBRow dbRow = new DBRow(listOfRowData);
				rows.add(dbRow);
			}

			if (rows.isEmpty()) {
				dbTable = new DBTable(schemaName, tableName, rows, offset,
						numberOfRowsToReturn, listOfColumnsInATable);
			} else {
				dbTable = new DBTable(schemaName, tableName, rows, offset,
						numberOfRowsToReturn, numberOfRowsToReturn);
			}

			Log.getInstance().debugMessage(
					"Returning all data for " + schemaName + "." + tableName
							+ " in MsSQLDBQueryEngine",
					this.getClass().getName());

			rs.close();
		} catch (SQLException exc) {
			
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Found " + rows.size() + " rows in " + schemaName + "."
						+ tableName, this.getClass().getName());

		return dbTable;
	}

	/**
	 * Returns a list of tables in the schema
	 * 
	 * @param schemaName
	 * @return - a list of Strings
	 * @throws DBEngineException
	 */
	public List listTablesInSchema(String databaseInstanceName)
			throws DBEngineException {
		Log.getInstance().infoMessage(
				"Listing tables in MsSQL database instance "
						+ databaseInstanceName + "...",
				this.getClass().getName());

		List listOfTables = new ArrayList();
		try {
			// Change the tablespace
			String sql = "use " + databaseInstanceName;
			// System.out.println(sql);
			this.getStatement().execute(sql);

			// Select the list of tables
			sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";

			Log.getInstance().debugMessage("SQL is: " + sql,
					this.getClass().getName());

			ResultSet rs = this.getStatement().executeQuery(sql);
			while (rs.next()) {
				String tablename = rs.getString(1);
				listOfTables.add(tablename);
			}

			Log.getInstance().debugMessage(
					"Finished listing all schemas for " + databaseInstanceName
							+ " in MsSQLDBQueryEngine",
					this.getClass().getName());

			rs.close();
		} catch (SQLException exc) {
			
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Found " + listOfTables.size()
						+ " tables in MsSQL database instance "
						+ databaseInstanceName, this.getClass().getName());

		return listOfTables;
	}

	/**
	 * Returns a list of columns in the table
	 * 
	 * @param databaseInstanceName
	 * @param tableName
	 * @return - a list of ColumnInfo objects
	 * @throws DBEngineException
	 */
	public List listColumnsInATable(String databaseInstanceName,
			String tableName) throws DBEngineException {
		Log.getInstance().infoMessage(
				"Listing columns in table " + databaseInstanceName + "."
						+ tableName + "...", this.getClass().getName());

		// Get the list of primary key column names
		List primaryKeyColumnNames = this.getPrimaryKeyColumnNames(
				databaseInstanceName, tableName);

		List listOfColumns = new ArrayList();
		try {
			String sql = "select * from " + tableName;

			Log.getInstance().debugMessage("SQL is: " + sql,
					this.getClass().getName());

			ResultSet rs = this.getStatement().executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				// Get the values from the ResultSetMetaData
				String columnName = rsmd.getColumnName(i);
				String equivalentJavaClass = rsmd.getColumnClassName(i);
				String columnTypeName = rsmd.getColumnTypeName(i);
				Integer columnDisplaysize = new Integer(rsmd
						.getColumnDisplaySize(i));
				int nullable = rsmd.isNullable(i);

				// Check if this column is a primary key column
				boolean isPrimaryKeyColumn = primaryKeyColumnNames
						.contains(columnName);
				Boolean isPrimaryKeyColumnBoolean = new Boolean(
						isPrimaryKeyColumn);

				// Build the column info
				String nullableNature = ColumnInfo.COLUMN_NULLABLE_NATURE_UNKNOWN;
				if (nullable == ResultSetMetaData.columnNullable) {
					nullableNature = ColumnInfo.COLUMN_NULLABLE;
				}

				if (nullable == ResultSetMetaData.columnNoNulls) {
					nullableNature = ColumnInfo.COLUMN_NOT_NULLABLE;
				}

				// Check if this column is editable/writable
				// boolean isEditable = rsmd.isDefinitelyWritable( i );
				// Boolean isEditableBoolean = new Boolean( isEditable );

				int columnType = rsmd.getColumnType(i);

				Boolean isAutoIncrement = new Boolean(rsmd.isAutoIncrement(i));

//				System.out.println("columntypename " + columnTypeName
//						+ " java class " + equivalentJavaClass + " "+columnType);

				ColumnInfo columnInfo = new ColumnInfo(columnName,
						columnTypeName, equivalentJavaClass, columnDisplaysize,
						nullableNature, isAutoIncrement,
						isPrimaryKeyColumnBoolean, Boolean.TRUE, new Integer(
								columnType));
				listOfColumns.add(columnInfo);
			}

			Log.getInstance().debugMessage(
					"Finished listing all columns in for "
							+ databaseInstanceName + "." + tableName
							+ " in MsSQLDBQueryEngine",
					this.getClass().getName());

			rs.close();
		} catch (SQLException exc) {
			
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Found " + listOfColumns.size() + " columns in table "
						+ databaseInstanceName + "." + tableName,
				this.getClass().getName());

		return listOfColumns;
	}

	
	public DBTable getFilteredDataInATable(String schemaName, String tableName,
			Filter filter) throws DBEngineException {
		Log.getInstance().infoMessage(
				"Getting filtered data for " + schemaName + "." + tableName
						+ "...", this.getClass().getName());
		Log.getInstance().infoMessage("Filter: " + filter.getSQLString(),
				this.getClass().getName());
		DBTable dbTable = null;
		List rows = new ArrayList();

		// Get the columns in the table
		List listOfColumnsInATable = listColumnsInATable(schemaName, tableName);

		try {
			// String sql = "select count(*) from " + tableName;
			String sql = "select * from " + tableName + " where "
					+ filter.getSQLString();

			// Log the SQL
			SQLLog.getInstance().logSQLStatement(sql);
			Log.getInstance().infoMessage(
					"SQL to get filtered data is: " + sql,
					this.getClass().getName());

			// Run the SQL
			System.out.println(sql);
			ResultSet rs = getStatement().executeQuery(sql);

			// Get the data from the result set and build a DBTable
			while (rs.next()) {
				// Get the data for every column
				List listOfRowData = new ArrayList();
				for (int i = 0; i < listOfColumnsInATable.size(); i++) {
					ColumnInfo columnInfo = (ColumnInfo) listOfColumnsInATable
							.get(i);
					String columnName = columnInfo.getColumnName();
					Object o = rs.getObject(columnName);

//					// if it is an oracle timestamp, get the timestamp
//					if ((o != null)
//							&& ((o.getClass().getName()).endsWith("TIMESTAMP"))) {
//						o = rs.getTimestamp(columnName);
//					} else {
//						if ((o != null)
//								&& ((o.getClass().getName())
//										.endsWith("TimeStamp"))) {
//							o = rs.getTimestamp(columnName);
//						} else {
//							if ((o != null)
//									&& ((o.getClass().getName())
//											.endsWith("Timestamp"))) {
//								o = rs.getTimestamp(columnName);
//							}
//						}
//					}

					DBTableCell dbTableCell = new DBTableCell(columnInfo, o,
							Boolean.FALSE);

					listOfRowData.add(dbTableCell);
				}
				DBRow dbRow = new DBRow(listOfRowData);
				rows.add(dbRow);
			}

			if (rows.isEmpty()) {
				dbTable = new DBTable(schemaName, tableName, rows, new Integer(
						0), new Integer(rows.size()), listOfColumnsInATable);
			} else {
				dbTable = new DBTable(schemaName, tableName, rows, new Integer(
						0), new Integer(rows.size()), new Integer(rows.size()));
			}
			rs.close();
		} catch (SQLException exc) {
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Row count for " + schemaName + "." + tableName + " is "
						+ rows.size(), this.getClass().getName());
		return dbTable;
	}

	
	public List getPrimaryKeyColumnNames(String schemaName, String tableName)
			throws DBEngineException {

		Log.getInstance().infoMessage("Listing primary key columns...",
				this.getClass().getName());

		List listOfPrimaryKeyColumnNames = new ArrayList();
		try {
			DatabaseMetaData databaseMetaData = this.getStatement()
					.getConnection().getMetaData();
			ResultSet rs = databaseMetaData.getPrimaryKeys(null, null,
					tableName);

			while (rs.next()) {
				String primaryKeyColumnNameForTable = rs
						.getString("COLUMN_NAME");
				String tableNameOfPrimaryKey = rs.getString("TABLE_NAME");
				String schemaNameForTable = rs.getString("TABLE_CAT");

				// if the primary key is for this table, add it
				if (tableName.equals(tableNameOfPrimaryKey)
						&& schemaName.equals(schemaNameForTable)) {
					listOfPrimaryKeyColumnNames
							.add(primaryKeyColumnNameForTable);
				}
			}
			rs.close();
		} catch (SQLException exc) {
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Found " + listOfPrimaryKeyColumnNames.size()
						+ " primary key columns", this.getClass().getName());

		return listOfPrimaryKeyColumnNames;
	}

	
	public Integer getRowCount(String schemaName, String tableName)
			throws DBEngineException {

		Log.getInstance()
				.infoMessage(
						"Getting row count for " + schemaName + "." + tableName
								+ "...", this.getClass().getName());

		Integer rowCount = null;
		try {
			// String sql = "select count(*) from " + tableName;
			String sql = "select count(*) from " + tableName;

			// Log the SQL
			SQLLog.getInstance().logSQLStatement(sql);

			// Run the SQL
			ResultSet rs = getStatement().executeQuery(sql);
			if (rs.next()) {
				rowCount = new Integer(rs.getInt(1));
			}
			rs.close();
		} catch (SQLException exc) {
			
			
			throw new DBEngineException(exc.getMessage());
		}

		Log.getInstance().infoMessage(
				"Row count for " + schemaName + "." + tableName + " is "
						+ rowCount, this.getClass().getName());
		return rowCount;
	}
	
	/**
	 * Lists the indexes
	 * @return
	 * @throws DBEngineException
	 */
    public DBTable listIndexes()
    	throws DBEngineException
    {
    	throw new UnsupportedOperationException();
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
