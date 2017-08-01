package org.dbbrowser.drivermanager;

import infrastructure.logging.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import org.dbbrowser.security.AsymmetricEncryptionEngine;
import org.dbbrowser.security.EncryptionEngineException;
import org.dbbrowser.ui.UIControllerForQueries;

/**
 * Used to get the connection to the database
 */
public class DBBrowserDriverManager
{
	private static DBBrowserDriverManager driverManager = new DBBrowserDriverManager();
	private Hashtable pooledConnections = new Hashtable(); 
	
	/**
	 * Private constructer as it is a singleton
	 *
	 */
	private DBBrowserDriverManager()
	{
		
	}
	
	/**
	 * Returns the singleton object
	 * @return
	 */
	public static DBBrowserDriverManager getInstance()
	{
		return driverManager;
	}

	/**
	 * Get the connection.  the 'name' attribute in the connectionInfo uniquely identifies a connection.  
	 * This class pools the connection and returns the same connection for subsequent calls with the 'same' 
	 * connectionInfo object.  Do not close the connection as it is cached. <br /> <br />
	 * <ul>
	 * <li>For MySQL, the url is of the form jdbc:mysql://localhost:3306/mysql</li>
	 * <li>For Oracle, the url is of the form jdbc:oracle:thin:@localhost:1521:live</li>
	 * </ul>
	 * @param connectionInfo
	 * @param masterPassword
	 * @return
	 * @throws DriverManagerException
	 */
	public Connection getConnection(ConnectionInfo connectionInfo, String masterPassword)
		throws DriverManagerException
	{
		Connection connection = null;
		Object connectionFromPool = this.pooledConnections.get(connectionInfo);
		if(connectionFromPool == null)
		{
			try
			{
				URL jdbcDriverURL = connectionInfo.getJdbcDriverJarFileLocation().toURL();
				URL[] urls = new URL[1];
				urls[0] = jdbcDriverURL;
				URLClassLoader urlClassLoader = new URLClassLoader( urls );
				Class driverClass = Class.forName( connectionInfo.getDriverClassName(), true, urlClassLoader );
				Driver driver = (Driver)driverClass.newInstance();
				
				//Decrypt the password
				String passwordInClearText = AsymmetricEncryptionEngine.decrypt( connectionInfo.getPassword(), masterPassword );
				
				//Get the connection
				Properties props = new Properties();
				props.put("user", connectionInfo.getUsername());
				props.put("password", passwordInClearText);
				
				//If it is a oracle connection, then check if encryption and checksum is required
				if( UIControllerForQueries.ORACLE_DBMS.equals(connectionInfo.getDBMSType()) )
				{
					if( connectionInfo.getEncryptionAlgorithm() != null )
					{
						props.put("oracle.net.encryption_types_client", connectionInfo.getEncryptionAlgorithm());
					}
					
					if( connectionInfo.getCheckSumAlgorithm() != null )
					{
						props.put("oracle.net.crypto_checksum_types_client", connectionInfo.getCheckSumAlgorithm());
					}
				}
				
				//Connect
				connection = driver.connect(connectionInfo.getDatabaseURL(), props);
				
				this.pooledConnections.put(connectionInfo, connection);
				Log.getInstance().infoMessage("Connection setup for " + connectionInfo.toString(), DBBrowserDriverManager.class.getName());
			}
			catch(MalformedURLException exc)
			{
				throw new DriverManagerException(exc);
			}
			catch(ClassNotFoundException exc)
			{
				throw new DriverManagerException(exc);
			}			
			catch(InstantiationException exc)
			{
				throw new DriverManagerException(exc);			
			}
			catch(IllegalAccessException exc)
			{
				throw new DriverManagerException(exc);			
			}			
			catch(SQLException exc)
			{
				throw new DriverManagerException(exc);			
			}
			catch(EncryptionEngineException exc)
			{
				throw new DriverManagerException(exc);							
			}
		}
		else
		{
			connection = (Connection)connectionFromPool;
		}
		return connection;
	}
}
