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
package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_EQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_IS_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_NOT_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_UNEQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NEXTVAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NULL;

import java.sql.SQLDataException;

import com.iksgmbh.sql.pojomemodb.DbProperties;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.ColumnData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.ColumnMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.Validator;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.Validator.ValidatorType;

/**
 * Metadata about a single table.
 * 
 * @author Reik Oberrath
 */
public class Column implements ColumnStatistics, ColumnMetaData, ColumnData
{	
	private String columnName;
	private String columnType;
	private boolean nullable;
	private int orderNumber;
	private SqlPojoMemoDB memoryDB;
	private Validator validator;

	public Column(final String columnName, 
			      final String columnType,
			      final boolean nullable,
			      final int orderNumber, 
			      final SqlPojoMemoDB memoryDB) throws SQLDataException 
	{
		this.columnName = columnName.toUpperCase();
		this.columnType = columnType.toUpperCase();
		this.nullable = nullable;
		this.orderNumber = orderNumber;
		this.memoryDB = memoryDB;
		try {
			this.validator = Validator.getInstance(columnType);
		} catch (SQLDataException e) {
			throw new SQLDataException(e.getMessage() + " for column '" + columnName + "'.");
		}
	}

	public int getIndexInTable() {
		return orderNumber - 1;
	}
	
	public ValidatorType getValidationType() {
		return validator.getType();
	}

	// #########################################################################################
	//                       S T A T I S T I C S   M E T H O D S
	// #########################################################################################
	
	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String getColumnType() {
		return columnType;
	}
	
	@Override
	public boolean isNullable() {
		return nullable;
	}
	
	@Override
	public int getOrderNumber() {
		return orderNumber;
	}
	
	@Override
	public String toString() {
		return "Column [columnName=" + columnName + ", columnType=" + columnType 
				    + ", nullable=" + nullable + ", orderNumber=" + orderNumber + "]";
	}

	
	// #########################################################################################
	//                            D A T A     M E T H O D S
	// #########################################################################################
	
	@Override
	public Object convertIntoColumnType(final String valueAsString) throws SQLDataException 
	{
		if (NULL.equals(valueAsString) || valueAsString == null) {
			return null;
		}
		
		if (valueAsString.endsWith(NEXTVAL)) {
			return getValueFromSequence(valueAsString);
		}
		
		if (DbProperties.REPLACE_EMPTY_STRING_BY_NULL && valueAsString.equals("''"))  {
			return null;
		}

		return validator.convertIntoColumnType(valueAsString);
	}

	private Object getValueFromSequence(String valueAsString) throws SQLDataException 
	{
		final int pos = valueAsString.indexOf("." + NEXTVAL);
		final String sequenceName = valueAsString.substring(0, pos);
		final SequenceData sequenceData;
		try {
			sequenceData = memoryDB.getSequenceData(sequenceName);
		} catch (SQLDataException e) {
			throw new SQLDataException("Sequence '" + sequenceName + "' is unkown.");
		}
		return new Long(sequenceData.nextVal());
	}

	/**
	 * Validates validation type specific settings.
	 * Nullable check is not performed here!
	 */
	@Override
	public void validate(Object value) throws SQLDataException {
		validator.validate(value);
	}

	/**
	 * Compares a data value against a WhereCondition value
	 * @param String comparator
	 * @param valueToCheck
	 * @return true if dataValueToCheck is valid 
	 * @throws SQLDataException 
	 */
	public boolean isWhereConditionMatched(final String conditionValueAsString, 
			                               final String comparator, 
			                               final Object dataValueToCheck) throws SQLDataException 
	{
		final Object conditionValue = convertIntoColumnType(conditionValueAsString);
		validate(conditionValue);
		
		if ( COMPARATOR_EQUAL.equals(comparator) ) {
			return checkForEquality(conditionValue, dataValueToCheck);
		}
			
		if ( COMPARATOR_UNEQUAL.equals(comparator) ) {
			return ! checkForEquality(conditionValue, dataValueToCheck);
		}
			
		if ( COMPARATOR_IS_NULL.equals(comparator) ) {
			return dataValueToCheck == null;
		}
		
		if ( COMPARATOR_NOT_NULL.equals(comparator) ) {
			return dataValueToCheck != null;
		}
		
		throw new RuntimeException("Unsupported comparator: " + comparator);
	}

	private boolean checkForEquality(Object conditionValue, Object dataValueToCheck) 
	{
		if (conditionValue == null && dataValueToCheck == null)  {
			return true;
		}
		
		if (conditionValue != null && dataValueToCheck == null)  {
			return false;
		}
		
		if (conditionValue == null && dataValueToCheck != null)  {
			return true;
		}
		
		return conditionValue.toString().equals(dataValueToCheck.toString());
	}

}