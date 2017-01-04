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
package com.iksgmbh.sql.pojomemodb.sqlparser.helper;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.SqlPojoMemoParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;

/**
 * Parses where conditions.
 *
 * @author Reik Oberrath
 */
public class WhereConditionParser extends SqlPojoMemoParser
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
		
		throw new SQLException("Comparator in Where condition is unknown: " + conditionAsString );
	}

	@Override
	protected String getSqlCommand() {
		return null;  // not needed for an helper of the helper classes 
	}

}