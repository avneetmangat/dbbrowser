package org.dbbrowser.drivermanager;

import java.util.List;

/**
 * This class stores the list of connection infos and the password used to encrypt the database passwords.  If the
 * master password provided in the constructer is null, it defaults to "DBBrowser"
 * @author amangat
 */
public class ConnectionInfos
{
	private static final String DEFAULT_MASTER_PASSWORD = "DBBrowser";
	private List listOfConnectionInfos = null;
	private String masterPassword = DEFAULT_MASTER_PASSWORD;
	
	/**
	 * Constructer
	 * @param listOfConnectionInfos
	 * @param masterPassword
	 */
	public ConnectionInfos(List listOfConnectionInfos, String inMasterPassword)
	{
		this.listOfConnectionInfos = listOfConnectionInfos;
		
		if( inMasterPassword != null && (!"".equals( inMasterPassword )) )
		{
			this.masterPassword = inMasterPassword;
		}
	}
	
	/**
	 * Returns the list of connection infos
	 * @return
	 */
	public List getListOfConnectionInfos()
	{
		return this.listOfConnectionInfos;
	}
	
	/**
	 * Returns the master password
	 * @return
	 */
	public String getMasterPassword()
	{
		return this.masterPassword;
	}
	
	/**
	 * Set the master password
	 * @param masterPassword
	 */
	public void setMasterPassword(String masterPassword)
	{
		this.masterPassword = masterPassword;
	}
	
	/**
	 * Returns true is password set by user
	 * @return
	 */
	public Boolean isPasswordSetByUser()
	{
		Boolean ans = Boolean.TRUE;
		if( DEFAULT_MASTER_PASSWORD.equals( this.getMasterPassword() ))
		{
			ans = Boolean.FALSE;
		}
		
		return ans;
	}
}
