package com.iksgmbh.sql.pojomemodb.dataobjects.validator;

import java.sql.SQLDataException;

public abstract class Validator {

	public enum ValidatorType { VARCHAR, VARCHAR2, NUMBER, DATE};

	public abstract void validate(Object value) throws SQLDataException;
	
	public abstract ValidatorType getType();
	
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
