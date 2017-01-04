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
package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

import java.sql.SQLException;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;

/**
 * Stores information of a where clause.
 *  
 * @author Reik Oberrath
 */
public class WhereCondition 
{	
	public static String[] COMPARATORS = { COMPARATOR_EQUAL, COMPARATOR_UNEQUAL, COMPARATOR_GREATER, COMPARATOR_LESS, 
                                           COMPARATOR_GREATER_EQUAL, COMPARATOR_LESS_EQUAL,
                                           COMPARATOR_IS_NULL, COMPARATOR_NOT_NULL};
	
	public static String[] NULL_COMPARATORS = { COMPARATOR_IS_NULL, COMPARATOR_NOT_NULL};
	
	private String columnName;
	private String comparator;
	private String valueAsString;

	public WhereCondition(String columnName, String comparator, String valueAsString) throws SQLException 
	{
		this.columnName = columnName;
		this.comparator = comparator;
		this.valueAsString = valueAsString;
		
		validateComparator(comparator);
	}
	
	private boolean isNullComparator(final String comparatorToValidate) 
	{
		for (String comparator : NULL_COMPARATORS) {
			if ( comparatorToValidate.equals(comparator) ) {
				return true; 
			}
		}
		
		return false;
	}
	
	private void validateComparator(final String comparatorToValidate) throws SQLException 
	{
		for (String comparator : COMPARATORS) {
			if ( comparatorToValidate.equals(comparator) ) {
				return;  // comparator is valid
			}
		}
		
		throw new SQLException("Unknown comparator '" + comparatorToValidate + "'.");
	}

	/**
	 * Returns left hand side of the where condition.
	 * Either a simple column name or a column ID ({table name}.{column name})
	 * @return name or ID of a column
	 */
	public String getColumnName() {
		return columnName;
	}
	
	public String getComparator() {
		return comparator;
	}
	
	/**
	 * Returns right hand side of the where condition.
	 * Either a constant value or - in case of a JoinCondition - a column ID ({table name}.{column name})
	 * @return value or ID of a column
	 */
	public String getValueAsString() {
		return valueAsString;
	}
	
	@Override
	public String toString() {
		if (valueAsString == null) {
			return "WhereCondition [" + columnName + " " + comparator + "]";
		}
		return "WhereCondition [" + columnName + " " + comparator + " " + valueAsString + "]";
	}

	/**
	 * A JoinCondition is a where condition that contains both on its right hand side and left hand side a column id.
	 * A column id is given ba <tableName>.<columnName>
	 * @return false if one side of the condition (typically the value) contains a constant value.
	 */
	public boolean isJoinCondition()
	{
		if (! columnName.contains("."))  {
			return false;
		}
		
		if (columnName.startsWith("'") || columnName.endsWith("'"))  {
			return false;
		}
		
		if (isNullComparator(comparator)) {
			return false;
		}
		
		return valueAsString.contains(".") && ! valueAsString.startsWith("'") && ! valueAsString.endsWith("'");
	}
	
}