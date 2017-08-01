package org.dbbrowser.ui.helper;

import infrastructure.propertymanager.PropertyManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBRow;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.ui.UIControllerForQueries;

/**
 * Private class representing the model for the table
 * @author amangat
 */
public class DBTableDataTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private List listOfColumnInfosIncludingRowHeaderColumn = new ArrayList();
    private DBTable dbTable = null;

    public DBTableDataTableModel(DBTable newDBTable)
    {
        dbTable = newDBTable;
        listOfColumnInfosIncludingRowHeaderColumn.addAll( dbTable.getListOfColumnInfos() );
        ColumnInfo dummyColumnInfoForRowHeadercolumn = new ColumnInfo("    ", ColumnInfo.COLUMN_TYPE_VARCHAR, null, null, null, null, Boolean.FALSE, Boolean.FALSE, null);

    	//if sorting enabled, sort the data in table and sort the columns
    	String sortColumnsFlag = PropertyManager.getInstance().getProperty("dbbrowser-ui-sort-columns-in-table");
    	if("true".equals( sortColumnsFlag ))
    	{
            Collections.sort( listOfColumnInfosIncludingRowHeaderColumn, new ColumnInfoComparator() );    		
    	}

        listOfColumnInfosIncludingRowHeaderColumn.add(0, dummyColumnInfoForRowHeadercolumn );
    }
    
    /**
     * Returns the column name
     * @return - String
     */
    public String getColumnName(int col)
    {
        ColumnInfo columnInfo = (ColumnInfo)listOfColumnInfosIncludingRowHeaderColumn.get( col );
        String columnName = columnInfo.getColumnName();

        //If it is a primary key column, add the word (PK) at the end
        if(columnInfo.isPrimaryKeyColumn().booleanValue())
        {
            columnName = columnName + "(PK)";
        }
        return columnName;
    }    

    /**
     * Returns the number for rows
     * @return - int
     */
    public int getRowCount()
    {
        return dbTable.getListOfRows().size();
    }

    /**
     * Returns the number of columns
     * @return - int
     */
    public int getColumnCount()
    {
        return listOfColumnInfosIncludingRowHeaderColumn.size();
    }

    /**
     * Returns the value for a row and column
     * @return Object
     */
    public Object getValueAt(int row, int col)
    {
        String cellValue = null;

        if( col==0 )
        {
            return "" + (row + 1);
        }
        else
        {
            DBRow dbRow = (DBRow)dbTable.getListOfRows().get(row);
            List listOfTableCells = dbRow.getListOFDBTableCells();

        	//if sorting enabled, sort the data in table and sort the columns
        	String sortColumnsFlag = PropertyManager.getInstance().getProperty("dbbrowser-ui-sort-columns-in-table");
        	if("true".equals( sortColumnsFlag ))
        	{            
        		Collections.sort( listOfTableCells, new DBTableCellComparator() );
        	}
        	
            DBTableCell dbTableCell = (DBTableCell)listOfTableCells.get(col-1);
            cellValue = dbTableCell.getFormattedValue();
        }

        return cellValue;
    }
    
    /**
     * Returns the object value for a row and column
     * @return Object - unformatted object
     */
    public Object getObjectValueAt(int row, int col)
    {
        Object cellValue = null;

        if( col==0 )
        {
            return "" + (row + 1);
        }
        else
        {
            DBRow dbRow = (DBRow)dbTable.getListOfRows().get(row);
            List listOfTableCells = dbRow.getListOFDBTableCells();

        	//if sorting enabled, sort the data in table and sort the columns
        	String sortColumnsFlag = PropertyManager.getInstance().getProperty("dbbrowser-ui-sort-columns-in-table");
        	if("true".equals( sortColumnsFlag ))
        	{            
        		Collections.sort( listOfTableCells, new DBTableCellComparator() );
        	}
        	
            DBTableCell dbTableCell = (DBTableCell)listOfTableCells.get(col-1);
            cellValue = dbTableCell.getValue();
        }

        return cellValue;
    }    
    

    /**
     * Returns the db table
     * @return
     */
    public DBTable getDBTable()
    {
        return dbTable;
    }
    
    /**
     * Returns the class for the column data
     * @return Class
     */
    public Class getColumnClass(int columnIndex)
    {
    	Class classToReturn = null;
    	
    	ColumnInfo columnInfo = (ColumnInfo)listOfColumnInfosIncludingRowHeaderColumn.get( columnIndex );
    	
    	//if the type is a number (Double or Integer), return a Number.class
    	if( (columnInfo.getColumnTypeName() != null) && (columnInfo.getColumnTypeName().equals( ColumnInfo.COLUMN_TYPE_NUMBER )) )
    	{
    		classToReturn = Number.class;
    	}
    	else
    	{
    		classToReturn = String.class;
    	}
    	
    	return classToReturn;
    }
}
