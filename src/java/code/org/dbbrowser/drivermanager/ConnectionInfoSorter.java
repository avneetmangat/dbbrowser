package org.dbbrowser.drivermanager;

import java.util.Comparator;
import java.util.Date;

/**
 * Sorts connection info on the basis of last used date 
 * @author amangat
 */
public class ConnectionInfoSorter implements Comparator
{
    /**
     * Compare 2 ConnectionInfo object by 'last used date' 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(Object o1, Object o2)
	{
		ConnectionInfo ci1 = (ConnectionInfo)o1;
		ConnectionInfo ci2 = (ConnectionInfo)o2;
		
		Date lastUsed1 = ci1.getLastUsed();
		Date lastUsed2 = ci2.getLastUsed();
		
		int returnValue = 0;
		if(lastUsed1 == null && lastUsed2 == null)
		{
			returnValue = 0;
		}
		else if(lastUsed1 == null)
		{
			returnValue = 1;
		}
		else if(lastUsed2 == null)
		{
			returnValue = -1;
		}	
		else
		{
			returnValue = lastUsed2.compareTo(lastUsed1);
		}
		return returnValue;
	}
}
