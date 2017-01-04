/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.sql.pojomemodb.utils;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
		
		StringBuilder sb = new StringBuilder ();
		int counter = 1;
		
		for (char c : content) 
		{
			if (c == ';') {
				if (counter == number) {
					return sb.toString().trim();
				}
				counter++;
				sb = new StringBuilder ();
			}
			else
			{
				sb.append(c);
			}
		}
		
		throw new RuntimeException("File contains less than " + number + " statements!");
	}

	public static final void execAllSqlStatement(final String pathAndFileName)
			                                     throws IOException, SQLException {
		List<String> sqlStatements = loadAllSqlStatement(pathAndFileName);
		for (String statement : sqlStatements) {
			SqlPojoMemoDB.execute(statement);
		}
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