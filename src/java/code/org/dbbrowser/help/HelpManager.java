package org.dbbrowser.help;

import infrastructure.logging.Log;
import javax.help.HelpSet;
import javax.help.HelpBroker;
import javax.help.CSH;
import javax.swing.*;
import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * Sets up help classes and context sensitive help
 */
public class HelpManager
{
    private static HelpManager helpManager = new HelpManager();
    private HelpSet helpSet = null;
    private HelpBroker helpBroker = null;

    /**
     * Private constructer as it is a singleton
     */
    private HelpManager()
    {
       initializeHelpManager();
    }

    /**
     * Returns the singleton HelpManager
     * @return
     */
    public static HelpManager getInstance()
    {
        return helpManager;
    }

    /**
     * Setup ContextSensitiveHelp (CSH)
     * @param trigger
     * @param target
     * @param targetInJavaHelpSet
     */
    public void registerCSH(AbstractButton trigger, Component target, String targetInJavaHelpSet)
    {
        helpBroker.enableHelpKey(target, targetInJavaHelpSet, helpSet, "javax.help.SecondaryWindow", "");
        helpBroker.enableHelp(target, targetInJavaHelpSet, helpSet);
        trigger.addActionListener(new CSH.DisplayHelpAfterTracking(helpBroker));
    }

    /**
     * Returns the ActionListener for 'Help' button
     * @return
     */
    public ActionListener getActionListenerForHelpEvents()
    {
        return new CSH.DisplayHelpFromSource( helpBroker );
    }

    /**
     * Sets up the help manager
     */
    private void initializeHelpManager()
    {
        Log.getInstance().infoMessage("Starting java help...", this.getClass().getName());

        //Find the HelpSet file and create the HelpSet object:
        String helpHS = "DBBrowser.hs";
        ClassLoader cl = this.getClass().getClassLoader();
        try
        {
            URL hsURL = HelpSet.findHelpSet(cl, helpHS);
            helpSet = new HelpSet(cl, hsURL);
            helpBroker = helpSet.createHelpBroker();
        }
        catch (Exception ee)
        {
    //	   Say what the exception really is
            Log.getInstance().infoMessage("HelpSet " + ee.getMessage(), this.getClass().getName());
            Log.getInstance().infoMessage("HelpSet "+ helpHS +" not found", this.getClass().getName());
        }
    }
}
