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
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import org.joda.time.DateTime;

import java.sql.SQLDataException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;


public class DateTypeValidator extends TypeValidator
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.DATE;

    @Override
    public void validateValueForType(Object value) throws SQLDataException
    {
        if (value == null) return; // nullable is not checked here

        try {
            if (value instanceof String) {
                convertIntoColumnType( (String) value );
                return;
            }

            if (value instanceof DateTime) {
                convertIntoColumnType( ((DateTime) value).toString() );
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
		if (SYSDATE.equals(valueAsString)
            || CURRENT_TIMESTAMP.equals(valueAsString)
            || GET_DATE.equals(valueAsString)) {
			return new DateTime();
		}
		
		if (valueAsString.startsWith(TO_DATE))			{
			return toDateTime(valueAsString.substring(TO_DATE.length()));
		}
		
		try {
			return new DateTime(valueAsString);
		} catch (Exception e) {
			throw new SQLDataException("Insert values '" + valueAsString + "' is no date.");
		}
	}
	
	private DateTime toDateTime(String dateString) throws SQLDataException 
	{
		try {
			dateString = StringParseUtil.removeSurroundingPrefixAndPostFix(dateString, "(", ")");
			
			final String[] splitResult = dateString.split(",");
			
			if (splitResult.length != 2) {
				throw new SQLDataException("Cannot parse to DateTime: " + dateString);
			}
			
			final String dateValue = StringParseUtil.removeSurroundingPrefixAndPostFix(splitResult[0], "'", "'");
			final String pattern = StringParseUtil.removeSurroundingPrefixAndPostFix(splitResult[1], "'", "'");
			
			return toDateTime(dateValue, translateFromOracleToJavaLiterals(pattern));
		} catch (Exception e) {
			throw new SQLDataException(e);
		}
	}
	
	private String translateFromOracleToJavaLiterals(String pattern) 
	{
		return pattern.replace('R', 'y')
				      .replace('D', 'd'); 
	}

	private DateTime toDateTime(final String value, 
			                    final String pattern) throws SQLDataException
	{
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		final Date date;
		
		try {
			date = simpleDateFormat.parse(value);
		} catch (ParseException e) {
			throw new SQLDataException("Cannot convert DateTime: dateAsString=" + value + ", pattern=" + pattern);
		}
		
		return new DateTime(date.getTime());
		
	}

	@Override
	public Boolean isValue1SmallerThanValue2(Object value1, Object value2) throws SQLDataException
	{
		if (value1 == null || value2 == null)
			return isValue1SmallerThanValue2ForNullvalues(value1, value2);

		final DateTime dt1 = (DateTime) value1;
		final DateTime dt2 = (DateTime) value2;

		int result = dt1.compareTo(dt2);

		if (result == 0) return null;

		return result == -1;
	}

}