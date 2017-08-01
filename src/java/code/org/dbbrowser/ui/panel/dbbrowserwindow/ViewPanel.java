package org.dbbrowser.ui.panel.dbbrowserwindow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.db.engine.model.View;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForUpdates;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.widget.SyntaxHighlighterPanel;
import org.dbbrowser.ui.widget.Table;

public class ViewPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private String VIEW_DEFINITON_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-db-views-view-definiton-tab-label", null);
    private String VIEW_DATA_TAB_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-db-views-view-data-tab-label", null);
    private String UPDATE_VIEW_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-update-view-button-label", null);
    private String UPDATE_VIEW_DEFINITON_BUTTON_ICON = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");
    
    private DBTable dbTable = null;
    private Table resultsTable = null;
    private JTabbedPane pane = new JTabbedPane();
    private View view  = null;
    private SyntaxHighlighterPanel viewDefinitionPanel = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    
    public ViewPanel(View view, DBTable dbTable, UIControllerForUpdates uiControllerForUpdates)
    {
    	this.view = view;
    	this.dbTable = dbTable;
    	this.uiControllerForUpdates = uiControllerForUpdates;
        resultsTable = new Table(null, null);
    }
    
    public void initialize()
    {
    	this.setLayout(new BorderLayout());
        DBTableDataTableModel dbTableDataTableModel = new DBTableDataTableModel( this.dbTable );

        //Display the view definiton
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout( new BoxLayout(viewPanel, BoxLayout.Y_AXIS) );
        viewPanel.setBorder( BorderFactory.createTitledBorder( this.view.getViewName() ) );
        this.viewDefinitionPanel = new SyntaxHighlighterPanel();
        this.viewDefinitionPanel.setText( this.view.getViewDefinition() );
        viewPanel.add( this.viewDefinitionPanel );
        
        //Add the button to update the view
        Button buttonToUpdateView = new Button(UPDATE_VIEW_BUTTON_LABEL, this, UPDATE_VIEW_BUTTON_LABEL, new ImageIcon(UPDATE_VIEW_DEFINITON_BUTTON_ICON), Boolean.TRUE);
        List listOfButtons = new ArrayList();
        listOfButtons.add( buttonToUpdateView );
        ButtonsPanel buttonsPanel = new ButtonsPanel( listOfButtons );
        viewPanel.add( buttonsPanel );
        pane.addTab( VIEW_DEFINITON_TAB_LABEL, viewPanel );
        
        //Display the results as a table
        Object[] messageParameters = new Object[]{"0", this.dbTable.getNumberOfRowsInTable(), this.dbTable.getNumberOfRowsInTable()};
        String title = InternationalizationManager.getInstance().getMessage("dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-panel-title", messageParameters);
        resultsTable.setBorder( BorderFactory.createTitledBorder( this.view.getViewName() + " - " + title ) );
        pane.addTab( VIEW_DATA_TAB_LABEL, this.resultsTable );
        this.add( pane );
        resultsTable.initializeTable( dbTableDataTableModel );
        
        //Add the key listener for syntax highlighter panel
        this.viewDefinitionPanel.getTextPane().addKeyListener( new RunSQLKeyBinding() );
    }
    
    public void actionPerformed(ActionEvent e)
    {
    	updateView();
    }
    
    private void updateView()
    {
    	//Update the view 
    	try
    	{
    		View updatedView = new View(this.view.getSchemaName(), this.view.getViewName(), this.viewDefinitionPanel.getText());
    		this.uiControllerForUpdates.updateViewDefinition( updatedView );
    	}
    	catch(DBEngineException exc)
    	{
            Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
            String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
            JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
    	}    	
    }
    
    private class RunSQLKeyBinding extends KeyAdapter
    {
    	public void keyPressed(KeyEvent e)
    	{
    		//if CTRL-Enter is pressed, run SQL
    		if( e.getKeyCode() == 10 && e.getModifiers() == 2)
    		{
    			updateView();
    		}
    	}
    }    
}
