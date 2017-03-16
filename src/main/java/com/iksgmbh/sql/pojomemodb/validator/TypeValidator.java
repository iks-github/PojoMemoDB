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
package com.iksgmbh.sql.pojomemodb.validator;

import com.iksgmbh.sql.pojomemodb.DbProperties;
import com.iksgmbh.sql.pojomemodb.validator.type.BooleanTypeValidator;
import com.iksgmbh.sql.pojomemodb.validator.type.DateTypeValidator;
import com.iksgmbh.sql.pojomemodb.validator.type.NumberTypeValidator;
import com.iksgmbh.sql.pojomemodb.validator.type.VarcharTypeValidator;

import java.sql.SQLDataException;

/**
 * Abstract parent class of all validators that assure type safety.
 * The concrete validator is derived from the columnType.
 * Other validation criteria (nullable, primary key constraints...) are not handled here!
 *
 * @author Reik Oberrath
 */
public abstract class TypeValidator
{
	public enum ValidatorType { VARCHAR, VARCHAR2, NUMBER, DATE, TIMESTAMP, TIME, BOOLEAN};

	public static final String MYSQL_BIT = "BIT";
	public enum MySqlNaturalNumberType { INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT };
	public enum MySqlDecimalNumberType { DECIMAL, NUMERIC, FLOAT, DOUBLE };
	public enum MySqlVarcharType { VARCHAR, VARCHAR2, TEXT, TINYTEXT, MEDIUMTEXT, LONGTEXT };

	public abstract void validateValueForType(Object value) throws SQLDataException;
	public abstract ValidatorType getType();
	public abstract Object convertIntoColumnType(String valueAsString) throws SQLDataException;

	/**
	 * returns false for value1 < value2,
	 * returns null value1 == value2,
	 * returns true for value1 > value2
	 * @param value1
	 * @param value2
	 * @return comparison result
	 * @throws SQLDataException
	 */
	public abstract Boolean isValue1SmallerThanValue2(Object value1, Object value2) throws SQLDataException;

	/**
	 * Maps columnType String to a validatorType.
	 * @param columnType
	 * @return validatorType
	 * @throws SQLDataException
	 */
	public static TypeValidator getInstance(final String columnType) throws SQLDataException
	{
		final ColumnTypeColumnInfoPair result = analyseValidatorType(columnType);

		switch (result.validationType) {
			case VARCHAR:       return new VarcharTypeValidator(result.columnType);
			case VARCHAR2:      return new VarcharTypeValidator(result.columnType);
			case DATE:          return new DateTypeValidator();
			case TIMESTAMP:     return new DateTypeValidator();
            case TIME:          return new DateTypeValidator();
            case NUMBER:        return new NumberTypeValidator(result.columnType);
            case BOOLEAN:       return new BooleanTypeValidator();
			default:            throw new SQLDataException("Unknown validation type '" + result.validationType + "'.");
		}
	}

	private static ColumnTypeColumnInfoPair analyseValidatorType(final String columnType) throws SQLDataException
	{
        for (ValidatorType validationType : ValidatorType.values()) {
			if (columnType.toUpperCase().startsWith(validationType.name())) {
				return new ColumnTypeColumnInfoPair(validationType, columnType);
			}
		}

		if (DbProperties.SUPPORT_MYSQL) {
			return analyseMySqlValidatorType(columnType);
		}

		throw new SQLDataException("Unknown column type '" + columnType + "'.");
	}

