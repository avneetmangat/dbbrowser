package org.dbbrowser.db.engine.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the data in the table
 * @author amangat
 */
public class DBTable
{
	private List listOfRows = null;
	private List listOfcolumnInfos = null;
	private String schemaName = null;
	private String tableName = null;
    private Integer offset = null;
	private Integer numberOfRowsToReturn = null;
	private Integer numberOfRowsInTable = null;

    /**
     * Constructer
     * @param schemaName
     * @param tableName
     * @param listOfRows
     * @param offset
     * @param numberOfRowsToReturn
     * @param numberOfRowsInTable
     */
	public DBTable(String schemaName, String tableName, List listOfRows, Integer offset, Integer numberOfRowsToReturn, Integer numberOfRowsInTable)
	{
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.listOfRows = listOfRows;
		this.offset = offset;
		this.numberOfRowsToReturn = numberOfRowsToReturn;
        this.numberOfRowsInTable = numberOfRowsInTable;		
    }

    /**
     * Constructer
     * @param schemaName
     * @param tableName
     * @param listOfRows
     * @param offset
     * @param numberOfRowsToReturn
     * @param listOfcolumnInfos
     * @param numberOfRowsInTable
     */
    public DBTable(String schemaName, String tableName, List listOfRows, Integer offset, Integer numberOfRowsToReturn, List listOfcolumnInfos)
	{
        this.schemaName = schemaName;
        this.tableName = tableName;
		this.listOfRows = listOfRows;
		this.listOfcolumnInfos = listOfcolumnInfos;
        this.offset = offset;
        this.numberOfRowsToReturn = numberOfRowsToReturn;
        this.numberOfRowsInTable = new Integer(0);
    }
	
    /**
	 * Returns the name of the schema
	 * @return
	 */
	public String getSchemaName()
	{
		return this.schemaName;
	}	
	
	/**
	 * Returns the name of the table
	 * @return
	 */
	public String getTableName()
	{
		return this.tableName;
	}

    /**
     * Returns the offset for the data
     * @return
     */
    public Integer getOffset()
	{
		return this.offset;
	}

    /**
     * Returns the number of rows to return
     * @return
     */
    public Integer getNumberOfRowsToReturn()
	{
		return this.numberOfRowsToReturn;
	}
	
	/**
	 * Returns the list of column info objects.  Each column info describes a column in the db table
	 * @return - a list of columnInfo objects
	 */
	public List getListOfColumnInfos()
	{
		//If there are no db rows and local list of column infos was set, then use the local list
		if( this.listOfcolumnInfos != null )
		{
			return this.listOfcolumnInfos;
		}
		
		Iterator i = this.listOfRows.iterator();
		List listOfColumnInfos = new ArrayList();
		
		//Only process the first row
		if(i.hasNext())
		{
			DBRow dbRow = (DBRow)i.next();
			List listOfTableCells = dbRow.getListOFDBTableCells();
			Iterator j = listOfTableCells.iterator();
			while(j.hasNext())
			{
				DBTableCell dbTableCell = (DBTableCell)j.next();
				ColumnInfo ci = dbTableCell.getColumnInfo();
				listOfColumnInfos.add( ci );
			}
		}
		return listOfColumnInfos;
	}

    /**
     * Get the number of rows in the table
     * @return
     */
    public Integer getNumberOfRowsInTable()
    {
        return numberOfRowsInTable;
    }

    /**
	 * Returns the list of rows in the db table
	 * @return - a list of DBRow objects 
	 */
	public List getListOfRows()
	{
		return this.listOfRows;
	}
}
