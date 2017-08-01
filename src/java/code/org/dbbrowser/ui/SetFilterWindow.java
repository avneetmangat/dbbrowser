package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import javax.swing.*;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.db.engine.model.ColumnInfo;
import org.dbbrowser.db.engine.model.DBTableCell;
import org.dbbrowser.db.engine.model.filter.CompoundFilter;
import org.dbbrowser.db.engine.model.filter.Filter;
import org.dbbrowser.db.engine.model.filter.SimpleFilter;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class SetFilterWindow implements ActionListener
{
    private static final long serialVersionUID = UIControllerForQueries.version;

    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;

    private String CHOOSE_FILTER_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-set-filter-window-panel-title-label", null);
    private String OK_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-license-window-ok-button-label", null);
    private String CLEAR_FILTER_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-set-filter-window-clear-filter-button-label", null);
    private String CANCEL_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-set-filter-window-cancel-label", null);

    private String OK_BUTTON_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-view-record-window-update-record-icon");
    private String CLEAR_FILTER_BUTTON_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-clear-filter-icon");
    private String CANCEL_BUTTON_ICON_FILE_NAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-cross-icon");
    
    private static final String NULL = "NULL";
    private static final String NOT_NULL = "NOT_NULL";

    private JPanel mainPanel = new JPanel();
    private JPanel filterPanel = new JPanel();
    private JDialog dialog = null;
    private ButtonsPanel navigationButonsPanel = null;
    private JComboBox boxForFirstColumn = null;
    private JComboBox boxForSecondColumn = null;
    private String[] operators = new String[]{"=", ">", "<", ">=", "<=", "<>"};
    private String[] NULL_PARAMETERS_OPTIONS = new String[]{NULL, NOT_NULL};
    private JComboBox boxForOperators1 = new JComboBox(operators);
    private JComboBox boxForOperators2 = new JComboBox(operators);
    private JComboBox fieldForFirstColumn = new JComboBox(NULL_PARAMETERS_OPTIONS);
    private JComboBox fieldForSecondColumn = new JComboBox(NULL_PARAMETERS_OPTIONS);
    private ButtonGroup group = new ButtonGroup();
    private Map mapOfColumnNameToColumnInfo = new HashMap();
    private Map mapOfDataTypeNameToFormatter = new HashMap();
    private static final DateFormat dateFormat = new SimpleDateFormat( DBTableCell.DATE_FORMAT_STRING );

    private List listOfColumnInfos = null;
    
    private Filter tableFilter = null;

    public SetFilterWindow(List listOfColumnInfos, Filter filter)
    {
        this.listOfColumnInfos = listOfColumnInfos;
        this.tableFilter = filter;
        this.fieldForFirstColumn.setEditable( true );
        this.fieldForSecondColumn.setEditable( true );
		this.fieldForFirstColumn.setSelectedItem("");
		this.fieldForSecondColumn.setSelectedItem("");        
        this.initialize();
        this.setupParsersForUserInput();
    }

    public void show()
    {
        this.dialog.setVisible( true );
    }
    
    public Filter getFilter()
    {
    	return this.tableFilter;
    }

    public void actionPerformed(ActionEvent e)
    {
    	if( OK_BUTTON_LABEL.equals( e.getActionCommand() ) )
    	{
    		Filter simpleFilter1 = null;
    		Filter simpleFilter2 = null;

    		//if the first filter is set
    		if( (!"".equals( this.boxForFirstColumn.getSelectedItem().toString() )) && (!"".equals( this.fieldForFirstColumn.getSelectedItem().toString() )) )
    		{
                try
                {
                	//if the user wants null or not null for right operand, then use null as object.  Simple Filter will format nulls
                	Object o = null;
                	if( NOT_NULL.equals( this.fieldForFirstColumn.getSelectedItem().toString() ) )
                	{
                		o = new Object();                		
                	} 
                	else if( NULL.equals( this.fieldForFirstColumn.getSelectedItem().toString() ) )
                	{
                		o = null;
                	}
                	else
                	{
                		o = this.buildObject(this.boxForFirstColumn.getSelectedItem().toString(), this.fieldForFirstColumn.getSelectedItem().toString());   //Throws formatting exception                		
                	}

                    simpleFilter1 = new SimpleFilter( this.boxForFirstColumn.getSelectedItem().toString(), this.boxForOperators1.getSelectedItem().toString(), o );

                    //Build filter 2 only if user has selected something from second combo box
                    if( (!"".equals( this.boxForSecondColumn.getSelectedItem().toString() )) && (!"".equals( this.fieldForSecondColumn.getSelectedItem().toString() )) )
                    {
                        Object o2 = this.buildObject(this.boxForSecondColumn.getSelectedItem().toString(), this.fieldForSecondColumn.getSelectedItem().toString());    //Throws formatting exception
                        simpleFilter2 = new SimpleFilter( this.boxForSecondColumn.getSelectedItem().toString(), this.boxForOperators2.getSelectedItem().toString(), o2 );
                    }
                    
                    //if both filters have been set, it is a compound filter
                    if( simpleFilter2 != null )
                    {
                        this.tableFilter = new CompoundFilter(simpleFilter1, this.group.getSelection().getActionCommand(), simpleFilter2);
                    }
                    else
                    {
                        //Only one filter has been set
                        this.tableFilter = simpleFilter1;
                    }

                    //Close the window
                    this.dialog.pack();
                    this.dialog.dispose();
                    this.dialog = null;
                }
                catch(NumberFormatException exc)
                {
                    JOptionPane.showMessageDialog(null, exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
                catch(ParseException exc)
                {
                    JOptionPane.showMessageDialog(null, exc.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }

            //if all filters are clear
    		if( ("".equals( this.boxForFirstColumn.getSelectedItem().toString() )) && ("".equals( this.fieldForFirstColumn.getSelectedItem().toString() )) && ("".equals( this.boxForSecondColumn.getSelectedItem().toString() )) && ("".equals( this.fieldForSecondColumn.getSelectedItem().toString() )) )
    		{
                this.dialog.pack();
                this.dialog.dispose();
                this.dialog = null;
            }
        }
    	
    	if( CLEAR_FILTER_BUTTON_LABEL.equals( e.getActionCommand() ) )
    	{
    		//Clear all fields
    		this.boxForFirstColumn.setSelectedItem("");
    		this.boxForSecondColumn.setSelectedItem("");
    		this.boxForOperators1.setSelectedItem("=");
    		this.boxForOperators2.setSelectedItem("=");
    		this.fieldForFirstColumn.setSelectedItem("");
    		this.fieldForSecondColumn.setSelectedItem("");
    		this.tableFilter = null;
    	}     	
    	
    	if( CANCEL_BUTTON_LABEL.equals( e.getActionCommand() ) )
    	{
	        this.dialog.pack();
	        this.dialog.dispose();
	        this.dialog = null;
    	}    	
    }
    
    private void setupParsersForUserInput()
    {
    	this.mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_VARCHAR, new MessageFormat("{0}"));		//Format varchar datatype using the message formatter
    	this.mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_VARCHAR2, new MessageFormat("{0}"));		//Format varchar datatype using the message formatter
    	this.mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_CHAR, new MessageFormat("{0}"));			//Format char datatype using the message formatter
    	this.mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_DATE, DBTableCell.getDateFormat());		//Format date datatype using the message formatter
    	this.mapOfDataTypeNameToFormatter.put(ColumnInfo.COLUMN_TYPE_NUMBER, DBTableCell.getDecimalFormat());			//Format date datatype using the message formatter    	
    }
    
    private Object buildObject(String columnName, String value)
            throws NumberFormatException, ParseException
    {
    	Object valueToReturn = null;
    	ColumnInfo ci = (ColumnInfo)this.mapOfColumnNameToColumnInfo.get( columnName );
    	
    	//Get the formatter
    	try
    	{
    		Format formatter = (Format)this.mapOfDataTypeNameToFormatter.get( ci.getColumnTypeName() );
    		valueToReturn = formatter.parseObject( value );
    		
    		//if it is an array, get the first element
    		if( valueToReturn instanceof Object[] )
    		{
    			valueToReturn = ((Object[])valueToReturn)[0];
    		}
    	}
    	catch(ParseException exc)
    	{
            String message = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-set-filter-window-invalid-data-format-error-message-label", null);
            throw new ParseException( message, exc.getErrorOffset() );
    	}
    	
		return valueToReturn;     	
    }

    private void initialize()
    {
        //Add the buttons group
        JRadioButton andButton = new JRadioButton("AND");
        andButton.setActionCommand( "AND" );
        andButton.setSelected( true );
        JRadioButton orButton = new JRadioButton("OR");
        orButton.setActionCommand( "OR" );
        this.group.add( andButton );
        this.group.add( orButton ); 
        
        //Setup the drop down list for the list of columns
        String [] listOfColumns = new String[ this.listOfColumnInfos.size() + 1 ];
        Iterator i = this.listOfColumnInfos.iterator();
        int counter = 1;
        listOfColumns[ 0 ] = "";
        while( i.hasNext() )
        {
            ColumnInfo ci = (ColumnInfo)i.next();
            String columnName = ci.getColumnName();
            listOfColumns[ counter ] = columnName;
            counter++;
            this.mapOfColumnNameToColumnInfo.put( columnName, ci );
        }
        boxForFirstColumn = new JComboBox( listOfColumns );
        boxForSecondColumn = new JComboBox( listOfColumns );
        
        //Set selected items
        if( this.tableFilter instanceof SimpleFilter )
        {
        	SimpleFilter sf = (SimpleFilter)this.tableFilter;
        	this.boxForFirstColumn.setSelectedItem( sf.getLeftOperand() );
        	this.boxForOperators1.setSelectedItem( sf.getOperator() );
        	
        	//if the right operand is a date, use the date format to format it
        	if( sf.getRightOperand() instanceof Date )
        	{
        		this.fieldForFirstColumn.setSelectedItem( dateFormat.format( sf.getRightOperand() ) );
        	}
        	else if( sf.getRightOperand() == null )		//If right operand is null, set 'NULL' as selected item 
        	{
        		this.fieldForFirstColumn.setSelectedItem(NULL);
        	}
        	else if( sf.getRightOperand().getClass().getSuperclass() == null )	//If right operand is not null, set 'NOT_NULL' as selected item
        	{
        		this.fieldForFirstColumn.setSelectedItem(NOT_NULL);
        	}        	
        	else
        	{
        		this.fieldForFirstColumn.setSelectedItem( sf.getRightOperand().toString() );
        	}        	
        }
        else if( this.tableFilter instanceof CompoundFilter )
        {
        	CompoundFilter cf = (CompoundFilter)this.tableFilter;
        	
        	//Set the first filter
        	SimpleFilter sf1 = (SimpleFilter)cf.getFilter1();
        	this.boxForFirstColumn.setSelectedItem( sf1.getLeftOperand() );
        	this.boxForOperators1.setSelectedItem( sf1.getOperator() );
        	
        	//if the right operand is a date, use the date format to format it
        	if( sf1.getRightOperand() instanceof Date )
        	{
        		this.fieldForFirstColumn.setSelectedItem( dateFormat.format( sf1.getRightOperand() ) );
        	}
        	else if( sf1.getRightOperand() == null )		//If right operand is null, set 'NULL' as selected item 
        	{
        		this.fieldForFirstColumn.setSelectedItem(NULL);
        	}
        	else if( sf1.getRightOperand() instanceof Object )	//If right operand is not null, set 'NOT_NULL' as selected item
        	{
        		this.fieldForFirstColumn.setSelectedItem(NOT_NULL);
        	}        	
        	else
        	{
        		this.fieldForFirstColumn.setSelectedItem( sf1.getRightOperand().toString() );
        	}         	
        	
        	//Set the second filter
        	SimpleFilter sf2 = (SimpleFilter)cf.getFilter2();
        	this.boxForSecondColumn.setSelectedItem( sf2.getLeftOperand() );
        	this.boxForOperators2.setSelectedItem( sf2.getOperator() );
        	
        	//if the right operand is a date, use the date format to format it
        	if( sf2.getRightOperand() instanceof Date )
        	{
        		this.fieldForSecondColumn.setSelectedItem( dateFormat.format( sf2.getRightOperand() ) );
        	}
        	else if( sf2.getRightOperand() == null )		//If right operand is null, set 'NULL' as selected item 
        	{
        		this.fieldForSecondColumn.setSelectedItem(NULL);
        	}
        	else if( sf2.getRightOperand() instanceof Object )	//If right operand is not null, set 'NOT_NULL' as selected item
        	{
        		this.fieldForSecondColumn.setSelectedItem(NOT_NULL);
        	}        	
        	else
        	{
        		this.fieldForSecondColumn.setSelectedItem( sf2.getRightOperand().toString() );
        	}          	
        	
        	//Set the join condition
        	String joinCondition = cf.getJoinCondition();
        	if( "AND".equals( joinCondition ))
        	{
        		andButton.setSelected( true );
        	}
        	else
        	{
        		orButton.setSelected( true );        		
        	}
        }
        
        //Set max size of widgets
        Dimension d1 = this.filterPanel.getMaximumSize();
        d1.height = 100;
        this.filterPanel.setMaximumSize( d1 );
        
        //Add to the panel
        this.filterPanel.setLayout( new GridLayout(3, 3) );
        this.filterPanel.add( this.boxForFirstColumn );
        this.filterPanel.add( this.boxForOperators1 );
        this.filterPanel.add( this.fieldForFirstColumn );
        this.filterPanel.add( andButton );
        this.filterPanel.add( orButton );
        this.filterPanel.add( new JLabel("") );
        this.filterPanel.add( this.boxForSecondColumn );
        this.filterPanel.add( this.boxForOperators2 );
        this.filterPanel.add( this.fieldForSecondColumn );
        this.filterPanel.setBorder( BorderFactory.createTitledBorder( CHOOSE_FILTER_LABEL ));
        
        this.mainPanel.setLayout( new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS));
        this.mainPanel.add( this.filterPanel );

        //Build the list of buttons for the navigation buttons panel
        List listOfButtons = new ArrayList();
        Button okButton = new Button(OK_BUTTON_LABEL, this, OK_BUTTON_LABEL, new ImageIcon(OK_BUTTON_ICON_FILE_NAME), Boolean.FALSE);
        Button clearFilterButton = new Button(CLEAR_FILTER_BUTTON_LABEL, this, CLEAR_FILTER_BUTTON_LABEL, new ImageIcon(CLEAR_FILTER_BUTTON_ICON_FILE_NAME), Boolean.FALSE);
        Button cancelButton = new Button(CANCEL_BUTTON_LABEL, this, CANCEL_BUTTON_LABEL, new ImageIcon(CANCEL_BUTTON_ICON_FILE_NAME), Boolean.FALSE);
        listOfButtons.add( okButton );
        listOfButtons.add( clearFilterButton );
        listOfButtons.add( cancelButton );

        //Setup the navigation panel
        navigationButonsPanel = new ButtonsPanel( listOfButtons );
        this.mainPanel.add( navigationButonsPanel );

        //Setup the frame
        this.dialog = new JDialog();
        this.dialog.setTitle( TITLE );
        this.dialog.setModal( true );
        this.dialog.setSize(600, 200);
        this.dialog.setLocationRelativeTo( null );
        this.dialog.getContentPane().add( this.mainPanel );
   }
}
