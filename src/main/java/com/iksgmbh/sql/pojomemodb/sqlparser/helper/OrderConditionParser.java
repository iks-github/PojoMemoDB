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

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.OrderCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.SqlPojoMemoParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses order conditions.
 *
 * @author Reik Oberrath
 */
public class OrderConditionParser extends SqlPojoMemoParser
{
	public static List<OrderCondition> doYourJob(final String orderClause) throws SQLException {
		return new OrderConditionParser().parseConditions(orderClause);
	}

	// TODO mehere conditions mit asc, desc testen
	public List<OrderCondition> parseConditions(final String orderClause) throws SQLException
	{
		if (StringParseUtil.isEmpty(orderClause))
			throw new SQLException("No column defined for order by!");

		String unparsedRest = orderClause;
		if (unparsedRest.startsWith("("))  {
			unparsedRest = StringParseUtil.removeSurroundingPrefixAndPostFix(unparsedRest, "(", ")");
		}
		
		final List<OrderCondition> toReturn = new ArrayList<OrderCondition>();
		InterimParseResult parseResult = null;
		while( unparsedRest.length() > 0 )
		{
			parseResult = StringParseUtil.parseNextValue(unparsedRest, StringParseUtil.COMMA);
			toReturn.add(parseOrderCondition(parseResult.parsedValue));
			unparsedRest = parseResult.unparsedRest;
		}

		return toReturn;
	}

	private OrderCondition parseOrderCondition(final String input) throws SQLException
	{
		final InterimParseResult parseResult = StringParseUtil.parseNextValue(input, StringParseUtil.SPACE);
		final String columnName = parseResult.parsedValue;

		String direction = SQLKeyWords.ASC;
		if (parseResult.unparsedRest.length() > 0)  {
			direction = parseResult.unparsedRest;
		}

		return new OrderCondition(columnName, direction);
	}


	@Override
	protected String getSqlCommand() {
		return null;  // not needed for an helper of the helper classes 
	}

}