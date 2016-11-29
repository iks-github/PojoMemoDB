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
