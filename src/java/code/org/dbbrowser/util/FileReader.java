package org.dbbrowser.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * utility file reader
 * @author Amangat
 */
public class FileReader
{
	/**
	 * Reads a file and reads the contents as a string
	 * @param filename
	 * @return - a string with the contents of the file or an error message
	 */
    public static String readFromFile(String filename)
    {
        String fileContents = "";
        StringBuffer buffer = new StringBuffer();
        try
        {
            BufferedReader reader = new BufferedReader( new java.io.FileReader(filename) );
            String line = reader.readLine();
            while( line != null )
            {
               buffer.append( line + "\n");
               line = reader.readLine();
            }
            reader.close();
            fileContents = buffer.toString();
        }
        catch(FileNotFoundException exc)
        {
            //Cant do anything - show an error message
            fileContents = exc.getMessage();
        }
        catch(IOException exc)
        {
           //Cant do anything - show an error message
            fileContents = exc.getMessage();
        }
        return fileContents;
    }
}
