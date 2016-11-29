package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata;

import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;

/**
 * List for methods to manage the content of the table store. 
 * 
 * @author Reik Oberrath
 */
public interface TableStoreMetaData extends TableStoreStatistics {

	void addTable(TableMetaData tableMetaData) throws SQLException;

	void dropAllTables();

	void dropTable(String tableName);

	void addSequence(Sequence sequence) throws SQLException;
	
	void dropAllSequences();

}
