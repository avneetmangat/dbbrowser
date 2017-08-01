package org.dbbrowser.db.engine;

import java.util.Observable;

/**
 * This class is an observable and a singleton.  It logs all the SQL statements run by the DBEngines.  It updates all registered observers when an SQL statement is run
 */
public class SQLLog extends Observable
{
    public static SQLLog sqlLog = new SQLLog();

    /**
     * Constructer is private as it is a singleton
     */
    private SQLLog()
    {
    }

    /**
     * Get the singleton object
     * @return
     */
    public static SQLLog getInstance()
    {
        return sqlLog;
    }

    /**
     * Log the SQL statement
     * @param sql
     */
    public void logSQLStatement(String sql)
    {
        this.setChanged();
        notifyObservers(sql);
    }
}
