package org.dbbrowser.ui.panel;

import java.awt.*;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.widget.Button;

/**
 * A buttons panel holds a group of buttons
 * @author amangat
 *
 */
public class ButtonsPanel extends JPanel
{
	private static final long serialVersionUID = UIControllerForQueries.version;
	private List listOfButtons = null;
	
	/**
	 * Constructer
	 * @param listOfButtons - list of Button widgets
	 */
	public ButtonsPanel(List listOfButtons)
	{
		this.listOfButtons = listOfButtons;
		this.initialize();
	}

    /**
     * Enable or disable the button with that name
     * @param buttonName
     * @param enable
     */
    public void enableButton(String buttonName, Boolean enable)
    {
        Component[] components = this.getComponents();
        for(int i=0; i<components.length; i++)
		{
			JButton jButton = (JButton)components[i];
            String actionCommand = jButton.getActionCommand();
            if( actionCommand.equals(buttonName))
            {
               jButton.setEnabled( enable.booleanValue() );
                break;
            }
        }
    }

    private void initialize()
	{
		this.setLayout( new FlowLayout( FlowLayout.CENTER ) );
		for(int i=0; i<this.listOfButtons.size(); i++)
		{
			Button button = (Button)this.listOfButtons.get(i);
			JButton jButton = new JButton();
			jButton.setText( button.getLabel() );
			jButton.addActionListener( button.getListener() );
			jButton.setActionCommand( button.getName() );
			jButton.setIcon( button.getIcon() );
			this.add( jButton );
		}
		
		Dimension d = new Dimension( this.getMaximumSize().width, 50);
		this.setMaximumSize(d);
	}
}
