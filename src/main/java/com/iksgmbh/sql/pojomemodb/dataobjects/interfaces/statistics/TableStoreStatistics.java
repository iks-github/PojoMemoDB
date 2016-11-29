package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics;

import java.sql.SQLDataException;
import java.util.List;

/**
 * List for getter methods available from outside that defines
 * which information can be accessed from outside the DB.
 * 
 * @author Reik Oberrath
 */
public interface TableStoreStatistics {

	int getNumberOfTables();

	/**
	 * Alphabetically sorted list of tables in the DB.
	 * @return sorted list of table names
	 */
	List<String> getTableNames();

	int getNumberOfRows(String tableName) throws SQLDataException;

	int getNumberOfColumns(String tableName) throws SQLDataException;

	/**
	 * Column names are sorted by the internal order.  
	 * @param tableName
	 * @return sorted list of column names
	 * @throws SQLDataException
	 */
	List<String> getNamesOfColumns(String tableName) throws SQLDataException;

	String getTypeOfColumn(String tableName, String columnName) throws SQLDataException;

	boolean isColumnNullable(String tableName, String columnName) throws SQLDataException;

}
