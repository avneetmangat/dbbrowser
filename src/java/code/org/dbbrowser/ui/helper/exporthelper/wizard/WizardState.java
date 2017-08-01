package org.dbbrowser.ui.helper.exporthelper.wizard;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class which represents the state of the wizard
 * @author amangat
 */
public class WizardState
{
	private static WizardState wizardState = new WizardState();
	private Map mapOfNameToState = new HashMap();
	
	/**
	 * Constructer is private as it is a singleton
	 *
	 */
	private WizardState()
	{
	}
	
	/**
	 * Returns the single instance
	 * @return
	 */
	public static WizardState getInstance()
	{
		return wizardState;
	}
	
	public void clearState()
	{
		mapOfNameToState = null;
		mapOfNameToState = new HashMap();
	}
	
	/**
	 * Returns the state for the key
	 * @param key
	 * @return
	 */
	public Object getState(Object key)
	{
		return mapOfNameToState.get( key );
	}
	
	/**
	 * Sets the state
	 * @param key
	 * @param value
	 */
	public void setState(Object key, Object value)
	{
		mapOfNameToState.put( key, value );
	}
	
	/**
	 * Returns the state as a map
	 * @return
	 */
	public Map getWizardState()
	{
		return mapOfNameToState;
	}
}
