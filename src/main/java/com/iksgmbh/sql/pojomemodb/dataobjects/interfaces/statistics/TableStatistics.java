package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics;

import java.sql.SQLDataException;
import java.util.List;

/**
 * List for getter methods available from outside that defines
 * which information of a table can be accessed from outside the DB.
 * 
 * @author Reik Oberrath
 */
public interface TableStatistics {

	String getTableName();

	int getNumberOfRows();

	int getNumberOfColumns();

	/**
	 * Column names are sorted by the internal order.  
	 * @param tableName
	 * @return sorted list of column names
	 * @throws SQLDataException
	 */
	List<String> getNamesOfColumns();

	String getTypeOfColumn(String columnName) throws SQLDataException;

	boolean isColumnNullable(String columnName) throws SQLDataException;

}
