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
}

