package org.dbbrowser.ui.helper;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
 * Used by the UI to show only jar files
 */
public class JarFileFilter extends FileFilter
{
    /**
     * Returns true if the File 'f' is not a directory and the filename ends with '.jar'
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
			
			if( (lastToken!=null) && (lastToken.equals("jar")))
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
		return "*.jar";
	}
}
