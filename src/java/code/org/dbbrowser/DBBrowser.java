package org.dbbrowser;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.lookandfeel.LookAndFeelHandler;
import infrastructure.propertymanager.PropertyManagementException;
import infrastructure.propertymanager.PropertyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.dbbrowser.ui.ConnectionInformationWindow;

public class DBBrowser
{
    public static void main(String[] args)
    {
        (new DBBrowser()).run();
    }

    /**
     * Starts the DBBrowser.  Does the following:<br />
     * 1. Initialize the property manager<br />
     * 2. Initialize the logger<br />
     * 3. Initialize the Message Bundle for internationalization<br />
     * 4. Set the default look and feel.  Gets the look and feel from the property file<br />
     * 6. Show the connection info window<br />
     */
    public void run()
    {
        System.out.println("Starting DBBrowser...");

        //1. Initialize the property manager
        try
        {
            File propsfile = new File("src/properties/db browser.properties");
            System.out.println("*** Loading property file: " + propsfile.getAbsolutePath() + " ***");
            PropertyManager.getInstance().initializeProperties( propsfile );
            System.out.println("*** Loaded properties ***");
        }
        catch(PropertyManagementException exc)
        {
            System.err.println("PropertyManager property file not found at location - src/properties/db browser.properties" );
            System.exit(-1);
        }


        //2. Initialize the log manager
        String log4jPropertyFilename = PropertyManager.getInstance().getProperty("dbbrowser-log4j-properties-file");
        Log.getInstance().initialize( log4jPropertyFilename );
        Log.getInstance().debugMessage("Logging has been initialized", DBBrowser.class.getName());


        //3. Initialize the Message Bundle for internationalization
        String pathForMessageResourceBundle = PropertyManager.getInstance().getProperty( "dbbrowser-i18n-properties" );
        try
        {
            InternationalizationManager.getInstance().initializeInternationalizationManager( new FileInputStream(pathForMessageResourceBundle) );
        }
        catch(IOException exc)
        {
            System.err.println("Message resource bundle not found at location - " + pathForMessageResourceBundle + ", " + exc.getMessage() );
            System.exit(-2);
        }

        //4. Set the default look and feel
        LookAndFeelHandler.getInstance().setThemepackInstallationDirectory("lib");
        //LookAndFeelHandler.getInstance().setLookAndFeel( PropertyManager.getInstance().getProperty("dbbrowser.ui.lookandfeel") );
        Log.getInstance().debugMessage("Using " + LookAndFeelHandler.getInstance().getCurrentLookAndFeel() + " look and feel", DBBrowser.class.getName());


        //5. Show the connection information window
        ConnectionInformationWindow connectionInformationWindow = new ConnectionInformationWindow();
        connectionInformationWindow.show();
    }
}
