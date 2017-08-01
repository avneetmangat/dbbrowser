package org.dbbrowser.db.engine.model.filter;

import junit.framework.TestCase;
import infrastructure.logging.Log;

public class SimpleFilterTest extends TestCase
{
	public SimpleFilterTest(String name)
	{
	    super(name);    
	}
	
	public void testGetSQLString()
	{
		Filter filter = new SimpleFilter("ucr", "=", "BB001");
		String sql = filter.getSQLString();
		
		Log.getInstance().debugMessage("SQL String from filter is: " + sql, this.getClass().getName());
		
		String expectedSQL = "(ucr='BB001')";
		assertTrue("SQL String should be " + expectedSQL, expectedSQL.equals(sql));
	}
	
	public void testNullRightOperand()
	{
		Filter filter = new SimpleFilter("ucr", "=", null);
		String sql = filter.getSQLString();
		
		Log.getInstance().debugMessage("SQL String from filter is: " + sql, this.getClass().getName());
		
		String expectedSQL = "(ucr is null)";
		assertTrue("SQL String should be " + expectedSQL, expectedSQL.equals(sql));
	}
	
	public void testNotNullRightOperand()
	{
		Filter filter = new SimpleFilter("ucr", "=", new Object());
		String sql = filter.getSQLString();
		
		Log.getInstance().debugMessage("SQL String from filter is: " + sql, this.getClass().getName());
		
		String expectedSQL = "(ucr is not null)";
		assertTrue("SQL String should be " + expectedSQL, expectedSQL.equals(sql));
	}		
}
