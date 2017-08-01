package org.dbbrowser.ui.helper;

import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.ui.helper.ColumnInfoComparator;

import java.util.Comparator;

/**
 * Sorts the list of db table cells by name
 */
public class DBTableCellComparator implements Comparator
{
    /**
     * Returns 1 if firstObject(ColumnInfo) is before secondObject(ColumnInfo)
     * @param firstObject
     * @param secondObject
     * @return
     */
    public int compare(Object firstObject, Object secondObject)
    {
        ColumnInfoComparator columnInfoComparator = new ColumnInfoComparator();
        DBTableCell dbTableCell1 = (DBTableCell)firstObject;
        DBTableCell dbTableCell2 = (DBTableCell)secondObject;
        int valueToReturn = columnInfoComparator.compare( dbTableCell1.getColumnInfo(), dbTableCell2.getColumnInfo() );

        columnInfoComparator = null;
        return valueToReturn;
    }
}
