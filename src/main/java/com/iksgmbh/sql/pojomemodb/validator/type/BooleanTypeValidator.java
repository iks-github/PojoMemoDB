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

import java.sql.SQLDataException;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.validator.TypeValidator;


public class BooleanTypeValidator extends TypeValidator
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.BOOLEAN;

    @Override
    public void validateValueForType(Object value) throws SQLDataException
    {
        if (value == null) return; // nullable is not checked here
        if (SQLKeyWords.NULL.equalsIgnoreCase(value.toString())) return;
        
        try {
            if (value instanceof String) {
                convertIntoColumnType( (String) value );
                return;
            }

            if (value instanceof Boolean) {
                convertIntoColumnType( "" + value );
                return;
            }

            throw new SQLDataException("Value '" + value + "' is not valid");

        } catch (SQLDataException e) {
            throw new SQLDataException("Value '" + value + "' is not valid");
        }
    }
	@Override 
	public ValidatorType getType() { 
		return VALIDATION_TYPE; 
	}

	@Override
	public Object convertIntoColumnType(String valueAsString) throws SQLDataException
    {
		if (valueAsString.equalsIgnoreCase("TRUE")) {
			return Boolean.TRUE;
		}
		
		if (valueAsString.equalsIgnoreCase("FALSE")) {
			return Boolean.FALSE;
		}
		
		throw new SQLDataException("Insert values '" + valueAsString + "' is no boolean.");
	}
	
	@Override
	public Boolean isValue1SmallerThanValue2(Object value1, Object value2) throws SQLDataException
	{
		if (value1 == null || value2 == null)
			return isValue1SmallerThanValue2ForNullvalues(value1, value2);

		final Boolean b1 = (Boolean) value1;
		final Boolean b2 = (Boolean) value2;

		int result = b1.compareTo(b2);

		if (result == 0) return null;

		return result == -1;
	}

}