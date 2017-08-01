package org.dbbrowser.ui.helper;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter used to show only 'ConnectionInfo' xml files
 * @author amangat
 */
public class ConnectionInfoFileFilter extends FileFilter
{
    /**
     * Returns true if the File 'f' is not a directory and the filename ends with '.xml'
     * @param f
     * @return
     */
    public boolean accept(File f)
	{
		boolean acceptSelectedFile = false;
		
		if(f.isFile())
		{
			String filename = f.getName();
			
			//Get the extension
			StringTokenizer st = new StringTokenizer(filename, ".");
			String lastToken = null;
			while(st.hasMoreTokens())
			{
				lastToken = st.nextToken();
			}
			
			if( (lastToken != null) && (lastToken.equals("xml")) )
			{
				acceptSelectedFile = true;
			}			
		}
		else
		{
			acceptSelectedFile = true;
		}
		
		return acceptSelectedFile;
	}

    /**
     * Returns the description for use in the UI
     * @return
     */
    public String getDescription()
	{
		return "*.xml";
	}
}
