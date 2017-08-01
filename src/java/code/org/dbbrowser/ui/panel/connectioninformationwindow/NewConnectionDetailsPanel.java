package org.dbbrowser.ui.panel.connectioninformationwindow;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.drivermanager.JDBCDriverSearchUtility;
import org.dbbrowser.security.AsymmetricEncryptionEngine;
import org.dbbrowser.security.EncryptionEngineException;
import org.dbbrowser.ui.helper.JarFileFilter;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.widget.ProgressDialog;

public class NewConnectionDetailsPanel extends JPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = UIControllerForQueries.version;

	private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private static final String SAVE_CHANGES_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-save-changes-button-label", null);
    private static final String TEST_CONNECTION_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-test-connection-button-label", null);

    private static final String SAVE_CHANGES_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-save-changes-icon");
    private static final String TEST_CONNECTION_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-connection-info-window-test-connection-icon");

    private static final String NO_ALGORITHM_SELECTED = null;
    private static final String COMBO_BOX_FOR_DBMS_TYPE = "COMBO_BOX_FOR_DBMS_TYPE";
    
    private ButtonsPanel buttons = null;
	private String[] dbmsTypes = {UIControllerForQueries.ORACLE_DBMS, UIControllerForQueries.MYSQL_DBMS, UIControllerForQueries.MSSQL_DBMS};
		
	private JLabel labelForName = new JLabel();
	private JTextField textFieldForName = new JTextField();
	
	private JLabel labelForDBMSType = new JLabel();
	private JComboBox comboBoxForDBMSType = new JComboBox(dbmsTypes);

	private static final String BUTTON_FOR_JDBC_JAR_LOCATION = "BUTTON_FOR_JDBC_JAR_LOCATION";
	private JLabel labelForJarFileLocation = new JLabel();
	private JButton buttonForJarFileLocation = null;

	private JLabel labelForDatabaseURL = new JLabel();
	private JTextField textFieldForDatabaseURL = new JTextField();
	
	private JLabel labelForJDBCDriverClassName = new JLabel();
	private JComboBox comboBoxForJDBCDriverClassName = new JComboBox();
	private JDBCDriverClassNameComboBoxModel jdbcDriverClassNameComboBoxModel = new JDBCDriverClassNameComboBoxModel();
	
	private JLabel labelForUsername = new JLabel();
	private JTextField textFieldForUsername = new JTextField();
	
	private JLabel labelForPassword = new JLabel();
	private JPasswordField passwordFieldForPassword = new JPasswordField();
	
	private JLabel labelForEncryptionAlgorithm = new JLabel();
	private JComboBox comboBoxForEncryptionAlgorithm = new JComboBox();
	private EncryptionAlgorithmComboBoxModel encryptionAlgorithmComboBoxModel = new EncryptionAlgorithmComboBoxModel(ConnectionInfo.LIST_OF_ENCRYPTION_ALGORITHMS);
	
	private JLabel labelForCheckSumAlgorithm = new JLabel();
	private JComboBox comboBoxForCheckSumAlgorithm = new JComboBox();
	private CheckSumAlgorithmComboBoxModel checkSumAlgorithmComboBoxModel = new CheckSumAlgorithmComboBoxModel(ConnectionInfo.LIST_OF_CHECKSUM_ALGORITHMS);
	
	private String jarFileLocation = null;
	private ProgressDialog progressDialog = null;
	private JDBCDriverSearchUtility jdbcDriverSearchUtility = null;
	
	private JPanel panelForFormFields = new JPanel();
	
	public NewConnectionDetailsPanel(ActionListener listener)
	{
		String panelTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connection-details-panel-title", null);
		this.setBorder(BorderFactory.createTitledBorder(panelTitle));
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		this.initialize();
		
		//Setup buttons panel
		Button saveChangesButton = new Button(SAVE_CHANGES_BUTTON_LABEL, listener, SAVE_CHANGES_BUTTON_LABEL, new ImageIcon(SAVE_CHANGES_BUTTON_ICON_FILENAME), Boolean.FALSE);
        Button testConnectionButton = new Button(TEST_CONNECTION_BUTTON_LABEL, listener, TEST_CONNECTION_BUTTON_LABEL, new ImageIcon(TEST_CONNECTION_BUTTON_ICON_FILENAME), Boolean.FALSE);

        ArrayList listOfButtons = new ArrayList();
		listOfButtons.add( saveChangesButton );
        listOfButtons.add( testConnectionButton );
		this.buttons = new ButtonsPanel( listOfButtons );

		this.add( this.buttons );		
	}
	
	private void initialize()
	{
		//Setup labels and fields
		panelForFormFields.setPreferredSize(new Dimension(100, 100));
		panelForFormFields.setLayout( new GridLayout(9, 2));
		
		//Label and text field for name
		String labelForNameLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-name-label", null);
		this.labelForName.setText(labelForNameLabel + ":");
		panelForFormFields.add( this.labelForName );
		panelForFormFields.add( this.textFieldForName );
		
		//Label and combo box for dbms type
		String labelStringForDBMSType = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-dbms-type-label", null);
		this.labelForDBMSType.setText(labelStringForDBMSType + ":");
		panelForFormFields.add( this.labelForDBMSType );
		panelForFormFields.add( this.comboBoxForDBMSType );
		this.comboBoxForDBMSType.addActionListener( this );
		
		//Label and button for jar file location
		String labelForJarFileLocationLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-jarfile-location-label", null);
		this.labelForJarFileLocation.setText(labelForJarFileLocationLabel + ":");
		panelForFormFields.add( this.labelForJarFileLocation );
		buttonForJarFileLocation = new JButton();
		String labelForJarFileLocationButton = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-jarfile-location-button-label", null);
		buttonForJarFileLocation.setText(labelForJarFileLocationButton);
		buttonForJarFileLocation.setActionCommand( BUTTON_FOR_JDBC_JAR_LOCATION );
		buttonForJarFileLocation.addActionListener( this );
		panelForFormFields.add( this.buttonForJarFileLocation );
		
		//Label and text field for database url
		String labelForDatabaseURLLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-database-url-label", null);
		this.labelForDatabaseURL.setText(labelForDatabaseURLLabel + ":");
		panelForFormFields.add( this.labelForDatabaseURL );
		panelForFormFields.add( this.textFieldForDatabaseURL );
		
		//Label and combo box for jdbc driver class name		
		String labelForJDBCDriverClassNameLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-jdbcdriver-classname-label", null);
		this.labelForJDBCDriverClassName.setText(labelForJDBCDriverClassNameLabel + ":");
		panelForFormFields.add( this.labelForJDBCDriverClassName );
		comboBoxForJDBCDriverClassName.setEditable(false);
		comboBoxForJDBCDriverClassName.setModel( jdbcDriverClassNameComboBoxModel );
		panelForFormFields.add( this.comboBoxForJDBCDriverClassName );
		
		//Label and text field for username
		String labelForUsernameLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-username-label", null);
		this.labelForUsername.setText(labelForUsernameLabel + ":");
		panelForFormFields.add( this.labelForUsername );
		panelForFormFields.add( this.textFieldForUsername );	
		
		//Label and text field for password
		String labelForPasswordLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-password-label", null);
		this.labelForPassword.setText(labelForPasswordLabel + ":");
		panelForFormFields.add( this.labelForPassword );
		panelForFormFields.add( this.passwordFieldForPassword );
		
		//Label and combo box for encryption algorithm		
		String labelForEncryptionAlgorithmLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-encryption-algorithm-label", null);
		this.labelForEncryptionAlgorithm.setText(labelForEncryptionAlgorithmLabel + ":");
		panelForFormFields.add( this.labelForEncryptionAlgorithm );
		comboBoxForEncryptionAlgorithm.setEditable(false);
		comboBoxForEncryptionAlgorithm.setModel( encryptionAlgorithmComboBoxModel );
		panelForFormFields.add( this.comboBoxForEncryptionAlgorithm );		
		
		//Label and combo box for checksum algorithm		
		String labelForCheckSumAlgorithmLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-checksum-algorithm-label", null);
		this.labelForCheckSumAlgorithm.setText(labelForCheckSumAlgorithmLabel + ":");
		panelForFormFields.add( this.labelForCheckSumAlgorithm );
		comboBoxForCheckSumAlgorithm.setEditable(false);
		comboBoxForCheckSumAlgorithm.setModel( checkSumAlgorithmComboBoxModel );
		panelForFormFields.add( this.comboBoxForCheckSumAlgorithm );		

		this.add( panelForFormFields );		
		
		//Add the action listener for comboBoxForDBMSType
		this.comboBoxForDBMSType.addActionListener( this );
		this.comboBoxForDBMSType.setActionCommand(COMBO_BOX_FOR_DBMS_TYPE);
	}
	
	public void setConnectionInfo(ConnectionInfo connectionInfo, String masterPassword)
	{
		//Set the name
		this.textFieldForName.setText( connectionInfo.getName() );
		
		//Set the dbms type
		this.comboBoxForDBMSType.setSelectedItem( connectionInfo.getDBMSType() );
		
		//Set the database url
		this.textFieldForDatabaseURL.setText( connectionInfo.getDatabaseURL() );
		
		//Set the username
		this.textFieldForUsername.setText( connectionInfo.getUsername() );
		
		//Set the password by decrypting it
		try
		{
			String clearText = AsymmetricEncryptionEngine.decrypt( connectionInfo.getPassword(), masterPassword);
			this.passwordFieldForPassword.setText( clearText );
		}
		catch(EncryptionEngineException exc)
		{
			//Should never happen
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();			
		}

		//Set the jdbc driver file name
		List listOfJDBCDriverClasses = new ArrayList();
		listOfJDBCDriverClasses.add( connectionInfo.getDriverClassName() );
		this.jdbcDriverClassNameComboBoxModel = new JDBCDriverClassNameComboBoxModel(listOfJDBCDriverClasses.toArray());
		this.comboBoxForJDBCDriverClassName.setModel( this.jdbcDriverClassNameComboBoxModel );
		
		//Set the jdbc driver jar file location
		this.buttonForJarFileLocation.setText( connectionInfo.getJdbcDriverJarFileLocation().getAbsolutePath() );
		this.jarFileLocation = connectionInfo.getJdbcDriverJarFileLocation().getAbsolutePath();
		
		//Set the combobox for algorithms if it is a oracle dbms
		if( UIControllerForQueries.ORACLE_DBMS.equals( connectionInfo.getDBMSType() ) )
		{
			this.comboBoxForEncryptionAlgorithm.setSelectedItem(connectionInfo.getEncryptionAlgorithm());
			this.comboBoxForCheckSumAlgorithm.setSelectedItem(connectionInfo.getCheckSumAlgorithm());
		}
	}
	
	public ConnectionInfo getConnectionInfo(String masterPassword)
	{
		ConnectionInfo connectionInfo = null;
		String name = this.textFieldForName.getText();
		String dbmsType = (String)this.comboBoxForDBMSType.getSelectedItem();
		String databaseURL = this.textFieldForDatabaseURL.getText();
		String username = this.textFieldForUsername.getText();
		String clearTextPassword = new String( this.passwordFieldForPassword.getPassword() );
		String driverClassName = (String)this.comboBoxForJDBCDriverClassName.getSelectedItem();
		
		if( (name!=null) && (databaseURL!=null) && (username!=null) && (clearTextPassword!=null) && (driverClassName!=null) && (this.jarFileLocation!=null) )
		{
			if( (!"".equals(name)) && (!"".equals(databaseURL)) && (!"".equals(username)) && (!"".equals(clearTextPassword)) && (!"".equals(driverClassName)) && (!"".equals(this.jarFileLocation)))
			{
				try
				{
					//Encrypt the password
					String encryptedPassword = AsymmetricEncryptionEngine.encrypt(clearTextPassword, masterPassword);
					
					if( UIControllerForQueries.ORACLE_DBMS.equals(this.comboBoxForDBMSType.getSelectedItem()) )
					{
						//if this is a Oracle DBMS, then get the encyption algorithm and checksum algorithm
						String encryptionAlgorithm = (String)this.comboBoxForEncryptionAlgorithm.getSelectedItem(); 
						String checksumAlgorithm = (String)this.comboBoxForCheckSumAlgorithm.getSelectedItem(); 
						connectionInfo = new ConnectionInfo(
								driverClassName,
								new File( this.jarFileLocation ),
								name,
								dbmsType,
								databaseURL,
								username,
								encryptedPassword,
								encryptionAlgorithm,
								checksumAlgorithm
								);					
					}
					else
					{
						connectionInfo = new ConnectionInfo(
								driverClassName,
								new File( this.jarFileLocation ),
								name,
								dbmsType,
								databaseURL,
								username,
								encryptedPassword,
								null,
								null
								);					
					}
				}
				catch(EncryptionEngineException exc)
				{
					//Should never happen
					Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
					exc.printStackTrace();								
				}
			}			
		}
		
		return connectionInfo;
	}
	
	public void clearFields()
	{
		this.textFieldForName.setText("");
		this.textFieldForDatabaseURL.setText("");
		this.textFieldForUsername.setText("");
		this.passwordFieldForPassword.setText("");
		this.textFieldForName.setText("");
		this.buttonForJarFileLocation.setText( InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-jarfile-location-label", null) );
		jdbcDriverClassNameComboBoxModel = new JDBCDriverClassNameComboBoxModel();
		this.comboBoxForJDBCDriverClassName.setModel( jdbcDriverClassNameComboBoxModel );
		this.comboBoxForEncryptionAlgorithm.setSelectedIndex(0);
		this.comboBoxForCheckSumAlgorithm.setSelectedIndex(0);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( COMBO_BOX_FOR_DBMS_TYPE.equals(e.getActionCommand()) )
		{
			String selectedDBMS = (String)this.comboBoxForDBMSType.getSelectedItem();
			if( UIControllerForQueries.ORACLE_DBMS.equals(selectedDBMS) )
			{
				this.panelForFormFields.add( this.labelForEncryptionAlgorithm );
				this.panelForFormFields.add( this.comboBoxForEncryptionAlgorithm );
				this.panelForFormFields.add( this.labelForCheckSumAlgorithm );
				this.panelForFormFields.add( this.comboBoxForCheckSumAlgorithm );
			}
			else
			{
				this.panelForFormFields.remove( this.labelForEncryptionAlgorithm );
				this.panelForFormFields.remove( this.comboBoxForEncryptionAlgorithm );
				this.panelForFormFields.remove( this.labelForCheckSumAlgorithm );
				this.panelForFormFields.remove( this.comboBoxForCheckSumAlgorithm );
			}
			this.updateUI();
		}
		
		if( BUTTON_FOR_JDBC_JAR_LOCATION.equals(e.getActionCommand()) )
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setMultiSelectionEnabled( false );
			jfc.setFileFilter( new JarFileFilter() );
			int selectedOption = jfc.showOpenDialog(null);
			if(selectedOption == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = jfc.getSelectedFile();
				this.jarFileLocation = selectedFile.getAbsolutePath();
				this.buttonForJarFileLocation.setText( this.jarFileLocation );
				
				try
				{
					//Setup the progress dialog
					JarFile jarFile = new JarFile( this.jarFileLocation );
					Enumeration enumeration = jarFile.entries();
					int totalNumberOfFilesInJarFile = 0;
					while( enumeration.hasMoreElements() )
					{
						totalNumberOfFilesInJarFile ++;
						enumeration.nextElement();
					}
					
					//Setup the progress bar
					String progressBarTitle = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
					String progressBarLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-progress-bar-label", null);;
					progressDialog = new ProgressDialog(progressBarTitle, progressBarLabel, new Integer(0), new Integer(totalNumberOfFilesInJarFile));
					progressDialog.show();
					
					//Get the list of jdbc drivers in the jar file 
					jdbcDriverSearchUtility = new JDBCDriverSearchUtility(new File( this.jarFileLocation ), progressDialog, this);
					jdbcDriverSearchUtility.start();
				}
				catch(IOException exc)
				{
					String cannotLoadJarFile = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connection-details-panel-exception-could-not-load-jdbc-driver-jar-file", null);
					JOptionPane.showMessageDialog(null, cannotLoadJarFile, TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void stateChanged(ChangeEvent e)
	{
		//Close the progress dialog
		this.progressDialog.setValue( this.progressDialog.getMaxValue() );	
		this.progressDialog.close();
		this.progressDialog = null;
		
		//Set the values in the combo box
		try
		{
			List listOfDriverClassesInJarFile = this.jdbcDriverSearchUtility.getListOfJDBCDriverClasses();
			
			//if no jdbc drivers found, raise an error message
			if( listOfDriverClassesInJarFile== null || listOfDriverClassesInJarFile.isEmpty() )
			{
				String noJDBCDriverInJARFile = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connections-details-panel-no-jdbc-driver-in-jar-file-error-message", null);
				JOptionPane.showMessageDialog(null, noJDBCDriverInJARFile, "DBBrowser", JOptionPane.ERROR_MESSAGE);				
			}
			
			this.jdbcDriverClassNameComboBoxModel = new JDBCDriverClassNameComboBoxModel(listOfDriverClassesInJarFile.toArray());
			this.comboBoxForJDBCDriverClassName.setModel( this.jdbcDriverClassNameComboBoxModel );			
		}
		catch(IOException exc)
		{
			String cannotLoadJarFile = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-new-connection-details-panel-exception-could-not-load-jdbc-driver-jar-file", null);
			JOptionPane.showMessageDialog(null, cannotLoadJarFile, "DBBrowser", JOptionPane.ERROR_MESSAGE);
		}
	}		
	
	private class JDBCDriverClassNameComboBoxModel extends DefaultComboBoxModel
	{
		private static final long serialVersionUID = 1l;
		
		public JDBCDriverClassNameComboBoxModel(Object[] items)
		{
			super(items);
		}
		
		public JDBCDriverClassNameComboBoxModel()
		{
			super();
		}		
	}
	
	private class EncryptionAlgorithmComboBoxModel extends DefaultComboBoxModel
	{
		private static final long serialVersionUID = 1l;
		
		public EncryptionAlgorithmComboBoxModel(Object[] items)
		{
			super();
			
			super.addElement( NO_ALGORITHM_SELECTED );
			
			for(int i=0; i < items.length; i++)
			{
				super.addElement( items[i] );
			}
		}
		
		public EncryptionAlgorithmComboBoxModel()
		{
			super();
		}		
	}	
	
	private class CheckSumAlgorithmComboBoxModel extends DefaultComboBoxModel
	{
		private static final long serialVersionUID = 1l;
		
		public CheckSumAlgorithmComboBoxModel(Object[] items)
		{
			super();
			
			super.addElement( NO_ALGORITHM_SELECTED );
			
			for(int i=0; i < items.length; i++)
			{
				super.addElement( items[i] );
			}
		}
		
		public CheckSumAlgorithmComboBoxModel()
		{
			super();
		}		
	}	
}
