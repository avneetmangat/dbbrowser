package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import javax.swing.*;

public class AboutWindow implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    private String ABOUT_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-about-window-tab-label", null);
    private String CREDITS_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-credits-window-tab-label", null);
    private String LICENSE_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-license-window-tab-label", null);
    private String CONTRIBUTORS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-contributors-window-tab-label", null);
    private String OK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-license-window-ok-button-label", null);

    private String README_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-readme-filename");
    private String CREDITS_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-credits-filename");
    private String LICENSE_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-license-filename");
    private String CONTRIBUTORS_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-contributors-filename");

    private String ABOUT_TAB_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-about-tab-icon");
    private String CREDITS_TAB_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-credits-tab-icon");
    private String LICENSE_TAB_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-license-tab-icon");
    private String CONTRIBUTORS_TAB_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-about-window-contributors-tab-icon");

    private String OK_BUTTON_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");

    private JPanel mainPanel = new JPanel();
    private JDialog dialog = null;
    private JTextArea textAreaForData = null;
    private JScrollPane panelForScrollPane = null;
    private ButtonsPanel navigationButonsPanel = null;
    private JTabbedPane tabbedPane = new JTabbedPane();

    public AboutWindow()
    {
        initialize();
    }

    public void show()
    {
        this.dialog.setVisible( true );
    }

    public void actionPerformed(ActionEvent e)
    {
        this.dialog.pack();
        this.dialog.dispose();
        this.dialog = null;
    }

    private void initialize()
    {
        //Setup the tabbed pane
        String dbbrowserReadmeFileContents = this.readFromFile( README_FILE_NAME );
        this.textAreaForData = new JTextArea(dbbrowserReadmeFileContents);
        this.textAreaForData.setLineWrap( true );
        this.textAreaForData.setWrapStyleWord( true );
        this.panelForScrollPane = new JScrollPane(textAreaForData);
        this.tabbedPane.addTab(ABOUT_TAB_LABEL, new ImageIcon(ABOUT_TAB_ICON_FILE_NAME), this.panelForScrollPane);

        String creditsFileContents = this.readFromFile( CREDITS_FILE_NAME );
        this.textAreaForData = new JTextArea(creditsFileContents);
        this.textAreaForData.setLineWrap( true );
        this.textAreaForData.setWrapStyleWord( true );
        this.panelForScrollPane = new JScrollPane(textAreaForData);
        this.tabbedPane.addTab(CREDITS_TAB_LABEL, new ImageIcon(CREDITS_TAB_ICON_FILE_NAME), this.panelForScrollPane);

        String licenseFileContents = this.readFromFile( LICENSE_FILE_NAME );
        this.textAreaForData = new JTextArea(licenseFileContents);
        this.textAreaForData.setLineWrap( true );
        this.textAreaForData.setWrapStyleWord( true );
        this.panelForScrollPane = new JScrollPane(textAreaForData);
        this.tabbedPane.addTab(LICENSE_LABEL, new ImageIcon(LICENSE_TAB_ICON_FILE_NAME), this.panelForScrollPane);

        //Contributors tab
        String contributorsFileContents = this.readFromFile( CONTRIBUTORS_FILE_NAME );
        this.textAreaForData = new JTextArea(contributorsFileContents);
        this.textAreaForData.setLineWrap( true );
        this.textAreaForData.setWrapStyleWord( true );
        this.panelForScrollPane = new JScrollPane(textAreaForData);
        this.tabbedPane.addTab(CONTRIBUTORS_LABEL, new ImageIcon(CONTRIBUTORS_TAB_ICON_FILE_NAME), this.panelForScrollPane);

        //Setup the main panel
        this.mainPanel.setLayout( new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS) );
        this.mainPanel.add( this.tabbedPane );

        //Build the list of buttons for the navigation buttons panel
        List listOfNavigationButtons = new ArrayList();
        Button okButton = new Button(OK_BUTTON_LABEL, this, OK_BUTTON_LABEL, new ImageIcon(OK_BUTTON_ICON_FILE_NAME), Boolean.FALSE);
        listOfNavigationButtons.add( okButton );

        //Setup the navigation panel
        navigationButonsPanel = new ButtonsPanel( listOfNavigationButtons );
        this.mainPanel.add( navigationButonsPanel );

        //Setup the frame
        this.dialog = new JDialog();
        this.dialog.setTitle( TITLE );
        this.dialog.setModal( true );
        this.dialog.setSize(600, 400);
        this.dialog.setLocationRelativeTo( null );
        this.dialog.getContentPane().add( this.mainPanel );
   }

    private String readFromFile(String filename)
    {
        String fileContents = "";
        StringBuffer buffer = new StringBuffer();
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader(filename) );
            String line = reader.readLine();
            while( line != null )
            {
               buffer.append( line + "\n");
               line = reader.readLine();
            }
            reader.close();
            fileContents = buffer.toString();
        }
        catch(FileNotFoundException exc)
        {
            //Cant do anything - show an error message
            fileContents = exc.getMessage();
        }
        catch(IOException exc)
        {
           //Cant do anything - show an error message
            fileContents = exc.getMessage();
        }
        return fileContents;
    }
}
