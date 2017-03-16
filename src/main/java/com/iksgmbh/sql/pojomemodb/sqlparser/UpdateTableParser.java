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
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedUpdateData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.helper.WhereConditionParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.SET;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.WHERE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;

public class UpdateTableParser extends SqlPojoMemoParser
{	
	public UpdateTableParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}

	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.SELECT_COMMAND.toLowerCase();
	}
	
	public ParsedUpdateData parseUpdateStatement(final String sql) throws SQLException 
	{
		InterimParseResult parseResult = parseNextValue(sql, WHERE);  
		final String whereClause = parseResult.unparsedRest;
		final String updateClause = parseResult.parsedValue;
		
		parseResult = parseTableName(updateClause);
		final String tableName = removeSurroundingQuotes(parseResult.parsedValue);
		
		if (! parseResult.unparsedRest.toLowerCase().startsWith(SET.toLowerCase())) {
			throw new SQLException("Missing SET declaration in update statement: " + sql);
		}
		
		final List<ApartValue> newValues = parseSetInstructions(parseResult.unparsedRest);
		final List<WhereCondition> whereConditions = WhereConditionParser.doYourJob(whereClause);
		
		return new ParsedUpdateData(tableName, newValues, whereConditions);
	}

	private List<ApartValue> parseSetInstructions(final String setInstruction) throws SQLException 
	{
		final List<ApartValue> toReturn = new ArrayList<ApartValue>();
		InterimParseResult parseResult = parseNextValue(setInstruction, SET); // cut leading SET

		while (parseResult.unparsedRest.contains(COMMA))  {
			parseResult = parseNextValue(parseResult.unparsedRest, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0)); 
			toReturn.add(parseApartValueFrom(parseResult.parsedValue));
		}
		
		if (parseResult.unparsedRest.length() > 0) {
			toReturn.add(parseApartValueFrom(parseResult.unparsedRest));
			
		}

		return toReturn;
	}

	private ApartValue parseApartValueFrom(final String apartValueAsString) throws SQLException 
	{
		final InterimParseResult parseResult = parseNextValue(apartValueAsString, EQUALSIGN); 
		final String columnName = removeSurroundingQuotes(parseResult.parsedValue);
		return new ApartValue(parseResult.unparsedRest, columnName);
	}

}