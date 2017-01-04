/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import com.iksgmbh.sql.pojomemodb.DbProperties;
import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.TableData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.TableStoreData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableStoreMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.oracle.DualTable;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Container for all data in the MemoryDB.
 * Its public methods belong to the three interfaces:
 * 
 * 1. TableStoreStatistics (getter methods visible from outside the DB)
 * 2. TableStoreMetaData   (methods to manage the structures of the tables in the DB)
 * 3. TableStoreData       (methods to manage the data content in the DB)
 * 
 * @author Reik Oberrath
 */
public class TableStore implements TableStoreStatistics, TableStoreMetaData, TableStoreData
{
	/**
	 * Store for all user tables in the DB
	 */
	private HashMap<String, Table> userTableMap;

	/**
	 * Store for all user tables in the DB
	 */
	private HashMap<String, Table> systemTableMap;
	
	/**
	 * Store for all tables in the DB
	 */
	private HashMap<String, Sequence> sequenceMap;
	

	public TableStore()  
	{
		userTableMap = new HashMap<String, Table>();
		systemTableMap = new HashMap<String, Table>();
		sequenceMap = new HashMap<String, Sequence>();
		
		initDualTableIfNeeded();
	}
	
	private void initDualTableIfNeeded()
	{
		if (DbProperties.USE_ORACLE_DUAL_TABLE) {
			systemTableMap.put(SQLKeyWords.DUAL, new DualTable(sequenceMap) );
		}
	}

	// #########################################################################################
	//                       S T A T I S T I C S   M E T H O D S
	// #########################################################################################

	@Override
	public int getNumberOfTables() {
		return userTableMap.size();
	}

	@Override
	public List<String> getTableNames() {
		final List<String> toReturn = new ArrayList<String>();
		toReturn.addAll( userTableMap.keySet() );
		Collections.sort(toReturn);
		return toReturn;
	}
	
	@Override
	public int getNumberOfRows(final String tableName) throws SQLDataException {
		return getTableStatistics(tableName).getNumberOfRows();
	}
	
	@Override
	public int getNumberOfColumns(final String tableName) throws SQLDataException {
		return getTableStatistics(tableName).getNumberOfColumns();
	}
	
	@Override
	public List<String> getNamesOfColumns(final String tableName) throws SQLDataException {
		return getTableStatistics(tableName).getNamesOfColumns();
	}

	@Override
	public String getTypeOfColumn(final String tableName, final String columnName) throws SQLDataException {
		return getTableStatistics(tableName).getTypeOfColumn(columnName);
	}

	@Override
	public boolean isColumnNullable(final String tableName, final String columnName) throws SQLDataException {
		return getTableStatistics(tableName).isColumnNullable(columnName);
	}

	private TableStatistics getTableStatistics(final String tableName) throws SQLDataException 
	{
		final TableStatistics tableStatistics = userTableMap.get(tableName);
		
		if (tableStatistics == null) {
			throw new SQLDataException("Unknown table: " + tableName);
		}
		
		return tableStatistics;
	}

	
	// #########################################################################################
	//                         M E T A D A T A   M E T H O D S
	// #########################################################################################
	

	@Override
	public void addTable(final TableMetaData tableMetaData) throws SQLException 
	{
		final String tableName = tableMetaData.getTableName().toUpperCase();
		
		if (userTableMap.containsKey(tableName)) {
			throw new SQLException("A table '" + tableName.toUpperCase() + "' is already existing in the database.");
		}
		
		userTableMap.put(tableName, (Table) tableMetaData);
	}

	@Override
	public void dropAllTables() {
		userTableMap.clear();
		systemTableMap.clear();
		initDualTableIfNeeded();
	}
	
	@Override
	public void dropAllSequences() {
		sequenceMap.clear();
	}
	
	
	@Override
	public void dropTable(final String tableName) {
		userTableMap.remove(tableName);
	}
	
	// #########################################################################################
	//                            D A T A      M E T H O D S
	// #########################################################################################
	
	@Override
	public TableData getTableData(final String tableName) throws SQLDataException 
	{
		final String upperCaseTableName = tableName.toUpperCase();
		TableData tableData = (TableData) userTableMap.get(upperCaseTableName);
		
		if (tableData == null) {
			tableData = (TableData) systemTableMap.get(tableName);
			
			if (tableData == null) {
				throw new SQLDataException("Unknown table <" + tableName + ">!");
			}
		}
		
		return tableData;
	}

	@Override
	public SequenceData getSequenceData(final String sequenceName) throws SQLDataException 
	{
		final SequenceData sequenceData = (SequenceData) sequenceMap.get(sequenceName);
		
		if (sequenceData == null) {
			throw new SQLDataException("Unknown sequence: " + sequenceData);
		}
		
		return sequenceData;
	}

	@Override
	public void addSequence(final Sequence sequence) throws SQLException {
		final String sequenceName = sequence.getSequenceName().toUpperCase();
		
		if (sequenceMap.containsKey(sequenceName)) {
			throw new SQLException("A sequence '" + sequenceName + "' is already existing in the database.");
		}
		
		sequenceMap.put(sequenceName, (Sequence) sequence);
	}

	public int removeAllContentOfAllTables() 
	{
		int toReturn = 0;
		
		final Set<Entry<String, Table>> entrySet = userTableMap.entrySet();
		
		for (Entry<String, Table> entry : entrySet) {
			toReturn += entry.getValue().removeAllContent();
		}
		
		return toReturn;
	}

}