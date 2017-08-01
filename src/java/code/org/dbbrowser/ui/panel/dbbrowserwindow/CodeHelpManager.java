package org.dbbrowser.ui.panel.dbbrowserwindow;

import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.widget.ProgressDialog;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

/**
 * Singleton class which manages building of code help.  It is a singleton, so code help is built only once for a database
 */
public class CodeHelpManager
{
    private static CodeHelpManager codeHelpManager = new CodeHelpManager();
    private Map mapOfDatabaseNameToJPopupMenu = new HashMap();
    private CodeHelpBuilder builder = null;

    private CodeHelpManager()
    {
    }

    /**
     * Returns the singleton CodeHelpManager
     * @return
     */
    public static CodeHelpManager getInstance()
    {
        return codeHelpManager;
    }

    public Boolean hasPopupMenuBeenbuild(String schemaName)
    {
        Object cachedPopupMenu = mapOfDatabaseNameToJPopupMenu.get( schemaName );
        boolean hasPopupMenuBeenBuild = false;

        if( cachedPopupMenu != null )
        {
            hasPopupMenuBeenBuild = true;
        }
        return new Boolean(hasPopupMenuBeenBuild);
    }

    /**
     * Returns the popup menu for a database.  Caches the pop up menu so it is built only once
     * @param popup menu
     * @return
     */
    public JPopupMenu getPopupMenu(UIControllerForQueries uicontroller, ActionListener l, ProgressDialog progressDialog, ChangeListener al)
    {
        //if the popup menu has already been built, return the cached version
        JPopupMenu pm = null;
        Object cachedPopupMenuObject = mapOfDatabaseNameToJPopupMenu.get( uicontroller.getConnectionInfo().getName() );
        if( cachedPopupMenuObject == null )
        {
            //build the menu
            //Start the code help builder
            builder = new CodeHelpBuilder(uicontroller, l, progressDialog, al);
            builder.start();

            pm = builder.getJPopupMenu();
            mapOfDatabaseNameToJPopupMenu.put(uicontroller.getConnectionInfo().getName(), pm);
        }
        else
        {
            //return the cached popup menu
            pm = (JPopupMenu)cachedPopupMenuObject;
        }

        return pm;
    }

    /**
     * Returns the cached popup menu
     * @param schemaName
     * @return
     */
    public JPopupMenu getPopupMenu(String schemaName)
    {
        JPopupMenu popupMenu =  builder.getJPopupMenu();
        mapOfDatabaseNameToJPopupMenu.put( schemaName, popupMenu );

        return popupMenu;
    }
}

