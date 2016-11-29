package com.iksgmbh.sql.pojomemodb.sqlparser.helper;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.AND;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.AbstractSqlPojoMemoryParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class WhereConditionParser extends AbstractSqlPojoMemoryParser
{
	public static final String AND_SEPARATOR = " " + AND + " ";
	
	public static List<WhereCondition> doYourJob(final String whereClause) throws SQLException {
		return new WhereConditionParser().parseConditions(whereClause);
	}
	
	public List<WhereCondition> parseConditions(final String whereClause) throws SQLException 
	{
		String unparsedRest = whereClause;
		if (unparsedRest.startsWith("("))  {
			unparsedRest = StringParseUtil.removeSurroundingPrefixAndPostFix(unparsedRest, "(", ")");
		}
		
		final List<String> whereConditionsAsString = new ArrayList<String>();
		InterimParseResult parseResult = null;
		while( unparsedRest.length() > 0 )
		{
			parseResult = StringParseUtil.parseNextValue(unparsedRest, AND_SEPARATOR);
			whereConditionsAsString.add(parseResult.parsedValue);
			unparsedRest = parseResult.unparsedRest;
		}
		
		final List<WhereCondition> toReturn = new ArrayList<WhereCondition>();
		for (String conditionAsString : whereConditionsAsString) {
			toReturn.add(parseCondition(conditionAsString));
		}
		
		return toReturn;
	}
	
	private WhereCondition parseCondition(final String conditionAsString) throws SQLException 
	{
		final String conditionAsLowerCaseString = conditionAsString.toLowerCase();
		if (conditionAsLowerCaseString.endsWith(COMPARATOR_IS_NULL.toLowerCase())) {
			int pos = conditionAsLowerCaseString.indexOf(COMPARATOR_IS_NULL.toLowerCase());
			return new WhereCondition(conditionAsString.substring(0, pos).trim(), COMPARATOR_IS_NULL, null);
		}
		
		if (conditionAsLowerCaseString.endsWith(COMPARATOR_NOT_NULL.toLowerCase())) {
			int pos = conditionAsLowerCaseString.indexOf(COMPARATOR_NOT_NULL.toLowerCase());
			return new WhereCondition(conditionAsString.substring(0, pos).trim(), COMPARATOR_NOT_NULL, null);
		}
		
		final String comparator = determineComparator(conditionAsLowerCaseString);
		final String[] splitResult = conditionAsString.trim().split(comparator);
		
		if (splitResult.length != 2) {
			throw new SQLException("Cannot parse condition '" + conditionAsString + "'. Expected something like AGE>100 or Name='Allan'.");
		}

		return new WhereCondition(splitResult[0].trim(), comparator, splitResult[1].trim());
	}

	private static String determineComparator(final String conditionAsString) throws SQLException 
	{
		for (String comparator : WhereCondition.COMPARATORS) {
			if ( conditionAsString.contains(comparator) ) {
				return comparator;
			}
		}
		
		throw new SQLException("Comparator in Where condition is unkown: " + conditionAsString );
	}

	@Override
	protected String getSqlCommand() {
		return null;  // not needed for an helper of the helper classes 
	}

}
