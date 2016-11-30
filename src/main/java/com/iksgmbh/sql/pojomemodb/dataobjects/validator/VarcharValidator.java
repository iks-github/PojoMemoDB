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

public class VarcharValidator extends Validator
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.VARCHAR;
	
	private int maxLength;
	
	public VarcharValidator(final String columnType) throws SQLDataException {
		try {
			this.maxLength = parseNextIntegerFromString(columnType);
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
	public void validate(Object value) throws SQLDataException {
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
}