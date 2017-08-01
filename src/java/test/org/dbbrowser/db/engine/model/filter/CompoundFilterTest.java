package org.dbbrowser.db.engine.model.filter;

import java.util.Date;

import infrastructure.logging.Log;
import junit.framework.TestCase;

public class CompoundFilterTest extends TestCase
{
	public CompoundFilterTest(String name)
	{
	    super(name);    
	}
	
	public void testGetSQLString()
	{
		Filter filter1 = new SimpleFilter("ucr", "=", "BB001");
		Filter filter2 = new SimpleFilter("dob", "=", new Date(105, 5, 5));
		Filter filter = new CompoundFilter( filter1, "and", filter2);
		String sql = filter.getSQLString();
		
		Log.getInstance().debugMessage("SQL String from filter is: " + sql, this.getClass().getName());
		
		String expectedSQL = "(ucr='BB001') and (dob='05-Jun-2005')";
		assertTrue("SQL String should be " + expectedSQL, expectedSQL.equals(sql));
	}
}
