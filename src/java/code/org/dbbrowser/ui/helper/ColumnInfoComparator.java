package org.dbbrowser.ui.helper;

import org.dbbrowser.db.engine.model.ColumnInfo;

import java.util.Comparator;

/**
 * Sorts the list of column info by name
 */
public class ColumnInfoComparator implements Comparator
{
    /**
     * Returns 1 if firstObject(ColumnInfo) is before secondObject(ColumnInfo)
     * @param firstObject
     * @param secondObject
     * @return
     */
    public int compare(Object firstObject, Object secondObject)
    {
        int valueToReturn = 0;
        ColumnInfo ci1 = (ColumnInfo)firstObject;
        ColumnInfo ci2 = (ColumnInfo)secondObject;

        String columnName1 = ci1.getColumnName();
        String columnName2 = ci2.getColumnName();

        //if either of the columns is a primary key, it appears first
        if( ci1.isPrimaryKeyColumn().booleanValue() )
        {
            valueToReturn = -1;
        }
        else if( ci2.isPrimaryKeyColumn().booleanValue() )
        {
            valueToReturn = 1;
        }
        else
        {
            valueToReturn = columnName1.compareTo( columnName2 );
        }

        return valueToReturn;

    }
}
