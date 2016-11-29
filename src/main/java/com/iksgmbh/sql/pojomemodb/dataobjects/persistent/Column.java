package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_EQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_IS_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_NOT_NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.COMPARATOR_UNEQUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NEXTVAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NULL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.SYSDATE;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.TO_DATE;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.iksgmbh.sql.pojomemodb.DbProperties;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.ColumnData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.ColumnMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.DateValidator;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.NumberValidator;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.Validator;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.Validator.ValidatorType;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.VarcharValidator;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;

/**
 * Metadata about a single table.
 * 
 * @author Reik Oberrath
 */
public class Column implements ColumnStatistics, ColumnMetaData, ColumnData
{	
	private String columnName;
	private String columnType;
	private boolean nullable;
	private int orderNumber;
	private SqlPojoMemoDB memoryDB;
	private Validator validator;

	public Column(final String columnName, 
			      final String columnType,
			      final boolean nullable,
			      final int orderNumber, 
			      final SqlPojoMemoDB memoryDB) throws SQLDataException 
	{
		this.columnName = columnName.toUpperCase();
		this.columnType = columnType.toUpperCase();
		this.nullable = nullable;
		this.orderNumber = orderNumber;
		this.memoryDB = memoryDB;
		this.validator = determineValidator(columnType);
	}
	
	private Validator determineValidator(final String columnType) throws SQLDataException 
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

	private ValidatorType determineValidatorType(final String columnType) throws SQLDataException 
	{
		for (ValidatorType validationType : ValidatorType.values()) {
			if (columnType.toUpperCase().startsWith(validationType.name())) {
				return validationType;
			}
		}
		
		throw new SQLDataException("Unknown column type '" + columnType + "' for column '" + columnName + "'.");
	}


	public int getIndexInTable() {
		return orderNumber - 1;
	}
	
	public ValidatorType getValidationType() {
		return validator.getType();
	}

	// #########################################################################################
	//                       S T A T I S T I C S   M E T H O D S
	// #########################################################################################
	
	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String getColumnType() {
		return columnType;
	}
	
	@Override
	public boolean isNullable() {
		return nullable;
	}
	
	@Override
	public int getOrderNumber() {
		return orderNumber;
	}
	
	@Override
	public String toString() {
		return "Column [columnName=" + columnName + ", columnType=" + columnType 
				    + ", nullable=" + nullable + ", orderNumber=" + orderNumber + "]";
	}

	
	// #########################################################################################
	//                            D A T A     M E T H O D S
	// #########################################################################################
	
	@Override
	public Object convertIntoColumnType(final String valueAsString) throws SQLDataException 
	{
		if (NULL.equals(valueAsString) || valueAsString == null) {
			return null;
		}
		
		if (valueAsString.endsWith(NEXTVAL)) {
			return getValueFromSequence(valueAsString);
		}
		
		if (DbProperties.REPLACE_EMPTY_STRING_BY_NULL && valueAsString.equals("''"))  {
			return null;
		}
		
		if (validator.getType() == ValidatorType.VARCHAR2 
			|| validator.getType() == ValidatorType.VARCHAR)
		{
			if (! valueAsString.startsWith("'") || 
				! valueAsString.endsWith("'") )
			{
				throw new SQLDataException("Insert VARCHAR value '" + valueAsString + "' is not surrounded by '.");
			}
			
			return valueAsString.substring( 1, valueAsString.length() - 1 );
		}
		
		if (validator.getType() == ValidatorType.NUMBER)
		{
			try {
				return new BigDecimal(valueAsString);
			} catch (NumberFormatException e) {
				throw new SQLDataException("Insert values '" + valueAsString + "' is no number.");
			}
		}
		
		if (validator.getType() == ValidatorType.DATE)
		{
			if (SYSDATE.equals(valueAsString)) {
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
		
		return valueAsString;
	}

	private Object getValueFromSequence(String valueAsString) throws SQLDataException 
	{
		final int pos = valueAsString.indexOf("." + NEXTVAL);
		final String sequenceName = valueAsString.substring(0, pos);
		final SequenceData sequenceData;
		try {
			sequenceData = memoryDB.getSequenceData(sequenceName);
		} catch (SQLDataException e) {
			throw new SQLDataException("Sequence '" + sequenceName + "' is unkown.");
		}
		return new Long(sequenceData.nextVal());
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

	/**
	 * Validates validation type specific settings.
	 * Nullable check is not performed here!
	 */
	@Override
	public void validate(Object value) throws SQLDataException {
		validator.validate(value);
	}

	/**
	 * Compares a data value against a WhereCondition value
	 * @param String comparator
	 * @param valueToCheck
	 * @return true if dataValueToCheck is valid 
	 * @throws SQLDataException 
	 */
	public boolean isWhereConditionMatched(final String conditionValueAsString, 
			                               final String comparator, 
			                               final Object dataValueToCheck) throws SQLDataException 
	{
		final Object conditionValue = convertIntoColumnType(conditionValueAsString);
		validate(conditionValue);
		
		if ( COMPARATOR_EQUAL.equals(comparator) ) {
			return checkForEquality(conditionValue, dataValueToCheck);
		}
			
		if ( COMPARATOR_UNEQUAL.equals(comparator) ) {
			return ! checkForEquality(conditionValue, dataValueToCheck);
		}
			
		if ( COMPARATOR_IS_NULL.equals(comparator) ) {
			return dataValueToCheck == null;
		}
		
		if ( COMPARATOR_NOT_NULL.equals(comparator) ) {
			return dataValueToCheck != null;
		}
		
		throw new RuntimeException("Unsupported comparator: " + comparator);
	}

	private boolean checkForEquality(Object conditionValue, Object dataValueToCheck) 
	{
		if (conditionValue == null && dataValueToCheck == null)  {
			return true;
		}
		
		if (conditionValue != null && dataValueToCheck == null)  {
			return false;
		}
		
		if (conditionValue == null && dataValueToCheck != null)  {
			return true;
		}
		
		return conditionValue.toString().equals(dataValueToCheck.toString());
	}

}
