package org.dbbrowser.db.engine.model.filter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple filter class represents a filter on a table.  Only one value is represented by a simple filter.
 * Multiple values can be joined to form a compound filter. 
 * @author amangat
 */
public class SimpleFilter implements Filter
{
	private String leftOperand = null;
	private String operator = null;
	private Object rightOperand = null;
	private static final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	
	/**
	 * Constructer
	 * @param leftOperand
	 * @param operator
	 * @param rightOperand
	 */
	public SimpleFilter(String leftOperand, String operator, Object rightOperand)
	{
		this.leftOperand = leftOperand;
		this.operator = operator;
		this.rightOperand = rightOperand; 
	}
	
	/**
	 * Returns the left operand
	 * @return
	 */
	public String getLeftOperand()
	{
		return this.leftOperand;
	}
	
	/**
	 * Returns the right operand
	 * @return
	 */
	public Object getRightOperand()
	{
		return this.rightOperand;
	}
	
	/**
	 * Returns the operator
	 * @return
	 */
	public String getOperator()
	{
		return this.operator;
	}
	
	/**
	 * Returns a string which can be used for SQL queries.  The string is of the form 
	 * (name='Test') and (dob>'24-MAR-05') and (score>100)
	 * @return
	 */
	public String getSQLString()
	{
		String sql = "(" + leftOperand + operator;
		
		//Format the right operand
		if( rightOperand == null )
		{
			sql = "(" + leftOperand + " is null)";
		}		
		else if( rightOperand instanceof String )
		{
			sql = sql + "'" + rightOperand + "')";
		}
		else if( rightOperand instanceof Integer )
		{
			sql = sql + rightOperand + ")";			
		}
		else if( rightOperand instanceof Date )
		{
			String dateString = dateFormat.format( (Date)this.rightOperand );
			sql = sql + "'" + dateString + "')";			
		}
		else if (rightOperand instanceof Double) 
		{
			sql = sql + rightOperand + ")";
		} 
		else if (rightOperand instanceof Float) 
		{
			sql = sql + rightOperand + ")";
		}
		else if (rightOperand instanceof Long) 
		{
			sql = sql + rightOperand + ")";
		}
		else if (rightOperand instanceof BigDecimal) 
		{
			sql = sql + rightOperand + ")";
		}
		else if( rightOperand instanceof Object )
		{
			sql = "(" + leftOperand + " is not null)";
		}		
		
		return sql;
	}
}
