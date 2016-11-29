package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics;

/**
 * List for getter methods available from outside that defines
 * which information of a column can be accessed from outside the DB.
 * 
 * @author Reik Oberrath
 */
public interface ColumnStatistics {

	String getColumnName();

	String getColumnType();

	boolean isNullable();

	int getOrderNumber();

}
