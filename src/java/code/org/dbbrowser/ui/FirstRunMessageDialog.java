package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.panel.ButtonsPanel;

public class FirstRunMessageDialog implements ActionListener, ItemListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private static final String OK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-first-run-message-dialog-ok-button-label", null);;
    private String OK_BUTTON_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");

    private JDialog dialog = null;
    private ButtonsPanel buttonPanel = null;
    private JPanel mainPanel = new JPanel();

    public FirstRunMessageDialog()
    {
        initialize();
    }

    public void show()
    {
        this.dialog.setVisible( true );
    }

    public void actionPerformed(ActionEvent e)
    {
        //Close the message dialog
        this.dialog.pack();
        this.dialog.setVisible( false );
        this.dialog = null;
    }

    private void initialize()
    {
        //Set null layout manager
        mainPanel.setLayout( null );

        //Add the title
        String firstRunMessageTitle = InternationalizationManager.getInstance().getMessage("dbbrowser-ui", "dbbrowser-ui-first-run-message-dialog-title", null);
        JLabel firstRunMessageTitleLabel = new JLabel(firstRunMessageTitle);
        firstRunMessageTitleLabel.setForeground( Color.RED );
        firstRunMessageTitleLabel.setBorder( BorderFactory.createEtchedBorder() );
        firstRunMessageTitleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        firstRunMessageTitleLabel.setBounds(0, 0, 600, 30);
        mainPanel.add( firstRunMessageTitleLabel );

        //Setup the text area and the scrollpane
        //Read the first run file and show it in the text area
        String messageFileName = PropertyManager.getInstance().getProperty("dbbrowser-first-run-message-filename");
        JTextPane editorPanel = null;
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader(messageFileName) );
            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while( line != null )
            {
               buffer.append( line + "\n");
               line = reader.readLine();
            }
            reader.close();

            editorPanel = new JTextPane();
            editorPanel.setText( buffer.toString() );
            JScrollPane pane = new JScrollPane(editorPanel);
            pane.setBounds(0, 30, 590, 250);
            mainPanel.add( pane );
        }
        catch(FileNotFoundException exc)
        {
            //Cant do anything - show an error message
            editorPanel.setText( exc.getMessage() );
        }
        catch(IOException exc)
        {
           //Cant do anything - show an error message
            editorPanel.setText( exc.getMessage() );
        }

        //Set the check box
        String checkBoxLabel = InternationalizationManager.getInstance().getMessage("dbbrowser-ui", "dbbrowser-ui-first-run-message-dialog-check-box-label", null);
        JCheckBox checkBox = new JCheckBox(checkBoxLabel);
        checkBox.setBounds(190, 280, 300, 30);
        checkBox.addItemListener( this );
        mainPanel.add( checkBox );

        //Setup the dialog
        this.dialog = new JDialog();
        this.dialog.setTitle( TITLE );
        this.dialog.setModal( true );
        this.dialog.setSize(600, 400);
        this.dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        this.dialog.setResizable( false );
        this.dialog.setLocationRelativeTo( null );
        this.dialog.getContentPane().add( mainPanel );
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.DESELECTED)
        {
            //Remove the buttons from the panel 
            this.mainPanel.remove( buttonPanel );
            this.mainPanel.updateUI();
        }

        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            //Add the panel for the button
            Button okButton = new Button(OK_BUTTON_LABEL, this, OK_BUTTON_LABEL, new ImageIcon(OK_BUTTON_ICON_FILE_NAME), Boolean.TRUE);
            List listOfButtons = new ArrayList();
            listOfButtons.add( okButton );
            buttonPanel = new ButtonsPanel(listOfButtons);
            buttonPanel.setBounds(0, 320, 590, 50);
            mainPanel.add(buttonPanel);
            this.mainPanel.updateUI();
        }
    }
}
