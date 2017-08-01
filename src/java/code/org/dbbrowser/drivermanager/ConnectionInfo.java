package org.dbbrowser.drivermanager;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * A connection info class represents the details of a JDBC connection to a database
 * @author amangat
 *
 */
public class ConnectionInfo implements Serializable
{
	public static final String DES56C_ENCRYPTION_ALGORITHM = "DES56C";
	public static final String DES40C_ENCRYPTION_ALGORITHM = "DES40C";
	public static final String RC4_40_ENCRYPTION_ALGORITHM = "RC4_40";
	public static final String RC4_56_ENCRYPTION_ALGORITHM = "RC4_56";
	
	public static final String MD5_CHECK_SUM_ALGORITHM = "MD5";
	
	public static final String[] LIST_OF_ENCRYPTION_ALGORITHMS = new String[]{DES56C_ENCRYPTION_ALGORITHM, DES40C_ENCRYPTION_ALGORITHM, RC4_40_ENCRYPTION_ALGORITHM, RC4_56_ENCRYPTION_ALGORITHM};
	public static final String[] LIST_OF_CHECKSUM_ALGORITHMS = new String[]{MD5_CHECK_SUM_ALGORITHM};
	
	private String driverClassName = null;
	private File jdbcDriverJarFileLocation = null;
	private String name = null;
	private String dbmsType = null;
	private String databaseURL = null;
	private String username = null;
	private String password = null;
	private Date lastUsed = null;
	private String encryptionAlgorithm = null;
	private String checkSumAlgorithm = null;
	
	private static final long serialVersionUID = 1l; 
	
	/**
	 * Constrcuter
	 * @param driverClassName
	 * @param jdbcDriverJarFileLocation
	 * @param name
	 * @param dbmsType
	 * @param databaseURL
	 * @param username
	 * @param password
	 * @param encryptionAlgorithm
	 * @param checkSumAlgorithm
	 */
	public ConnectionInfo(String driverClassName, File jdbcDriverJarFileLocation, String name, String dbmsType, String databaseURL, String username, String passwordString, String encryptionAlgorithm, String checkSumAlgorithm)
	{
		this.driverClassName = driverClassName;
		this.jdbcDriverJarFileLocation = jdbcDriverJarFileLocation;
		this.name = name;
		this.dbmsType = dbmsType;
		this.databaseURL = databaseURL;
		this.username = username;
		this.password = passwordString;
		this.encryptionAlgorithm = encryptionAlgorithm;
		this.checkSumAlgorithm = checkSumAlgorithm;
	}
	
	/**
	 * Return the time this connection was last used
	 * @return
	 */
	public Date getLastUsed()
	{
		return this.lastUsed;
	}
	
	/**
	 * Update the last time this connection was used
	 * @param updatedLastUsedDate
	 */
	public void setLastUsed(Date updatedLastUsedDate)
	{
		this.lastUsed = updatedLastUsedDate;
	}
	
	/**
	 * Get the driver class name, e.g. 'oracle.jdbc.OracleDriver'
	 * @return
	 */
	public String getDriverClassName()
	{
		return this.driverClassName;
	}
	
	/**
	 * Get the location fo the jar file, e.g. c:/drivers/ojdbc14.jar
	 * @return
	 */
	public File getJdbcDriverJarFileLocation()
	{
		return this.jdbcDriverJarFileLocation;
	}
	
	/**
	 * Get the name specified for this connection - any unique name.  E.g. 'Oracle connection to my database'
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Return the dbms type - e.g. Oracle, MySQL
	 * @return
	 */
	public String getDBMSType()
	{
		return this.dbmsType;
	}
	
	/**
	 * Get the database URL
	 * @return
	 */
	public String getDatabaseURL()
	{
		return this.databaseURL;
	}
	
	/**
	 * get the username
	 * @return
	 */
	public String getUsername()
	{
		return this.username;
	}
	
	/**
	 * Get the password
	 * @return
	 */
	public String getPassword()
	{
		return this.password;
	}
	
	/**
	 * Returns the encryption algorithm used, or null if there is no encryption required
	 * @return
	 */
	public String getEncryptionAlgorithm()
	{
		return this.encryptionAlgorithm;
	}
	
	/**
	 * Returns the checkSumAlgorithm, or null if no check sum is in use
	 * @return
	 */
	public String getCheckSumAlgorithm()
	{
		return this.checkSumAlgorithm;
	}
	
	/**
	 * Overrides the hashCode method of Object so it can be used as a key in a hashtable
	 */
	public int hashCode()
	{
		return this.driverClassName.hashCode() + this.databaseURL.hashCode() + this.username.hashCode() + this.password.hashCode();
	}
	
	/**
	 *  Overrides the equals method of Object so it can be used as a key in a hashtable.  Only 
	 *  uses the connectionInfo.getName() to make the comparision.  If 2 connection info objects have the same name, 
	 *  they are equal.  The name uniquely identifies the connection info object 
	 */
	public boolean equals(Object obj)
	{
		boolean equal = false;
		if(obj instanceof ConnectionInfo)
		{
			ConnectionInfo otherConnectionInfo = (ConnectionInfo)obj;
			String otherConnectionInfoName = otherConnectionInfo.getName();
			if( this.getName().equals(otherConnectionInfoName))
			{
				equal = true;
			}
		}
		return equal;
	}

    /**
     * For debugging only
     * @return
     */
    public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Connection Info\n");
		buffer.append("Name: " + this.getName() + "\n");
		buffer.append("DBMS Type: " + this.getDBMSType() + "\n");		
		buffer.append("Database URL: " + this.getDatabaseURL() + "\n");
		buffer.append("Driver class: " + this.getDriverClassName() + "\n");
		buffer.append("JDBC Jar location: " + this.getJdbcDriverJarFileLocation().toString() + "\n");
		buffer.append("Username: " + this.getUsername() + "\n");
		buffer.append("Password: " + this.getPassword() + "\n");
		buffer.append("Last used: " + this.getLastUsed() + "\n");
		buffer.append("Encryption algorithm: " + this.getEncryptionAlgorithm() + "\n");
		buffer.append("Checksum algorithm: " + this.getCheckSumAlgorithm() + "\n");
		
		return buffer.toString();
	}
}
