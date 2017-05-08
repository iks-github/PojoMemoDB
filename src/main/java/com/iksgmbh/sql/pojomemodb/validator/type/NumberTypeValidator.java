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

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.validator.TypeValidator;

import java.math.BigDecimal;
import java.sql.SQLDataException;

public class NumberTypeValidator extends TypeValidator
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.NUMBER;
	
	int maxLength;
	int numberDecimalPlaces; 
	
	public NumberTypeValidator(final String columnType) throws SQLDataException
	{
		try {
			this.maxLength = parseMaxLengthFromString(columnType);
		} catch (IllegalArgumentException e) {
			// practically accept all numbers
			this.maxLength = 1000;
			this.numberDecimalPlaces = 1000; 
		}

        try {
            this.numberDecimalPlaces = parseDecimalPlacesFromString(columnType);
        } catch (IllegalArgumentException e) {
            this.numberDecimalPlaces = 0;
        }
	}


	@Override
	public String toString() {
		return "ValidationData [validationType=" + VALIDATION_TYPE 
				+ ", maxLength=" + maxLength
				+ ", numberDecimalPlaces=" + numberDecimalPlaces + "]";
	}

	@Override
	public void validateValueForType(Object value) throws SQLDataException
    {
        if ( value == null ) return; // nullable is not checked here
        if (SQLKeyWords.NULL.equalsIgnoreCase(value.toString())) return;
        
        BigDecimal number;

        try {
        	number = (BigDecimal) convertIntoColumnType( "" + value );
        } catch (SQLDataException e) {
            throw new SQLDataException("Value '" + value + "' is not valid");
        }
        
        checkMaxLength(number);
    }

	private void checkMaxLength(final BigDecimal number) 
	{
		
		
	}


	@Override
	public ValidatorType getType() {
		return VALIDATION_TYPE;
	}


	@Override
	public Object convertIntoColumnType(String valueAsString) throws SQLDataException {
		try {
			return new BigDecimal(valueAsString);
		} catch (NumberFormatException e) {
			throw new SQLDataException("Insert values '" + valueAsString + "' is no number.");
		}
	}

	@Override
	public Boolean isValue1SmallerThanValue2(Object value1, Object value2) throws SQLDataException
	{
		if (value1 == null || value2 == null)
			return isValue1SmallerThanValue2ForNullvalues(value1, value2);

		final BigDecimal number1 = (BigDecimal) value1;
		final BigDecimal number2 = (BigDecimal) value2;

		int result = number1.compareTo(number2);

		if (result == 0) return null;

		return result == -1;
	}
}