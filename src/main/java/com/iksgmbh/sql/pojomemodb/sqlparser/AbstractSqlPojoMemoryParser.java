package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.APOSTROPY;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.COMMA;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public abstract class AbstractSqlPojoMemoryParser 
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
