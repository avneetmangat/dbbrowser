package org.dbbrowser.ui.panel.connectioninformationwindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.dbbrowser.ui.UIControllerForQueries;

/**
 * This class presents the user with a UI which is used to enter a password
 * @author amangat
 *
 */
public class PasswordInputWindow extends JDialog implements ActionListener
{
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
	
	private static final long serialVersionUID = UIControllerForQueries.version;
    private static final String PASSWORD_1_INPUT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-type-password-label", null);
    private static final String PASSWORD_2_INPUT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-re-type-password-label", null);
    private static final String PASSWORDS_DONT_MATCH_ERROR_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-known-connections-panel-passwords-dont-match", null);
    private static final String OK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-first-run-message-dialog-ok-button-label", null);
    private static final String OK_BUTTON_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");
    
    private JLabel label1 = new JLabel(PASSWORD_1_INPUT_LABEL);
    private JPasswordField passwordField1 = new JPasswordField();
    private JLabel label2 = new JLabel(PASSWORD_2_INPUT_LABEL);
    private JPasswordField passwordField2 = new JPasswordField();
    private JButton okButton = new JButton(OK_BUTTON_LABEL);
    private String password = null;
    private boolean confirmPassword = true;
	
	public PasswordInputWindow(Boolean confirmPassword)
	{
		this.setTitle( TITLE );
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setResizable( false );
		this.setModal( true );
		
		this.confirmPassword = confirmPassword.booleanValue();
		
		this.getContentPane().setLayout(null);
		
		this.label1.setBounds(30, 30, 130, 30);
		this.passwordField1.setBounds(160, 30, 120, 30);
		this.label2.setBounds(30, 90, 130, 30);
		this.passwordField2.setBounds(160, 90, 120, 30);
		this.okButton.setBounds(100, 130, 100, 30);
		
		this.passwordField1.addKeyListener( new PasswordFieldKeyAdapter() );
		this.passwordField2.addKeyListener( new PasswordFieldKeyAdapter() );
		this.okButton.addActionListener( this );
		this.okButton.setIcon( new ImageIcon(OK_BUTTON_ICON_FILENAME) );
		this.okButton.setEnabled( false );
		
		this.getContentPane().add(label1);
		this.getContentPane().add(passwordField1);
		
		//If confirmation is required, add second field
		if( this.confirmPassword )
		{
			this.getContentPane().add(label2);
			this.getContentPane().add(passwordField2);
		}
		this.getContentPane().add(okButton);
		
		this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String password1 = new String(this.passwordField1.getPassword());
		String password2 = new String(this.passwordField2.getPassword());
		
		//if confirmation of 2 passwords required, do the check
		if( this.confirmPassword )
		{
			if( password1 != null && password2 != null && (!"".equals(password1)) && (!"".equals(password2)) && password1.equals( password2 ))
			{
				this.password = password1;
				this.hide();
				this.pack();
			}
			else
			{
				this.okButton.setEnabled( false );
				this.passwordField1.setText( "" );
				this.passwordField2.setText( "" );
				this.passwordField1.requestFocus();
				JOptionPane.showMessageDialog(null, PASSWORDS_DONT_MATCH_ERROR_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			this.password = password1;
			this.hide();
			this.pack();
		}
	}
	
	private class PasswordFieldKeyAdapter extends KeyAdapter
	{
		public void keyTyped(KeyEvent e)
		{
			if( confirmPassword )
			{
				if( passwordField1.getPassword().length > 0 && passwordField2.getPassword().length > 0 )
				{
					okButton.setEnabled( true );
				}
				else
				{
					okButton.setEnabled( false );
				}
			}
			else
			{
				if( passwordField1.getPassword().length > 0 )
				{
					okButton.setEnabled( true );
				}
				else
				{
					okButton.setEnabled( false );
				}
			}
		}
	}
}
