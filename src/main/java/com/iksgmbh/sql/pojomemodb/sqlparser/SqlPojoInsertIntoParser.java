package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.VALUES;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import java.sql.SQLException;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedInsertData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class SqlPojoInsertIntoParser extends AbstractSqlPojoMemoryParser
{	
	public SqlPojoInsertIntoParser(final SqlPojoMemoDB memoryDb)  {
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
