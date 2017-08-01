package org.dbbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class OpenConnectionsSummary extends JFrame implements ActionListener, Runnable
{
	private JButton start = null;
	private JButton stop = null;
	private boolean stopped = false;
	
	public static void main(String[] args)
	{
		System.out.println("Starting application...");		
		(new OpenConnectionsSummary()).show();
	}
	
	public OpenConnectionsSummary()
	{
		super("Open connections summary");
		this.setSize(600, 400);
		this.setLocationRelativeTo( null );
		
		start = new JButton("Start");
		start.addActionListener( this );
		stop = new JButton("Stop");
		stop.addActionListener( this );
		stop.setEnabled( false );
		
		this.setResizable( false );
		this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS) );
		this.getContentPane().add( start );
		this.getContentPane().add( stop );
		
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("Pressed " + e.getActionCommand());		
		
		if("Start".equals(e.getActionCommand()))
		{
			stopped = false;
			(new Thread(this)).start();
			start.setEnabled( false );
			stop.setEnabled( true );
		}

		if("Stop".equals(e.getActionCommand()))
		{
			stopped = true;
			start.setEnabled( true );
			stop.setEnabled( false );
		}		
	}
	
		
	public void run()
	{
		try
		{
			System.out.println("Pinging...");					
			String sql = "select status, count(schemaname) as open_connections from v$session where schemaname='XCSSYS' group by status order by status";
			DateFormat format = new SimpleDateFormat("HH:mm:ss");
			Class.forName("oracle.jdbc.OracleDriver");
			
			//Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:migrate", "system", "qwerty");
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.223.87:1521:xcpt", "xcssys", "portal");
			
			
			FileWriter fw = new FileWriter("open-connections.csv", true);
			BufferedWriter writer = new BufferedWriter(fw);
			
			while(!stopped)
			{
				//Run the sql
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
				int totalOpenConnections = 0;
				
				String data = "";
				while( rs.next() )
				{
					data = data + rs.getString("status");
					data = data + ", ";
					int connections = rs.getInt("open_connections");
					data = data + connections;
					data = data + ", ";
					totalOpenConnections = totalOpenConnections + connections;
				}
				data = data + "TOTAL, " + totalOpenConnections + ", ";
				
				Date now = new Date();
				data = data + format.format( now );
				System.out.println(data);
				writer.write(data + "\n");
				
				rs.close();
				statement.close();
				writer.flush();
				
				Thread.sleep(1000);
			}
			
			connection.close();
			writer.close();
			fw.close();
		}
		catch(Exception exc)
		{
			System.out.println("*** Exception ***");
			System.out.println(exc.getMessage());
		}
		System.out.println("End");
	}
}
