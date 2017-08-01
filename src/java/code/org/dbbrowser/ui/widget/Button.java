package org.dbbrowser.ui.widget;

import java.awt.event.ActionListener;
import javax.swing.Icon;

/**
 * A class which represents a button.  Build a button and add it to the buttons panel.  
 * @author amangat
 */
public class Button
{
	private String name = null;
	private ActionListener listener = null;
	private String label = null;
	private Icon icon = null;
	private Boolean defaultFocussedButton = null;
	
	/**
	 * Constructer
	 * @param name
	 * @param listener
	 * @param label
	 * @param icon
	 * @param defaultFocussedButton
	 */
	public Button(String name, ActionListener listener, String label, Icon icon, Boolean defaultFocussedButton)
	{
		this.name = name;
		this.listener = listener;
		this.label = label;
		this.icon = icon;
		this.defaultFocussedButton = defaultFocussedButton;
	}
	
	/**
	 * Returns the name
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the action listener
	 * @return
	 */
	public ActionListener getListener()
	{
		return this.listener;
	}
	
	/**
	 * Returns the label for the button
	 * @return
	 */
	public String getLabel()
	{
		return this.label;
	}
	
	/**
	 * Returns the icon for the button
	 * @return
	 */
	public Icon getIcon()
	{
		return this.icon;
	}
	
	/**
	 * Returns true if this button in the group should be selected by default
	 * @return
	 */
	public Boolean isDefaultFocussedButton()
	{
		return this.defaultFocussedButton;
	}
}
