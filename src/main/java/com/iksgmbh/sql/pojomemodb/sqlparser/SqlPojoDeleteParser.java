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

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.FROM;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.WHERE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import java.sql.SQLException;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedDeleteData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.helper.WhereConditionParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class SqlPojoDeleteParser extends AbstractSqlPojoMemoryParser
{	
	public SqlPojoDeleteParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}

	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.DELETE_COMMAND.toLowerCase();
	}
	
	public ParsedDeleteData parseDeleteStatement(final String sql) throws SQLException 
	{
		InterimParseResult parseResult = parseNextValue(sql, WHERE);  
		final String whereClause = parseResult.unparsedRest;
		String deleteClause = parseResult.parsedValue;
		
		deleteClause = deleteClause.substring(getSqlCommand().length()).trim();

		if ( deleteClause.toLowerCase().startsWith("*") ) {
			deleteClause = deleteClause.substring(1).trim();
		}

		if (! deleteClause.toLowerCase().startsWith(FROM.toLowerCase())) {
			throw new SQLException("Missing FROM declaration in delete statement: " + sql);
		} else {
			deleteClause = deleteClause.substring(FROM.length()).trim();
		}
		
		final String tableName = deleteClause;
		
		final List<WhereCondition> whereConditions = WhereConditionParser.doYourJob(whereClause);
		
		return new ParsedDeleteData(tableName, whereConditions);

	}
	
}