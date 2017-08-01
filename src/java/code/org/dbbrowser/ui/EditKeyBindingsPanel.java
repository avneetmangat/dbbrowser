package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.helper.KeyBindingsSerializer;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.util.TableSorter;

public class EditKeyBindingsPanel extends JPanel implements ActionListener
{
	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);
	
	private static final String EXISTING_KEY_BINDINGS_PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-existing-key-bindings-panel-title", null);
	private static final String NEW_KEY_BINDINGS_PANEL_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-new-key-bindings-panel-title", null);
	private static final String DESCRIPTION_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-table-header-description-label", null);
	private static final String KEY_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-table-header-key-label", null);
	private static final String ACTION_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-table-header-action-label", null);

	private static final String SHOW_SQL_LOG_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-show-sql-log-label", null);
	private static final String SORT_COLUMNS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-sort-columns", null);
	
	private static final String EDIT_KEY_BINDINGS_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-edit-key-bindings-panel-edit-key-bindings-button-label", null);
	private static final String EDIT_KEY_BINDINGS_ICON_FILENAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-connection-info-window-edit-connection-info-icon" );
	private static final String DELETE_KEY_BINDINGS_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-edit-key-bindings-panel-delete-key-bindings-button-label", null);
	private static final String DELETE_KEY_BINDINGS_ICON_FILENAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-connection-info-window-delete-connection-info-icon" );
	private static final String SAVE_CHANGES_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-edit-key-bindings-panel-save-changes-button-label", null);
	private static final String SAVE_CHANGES_ICON_FILENAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-view-record-window-update-record-icon" );
	
	private Icon iconForSaveChanges = new ImageIcon(SAVE_CHANGES_ICON_FILENAME);
	private Icon iconForEditKeyBindings = new ImageIcon(EDIT_KEY_BINDINGS_ICON_FILENAME);
	private Icon iconForDeleteKeyBindings = new ImageIcon(DELETE_KEY_BINDINGS_ICON_FILENAME);
	
	private JTable tableForKeyBindings = new JTable();
	private JScrollPane paneForKeyBindings = new JScrollPane( tableForKeyBindings );
	
	private JTextField textFieldForDescription = new JTextField("");
	private JTextField textFieldForKey = new JTextField("");
	private JComboBox boxForActions = new JComboBox( KeyBinding.getAllActions().toArray() );
	
	private JTextField textFieldForSQLStatementTerminator = new JTextField("");
	private JCheckBox checkBoxForAutoCommit = new JCheckBox();
	private JCheckBox checkBoxForShowSQL = new JCheckBox();
	private JCheckBox checkBoxForSortColumns = new JCheckBox();

	//Icons for actions
	private static final String RUN_SQL_ICON_FILENAME = PropertyManager.getInstance().getProperty( "dbbrowser-ui-dbbrowser-window-sql-tab-run-sql-icon" );
    private static final String connectIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-test-connection-icon");
    private static final String dropConnectionIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-remove-sql-tab-button-icon");
    private static final String exitIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-file-menu-exit-icon");
    private static final String lookAndFeelIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-lookandfeel-icon");
    private static final String importIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-runbatchfile-icon");
    private static final String refreshIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-refresh-icon");
    private static final String aboutIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-about-menu-icon");
    private static final String viewToolbarIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-view-menu-toolbar-icon");
    private static final String viewSQLLogIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-view-menu-show-sql-log-icon");
    private static final String preferencesIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-preferences-icon");
    private static final String reverseEngineerIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-reverse-engineer-icon");
    private static final String commitIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-commit-button-icon");
    private static final String rollbackIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-sql-tab-rollback-button-icon");
    private static final String helpIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-help-icon");
    private static final String commentsFeedbackIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-comments-feedback-menu-icon");
    private static final String windowIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-icon-filename");
    
	
	private TableSorter tableSorter = null;
	private List listOfKeyBindings = null;
	private Observer observer = null;
	
	public EditKeyBindingsPanel(Observer observer)
	{
		this.observer = observer;
		initialize();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//Handle Save changes
		if( SAVE_CHANGES_BUTTON_LABEL.equals(e.getActionCommand()))
		{
			String description = this.textFieldForDescription.getText();
			KeyStroke keyStroke = KeyStroke.getKeyStroke( this.textFieldForKey.getText() );
			
			//Check both description and keystroke have been set
			if( (description != null && (!"".equals(description))) && keyStroke != null )
			{
				int keyCode = keyStroke.getKeyCode();
				int modifier = keyStroke.getModifiers();
				String action = (String)this.boxForActions.getSelectedItem();
				
				KeyBinding keyBinding = new KeyBinding(description, new Integer(keyCode), new Integer(modifier), action);
				
				//if the list already contains a key binding with this description, then replace it with this one
				if( this.listOfKeyBindings.contains(keyBinding) )
				{
					this.listOfKeyBindings.remove( keyBinding );
				}
				
				this.listOfKeyBindings.add( keyBinding );
				
				//Save the changes to XML file
				try
				{
					KeyBindingsSerializer.serializeKeyBindings( this.listOfKeyBindings );
	
					this.textFieldForDescription.setText("");
					this.textFieldForKey.setText("");
					this.boxForActions.setSelectedIndex(0);
	
					((AbstractTableModel)this.tableSorter.getTableModel()).fireTableDataChanged();
					
					//Update the observer
					this.observer.update(null, null);
				}
				catch(IOException exc)
				{
					String[] params = new String[]{exc.getMessage()};
		            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-save-error-message", params);
		            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);							
				}
			}
			else
			{
	            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-values-not-entered-error-message", null);
	            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);							
			}
		}
		
		//Handle Save changes
		if( EDIT_KEY_BINDINGS_BUTTON_LABEL.equals(e.getActionCommand()))
		{
			Integer selectedRow = getSelectedRow();
            //if the source is a row in the table
            if( selectedRow != null )
            {
            	KeyBinding keyBinding = (KeyBinding)this.listOfKeyBindings.get( selectedRow.intValue() );
            	this.textFieldForDescription.setText( keyBinding.getDescription() );
            	this.textFieldForKey.setText( KeyEvent.getKeyText( keyBinding.getKeyCode().intValue() ) );
            	this.boxForActions.setSelectedItem( keyBinding.getAction() );
            }
            else
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-key-not-selected-error-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);
            } 			
		}
		
		if( DELETE_KEY_BINDINGS_BUTTON_LABEL.equals(e.getActionCommand()))
		{
			Integer selectedRow = getSelectedRow();
            //if the source is a row in the table
            if( selectedRow != null )
            {
            	this.listOfKeyBindings.remove( selectedRow.intValue() );
            	this.textFieldForDescription.setText( "" );
            	this.textFieldForKey.setText( "" );
            	this.boxForActions.setSelectedIndex(0);
            	
            	try
            	{
            		KeyBindingsSerializer.serializeKeyBindings( this.listOfKeyBindings );
        			((AbstractTableModel)this.tableSorter.getTableModel()).fireTableDataChanged();            		
				}
				catch(IOException exc)
				{
					String[] params = new String[]{exc.getMessage()};
		            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-save-error-message", params);
		            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);							
				}				            	
            }
            else
            {
                String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-key-not-selected-error-message", null);
                JOptionPane.showMessageDialog(null, errorMessage, TITLE, JOptionPane.WARNING_MESSAGE);
            } 			
		}		
	}
	
	public Integer getSelectedRow()
	{
		int selectedRow = this.tableForKeyBindings.getSelectedRow();

        Integer selectedRowInteger = null;
        if( selectedRow >= 0 )
        {
            selectedRowInteger = new Integer( this.tableSorter.modelIndex( selectedRow ) );
        }
        return selectedRowInteger;
	}	
	
	private void initialize()
	{
		//Load the key bindings
		try
		{
			this.listOfKeyBindings = KeyBindingsSerializer.deserializeKeyBindings();
		}
		catch(FileNotFoundException exc)
		{
			//Ignore if they have not been set
            this.listOfKeyBindings = new ArrayList();
		}
		catch(IOException exc)
		{
			String[] params = new String[]{exc.getMessage()};
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-preferences-window-key-bindings-save-error-message", params);
            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);
            this.listOfKeyBindings = new ArrayList();
		}
		
		//Setup the panel
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Panel for existing key bindings
		JPanel panelForExistingKeyBindings = new JPanel();
		panelForExistingKeyBindings.setBorder( BorderFactory.createTitledBorder( EXISTING_KEY_BINDINGS_PANEL_TITLE ) );
		panelForExistingKeyBindings.setLayout( new BoxLayout(panelForExistingKeyBindings, BoxLayout.PAGE_AXIS) );
		
		//Setup table for existing key bindings
		this.tableSorter = new TableSorter( new KeyBindingsTableModel() );
		this.tableSorter.setTableHeader( this.tableForKeyBindings.getTableHeader() );
		this.tableForKeyBindings.setModel( tableSorter );
		panelForExistingKeyBindings.add( this.paneForKeyBindings );
		
		//Build the list of buttons to delete selected key binding
		List listOfButtons = new ArrayList();
		Button editKeyBindingsButton = new Button(EDIT_KEY_BINDINGS_BUTTON_LABEL, this, EDIT_KEY_BINDINGS_BUTTON_LABEL, iconForEditKeyBindings, Boolean.FALSE);
		listOfButtons.add( editKeyBindingsButton );
		Button deleteKeyBindingsButton = new Button(DELETE_KEY_BINDINGS_BUTTON_LABEL, this, DELETE_KEY_BINDINGS_BUTTON_LABEL, iconForDeleteKeyBindings, Boolean.FALSE);
		listOfButtons.add( deleteKeyBindingsButton );
		
		//Setup the navigation panel
		ButtonsPanel butonsPanel = new ButtonsPanel( listOfButtons );
		panelForExistingKeyBindings.add( butonsPanel );
		
		this.add( panelForExistingKeyBindings );
		
		
		//Panel for new key bindings
		JPanel panelForNewKeyBindings = new JPanel();
		panelForNewKeyBindings.setBorder( BorderFactory.createTitledBorder( NEW_KEY_BINDINGS_PANEL_TITLE ) );
		panelForNewKeyBindings.setLayout( new BoxLayout(panelForNewKeyBindings, BoxLayout.PAGE_AXIS) );
		
		//Textfield and label for description
		JPanel panelForFormFields = new JPanel();
		panelForFormFields.setPreferredSize(new Dimension(100, 100));
		panelForFormFields.setLayout( new GridLayout(3, 2));

		JLabel labelForDescription = new JLabel(DESCRIPTION_LABEL);
		panelForFormFields.add( labelForDescription );
		panelForFormFields.add( textFieldForDescription );
		
		JLabel labelForKey = new JLabel(KEY_LABEL);
		panelForFormFields.add( labelForKey );
		textFieldForKey.addKeyListener( new KeyTextFieldKeyListener() );
		panelForFormFields.add( textFieldForKey );

		JLabel labelForAction = new JLabel(ACTION_LABEL);
		panelForFormFields.add( labelForAction );
		
		//Set the renderer for combo box
		boxForActions.setRenderer( new KeyBindingsComboBoxRenderer() );
		panelForFormFields.add( boxForActions );

		//Add form fields
		panelForNewKeyBindings.add( panelForFormFields );

		//Build the list of buttons to save new key binding
		listOfButtons = new ArrayList();
		Button firstRecordButton = new Button(SAVE_CHANGES_BUTTON_LABEL, this, SAVE_CHANGES_BUTTON_LABEL, iconForSaveChanges, Boolean.FALSE);
		listOfButtons.add( firstRecordButton );
		
		//Setup the navigation panel
		butonsPanel = new ButtonsPanel( listOfButtons );
		panelForNewKeyBindings.add( butonsPanel );
		
		
		this.add( panelForNewKeyBindings );
	}
	
	private class KeyBindingsTableModel extends AbstractTableModel 
	{
		private static final long serialVersionUID = 1l;
		private String[] columnNames = new String[3];
		
		private KeyBindingsTableModel()
		{
			columnNames[0] = DESCRIPTION_LABEL;
			columnNames[1] = KEY_LABEL;
			columnNames[2] = ACTION_LABEL;
		}
		
	    public String getColumnName(int col) 
	    {
	        return columnNames[col];
	    }
	    
	    public int getRowCount() 
	    { 
	    	return listOfKeyBindings.size(); 
	    }
	    
	    public int getColumnCount() 
	    { 
	    	return 3; 
	    }
	    
	    public Object getValueAt(int row, int col) 
	    {
	    	KeyBinding keyBinding = null;
	    	String cellValue = null;
	        switch (col) {
	            case 0:  
	            	keyBinding = (KeyBinding)listOfKeyBindings.get(row);
	            	cellValue = keyBinding.getDescription();
	            	break;
	            case 1:
	            	keyBinding = (KeyBinding)listOfKeyBindings.get(row);
	            	cellValue = KeyEvent.getKeyText( keyBinding.getKeyCode().intValue() );// + " - " + keyBinding.getModifierCode().intValue();
	            	break;	            	
	            case 2:  
	            	keyBinding = (KeyBinding)listOfKeyBindings.get(row);
	            	cellValue = keyBinding.getAction();
	            	break;
	        }
	        return cellValue;
	    }
	}
	
	private class KeyBindingsComboBoxRenderer implements ListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
	    	String iconFileName = null;
	        switch (index) 
	        {
            	case 0:
            		iconFileName = helpIconFileName;
            		break;	        
	            case 1:
	            	iconFileName = connectIconFileName;
	            	break;	            	
	            case 2:  
	            	iconFileName = dropConnectionIconFileName;
	            	break;
	            case 3:  
	            	iconFileName = importIconFileName;
	            	break;
	            case 4:  
	            	iconFileName = exitIconFileName;
	            	break;
	            case 5:  
	            	iconFileName = preferencesIconFileName;
	            	break;
	            case 6:  
	            	iconFileName = commitIconFileName;
	            	break;
	            case 7:  
	            	iconFileName = rollbackIconFileName;
	            	break;
	        }			
			JLabel label = new JLabel(value.toString());
			
			if( iconFileName != null )
			{
				label.setIcon( new ImageIcon(iconFileName) );
			}
			return label;
		}
	}
	
	private class KeyTextFieldKeyListener extends KeyAdapter
	{
		public void keyReleased(KeyEvent e)
		{
			e.consume();			
		}
		
		public void keyTyped(KeyEvent e)
		{
			e.consume();
		}
		
		public void keyPressed(KeyEvent e)
		{
			textFieldForKey.setText( KeyEvent.getKeyText( e.getKeyCode() ) );
			e.consume();
		}
	}
}
