package org.dbbrowser;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class JDBCSSL
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	    // Register the Oracle JDBC driver
	    System.out.println("Registring the driver...");
	    Connection conn = null;
	    
	    try 
	    {
		    Class driverClass = Class.forName( "oracle.jdbc.OracleDriver", true, JDBCSSL.class.getClassLoader() );
		    Driver driver = (Driver)driverClass.newInstance();
	    	Properties props = new Properties();
	      //props.put("oracle.net.encryption_client", "REQUIRED");
	      props.put("oracle.net.encryption_types_client", "DES56C");
	      //props.put("oracle.net.crypto_checksum_client", "REQUIRED");
	      //props.put("oracle.net.crypto_checksum_types_client","MD5");
			props.put("user", "xcs_charging");
			props.put("password", "abcxyz");
	      
	      
	      conn = driver.connect("jdbc:oracle:thin:@romeo:1521:movdb", props);
	      
		    // Create a Statement
		    Statement stmt = conn.createStatement ();
		 
		    // Select the ENAME column from the EMP table
		    ResultSet rset = stmt.executeQuery ("select ENAME from EMP");
		 
		    // Iterate through the result and print the employee names
		    while (rset.next ())
		    {
		       System.out.println (rset.getString (1));
		    }	      
		    conn.close();
	    } 
	    catch (Exception e) 
	    {
	    	System.out.println(e.getMessage());
	    	e.printStackTrace();
	    }

	}

}
