package org.dbbrowser.ui.widget;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class shows the popup menu on a trigger.  It is a convenience class
 */
public class BasicPopupListener extends MouseAdapter
{
    private JPopupMenu jPopupMenu = null;

    /**
     * Constructer
     * @param jPopupMenu
     */
    public BasicPopupListener(JPopupMenu jPopupMenu)
    {
        this.jPopupMenu = jPopupMenu;
    }

    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}