package org.dbbrowser.ui.helper.exporthelper.wizard;

import infrastructure.internationalization.InternationalizationManager;
import javax.swing.table.AbstractTableModel;
import org.dbbrowser.ui.helper.exporthelper.ExportHelperException;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.DateFormatPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.ExportConfirmationPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.ExportProgressPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.ExportTypePanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.HeaderFooterPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.IncludeTableColumnsPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.OverviewPanelDescriptor;
import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.PageSetupPanelDescriptor;
import com.nexes.wizard.Wizard;

/**
 * Class used to export a table to PDF file
 */
public class DataExporterUsingWizard
{
    private static final String TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-title-label", null);;
	
	/**
	 * Export the data in the table to the specified file as a PDF file
	 * @param abstractTableModel
	 * @throws ExportHelperException
	 */
	public void export(AbstractTableModel abstractTableModel) 
    {
		//Clean the Wizardstate
		WizardState.getInstance().clearState();
		
        Wizard wizard = new Wizard();
        wizard.getDialog().setTitle(TITLE);
        
        OverviewPanelDescriptor overviewPanelDescriptor = new OverviewPanelDescriptor();
        wizard.registerWizardPanel(OverviewPanelDescriptor.IDENTIFIER, overviewPanelDescriptor);
        
        ExportTypePanelDescriptor exportTypePanelDescriptor = new ExportTypePanelDescriptor();
        wizard.registerWizardPanel(ExportTypePanelDescriptor.IDENTIFIER, exportTypePanelDescriptor);

        DateFormatPanelDescriptor dateFormatPanelDescriptor = new DateFormatPanelDescriptor();
        wizard.registerWizardPanel(DateFormatPanelDescriptor.IDENTIFIER, dateFormatPanelDescriptor);
        
        IncludeTableColumnsPanelDescriptor includeTableColumnsPanelDescriptor = new IncludeTableColumnsPanelDescriptor(abstractTableModel);
        wizard.registerWizardPanel(IncludeTableColumnsPanelDescriptor.IDENTIFIER, includeTableColumnsPanelDescriptor);
        
        ExportConfirmationPanelDescriptor exportConfirmationPanelDescriptor = new ExportConfirmationPanelDescriptor();
        wizard.registerWizardPanel(ExportConfirmationPanelDescriptor.IDENTIFIER, exportConfirmationPanelDescriptor);

        HeaderFooterPanelDescriptor headerFooterPanelDescriptor = new HeaderFooterPanelDescriptor();
        wizard.registerWizardPanel(HeaderFooterPanelDescriptor.IDENTIFIER, headerFooterPanelDescriptor);
        
        PageSetupPanelDescriptor pageSetupPanelDescriptor = new PageSetupPanelDescriptor();
        wizard.registerWizardPanel(PageSetupPanelDescriptor.IDENTIFIER, pageSetupPanelDescriptor);
        
        ExportProgressPanelDescriptor exportProgressPanelDescriptor = new ExportProgressPanelDescriptor(abstractTableModel);
        wizard.registerWizardPanel(ExportProgressPanelDescriptor.IDENTIFIER, exportProgressPanelDescriptor);

        wizard.setCurrentPanel(OverviewPanelDescriptor.IDENTIFIER);

        wizard.getDialog().setSize(600, 400);
        wizard.getDialog().setLocationRelativeTo( null );
        
        int ret = wizard.showModalDialog();
	}
}
