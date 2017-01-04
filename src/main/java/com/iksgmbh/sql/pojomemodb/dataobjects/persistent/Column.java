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

import com.iksgmbh.sql.pojomemodb.DbProperties;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.ColumnData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.ColumnMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;
import com.iksgmbh.sql.pojomemodb.validator.TypeValidator;
import com.iksgmbh.sql.pojomemodb.validator.TypeValidator.ValidatorType;

import java.sql.SQLDataException;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;

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
	private TypeValidator typeValidator;
	private String defaultValue;
    private String primaryKeyId;
    private String uniqueConstraintId;

    public Column(final ColumnInitData columnInitData,
                  final int orderNumber,
				  final SqlPojoMemoDB memoryDB) throws SQLDataException
	{
        this.columnName = columnInitData.columnName.toUpperCase();
        this.columnType = columnInitData.columnType.toUpperCase();
        this.nullable = columnInitData.nullable;
        this.defaultValue = columnInitData.defaultValue;
        this.primaryKeyId = columnInitData.primaryKey;
        this.uniqueConstraintId = columnInitData.uniqueKey;
        this.orderNumber = orderNumber;
        this.memoryDB = memoryDB;

		try {
			this.typeValidator = TypeValidator.getInstance(columnType);
		} catch (SQLDataException e) {
			throw new SQLDataException(e.getMessage() + " Concerned column: '" + columnName + "'.");
		}
        try {
            typeValidator.validateValueForType(defaultValue);
        } catch (SQLDataException e) {
            throw new SQLDataException("Value '" + defaultValue + "' is not valid for column " + columnName + "!");
        }
	}

	public int getIndexInTable() {
		return orderNumber - 1;
	}
	
	public ValidatorType getValidationType() {
		return typeValidator.getType();
	}

	public TypeValidator getTypeValidator() {
		return typeValidator;
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

		return typeValidator.convertIntoColumnType(valueAsString);
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
		typeValidator.validateValueForType(value);
	}

	/**
	 * Compares a data value against a WhereCondition value
     * @param conditionValueAsString
	 * @param comparator
	 * @param dataValueToCheck
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

	public String getDefaultValue() {
		return defaultValue;
	}

    @Override
    public String getPrimaryKeyId() {
        return primaryKeyId;
    }

    @Override
    public String getUniqueConstraintId() {
        return uniqueConstraintId;
    }


    public boolean areDublicatesAllowed() {
        return primaryKeyId == null && uniqueConstraintId == null;
    }

    public void setPrimaryKeyId(String primaryKeyId) {
        this.primaryKeyId = primaryKeyId;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setUniqueConstraintId(String id) {
        this.uniqueConstraintId = id;
    }
}