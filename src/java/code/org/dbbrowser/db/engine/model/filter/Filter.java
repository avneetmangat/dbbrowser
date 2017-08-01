package org.dbbrowser.db.engine.model.filter;

public interface Filter
{
	/**
	 * Returns a string which can be used for SQL queries.  The string is of the form 
	 * (name='Test') and (dob>'24-MAR-05') and (score>100)
	 * @return
	 */
	public String getSQLString();
	
}
