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

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedInsertData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

import java.sql.SQLException;
import java.util.List;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.VALUES;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

public class InsertIntoParser extends SqlPojoMemoParser
{	
	public InsertIntoParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}
	
	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.INSERT_INTO_COMMAND.toLowerCase();
	}
	
	public ParsedInsertData parseInsertStatement(final String sql) throws SQLException 
	{
		InterimParseResult parseResult = parseTableName(sql);
		final String tableName = removeSurroundingQuotes(parseResult.parsedValue);
		
		if (! parseResult.unparsedRest.startsWith(OPENING_PARENTHESIS)) {
			throw new SQLException("Missing opening parenthesis in sql statement: " + sql);
		}
		
		parseResult = parseNextValue(parseResult.unparsedRest.substring(1), CLOSING_PARENTHESIS);
		final String columnNameString = parseResult.parsedValue;
		final List<String> parsedColumnNames = parseCommaSeparatedColumnNameList(columnNameString);
		
		if (! parseResult.unparsedRest.toLowerCase().startsWith(VALUES.toLowerCase())) {
			throw new SQLException("Missing VALUES declaration in insert statement: " + sql);
		}
		
		int pos = parseResult.unparsedRest.indexOf(OPENING_PARENTHESIS) + 1;
		parseResult.unparsedRest = parseResult.unparsedRest.substring(pos);
		parseResult = parseNextValue(parseResult.unparsedRest, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0));
		final String dataValuesString = parseResult.parsedValue;
		final List<String> parsedDataValues = parseCommaSeparatedValuesList(dataValuesString);
		
		return new ParsedInsertData(tableName, parsedColumnNames, parsedDataValues);
	}

}