package com.iksgmbh.sql.pojomemodb.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a single SQL statement from a text file.
 * 
 * @author Reik Oberrath
 */
public class SqlStatementLoader {

	/**
	 * Loads a single SQL statement from a text file.
	 * 
	 * @param pathAndFileName defines sql file
	 * @param number e.g. 1 for loading the first statement in the file, 2 for the second...
	 * @throws IOException 
	 */
	public static final String loadSqlStatement(final String pathAndFileName, 
			                                    final int number) 
			                                    throws IOException
	{
		if (number < 0) {
			throw new IllegalArgumentException("Invalid number.");
		}
		
		final File file = new File(pathAndFileName);
		final char[] content = FileUtil.getFileContent(file).toCharArray();
		
		StringBuffer sb = new StringBuffer();
		int counter = 1;
		
		for (char c : content) 
		{
			if (c == ';') {
				if (counter == number) {
					return sb.toString().trim();
				}
				counter++;
				sb = new StringBuffer();
			}
			else
			{
				sb.append(c);
			}
		}
		
		throw new RuntimeException("File contains less than " + number + " statements!");
	}
	
	/**
	 * Loads all SQL statements from a text file.
	 * 
	 * @param pathAndFileName defines sql file
	 * @throws IOException 
	 */
	public static final List<String> loadAllSqlStatement(final String pathAndFileName) 
			                                       throws IOException
	{
		final List<String> toReturn = new ArrayList<String>();
		final File file = new File(pathAndFileName);
		final char[] content = FileUtil.getFileContent(file).toCharArray();
		
		StringBuilder sb = new StringBuilder();
		
		for (char c : content) 
		{
			if (c == ';') {
				final String statement = sb.toString().trim();
				toReturn.add( cutComments(statement) );
				sb = new StringBuilder();
			}
			else
			{
				sb.append(c);
			}
		}
		
		return toReturn;
	}

	private static String cutComments(final String statement) {
		final StringBuilder sb = new StringBuilder();
		final String[] splitResult = statement.split(System.getProperty("line.separator"));
		for (String splitPart : splitResult) 
		{
			if (splitPart.trim().length() > 0 && ! splitPart.trim().startsWith("--"))
			{
				sb.append(splitPart).append(" ");
			}
		}
		
		return sb.toString().trim();
	}
	
}
