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
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSequenceData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;

import java.sql.SQLException;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.START_WITH;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;

public class CreateSequenceParser extends SqlPojoMemoParser
{
	public CreateSequenceParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}
	
	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.CREATE_SEQUENCE_COMMAND.toLowerCase();
	}
	
	public ParsedSequenceData parseCreateSequenceStatement(final String sql) throws SQLException 
	{
		final String sqlWithoutCommand = sql.substring(getSqlCommand().length()).trim();
		int pos = sqlWithoutCommand.indexOf(SPACE);
		String tmpString = sqlWithoutCommand.substring(0, pos);
		final String sequenceName = removeSurroundingQuotes(tmpString);
		
		pos = sqlWithoutCommand.toLowerCase().indexOf(START_WITH.toLowerCase());
		
		long currentValue;
		if (pos == -1) {
			currentValue = 0;
		} else {
			tmpString = sqlWithoutCommand.substring(pos + START_WITH.length()).trim();
			pos = tmpString.indexOf(SPACE);
			String currentValueAsString = tmpString.substring(0, pos).trim();
			
			try {
				currentValue = Long.valueOf(currentValueAsString);
			} catch (Exception e) {
				throw new SQLException("Unable to parse start value of sequence '" + sequenceName + "' to a number: " + currentValueAsString);
			}
		}
		
		return new ParsedSequenceData(sequenceName, currentValue );
	}
	
}