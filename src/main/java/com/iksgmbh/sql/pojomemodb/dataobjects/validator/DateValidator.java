package com.iksgmbh.sql.pojomemodb.dataobjects.validator;

import java.sql.SQLDataException;

public class DateValidator extends Validator 
{
	private static final ValidatorType VALIDATION_TYPE = ValidatorType.DATE;
	
	@Override 
	public void validate(Object value) throws SQLDataException {
		// nothing to do here
	}
	
	@Override 
	public ValidatorType getType() { 
		return VALIDATION_TYPE; 
	}
}


