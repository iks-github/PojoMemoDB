package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

/**
 * A single value stored temporarily apart a table.
 * The value does not have a defined type, but belongs
 * to column of a existing table. 
 * The value will be validated against the column's type.
 * 
 * @author Reik Oberrath
 */
public class ApartValue {

	private String valueAsString;
	private String columnName;
	
	public String getValueAsString() {
		return valueAsString;
	}
	
	public ApartValue(String valueAsString, String columnName) {
		super();
		this.valueAsString = valueAsString;
		this.columnName = columnName;
	}
	
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String toString() {
		return "ApartValue [valueAsString=" + valueAsString + ", columnName=" + columnName + "]";
	}
	
	
}
