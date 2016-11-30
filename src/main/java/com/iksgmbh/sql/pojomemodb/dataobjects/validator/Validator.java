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

import java.sql.SQLDataException;

public abstract class Validator {

	public enum ValidatorType { VARCHAR, VARCHAR2, NUMBER, DATE};

	public abstract void validate(Object value) throws SQLDataException;
	public abstract ValidatorType getType();
	public abstract Object convertIntoColumnType(String valueAsString) throws SQLDataException;
	
	public static Validator getInstance(final String columnType) throws SQLDataException 
	{
		final ValidatorType validationType = determineValidatorType(columnType);
		
		switch (validationType) {
			case VARCHAR:  return new VarcharValidator(columnType);
			case VARCHAR2: return new VarcharValidator(columnType);
			case DATE:     return new DateValidator();
			case NUMBER:   return new NumberValidator(columnType);
			default:       throw new SQLDataException("Unknown column type '" + columnType + "'.");
		}
	}

	private static ValidatorType determineValidatorType(final String columnType) throws SQLDataException 
	{
		for (ValidatorType validationType : ValidatorType.values()) {
			if (columnType.toUpperCase().startsWith(validationType.name())) {
				return validationType;
			}
		}
		
		throw new SQLDataException("Unknown column type '" + columnType + "'");
	}
	
	
	protected int parseNextIntegerFromString(final String input) throws SQLDataException 
	{
		final char[] charArray = input.toCharArray();
		
		String toReturn = "";
		boolean startParsingNumber = false;
		
		for (char c : charArray) 
		{
			if (isDigit(c)) {
				startParsingNumber = true;
				toReturn += c;
			}
			else
			{
				if (startParsingNumber) {
					return Integer.valueOf(toReturn).intValue();
				}
			}
		}
		
		throw new IllegalArgumentException("Unexpected input: " + input);
	}

	protected boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

}