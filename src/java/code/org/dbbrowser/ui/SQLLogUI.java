package org.dbbrowser.ui;

import infrastructure.propertymanager.PropertyManager;
import infrastructure.internationalization.InternationalizationManager;
import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.dbbrowser.db.engine.SQLLog;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.panel.ButtonsPanel;

/**
 * Displays a log of all SQL statements
 */
public class SQLLogUI implements Observer, ActionListener
{
    private static final String SQL_LOG_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-sql-log-window-icon");
    private static final String CLEAR_SQL_LOG_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-sql-log-window-clear-log-button-icon");

    private static final String SQL_LOG_WINDOW_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-sql-log-window-title", null);
    private static final String SQL_LOG_CLEAR_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-sql-log-window-clear-log-button-label", null);

    private JFrame frame = new JFrame();
    private JTextArea sqlLog = new JTextArea();
    private JScrollPane paneForSQL = null;

    /**
     * Constructer
     */
    public SQLLogUI()
    {
        initialize();
    }

    /**
     * Show the UI
     */
    public void show()
    {
        this.frame.show();
    }

    //Hide the SQL Logger UI
    public void hide()
    {
        this.frame.hide();
    }

    /**
     * Called by the observable to inform the observer when a change occurs
     * @param observable
     * @param args
     */
    public void update(Observable observable, Object args)
    {
       sqlLog.append( args.toString() + "\n\n" );
       this.paneForSQL.getVerticalScrollBar().setValue( this.paneForSQL.getVerticalScrollBar().getMaximum() + 1000 );
    }

    public void actionPerformed(ActionEvent e)
    {
        this.sqlLog.setText("");
    }

    private void initialize()
    {
        //Setup the frame
        this.frame.setTitle(SQL_LOG_WINDOW_TITLE);
        this.frame.setSize(700, 300);
        this.frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
        this.frame.setIconImage( (new ImageIcon(SQL_LOG_ICON_FILENAME)).getImage() );
        this.frame.toBack();

        //Setup the widgets
        this.frame.getContentPane().setLayout( new BoxLayout( this.frame.getContentPane(), BoxLayout.PAGE_AXIS ));
        this.sqlLog.setEditable( false );
        this.sqlLog.setLineWrap( true );
        this.paneForSQL = new JScrollPane( this.sqlLog );
        paneForSQL.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        this.frame.getContentPane().add( paneForSQL );

        //Add the button to clear the log
        Button buttonToClearSQLLog = new Button(SQL_LOG_CLEAR_BUTTON_LABEL, this, SQL_LOG_CLEAR_BUTTON_LABEL, new ImageIcon(CLEAR_SQL_LOG_ICON_FILENAME), Boolean.FALSE);
        List listOfButtons = new ArrayList();
        listOfButtons.add( buttonToClearSQLLog );
        ButtonsPanel buttonsPanel = new ButtonsPanel(listOfButtons);
        this.frame.getContentPane().add( buttonsPanel );

        //Add the UI as an observor to the SQLLog
        SQLLog.getInstance().addObserver( this );
    }
}
