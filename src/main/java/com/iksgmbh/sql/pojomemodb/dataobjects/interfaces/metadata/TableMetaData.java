package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata;

import java.sql.SQLDataException;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStatistics;

/**
 * List for methods to manage the structure of a table. 
 * 
 * @author Reik Oberrath
 */
public interface TableMetaData extends TableStatistics {

	void createNewColumn(String columnName, String columnType, 
			             boolean nullable,  SqlPojoMemoDB memoryDb) throws SQLDataException;

}
