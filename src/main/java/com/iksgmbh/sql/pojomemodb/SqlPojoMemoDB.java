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
package com.iksgmbh.sql.pojomemodb;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.connection.SqlPojoConnection;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.TableStoreData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableStoreMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.TableStore;

/**
 * This class represents the main class of an incomplete but lightweight implementation of a SQL database,
 * that stores its whole data only in memory. It is lightweight because its a pure Java implementation
 * that does not depend on any framework.
 * Although incomplete, many of the most basic functionalities are supported 
 * (details see https://github.com/iks-github/SqlPojoMemoDB/wiki).
 * Therefore it can be used to replace a heavyweight sql database such as Oracle for Unit testing.
 * 
 * If you need a functionality of a real DB that is not yet implemented here,
 * feel free to implement it (test driven!)...
 * 
 * This class provides a STATIC interface which uses a private static instance of this class (Singleton).
 * This interface is defined by a small number of public static methods.
 * The small number is supposed to allow a sufficient handling of the database. 
 * 
 * @author Reik Oberrath
 */
public class SqlPojoMemoDB
{
	private static final SqlPojoMemoDB SQL_MEMORY_DB = new SqlPojoMemoDB();

	private TableStore tableStore;
	private SqlExecutor sqlExecutor;
	private SqlPojoConnection connection;

	/**
	 * Private Singleton Constructor!
	 */
	private SqlPojoMemoDB()  {
		tableStore = new TableStore();
		sqlExecutor = new SqlExecutor(this);
		connection = new SqlPojoConnection();
	}
	
	// #########################################################################################
	//             P U B L I C   S T A T I C   I N T E R F A C E    M E T H O D S
	// #########################################################################################
	
	/**
	 * For test purpose
	 * @return SqlPojoMemoryDB 
	 */
	public static SqlPojoMemoDB getInstance() {
		return SQL_MEMORY_DB;
	}
	
	/**
	 * @return the single java.sql.Connection instance this MemoryDB 
	 */
	public static Connection getConnection() {
		return SQL_MEMORY_DB.connection;
	}
	
	/**
	 * Executes a sql statement
	 * @param data found by a select statement or null (e.g. for insert statements)
	 * @throws SQLException 
	 */
	public static Object execute(final String sql) throws SQLException {
		return SQL_MEMORY_DB.executeSqlStatement(sql.trim());
	}

	/**
	 * Some metrics on the data in the DB.
	 * @return ContentStatistics
	 */
	public static TableStoreStatistics getDbStatistics() {
		return SQL_MEMORY_DB.getTableStoreStatistics();
	}

	/**
	 * Removes all tables and sequences from the DB. 
	 * Total clean up!
	 */
	public static void reset() {
		SQL_MEMORY_DB.tableStore.dropAllTables();
		SQL_MEMORY_DB.tableStore.dropAllSequences();
	}

	/**
	 * Removes a single table from the DB.
	 */
	public static void dropTable(final String tableName) {
		SQL_MEMORY_DB.tableStore.dropTable(tableName);
	}

	/**
	 * Results in each table being empty.
	 * @return total number of datasets deleted
	 */
	public static int removeAllContentOfAllTables() {
		return SQL_MEMORY_DB.tableStore.removeAllContentOfAllTables();
	}
	
	
	// #########################################################################################
	//                         I N S T A N C E   M E T H O D S
	// #########################################################################################
	
	
	public TableStoreStatistics getTableStoreStatistics() {
		return tableStore;
	}
	
	public TableStoreMetaData getTableStoreMetaData() {
		return tableStore;
	}
	
	public TableStoreData getTableStoreData() {
		return tableStore;
	}

	private String removeLineBreaks(String sql) {
		return sql.replace("\n", " ").replace("\r", "");
	}

	public SequenceData getSequenceData(final String sequenceName) throws SQLDataException {
		return tableStore.getSequenceData(sequenceName.toUpperCase());
	}
	
	private Object executeSqlStatement(String sql) throws SQLException {
		return sqlExecutor.executeSqlStatement( removeLineBreaks(sql) );
	}
	
}