	private static ColumnTypeColumnInfoPair analyseMySqlValidatorType(String columnType) throws SQLDataException
	{
		for (MySqlNaturalNumberType mySqlNaturalNumberType : MySqlNaturalNumberType.values()) {
			if (columnType.toUpperCase().startsWith(mySqlNaturalNumberType.name()))
            {
                String sizeInfo = columnType.substring(mySqlNaturalNumberType.name().length()).trim();

                if (sizeInfo.length() == 0)
                    return new ColumnTypeColumnInfoPair(ValidatorType.NUMBER, "NUMBER(1000)");

                return new ColumnTypeColumnInfoPair(ValidatorType.NUMBER, "NUMBER" + sizeInfo);
			}
		}

		for (MySqlDecimalNumberType mySqlDecimalNumberType : MySqlDecimalNumberType.values()) {
			if (columnType.toUpperCase().startsWith(mySqlDecimalNumberType.name()))
            {
                String sizeInfo = columnType.substring(mySqlDecimalNumberType.name().length()).trim();

                if (sizeInfo.length() == 0)
                    return new ColumnTypeColumnInfoPair(ValidatorType.NUMBER, "NUMBER(1000)");

                return new ColumnTypeColumnInfoPair(ValidatorType.NUMBER, "NUMBER" + sizeInfo);
			}
		}

		for (MySqlVarcharType mySqlVarcharType : MySqlVarcharType.values()) {
			if (columnType.toUpperCase().startsWith(mySqlVarcharType.name()))
            {
                String sizeInfo = columnType.substring(mySqlVarcharType.name().length()).trim();

                if (sizeInfo.length() == 0)
                    return new ColumnTypeColumnInfoPair(ValidatorType.VARCHAR, "VARCHAR(4000)");

                return new ColumnTypeColumnInfoPair(ValidatorType.VARCHAR, "VARCHAR" + sizeInfo);
			}
		}

		if (columnType.toUpperCase().startsWith(MYSQL_BIT)) {
            return new ColumnTypeColumnInfoPair(ValidatorType.VARCHAR, "VARCHAR(1)");
		}

		throw new SQLDataException("Unknown column type '" + columnType + "'." );
	}

    protected int parseDecimalPlacesFromString(final String input) throws SQLDataException {
        final char[] charArray = input.toCharArray();
        String toReturn = "";
        boolean isTakeDigitMode = false;

        for (char c : charArray)
        {
            if (',' == c) {
                isTakeDigitMode = true;
            } else if (')' == c) {
                isTakeDigitMode = false;
            } else if (isTakeDigitMode) {
                toReturn += c;
            }
        }

        if (toReturn.length() == 0)  {
            throw new IllegalArgumentException("Non parseable maximum length: '" + toReturn + "'.");
        }

        try {
            return Integer.valueOf(toReturn).intValue();
        } catch (Exception e) {
            throw new SQLDataException("Non parseable decimal place: '" + toReturn + "'.");
        }
    }


	protected int parseMaxLengthFromString(final String input) throws SQLDataException {
		final char[] charArray = input.toCharArray();
		String toReturn = "";
		boolean isTakeDigitMode = false;

		for (char c : charArray)
		{
			if ('(' == c) {
                isTakeDigitMode = true;
            } else if (')' == c) {
                isTakeDigitMode = false;
            } else if (',' == c) {
                isTakeDigitMode = false;
            } else if (' ' == c) {
                isTakeDigitMode = false;
            } else if (isTakeDigitMode) {
                toReturn += c;
            }
		}

        if (toReturn.length() == 0 || "*".equals(toReturn))  {
            throw new IllegalArgumentException("Non parseable maximum length: '" + toReturn + "'.");
        }

        try {
            return Integer.valueOf(toReturn).intValue();
        } catch (IllegalArgumentException e) {
            throw new SQLDataException("Non parseable maximum length: '" + toReturn + "'.");
        }
	}

	protected boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * Takes a null value as something smaller than a non-null value.
	 * @param value1
	 * @param value2
	 * @return null in case of two null values
	 */
	protected Boolean isValue1SmallerThanValue2ForNullvalues(Object value1, Object value2)
	{
		if (value1 == null) {
			if (value2 == null) {
				return null;  // both are equal
			} else {
				return true;
			}
		} else {
			if (value2 == null) {
				return false;
			} else {
				throw new RuntimeException("Unexpected Scenario");
			}
		}
	}

    static class ColumnTypeColumnInfoPair {
        ValidatorType validationType;
        String columnType;

        ColumnTypeColumnInfoPair(ValidatorType vType, String cType) {
            this.validationType = vType;
            this.columnType = cType;
        }

    }
}