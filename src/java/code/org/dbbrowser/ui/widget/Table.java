package org.dbbrowser.ui.widget;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import org.dbbrowser.ui.*;
import org.dbbrowser.ui.helper.DBTableDataTableModel;
import org.dbbrowser.ui.helper.exporthelper.wizard.DataExporterUsingWizard;
import org.dbbrowser.util.TableSorter;
import org.dbbrowser.help.HelpManager;

/**
 * A table which can be sorted by clicking on the row header
 */
public class Table extends JPanel implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String POP_UP_MENU_COPY_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-copy-menu-item-label", null);;
    private static final String POP_UP_MENU_EXPORT_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-tools-menu-copy-label", null);;
    private static final String POP_UP_MENU_WHATS_THIS_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-whats-this-menu-item-label", null);;
    private static final String POP_UP_MENU_HELP_MENU_ITEM_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-browser-tab-tabledata-popup-menu-help-menu-item-label", null);;

    private static final String COPY_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-popup-menu-copy-icon-filename");
    private static final String EXPORT_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-export-icon");
    private static final String WHATS_THIS_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-popup-menu-whats-this-icon-filename");
    private static final String HELP_POPUP_ITEM_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-toolbar-help-icon");

    private UIControllerForQueries uiControllerForQueries = null;
    private UIControllerForUpdates uiControllerForUpdates = null;
    private AbstractTableModel abstractTableModel = null;

    //Widgets in the panel
    private JScrollPane paneForTable = null;
    private TableSorter sorter = null;
    private JTable tableForTableData = null;

    //Popup menu
    private JPopupMenu popupMenuForResultsTable = null;

    /**
     * Constructer
     * @param uiControllerForQueries
     * @param uiControllerForUpdates
     */
    public Table(UIControllerForQueries uiControllerForQueries, UIControllerForUpdates uiControllerForUpdates)
    {
        this.uiControllerForQueries = uiControllerForQueries;
        this.uiControllerForUpdates = uiControllerForUpdates;
        this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
    }

    /**
     * Returns the selected row
     * @return
     */
    public Integer getSelectedRow()
    {
        int selectedRow = this.tableForTableData.getSelectedRow();
        Integer returnValue = null;
        if(selectedRow >= 0)
        {
            int modelIndex = sorter.modelIndex( selectedRow );
            returnValue = new Integer(modelIndex);
        }
        return returnValue;
    }

    /**
     * Update the table
     */
    public void update()
    {
        this.abstractTableModel.fireTableDataChanged();
        this.tableForTableData.updateUI();
    }

    /**
     * Set up table for display
     * @param abstractTableModel
     */
    public void initializeTable(AbstractTableModel abstractTableModel)
    {
        this.abstractTableModel = abstractTableModel;

        //If the panel already had a table(scrollpane), remove it
        if( this.tableForTableData != null )
        {
            this.remove( this.paneForTable );
            this.paneForTable = null;
            this.tableForTableData = null;
        }

        //Wrap the table model around a sorter
        sorter = new TableSorter(this.abstractTableModel);

        //Build a new JTable
        this.tableForTableData = new JTable(sorter);
        sorter.setTableHeader( this.tableForTableData.getTableHeader() );

        //Add listener for Double clicks
        this.tableForTableData.addMouseListener( new TableMouseListener() );

        //Allow cell selection
        this.tableForTableData.setCellSelectionEnabled( true );

        //Other values
        this.tableForTableData.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );	//if we dont set this, scrollbar is never shown

        //Set the min width of each column so it as wide as it needs to be
        //Set the table header renderer for the columns which contain the primary key
        FontMetrics fontMetrics = this.getGraphics().getFontMetrics();

        for(int i=0; i<this.abstractTableModel.getColumnCount(); i++)
        {
            TableColumn tc = this.tableForTableData.getColumnModel().getColumn( i );

            //Set the width of first column
            if(i == 0)
            {
                tc.setPreferredWidth(80);
            }
            else
            {
                //Set the width of the subsequent columns to be little more than width of the column header
                String columnName = this.abstractTableModel.getColumnName(i);
                int columnWidth = fontMetrics.stringWidth(columnName) + 60;
                tc.setPreferredWidth(columnWidth);
            }
        }

        //Set the renderer for the first column
        //TableColumn firstColumn = this.tableForTableData.getColumnModel().getColumn(0);
        //firstColumn.setCellRenderer(new RowHeaderCellRenderer());

        //Wrap the table in a scrollpane and add the scrollpane to the panel
        this.paneForTable = new JScrollPane( this.tableForTableData );
        this.add( this.paneForTable );

        //Add the popup menu
        popupMenuForResultsTable = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem(POP_UP_MENU_COPY_MENU_ITEM_LABEL, new ImageIcon(COPY_POPUP_ITEM_ICON_FILENAME));
        copyMenuItem.addActionListener(this);
        popupMenuForResultsTable.add(copyMenuItem);
        JMenuItem exportMenuItem = new JMenuItem(POP_UP_MENU_EXPORT_MENU_ITEM_LABEL, new ImageIcon(EXPORT_POPUP_ITEM_ICON_FILENAME));
        exportMenuItem.addActionListener(this);
        popupMenuForResultsTable.add(exportMenuItem);
        popupMenuForResultsTable.addSeparator();
        JMenuItem whatsThisMenuItem = new JMenuItem(POP_UP_MENU_WHATS_THIS_MENU_ITEM_LABEL, new ImageIcon(WHATS_THIS_POPUP_ITEM_ICON_FILENAME));
        HelpManager.getInstance().registerCSH(whatsThisMenuItem, this, "data_browser");
        popupMenuForResultsTable.add(whatsThisMenuItem);
        JMenuItem helpMenuItem = new JMenuItem(POP_UP_MENU_HELP_MENU_ITEM_LABEL, new ImageIcon(HELP_POPUP_ITEM_ICON_FILENAME));
        helpMenuItem.addActionListener( HelpManager.getInstance().getActionListenerForHelpEvents() );
        popupMenuForResultsTable.add(helpMenuItem);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListenerForResultsTable = new BasicPopupListener( this.popupMenuForResultsTable );
        this.tableForTableData.addMouseListener( popupListenerForResultsTable );

        //Update the table
        this.abstractTableModel.fireTableDataChanged();
        this.tableForTableData.doLayout();
        this.updateUI();
    }

    public void actionPerformed(ActionEvent e)
    {
        //If the user wants to copy something
        if( POP_UP_MENU_COPY_MENU_ITEM_LABEL.equals(e.getActionCommand()) )
        {
            int[] selectedRows = this.tableForTableData.getSelectedRows();
            int[] selectedColumns = this.tableForTableData.getSelectedColumns();

            StringBuffer buffer = new StringBuffer();
            for(int i=0; i<selectedRows.length; i++)
            {
                for(int j=0; j<selectedColumns.length; j++)
                {
                    Object selectedCell = this.tableForTableData.getValueAt(selectedRows[i], selectedColumns[j]);
                    String selectedValue = selectedCell.toString();

                    if( j == selectedColumns.length-1 )
                    {
                        buffer.append(selectedValue);
                    }
                    else
                    {
                        buffer.append(selectedValue + "\t");
                    }
                }

                if( i != selectedRows.length-1 )
                {
                    buffer.append("\n");
                }
            }

            //Copy the contents to the system clipboard
            StringSelection ss = new StringSelection(buffer.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        }

        //If the user wants to copy something
        if( POP_UP_MENU_EXPORT_MENU_ITEM_LABEL.equals(e.getActionCommand()) )
        {
        	DataExporterUsingWizard dataExporterUsingWizard = new DataExporterUsingWizard();
        	dataExporterUsingWizard.export( this.abstractTableModel );
        }
    }

    /**
     * Listener for double clicks
     * @author amangat
     */
    private class TableMouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent evt)
        {
            if (evt.getClickCount() == 2)
            {
                Component sourceComponent = evt.getComponent();

                //if the source is a jtabel, get the selected row
                if( sourceComponent instanceof JTable )
                {
                    JTable aTable = (JTable)sourceComponent;
                    int selectedRow = aTable.getSelectedRow();

                    //if the source is a row in the table
                    if( selectedRow != -1 )
                    {
                        //if there are any rows to display
                        if( abstractTableModel.getRowCount() != 0 )
                        {
                            if(abstractTableModel instanceof DBTableDataTableModel)
                            {
                                DBTableDataTableModel dbTableDataTableModel = (DBTableDataTableModel)abstractTableModel;

                                //Get the index of the row selected from the model
                                int modelIndex = sorter.modelIndex( selectedRow );

                                //Build the view record window
                                ViewRecordWindow viewRecordWindow = new ViewRecordWindow( uiControllerForUpdates, dbTableDataTableModel.getDBTable(), new Integer(modelIndex));
                                viewRecordWindow.show();
                            }
                        }
                    }
                }
            }
        }
    }
}
	