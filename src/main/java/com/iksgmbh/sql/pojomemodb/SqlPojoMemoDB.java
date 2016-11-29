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
 * Although incomplete, many of the most basic functionalities are supported.
 * Therefore it can be used to replace a heavyweight sql database such as Oracle for Unit testing.
 * 
 * Supported features are:
 * - creating and deleting tables,
 * - inserting, updating, deleting rows of data in a table
 * - selecting a subset of columns from a table
 * - validating for type safety (data values are only accepted if they match their column type) for 
 *   following supported column types: 
 *     NUMBER    e.g. NUMBER(10,2), NUMBER(1) or NUMBER  
 *     VARCHAR2  e.g. VARCHAR2(50) or VARCHAR2(50 CHARS)  
 *     DATE
 * - selecting a subset of data rows from a table by where clauses (only AND operator)
 *   using supported comparator types:  =,   <>,  IS NULL,  IS NOT NUL
 * - nullable check
 * - PreparedStatement and ResultSet for the following supported Java types:
 *   String, int, long 
 * - SQL functions to_date and to_char for date columns
 * - using table alias names in select statements
 * - joining of tables (using ANSI and NON-ANSI syntax) but only JOIN or INNER JOIN with "="-comparator in join condition
 * - support of sequences
 * 
 * Currently not supported features are e.g.:
 * 
 * - constraints of any type
 * - orderBy
 * - joining of tables other than INNER JOIN and joining with comparators in join condition other than "="
 * - alter table (modifying table structure after creating)
 * - commit
 * - multi-threaded access to the data in the db
 * - unsupported comparator types in where clauses:  <=,   <,   >,  >=, like
 * - unsupported OR operator for where clauses
 * - JOINs
 * - SQL functions such as count, max, min
 * - subselects
 * - WITH clauses
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
