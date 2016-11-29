package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data;

import java.sql.SQLDataException;
import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;

/**
 * List of methods to handle the data values assigned to a column. 
 * 
 * @author Reik Oberrath
 */
public interface ColumnData extends ColumnStatistics {

	Object convertIntoColumnType(String valueAsString) throws SQLDataException;

	/**
	 * Validates validation type specific settings.
	 * Nullable check is not performed here!
	 */
	void validate(Object value) throws SQLDataException;

	/**
	 * Compares a data value against a WhereCondition value
	 * @param String comparator
	 * @param valueToCheck
	 * @return true if dataValueToCheck is valid 
	 * @throws SQLException 
	 */
	public boolean isWhereConditionMatched(String conditionValue, String comparator, Object dataValueToCheck) throws SQLDataException;	
}
