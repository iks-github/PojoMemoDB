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
package com.iksgmbh.sql.pojomemodb.validator.type;

import com.iksgmbh.sql.pojomemodb.validator.TypeValidator;

import java.sql.SQLDataException;

public class VarcharTypeValidator extends TypeValidator
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.VARCHAR;
	
	private int maxLength;
	
	public VarcharTypeValidator(final String columnType) throws SQLDataException {
		try {
			this.maxLength = parseMaxLengthFromString(columnType);
		} catch (IllegalArgumentException e) {
			throw new SQLDataException("Invalid column type '" + columnType + "'. Something expected like VARCHAR(50 CHAR).");
		}
	}

	@Override
	public String toString() {
		return "ValidationData [validationType=" + VALIDATION_TYPE 
				+ ", maxLength=" + maxLength + "]";
	}
	
	@Override
	public void validateValueForType(Object value) throws SQLDataException {
        // nothing to do here
	}

	@Override
	public ValidatorType getType() {
		return VALIDATION_TYPE;
	}

	@Override
	public Object convertIntoColumnType(String valueAsString) throws SQLDataException {
		if (! valueAsString.startsWith("'") || 
			! valueAsString.endsWith("'") )
		{
			throw new SQLDataException("Insert VARCHAR value '" + valueAsString + "' is not surrounded by '.");
		}
		
		return valueAsString.substring( 1, valueAsString.length() - 1 );
	}

	@Override
	public Boolean isValue1SmallerThanValue2(Object value1, Object value2) throws SQLDataException
	{
		if (value1 == null || value2 == null)
			return isValue1SmallerThanValue2ForNullvalues(value1, value2);

		final String string1 = (String) value1;
		final String string2 = (String) value2;

		int result = string1.compareTo(string2);

		if (result < 0) return true;
		if (result > 0) return false;

		return null;
	}

}