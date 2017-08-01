package org.dbbrowser.db.engine.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a row in a table
 * @author amangat
 *
 */
public class DBRow
{
	private List listOFDBTableCells = null;
	
	/**
	 * Constructer
	 * @param listOFDBTableCells
	 */
	public DBRow(List listOFDBTableCells)
	{
		this.listOFDBTableCells = listOFDBTableCells;
	}
	
	/**
	 * Returns a list of cells in the table
	 * @return - a list of DBTableCell objects
	 */
	public List getListOFDBTableCells()
	{
		return this.listOFDBTableCells;
	}

	/**
	 * Returns a list of primary key cells
	 * @return - a list of DBTableCells
	 */
	public List getListOfPrimaryKeyDBTableCells()
	{
		List listOfPrimaryKeyDBTableCells = new ArrayList();
		
		Iterator i = this.listOFDBTableCells.iterator();
		while(i.hasNext())
		{
			DBTableCell dbTableCell = (DBTableCell)i.next();
			boolean isPrimaryKeyColumn = dbTableCell.getColumnInfo().isPrimaryKeyColumn().booleanValue();
			if(isPrimaryKeyColumn)
			{
				listOfPrimaryKeyDBTableCells.add( dbTableCell );
			}
		}
		
		return listOfPrimaryKeyDBTableCells;
	}
	
	/**
	 * Returns a list of non-primary key cells which have changed.  Primary key values cant be changed
	 * @return - a list of DBTableCells
	 */	
	public List getListOfChangedNonPrimaryKeyCells()
	{
		List listOfChangedNonPrimaryKeyDBTableCells = new ArrayList();
		
		Iterator i = this.listOFDBTableCells.iterator();
		while(i.hasNext())
		{
			DBTableCell dbTableCell = (DBTableCell)i.next();
			boolean isPrimaryKeyColumn = dbTableCell.getColumnInfo().isPrimaryKeyColumn().booleanValue();
			boolean isChangedByUser = dbTableCell.isChangedByUser().booleanValue();
			
			if( (! isPrimaryKeyColumn) && isChangedByUser )
			{
				listOfChangedNonPrimaryKeyDBTableCells.add( dbTableCell );
			}
		}
		
		return listOfChangedNonPrimaryKeyDBTableCells;		
	}
}
