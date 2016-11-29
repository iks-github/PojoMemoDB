package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.SET;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.WHERE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.COMMA;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.EQUALSIGN;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedUpdateData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.helper.WhereConditionParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class SqlPojoUpdateParser extends AbstractSqlPojoMemoryParser
{	
	public SqlPojoUpdateParser(final SqlPojoMemoDB memoryDb)  {
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
