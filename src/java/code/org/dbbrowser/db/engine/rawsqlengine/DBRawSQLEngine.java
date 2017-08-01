package org.dbbrowser.db.engine.rawsqlengine;

import java.sql.Statement;

import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;

/**
 * Run raw SQL ststements.  The raw SQL statements are entered by the user and may be Update, Select, Delete etc
 */
public interface DBRawSQLEngine
{
    /**
     * Run the SQL statement and return the result if any.
     * @param sql
     * @return - DBTable if there is any result or null
     * @throws DBEngineException
     */
    public DBTable runRawSQL(String sql) throws DBEngineException;

    /**
     * Returns the Statement used to run the SQL
     * @return
     */
    public Statement getStatement();
}
