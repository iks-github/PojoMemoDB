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
package com.iksgmbh.sql.pojomemodb.sqlparser;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

public abstract class SqlPojoMemoParser
{
	protected SqlPojoMemoDB memoryDb;

	abstract protected String getSqlCommand();
	

	/**
	 * Cuts leading SQL command and splits resulting rest string by SPACE
	 * in order to separate table name.
	 * @param sql
	 * @return InterimParseResult with table name as parsedValue
	 * @throws SQLException
	 */
	protected InterimParseResult parseTableName(final String sql) throws SQLException 
	{
		final String toReturn = sql.substring(getSqlCommand().length()).trim();
		return parseNextValue(toReturn, SPACE);
	}


	protected String removeSurroundingQuotes(String value) throws SQLException 
	{
		return StringParseUtil.removeSurroundingPrefixAndPostFix(value, "\"", "\"");
	}
	
	// This algorithm is a big mess and needs to be replaced by a state maschine approach
	protected List<String> parseCommaSeparatedColumnNameList(String input) 
	{
		final List<String> toReturn = new ArrayList<String>();
		InterimParseResult parseResult = null;
		
		while (input.length() > 0)
		{
			parseResult = parseNextValue(input, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0));
			toReturn.add(parseResult.parsedValue);
			input = parseResult.unparsedRest;
		}
		
		return toReturn;
	}	

	// This algorithm is a big mess and needs to be replaced by a state maschine approach
	protected List<String> parseCommaSeparatedValuesList(String input) 
	{
		final List<String> toReturn = new ArrayList<String>();
		InterimParseResult parseResult = null;
		
		while (input.length() > 0)
		{
			if (input.startsWith("to_date"))  {
				parseResult = parseNextValue(input, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0));
			} else {
				parseResult = parseNextValue(input, APOSTROPY.charAt(0), APOSTROPY.charAt(0), COMMA.charAt(0));
			}
			toReturn.add(parseResult.parsedValue);
			input = parseResult.unparsedRest;
		}
		
		return toReturn;
	}	
	
}