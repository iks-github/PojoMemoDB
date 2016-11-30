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
package com.iksgmbh.sql.pojomemodb.dataobjects.validator;

import java.math.BigDecimal;
import java.sql.SQLDataException;

public class NumberValidator extends Validator 
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.NUMBER;
	
	int maxLength;
	int numberDecimalPlaces; 
	
	public NumberValidator(final String columnType) throws SQLDataException 
	{
		try {
			this.maxLength = parseNextIntegerFromString(columnType);
		} catch (IllegalArgumentException e) {
			// practically accept all numbers
			this.maxLength = 1000;
			this.numberDecimalPlaces = 1000; 
		}
		
		final int pos = columnType.indexOf(maxLength) + ("" + maxLength).length();
		final String unparsedRest = columnType.substring(pos);
		
		try {
			this.numberDecimalPlaces = parseNextIntegerFromString(unparsedRest);
		} catch (IllegalArgumentException e) {
			// accept no decimal places
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
	public void validate(Object value) throws SQLDataException {
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
}