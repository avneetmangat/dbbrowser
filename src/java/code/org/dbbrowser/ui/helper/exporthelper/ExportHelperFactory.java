package org.dbbrowser.ui.helper.exporthelper;

import infrastructure.logging.Log;
import java.util.HashMap;
import java.util.Map;

public class ExportHelperFactory
{
	private static Map mapOfExportTypeToExportHelper = new HashMap();
	
	static
	{
		mapOfExportTypeToExportHelper.put("pdf", new ExporterForPDFFile());
		mapOfExportTypeToExportHelper.put("csv", new ExporterForCSVFile());
		mapOfExportTypeToExportHelper.put("xls", new ExporterForXLSFile());
		mapOfExportTypeToExportHelper.put("sql", new ExporterForSQLFile());
		Log.getInstance().debugMessage("ExportHelperFactory initialized", ExportHelperFactory.class.getName());		
	}
	
	/**
	 * Return an export heler for the export type
	 * @param exportType
	 * @return
	 * @throws ExportHelperException
	 */
	public static ExportHelper getExportHelper(String exportType)
		throws ExportHelperException
	{
		Object o = mapOfExportTypeToExportHelper.get( exportType );
		if( o == null )
		{
			Log.getInstance().fatalMessage("Invalid exportType in ExportHelperFactory.getExportHelper: " + exportType, ExportHelperFactory.class.getName());
			throw new ExportHelperException("Invalid exportType in ExportHelperFactory.getExportHelper: " + exportType);
		}
		
		return (ExportHelper)o;
	}
}
