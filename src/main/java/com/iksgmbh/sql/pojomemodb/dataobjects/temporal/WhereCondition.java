package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_EQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_IS_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_LARGER;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_LARGER_EQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_LOWER;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_LOWER_EQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_NOT_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_UNEQUAL;

import java.sql.SQLException;

/**
 * Parses and stores information of a where clause.
 *  
 * @author Reik Oberrath
 */
public class WhereCondition 
{	
	public static String[] COMPARATORS = { COMPARATOR_EQUAL, COMPARATOR_UNEQUAL, COMPARATOR_LARGER, COMPARATOR_LOWER, 
                                           COMPARATOR_LARGER_EQUAL, COMPARATOR_LOWER_EQUAL,
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
