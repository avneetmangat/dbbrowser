package org.dbbrowser.db.engine.model.filter;

public class CompoundFilter implements Filter
{
	private Filter filter1 = null;
	private String joinCondition = null;
	private Filter filter2 = null;
	
	public CompoundFilter(Filter filter1, String joinCondition, Filter filter2)
	{
		this.filter1 = filter1;
		this.joinCondition = joinCondition;
		this.filter2 = filter2;
	}
	
	/**
	 * Returns the first filter
	 * @return
	 */
	public Filter getFilter1()
	{
		return this.filter1;
	}
	
	/**
	 * Returns the second filter
	 * @return
	 */
	public Filter getFilter2()
	{
		return this.filter2;
	}	
	
	/**
	 * Returns the join condition e.g. AND, OR
	 * @return
	 */
	public String getJoinCondition()
	{
		return this.joinCondition;
	}
	
	/**
	 * Returns a string which can be used for SQL queries.  The string is of the form 
	 * (name='Test') and (dob>'24-MAR-05') and (score>100)
	 * @return
	 */
	public String getSQLString()
	{
		String sql1 = this.filter1.getSQLString();
		String sql2 = this.filter2.getSQLString();
		String sql = sql1 + " " + this.joinCondition + " " + sql2;
		
		return sql;
	}
}
