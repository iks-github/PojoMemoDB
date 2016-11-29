package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data;

import java.sql.SQLDataException;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;

/**
 * List for methods to manage the data content of a table. 
 * 
 * @author Reik Oberrath
 */
public interface TableStoreData extends TableStoreStatistics {

	TableData getTableData(String tableName) throws SQLDataException;
	SequenceData getSequenceData(String sequenceName) throws SQLDataException;

}
