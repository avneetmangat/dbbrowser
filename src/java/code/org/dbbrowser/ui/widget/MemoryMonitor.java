package org.dbbrowser.ui.widget;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MemoryMonitor
{
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);
	private static final String LABEL_FOR_RECLAIM_MEMORY_BUTTON = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-memory-monitor-window-reclaim-memory-button-label", null);
    private static final String RECLAIM_MEMORY_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-empty-trash-icon");
	
	private JFrame frame = null;
	private JProgressBar progressBar = null;
	private JLabel labelForMemoryStatus = new JLabel("");
	private Runtime runtime = Runtime.getRuntime();
	private double maxValue = 0;
	
	public MemoryMonitor()
	{
		this.initialise();
	}
	
	public void show()
	{
		//Show the frame
		this.frame.setVisible( true );
	}
	
	private void initialise()
	{
		//Set the max value
		this.maxValue = (double)this.runtime.maxMemory();
		//Set the size of frame
		this.frame = new JFrame(TITLE);
		this.frame.setSize( 300, 150 );
		this.frame.setLocationRelativeTo( null );
		this.frame.getContentPane().setLayout( new BorderLayout());
		this.frame.setResizable( false );
		
		//Add the widgets
		JPanel panelForProgress = new JPanel();
		panelForProgress.setLayout( new BoxLayout(panelForProgress, BoxLayout.PAGE_AXIS) );

		//Add the progress bar
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted( true );
		this.progressBar.setValue( 0 );
		this.progressBar.setBorder( BorderFactory.createEtchedBorder() );
		panelForProgress.add( this.progressBar );
		
		//Add label to show memory
		JPanel p = new JPanel();
		p.setMaximumSize(new Dimension(300, 100 ));
		p.add(labelForMemoryStatus);
		panelForProgress.add( p );		
		
		this.frame.getContentPane().add( panelForProgress, BorderLayout.CENTER );
		
		//Add the buttons panel
		JPanel panelForButton = new JPanel();
		panelForButton.setLayout( new BorderLayout() );
		JButton buttonToRunGC = new JButton( LABEL_FOR_RECLAIM_MEMORY_BUTTON, new ImageIcon(RECLAIM_MEMORY_ICON_FILENAME) );
		buttonToRunGC.addActionListener( new ListenerForButtonToRunGC() );
		panelForButton.add( buttonToRunGC, BorderLayout.CENTER );
		this.frame.getContentPane().add( panelForButton, BorderLayout.SOUTH );
		
		//Add dummy panels
		this.frame.getContentPane().add( new JPanel(), BorderLayout.NORTH );
		this.frame.getContentPane().add( new JPanel(), BorderLayout.WEST );
		this.frame.getContentPane().add( new JPanel(), BorderLayout.EAST );	
		
		MemoryMonitorTimer memoryMonitorTimer = new MemoryMonitorTimer();
		memoryMonitorTimer.start();
	}
	
	private class MemoryMonitorTimer extends Thread
	{
		public void run()
		{
			try
			{
				while(true)
				{
					Double d = new Double( runtime.totalMemory()/maxValue*100 );
					progressBar.setValue( d.intValue() );
					
					Double[] parameters = new Double[]{new Double(runtime.totalMemory()/1000000), new Double(runtime.maxMemory()/1000000)};
					String LABEL_FOR_MEMORY_STATUS = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-memory-monitor-window-memory-consumption-label", parameters);
					labelForMemoryStatus.setText( LABEL_FOR_MEMORY_STATUS );
					Thread.sleep(1000);
				}
			}
			catch(Exception exc)
			{
				Log.getInstance().fatalMessage("*** Fatal exception in memory monitor ***\n" + exc.getMessage(), MemoryMonitor.class.getName());
			}
		}
	}
	
	private class ListenerForButtonToRunGC implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.gc();
			Log.getInstance().debugMessage("*** Garbage collector invoked ***", MemoryMonitor.class.getName());
		}
	}
}
