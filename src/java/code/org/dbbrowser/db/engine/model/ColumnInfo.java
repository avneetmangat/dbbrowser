package org.dbbrowser.db.engine.model;

/**
 * This class holds information about a DB column
 * @author amangat
 *
 */
public class ColumnInfo
{
	public static final String COLUMN_NULLABLE = "NULLABLE";
	public static final String COLUMN_NOT_NULLABLE = "NOT_NULLABLE";
	public static final String COLUMN_NULLABLE_NATURE_UNKNOWN = "UNKNOWN";	
	
	public static final String COLUMN_TYPE_VARCHAR = "VARCHAR";				//String
	public static final String COLUMN_TYPE_VARCHAR2 = "VARCHAR2";			//String
	public static final String COLUMN_TYPE_NUMBER = "NUMBER";				//Integer
	public static final String COLUMN_TYPE_BIG_DECIMAL = "BIG_DECIMAL";		//BigDecimal
	public static final String COLUMN_TYPE_DATE = "DATE";					//Date
	public static final String COLUMN_TYPE_DATE_TIME = "DATETIME";					//Date
	public static final String COLUMN_TYPE_TIMESTAMP = "TIMESTAMP";			//Date
	public static final String COLUMN_TYPE_BIG_INT = "BIG_INT";				//Long
	public static final String COLUMN_TYPE_CHAR = "CHAR";					//String
	public static final String COLUMN_TYPE_DECIMAL = "DECIMAL";				//BigDecimal
	public static final String COLUMN_TYPE_FLOAT = "FLOAT";					//Float
	public static final String COLUMN_TYPE_REAL = "REAL";					//Double
	public static final String COLUMN_TYPE_SMALL_INT = "SMALL_INT";			//Integer
	public static final String COLUMN_TYPE_TINY_INT = "TINY_INT";			//Integer

	private String columnName = null;
	private String columnTypeName = null;
	private String equivalentJavaClass = null;
	private Integer columnDisplaySize = null;
	private String nullableNature = null;
	private Boolean isAutoIncrement = null;
	private Boolean isPrimaryKeyColumn = null;
	private Boolean editable = null;
	private Integer columnType = null;
	
	/**
	 * Constructer
	 * @param columnName
	 * @param columnTypeName
	 * @param equivalentJavaClass
	 * @param columnDisplaySize
	 * @param nullableNature
	 * @param isPrimaryKeyColumn
	 * @param editable
	 * @param columnType
	 */
	public ColumnInfo(String columnName, String columnTypeName, String equivalentJavaClass, Integer columnDisplaySize, String nullableNature, Boolean isAutoIncrement, Boolean isPrimaryKeyColumn, Boolean editable, Integer columnType)
	{
		this.columnName = columnName;
		this.columnTypeName = columnTypeName;
		this.equivalentJavaClass = equivalentJavaClass;
		this.columnDisplaySize = columnDisplaySize;
		this.nullableNature = nullableNature;
		this.isAutoIncrement = isAutoIncrement;
		this.isPrimaryKeyColumn = isPrimaryKeyColumn;
		this.editable = editable;
		this.columnType = columnType;
	}
	
	/**
	 * Get the column name
	 * @return
	 */
	public String getColumnName()
	{
		return this.columnName;
	}
	
	/**
	 * Returns the column type name e.g. NUMBER, VARCHAR and DATE.  The types come from java.sql.Types
	 * @return
	 */
	public String getColumnTypeName()
	{
		return this.columnTypeName;
	}
	
	/**
	 * Get the equivalent java class to represent this column e.g. java.lang.Integer
	 * @return
	 */
	public String getEquivalentJavaClass()
	{
		return this.equivalentJavaClass;
	}
	
	/**
	 * Returns the display size of the column
	 * @return
	 */
	public Integer getColumnDisplaySize()
	{
		return this.columnDisplaySize;
	}
	
	/**
	 * Returns a String describing the nullable nature
	 * <ul>
	 * 	<li>ColumnInfo.NULLABLE - if the column is nullable</li>
	 * 	<li>ColumnInfo.NOT_NULLABLE - if the column is not nullable</li>
	 * 	<li>ColumnInfo.UNKNOWN - if the nature cannot be determined</li>
	 * </ul>
	 * @return
	 */
	public String getNullableNature()
	{
		return this.nullableNature;
	}	
	
	/**
	 * Returns true if this column is auto incremented
	 * @return
	 */
	public Boolean isAutoIncrement()
	{
		return this.isAutoIncrement;
	}
	
	/**
	 * Returns true if it is a primary key column
	 * @return
	 */
	public Boolean isPrimaryKeyColumn()
	{
		return this.isPrimaryKeyColumn;
	}
	
	/**
	 * Returns true if this column is editable
	 * @return
	 */
	public Boolean isEditable()
	{
		return this.editable;
	}
	
	/**
	 * Returns the column type as defined in the java.sql.Types
	 * @return
	 */
	public Integer getColumnType()
	{
		return this.columnType;
	}
	
	/**
	 * For debugging only
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Column Info----------------\n");
		buffer.append("Column name: " + this.getColumnName() + "\n");
		buffer.append("Column type: " + this.getColumnTypeName() + "\n");
		buffer.append("Java class: " + this.getEquivalentJavaClass() + "\n");
		buffer.append("Is primary key column: " + this.isPrimaryKeyColumn() + "\n");
		buffer.append("Is editable: " + this.isEditable() + "\n");
		buffer.append("Column type: " + this.getColumnType() + "\n");
		
		return buffer.toString();
	}
}
