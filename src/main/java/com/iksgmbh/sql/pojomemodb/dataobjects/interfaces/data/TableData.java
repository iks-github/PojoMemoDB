package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;

/**
 * List for methods to manage the data content of a table. 
 * 
 * @author Reik Oberrath
 */
public interface TableData extends TableStatistics {

	void insertDataRow(List<ApartValue> values) throws SQLDataException;

	List<Object[]> select(List<String> selectedColumns, List<WhereCondition> whereConditions) throws SQLException;

	int update(List<ApartValue> newValues, List<WhereCondition> whereConditions) throws SQLException;

	int delete(List<WhereCondition> whereConditions) throws SQLDataException;

}
