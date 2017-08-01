package org.dbbrowser.ui;

import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import infrastructure.internationalization.InternationalizationManager;

public class PreferencesWindow
{
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);
	private static final String PREFERENCES_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-preferences-panel-title", null);
	private static final String KEY_BINDINGS_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-edit-key-bindings-panel-title", null);

	private JFrame preferencesFrame = null;
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	private Observer observerForChangesInPreferences = null;
	
	
	public PreferencesWindow(Observer observer)
	{
		this.observerForChangesInPreferences = observer;
		this.preferencesFrame = new JFrame(TITLE);
		this.preferencesFrame.setResizable( true );
		this.preferencesFrame.setSize(600, 400);
		this.preferencesFrame.setLocationRelativeTo( null );
		
		initialize();
		
	}
	
	public void show()
	{
		preferencesFrame.setVisible( true );
	}
	
	private void initialize()
	{
		//Add the preferences panel
		PreferencesPanel preferencesPanel = new PreferencesPanel();
		this.tabbedPane.add( PREFERENCES_TAB_LABEL, preferencesPanel );
		
		//Add the key bindings panel
		EditKeyBindingsPanel editKeyBindingsPanel = new EditKeyBindingsPanel( this.observerForChangesInPreferences );
		this.tabbedPane.add( KEY_BINDINGS_TAB_LABEL, editKeyBindingsPanel );		
		
		this.preferencesFrame.getContentPane().add( this.tabbedPane );
	}
}
