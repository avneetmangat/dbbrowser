package org.dbbrowser.drivermanager;

import infrastructure.logging.Log;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A utility class used to search for JDBC driver classes in a jar file.
 * JDBC driver classes are classes which implement the 'java.sql.Driver' interface.  This search utility extends Thread and can
 * run in a seperate thread while informing the change listeners of the progress.
 */
public class JDBCDriverSearchUtility extends Thread
{
	private File jdbcDriverJarFileLocation = null;
	private ChangeListener changeListener = null;
	private ChangeListener processCompletedListener = null;
	private List listOfDrivers = new ArrayList();;
	private IOException exc = null;

    /**
     * Constructer
     * @param jdbcDriverJarFileLocation
     * @param changeListener
     * @param processCompletedListener
     */
    public JDBCDriverSearchUtility(File jdbcDriverJarFileLocation, ChangeListener changeListener, ChangeListener processCompletedListener)
	{
		this.jdbcDriverJarFileLocation = jdbcDriverJarFileLocation;
		this.changeListener = changeListener;
		this.processCompletedListener = processCompletedListener;
	}

    /**
     * Run this method in a seperate thread.  This method: <br/>
     * <ol>
     *  <li>Informs the changeListener when it starts and during the progress of the search</li>
     *  <li>Searches the jar file for classes which implement the java.sql.Driver interface using 'Driver.class.isAssignableFrom(aClass)' statement</li>
     *  <li>Informs the changeListener when it is finished</li>
     * </ol>
     */
    public void run()
	{
		try
		{
			URL jdbcDriverURL = jdbcDriverJarFileLocation.toURL();
			URL[] urls = new URL[1];
			urls[0] = jdbcDriverURL;
			URLClassLoader urlclassLoader = new URLClassLoader( urls );
			int countOfFilesProcessed = 0;
			
			//List all the classes from the jar file
			JarFile jarJDBCDriverFile = new JarFile( jdbcDriverJarFileLocation );
			Enumeration filesInJarfile = jarJDBCDriverFile.entries();
			while( filesInJarfile.hasMoreElements() )
			{
				Object o = filesInJarfile.nextElement();
				JarEntry jarEntry = (JarEntry)o;
				
				//Raise an event so listener knows of the change
				if( changeListener != null )
				{
					ChangeEvent event = new ChangeEvent( new Integer(countOfFilesProcessed) );
					changeListener.stateChanged( event );
					countOfFilesProcessed++;
				}
				
				try
				{
					String jarEntryName = jarEntry.getName();
					Log.getInstance().debugMessage("Jar entry name is: " + jarEntryName, DBBrowserDriverManager.class.getName());
					int g = jarEntryName.lastIndexOf(".class");
					
					if( g != -1)
					{
						jarEntryName = jarEntryName.substring(0, g);
						jarEntryName = jarEntryName.replace('/', '.');
						try
						{
							Class c = Class.forName(jarEntryName, false, urlclassLoader);
							Log.getInstance().debugMessage("Class is: " + c.toString(), DBBrowserDriverManager.class.getName());
							
							if(Driver.class.isAssignableFrom(c))
							{
								Log.getInstance().infoMessage("Driver found, class is " + c.toString(), DBBrowserDriverManager.class.getName());
								listOfDrivers.add(jarEntryName);
							}
						}
						catch(NoClassDefFoundError exc)
						{
							Log.getInstance().debugMessage("Class " + jarEntryName + "not tested", DBBrowserDriverManager.class.getName());
						}
					}
				}
				catch(ClassNotFoundException exc)
				{
					Log.getInstance().debugMessage(exc.getClass().getName() + " - " + exc.getMessage(), DBBrowserDriverManager.class.getName());
				}	
			}
	
			jarJDBCDriverFile.close();
		}
		catch(IOException exc)
		{
			this.exc = exc;
		}
		
		ChangeEvent event = new ChangeEvent( "Process completed" );		
		processCompletedListener.stateChanged( event );
	}

    /**
     * Call this method when the process is completed to get the list of JDBC driver classes
     * @return - a list of Strings
     * @throws IOException
     */
    public List getListOfJDBCDriverClasses()
		throws IOException
	{
		if(exc!=null)
		{
			throw exc;
		}
		return this.listOfDrivers;
	}
}
