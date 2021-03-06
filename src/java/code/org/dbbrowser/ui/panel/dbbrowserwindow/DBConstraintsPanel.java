package org.dbbrowser.ui.panel.dbbrowserwindow;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dbbrowser.db.engine.exception.DBEngineException;
import org.dbbrowser.db.engine.model.DBTable;
import org.dbbrowser.drivermanager.ConnectionInfo;
import org.dbbrowser.ui.UIControllerForQueries;
import org.dbbrowser.ui.UIControllerForUpdates;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.widget.Table;

public class DBConstraintsPanel extends JPanel implements ActionListener, ChangeListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private String addNewRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-add-new-record-icon");
    private String deleteRecordIconFileName = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-delete-record-icon");

    private Icon iconForAddNewRecordButton = new ImageIcon( addNewRecordIconFileName );
    private Icon iconForDeleteRecordButton = new ImageIcon( deleteRecordIconFileName );

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
    private static final String ADD_NEW_CONSTRAINT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-constraints-tab-add-new-constraint-button-label", null);;
    private static final String REMOVE_CONSTRAINT_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-constraints-tab-remove-constraint-button-label", null);;

    private UIControllerForQueries uiControllerForQueries = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    
    private Table tableForConstraints = null;
    private ButtonsPanel buttonsPanel = null;

    private boolean constraintsSetupCompleted = false;

    public DBConstraintsPanel(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates)
    {
        this.uiControllerForQueries = uiControllerForQueries;
        this.uiControllerForUpdates = uiControllerForUpdates;
        initialize();
    }

    public void refresh()
    {
    	showConstraints();
    }

    public void stateChanged(ChangeEvent e)
    {
        //Setup indexes only if this panel has been selected
        Object source = e.getSource();
        if( (source != null) && (source instanceof JTabbedPane) )
        {
            //Get the selected tab
            JTabbedPane pane = (JTabbedPane)source;
            Component selectedComponent = pane.getSelectedComponent();
            if( (selectedComponent != null) && (selectedComponent instanceof DBConstraintsPanel) )
            {
                //If indexes have not been retrieved, retrieve them and cache them
                if( ! constraintsSetupCompleted )
                {
                	showConstraints();
                }
            }
        }
    }

    private void showConstraints()
    {
            try
            {
                //Remove existing panels
                if( this.tableForConstraints != null )
                {
                    this.remove( this.tableForConstraints );
                    this.remove( this.buttonsPanel );
                }

                DBTable dbTableOfConstraints = this.uiControllerForQueries.listConstraints();
                
                //Add the table
                this.tableForConstraints = new Table( this.uiControllerForQueries, this.uiControllerForUpdates );
                this.add( tableForConstraints );

                //Set the buttons panel
                Button addNewIndexButton = new Button(ADD_NEW_CONSTRAINT_BUTTON_LABEL,this, ADD_NEW_CONSTRAINT_BUTTON_LABEL, iconForAddNewRecordButton, Boolean.TRUE);
                Button removeIndexButton = new Button(REMOVE_CONSTRAINT_BUTTON_LABEL,this, REMOVE_CONSTRAINT_BUTTON_LABEL, iconForDeleteRecordButton, Boolean.FALSE);
                List listOfButtons = new ArrayList();
                listOfButtons.add( addNewIndexButton );
                listOfButtons.add( removeIndexButton );
                buttonsPanel = new ButtonsPanel( listOfButtons );

                this.add( buttonsPanel );
                
                //Initialize table
                this.initializeTable( dbTableOfConstraints );
                this.constraintsSetupCompleted = true;
            }
            catch(DBEngineException exc)
            {
                Log.getInstance().fatalMessage(exc.getClass().getName() + " - " + exc.getMessage(), BrowserPanel.class.getName());
                String sqlFailedLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-sql-failed", null);
                JOptionPane.showMessageDialog(null, sqlFailedLabel + " - " + exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
            catch(UnsupportedOperationException exc)
            {
            	ConnectionInfo ci = this.uiControllerForQueries.getConnectionInfo();
                String[] dbmsTypes = new String[]{ci.getDBMSType()};
                String featureSupportedByDBMSLabel = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-feature-not-supported-error-message", dbmsTypes);
                JLabel label = new JLabel(featureSupportedByDBMSLabel);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                this.setLayout( new BorderLayout() );
                this.add( label );            	
            }
    }

    private void initialize()
    {
        //Set the table for data
        this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
    }

    public void initializeTable(DBTable dbTableOfSequences)
    {
        this.tableForConstraints.initializeTable( new DBTableDataTableModel(dbTableOfSequences) );
    }

    public void actionPerformed(ActionEvent e)
    {
        //Add a new constraint
        if( ADD_NEW_CONSTRAINT_BUTTON_LABEL.equals(e.getActionCommand()))
        {
            String UNIMPLEMENTED_FEATURE_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-unimplemented-feature", null);
            JOptionPane.showMessageDialog(null, UNIMPLEMENTED_FEATURE_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
        }

        //Remove the selected constraint
        if( REMOVE_CONSTRAINT_BUTTON_LABEL.equals(e.getActionCommand()))
        {
            String UNIMPLEMENTED_FEATURE_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-unimplemented-feature", null);
            JOptionPane.showMessageDialog(null, UNIMPLEMENTED_FEATURE_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
