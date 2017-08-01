package org.dbbrowser.ui.widget;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.ui.UIControllerForQueries;

/**
 * A dialog which shows the progress of a task and some label
 */
public class ProgressDialog implements ChangeListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;

	private String title = "";
	private String textForLabel = "";
	private JDialog frame = null;
	private JProgressBar aProgressBar = null;
	private JLabel label = null;
	private Integer minValue = null;
	private Integer maxValue = null;

    /**
     * Constructer
     * @param title
     * @param textForLabel
     * @param minValue
     * @param maxValue
     */
    public ProgressDialog(String title, String textForLabel, Integer minValue, Integer maxValue)
	{
		this.title = title;
		this.textForLabel = textForLabel;
		this.minValue = minValue;
		this.maxValue = maxValue;
		initialize();
	}
	
	private void initialize()
	{
		//Setup the frame
		this.frame = new JDialog();
		this.frame.setTitle( this.title );
		this.frame.setResizable( false );
		this.frame.getContentPane().setLayout( new FlowLayout() );
		this.frame.setSize(300, 100);
		this.frame.setLocationRelativeTo( null );
		this.frame.addWindowFocusListener( new ProgressDialogWindowAdapter() );
		this.frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		
		//Setup the progress bar
		aProgressBar = new JProgressBar(this.minValue.intValue(), this.maxValue.intValue());
		aProgressBar.setValue(this.minValue.intValue());
		aProgressBar.setStringPainted( true );
		
		//Set the label
		this.label = new JLabel( this.textForLabel );
		
		//Add the widgets
		this.frame.getContentPane().add( this.aProgressBar );
		this.frame.getContentPane().add( this.label );
	}

    /**
     * Returns the maximum value for the progress of the task
     * @return
     */
    public Integer getMaxValue()
	{
		return this.maxValue;
	}

    /**
     * Returns the starting(min) value
     * @return
     */
    public Integer getMinValue()
	{
		return this.minValue;
	}

    /**
     * Call this method to set the value during the progress of the task.
     * @param newValue
     */
    public void setValue(Integer newValue)
	{
		this.aProgressBar.setValue( newValue.intValue() );
	}

    /**
     * Set to true to make it an indeterminate progress bar
     * @param b
     */
    public void setIndeterminate(boolean b)
	{
		this.aProgressBar.setIndeterminate( b );
	}

    /**
     * Set true if you want to see the labels
     * @param b
     */
    public void setStringPainted(boolean b)
	{
		this.aProgressBar.setStringPainted( b );
	}

    /**
     * Call this during the progress of the task.  This calls setValue as well.  Call this method if it is a determinate progress bar
     * @param e
     */
    public void stateChanged(ChangeEvent e)
	{
		int progress = ((Integer)e.getSource()).intValue();
		this.aProgressBar.setValue( progress );
	}

    /**
     * Shows the progress bar
     */
    public void show()
	{
		this.frame.show();
	}

    /**
     * Call this method to close the progress bar when the task is completed
     */
    public void close()
	{
		this.frame.pack();
		this.frame.dispose();
	}
	
	private class ProgressDialogWindowAdapter extends WindowAdapter
	{
		public void windowLostFocus(WindowEvent e)
		{
			frame.toFront();
		}
	}
}
