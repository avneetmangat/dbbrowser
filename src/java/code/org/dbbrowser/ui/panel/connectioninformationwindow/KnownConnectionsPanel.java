package org.dbbrowser.ui.panel.connectioninformationwindow;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManagementException;
import infrastructure.propertymanager.PropertyManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.ConnectionInfos;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.util.TableSorter;

public class KnownConnectionsPanel extends JPanel implements ActionListener
{
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
	
	private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String CONNECT_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-connect-icon");
    private static final String DELETE_CONNECTION_INFO_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-delete-connection-info-icon");
    private static final String EDIT_CONNECTION_INFO_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-edit-connection-info-icon");
    private static final String USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-use-master-password");
    private static final String USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-use-master-password-label", null);

	private JTable tableOfKnownConnections = null;
	private TableSorter sorter = null;
	private ConnectionInfos connectionInfos = null;
	private static final int COLUMN_COUNT = 3;
	private KnownConnectionsTableModel knownConnectionsTableModel = null;
	private JCheckBox checkBoxToUseMasterPassword = new JCheckBox( USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO_LABEL );
	
	public KnownConnectionsPanel(ConnectionInfos connectionInfos, ActionListener listener, MouseListener knownConnectionsTableMouseListener)
	{
		this.tableOfKnownConnections = new JTable();
		this.tableOfKnownConnections.addMouseListener( knownConnectionsTableMouseListener );
		JScrollPane pane = new JScrollPane( tableOfKnownConnections );
		this.add( pane );
		
		this.connectionInfos = connectionInfos;
		String panelTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-title", null);
		this.setBorder(BorderFactory.createTitledBorder(panelTitle));
		
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		//Set the table attributes
		knownConnectionsTableModel = new KnownConnectionsTableModel();
		sorter = new TableSorter(knownConnectionsTableModel);
		
		sorter.setTableHeader(tableOfKnownConnections.getTableHeader());
		this.tableOfKnownConnections.setModel( sorter );
		
		//Add the checkbox to use master password or not
		if( "true".equals( USE_MASTER_PASSWORD_TO_ENCRYPT_CONNECTION_INFO ) )
		{
			this.checkBoxToUseMasterPassword.setSelected( true );
		}
		this.checkBoxToUseMasterPassword.addActionListener( this );
		
		//if there are no connection infos, allow the user to choose whether to use a master password or not
		if( this.connectionInfos.getListOfConnectionInfos().size() != 0 )
		{
			this.checkBoxToUseMasterPassword.setEnabled( false );
		}
		
		JPanel p = new JPanel();
		p.add( this.checkBoxToUseMasterPassword );
		
		this.add( p );		
		
		//Select the first row if there are more than one rows
		if( this.tableOfKnownConnections.getRowCount() > 0 )
		{
				this.tableOfKnownConnections.setRowSelectionInterval(0, 0);
		}
		
		//Setup buttons panel
		String connectButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-connect-button-label", null);
		Button connectButton = new Button(connectButtonLabel, listener, connectButtonLabel, new ImageIcon(CONNECT_BUTTON_ICON_FILENAME), Boolean.TRUE);
		String deleteConnectionInfoButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-delete-connection-info-button-label", null);
		Button deleteConnectionInfoButton = new Button(deleteConnectionInfoButtonLabel, listener, deleteConnectionInfoButtonLabel, new ImageIcon(DELETE_CONNECTION_INFO_BUTTON_ICON_FILENAME), Boolean.FALSE);
		String editConnectionInfoButtonLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-edit-connection-info-button-label", null);
		Button editConnectionInfoButton = new Button(editConnectionInfoButtonLabel, listener, editConnectionInfoButtonLabel, new ImageIcon(EDIT_CONNECTION_INFO_BUTTON_ICON_FILENAME), Boolean.FALSE);
		
		ArrayList listOfButtons = new ArrayList();
		listOfButtons.add( connectButton );
		listOfButtons.add( deleteConnectionInfoButton );
		listOfButtons.add( editConnectionInfoButton );
		ButtonsPanel buttons = new ButtonsPanel( listOfButtons );

		this.add( buttons );
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if( this.checkBoxToUseMasterPassword.isSelected() )
			{
				PasswordInputWindow piw = new PasswordInputWindow(Boolean.TRUE);
				piw.show();
				
				if( piw.getPassword() != null )
				{
					//Set the master password
					this.connectionInfos.setMasterPassword( piw.getPassword() );
					PropertyManager.getInstance().setProperty("dbbrowser-ui-connection-info-window-use-master-password", "true");
				}
				else
				{
					this.checkBoxToUseMasterPassword.setSelected( false ); 
					PropertyManager.getInstance().setProperty("dbbrowser-ui-connection-info-window-use-master-password", "true");
				}
			}
			else
			{
				//Get the connection info
				PropertyManager.getInstance().setProperty("dbbrowser-ui-connection-info-window-use-master-password", "false");
			}
		}
		catch(PropertyManagementException exc)
		{
            String errorMessage = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-master-password-set-failed-error-message", null);
            JOptionPane.showMessageDialog(null ,errorMessage, TITLE, JOptionPane.ERROR_MESSAGE);													
		}
	}
	
	public Integer getSelectedRow()
	{
		int selectedRow = this.tableOfKnownConnections.getSelectedRow();

        Integer selectedRowInteger = null;
        if( selectedRow >= 0 )
        {
            selectedRowInteger = new Integer( this.sorter.modelIndex( selectedRow ) );
        }
        return selectedRowInteger;
	}
	
	public void update()
	{
		this.knownConnectionsTableModel.fireTableDataChanged();
		
		//if there are any connection infos, disable the checkbox
		if( this.connectionInfos.getListOfConnectionInfos().size() > 0 )
		{
			this.checkBoxToUseMasterPassword.setEnabled( false );
		}
	}
	
	private class KnownConnectionsTableModel extends AbstractTableModel 
	{
		private static final long serialVersionUID = 1l;
		private String[] columnNames = new String[COLUMN_COUNT];
		
		private KnownConnectionsTableModel()
		{
			String column1 = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-table-columnheader-name", null);
			String column2 = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-table-columnheader-databaseurl", null);
			String column3 = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-table-columnheader-lastused", null);

			columnNames[0] = column1;
			columnNames[1] = column2;
			columnNames[2] = column3;
		}
		
	    public String getColumnName(int col) 
	    {
	        return columnNames[col];
	    }
	    
	    public int getRowCount() 
	    { 
	    	return connectionInfos.getListOfConnectionInfos().size(); 
	    }
	    
	    public int getColumnCount() 
	    { 
	    	return COLUMN_COUNT; 
	    }
	    
	    public Object getValueAt(int row, int col) 
	    {
	    	ConnectionInfo ci = null;
	    	String cellValue = null;
	        switch (col) {
	            case 0:  
	            	ci = (ConnectionInfo)connectionInfos.getListOfConnectionInfos().get(row);
	            	cellValue = ci.getName();
	            	break;
	            case 1:
	            	ci = (ConnectionInfo)connectionInfos.getListOfConnectionInfos().get(row);
	            	cellValue = ci.getDatabaseURL();
	            	break;	            	
	            case 2:  
	            	ci = (ConnectionInfo)connectionInfos.getListOfConnectionInfos().get(row);
	            	if(ci.getLastUsed() == null)
	            	{
	            		cellValue = "";
	            	}
	            	else
	            	{
	            		cellValue = ci.getLastUsed().toString();
	            	}
	            break;
	        }
	        return cellValue;
	    }
	}	
}
