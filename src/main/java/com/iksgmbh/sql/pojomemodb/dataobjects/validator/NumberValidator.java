package com.iksgmbh.sql.pojomemodb.dataobjects.validator;

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
}

