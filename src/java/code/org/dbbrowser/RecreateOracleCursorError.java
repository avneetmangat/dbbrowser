package org.dbbrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RecreateOracleCursorError
{
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Starting test...");
			
			//Load the driver class.  It gets automatically registered with the DriverManager
			Class.forName("oracle.jdbc.OracleDriver");
			
			//Connect
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@ophelia:1521:XISTEST", "xcssys", "portal");
			
			int counter = 5000;
			for(int i=0; i<counter; i++)
			{
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select count(*) from claim");
				
				System.out.println(i);
				statement.close();
			}
			
			connection.close();
		}
		catch(Exception exc)
		{
			System.out.println("*** ERROR ***\n");
			System.out.println(exc.getMessage());
		}
		System.out.println("end of test");
	}

}
