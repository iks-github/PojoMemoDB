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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.TableStore;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;
import com.iksgmbh.sql.pojomemodb.utils.SqlStatementLoader;

public class SqlPojoMemoDBTest {
	
	private static final SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
	
	private static final String TEST_CREATE_SQL_FILE = "src/test/resources/create.sql";
	private static final String TEST_INSERT_SQL_FILE = "src/test/resources/insert.sql";


	@Before
	public void setup() {
		SqlPojoMemoDB.reset();
	}
	
	@Test
	public void returnsConnection() {
		final Connection connection = SqlPojoMemoDB.getConnection();
		assertNotNull("Connection must not be null!", connection);
	}
	
	@Test
	public void deletesAllTableContent() throws SQLException 
	{
		// arrange
		final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(1))";
		final String createTableStatement2 = "create table TEST_TABLE_NAME2 (ID NUMBER(2))";
		final String insertTableStatement1 = "insert into TEST_TABLE_NAME1 (ID) values (1))";
		final String insertTableStatement2 = "insert into TEST_TABLE_NAME2 (ID) values (2))";
		SqlPojoMemoDB.execute( createTableStatement1 );
		SqlPojoMemoDB.execute( createTableStatement2 );
		SqlPojoMemoDB.execute( insertTableStatement1 );
		SqlPojoMemoDB.execute( insertTableStatement2 );
		assertEquals("NumberOfRows", 1, SqlPojoMemoDB.getDbStatistics().getNumberOfRows("TEST_TABLE_NAME1"));
		assertEquals("NumberOfRows", 1, SqlPojoMemoDB.getDbStatistics().getNumberOfRows("TEST_TABLE_NAME2"));
		
		
		// act
		SqlPojoMemoDB.removeAllContentOfAllTables();
		
		// assert
		assertEquals("NumberOfRows", 0, SqlPojoMemoDB.getDbStatistics().getNumberOfRows("TEST_TABLE_NAME1"));
		assertEquals("NumberOfRows", 0, SqlPojoMemoDB.getDbStatistics().getNumberOfRows("TEST_TABLE_NAME2"));
	}
	
	

	// #############################################################################################
	//                      C R E A T E   T A B L E   S T A T E M E N T   T E S T S
	// #############################################################################################

	
	@Test
	public void createsNewVerySimpleTable() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10))";
		
		// act
		final String result = (String) SqlPojoMemoDB.execute( createTableStatement );
		
		// assert
		assertEquals("result", "Table TEST_TABLE_NAME has been created.", result);
		
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		assertEquals("number of tables", 1, dbContent.getNumberOfTables());
		assertEquals("number of rows", 0, dbContent.getNumberOfRows("TEST_TABLE_NAME"));
		assertEquals("number of columns", 1, dbContent.getNumberOfColumns("TEST_TABLE_NAME"));
		assertEquals("name of column", "ID", dbContent.getNamesOfColumns("TEST_TABLE_NAME").get(0));
		assertEquals("type of column", "NUMBER(10)", dbContent.getTypeOfColumn("TEST_TABLE_NAME", "ID"));
		assertEquals("type of column", "true", "" + dbContent.isColumnNullable("TEST_TABLE_NAME", "ID"));
	}
	
	@Test
	public void createsNewTable() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10,0) NOT NULL ENABLE, NAME VARCHAR(50))";
		
		// act
		SqlPojoMemoDB.execute( createTableStatement );
		
		// assert
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		assertEquals("number of tables", 1, dbContent.getNumberOfTables());
		assertEquals("number of rows", 0, dbContent.getNumberOfRows("TEST_TABLE_NAME"));
		assertEquals("number of columns", 2, dbContent.getNumberOfColumns("TEST_TABLE_NAME"));
		assertEquals("name of column", "ID", dbContent.getNamesOfColumns("TEST_TABLE_NAME").get(0));
		assertEquals("name of column", "NAME", dbContent.getNamesOfColumns("TEST_TABLE_NAME").get(1));
		assertEquals("type of column", "NUMBER(10,0)", dbContent.getTypeOfColumn("TEST_TABLE_NAME", "ID"));
		assertEquals("type of column", "VARCHAR(50)", dbContent.getTypeOfColumn("TEST_TABLE_NAME", "NAME"));
		assertEquals("type of column", "false", "" + dbContent.isColumnNullable("TEST_TABLE_NAME", "ID"));
		assertEquals("type of column", "true", "" + dbContent.isColumnNullable("TEST_TABLE_NAME", "NAME"));
	}
	
	@Test
	public void createsThreeDifferentTables() throws SQLException 
	{
		// arrange
		//final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(10))";
		final String createTableStatement2 = "create table TEST_TABLE_NAME2 (ID NUMBER(10), NAME VARCHAR2(50))";
		final String createTableStatement3 = "create table TEST_TABLE_NAME3 (ID NUMBER(10) NOT NULL ENABLE)";
		
		// act
		//SqlPojoMemoryDB.execute( createTableStatement1 );
		SqlPojoMemoDB.execute( createTableStatement2 );
		SqlPojoMemoDB.execute( createTableStatement3 );
		
		// assert
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		//assertEquals("number of tables", 3, dbContent.getNumberOfTables());
		//assertEquals("number of columns", 1, dbContent.getNumberOfColumns("TEST_TABLE_NAME1"));
		assertEquals("number of columns", 2, dbContent.getNumberOfColumns("TEST_TABLE_NAME2"));
		assertEquals("number of columns", 1, dbContent.getNumberOfColumns("TEST_TABLE_NAME3"));
	}
	
	@Test
	public void createsComplexTable() throws SQLException, IOException 
	{
		// arrange
		final String createTableStatement = SqlStatementLoader.loadSqlStatement(TEST_CREATE_SQL_FILE, 1);
		
		// act
		SqlPojoMemoDB.execute( createTableStatement );
		
		// assert
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		final String tableName = dbContent.getTableNames().get(0);
		assertEquals("number of rows", 0, dbContent.getNumberOfRows( tableName ));
		assertEquals("number of columns", 9, dbContent.getNumberOfColumns( tableName ));
		assertEquals("name of column", "UPDATED_BY", dbContent.getNamesOfColumns( tableName ).get(8));
		assertEquals("type of column", "VARCHAR2(50 CHAR)", dbContent.getTypeOfColumn( tableName , "UPDATED_BY"));
		assertEquals("type of column", "false", "" + dbContent.isColumnNullable( tableName , "UPDATED_BY"));
	}

	@Test
	public void createsNewTable_MySql() throws SQLException, NoSuchFieldException, IllegalAccessException
    {
		// arrange
        SqlPojoMemoDB.reset();
        DbProperties.SUPPORT_MYSQL = true;

		final String createTableStatement = "CREATE TABLE hersteller (ID int PRIMARY KEY NOT NULL," +
				                                                      "Bezeichnung varchar(60) NOT NULL," +
																	  "Code int," +
																	  "Status int DEFAULT 0 NOT NULL," +
																	  "AenderungsTimeStamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL," +
																	  "User varchar(60) NOT NULL," +
																	  "Memo longtext," +
																	  "LockedBy varchar(100)," +
																	  "Type int DEFAULT 1 NOT NULL," +
																	  "Group decimal(10,0) DEFAULT 0 NOT NULL" +
																	  ");";

		// act
		final String result = (String) SqlPojoMemoDB.execute( createTableStatement );

		// assert
		assertEquals("result", "Table HERSTELLER has been created.", result);

		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		assertEquals("number of tables", 1, dbContent.getNumberOfTables());
		assertEquals("number of rows", 0, dbContent.getNumberOfRows("HERSTELLER"));
		assertEquals("number of columns", 10, dbContent.getNumberOfColumns("HERSTELLER"));
		assertEquals("name of column", "ID", dbContent.getNamesOfColumns("HERSTELLER").get(0));
		assertEquals("type of column", "INT", dbContent.getTypeOfColumn("HERSTELLER", "ID"));
		assertEquals("type of column", "false", "" + dbContent.isColumnNullable("HERSTELLER", "ID"));
	}
	
	
	
	@Test
	public void throwsExceptionForUnkownColumnTypeIncreateTableStatement() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID UNKOWN_TYPE, NAME VARCHAR(5))";
		
		try {
			// act			
			SqlPojoMemoDB.execute( createTableStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Unknown column type 'UNKOWN_TYPE'. Concerned column: 'ID'.", e.getMessage());
		}
 	}
	
	@Test
	public void dropsAndRecreatesTable() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table \"TEST_TABLE_NAME\" (\"ID\" NUMBER(10,0) )";
		SqlPojoMemoDB.execute( createTableStatement );
		assertEquals("number of tables", 1, SqlPojoMemoDB.getDbStatistics().getNumberOfTables());
		
		// act 1
		SqlPojoMemoDB.dropTable("TEST_TABLE_NAME");
		
		// assert 1
		assertEquals("number of tables", 0, SqlPojoMemoDB.getDbStatistics().getNumberOfTables());
		
		// act 2
		SqlPojoMemoDB.execute( createTableStatement );
		
		// assert 2
		assertEquals("number of tables", 1, SqlPojoMemoDB.getDbStatistics().getNumberOfTables());
	}
	
	@Test
	public void throwsExceptionForTableNameDouble() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(5), NAME VARCHAR(5), DATE DATE )";
		SqlPojoMemoDB.execute( createTableStatement );
		
		try {
			// act			
			SqlPojoMemoDB.execute( createTableStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLException e) {		
			assertEquals("error message", "A table 'TEST_TABLE_NAME' is already existing in the database.", e.getMessage());
		}
 	}
	
	// #############################################################################################
	//                           I N S E R T   S T A T E M E N T   T E S T S
	// #############################################################################################

	@Test
	public void insertsDataRowIntoTable() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(2), NAME VARCHAR(5))";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (NAME, ID) VALUES ('abc', 12)";
		
		// act
		SqlPojoMemoDB.execute( insertStatement );
		SqlPojoMemoDB.execute( insertStatement );
		final String result = (String) SqlPojoMemoDB.execute( insertStatement );
		
		// assert
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		assertEquals("row number", 3,  dbContent.getNumberOfRows("TEST_TABLE_NAME"));
		assertEquals("result", "Data row has been inserted into TEST_TABLE_NAME.", result);
 	}
		
	@Test
	public void throwsExceptionWithMessageOnAllInvalidValues() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(5), NAME VARCHAR(5), DATE DATE )";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (ID, NAME, DATE) VALUES (noNumberValue, invalidVarchar, noDateValue)";
		
		try {
			// act			
			SqlPojoMemoDB.execute( insertStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLException e) {		
			assertTrue("Unexpected error message", e.getMessage().contains("Insert values 'noNumberValue' is no number."));
			assertTrue("Unexpected error message", e.getMessage().contains("Insert VARCHAR value 'invalidVarchar' is not surrounded by '."));
			assertTrue("Unexpected error message", e.getMessage().contains("Insert values 'noDateValue' is no date."));
		}
 	}

	@Test
	public void insertsNullIntoDataRow() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(2), NAME VARCHAR(5))";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (NAME, ID) VALUES (null, null)";
		
		// act
		SqlPojoMemoDB.execute( insertStatement );
		
		// assert
		final String selectStatement = "select * from TEST_TABLE_NAME";
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertNull("not null expected", result.get(0)[0]);
		assertNull("not null expected", result.get(0)[1]);
 	}
	
	@Test
	public void throwsExceptionForNotAllowedNullValue() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER NOT NULL ENABLE, From DATE, Name VARCHAR2(50) NOT NULL ENABLE, Until Date)";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (ID) VALUES (null)";
		
		try {
			// act			
			SqlPojoMemoDB.execute( insertStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLException e) {	
			assertTrue("Unexpected error message", e.getMessage().contains("Null value not allowed for column 'ID'."));
			assertTrue("Unexpected error message", e.getMessage().contains("Null value not allowed for column 'NAME'."));
		}
 	}

	@Test
	public void insertsTextWithSpaces() throws SQLException, IOException 
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID VARCHAR(10), NAME VARCHAR(10))";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (ID, NAME) VALUES ('A b C', 'x Y z')";
		
		// act
		SqlPojoMemoDB.execute( insertStatement );
		
		// assert
		final TableStoreStatistics dbContent = SqlPojoMemoDB.getDbStatistics();
		assertEquals("row number", 1,  dbContent.getNumberOfRows("TEST_TABLE_NAME"));
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute("select * from TEST_TABLE_NAME")).getDataRows();
		
		assertEquals("field content", "A b C",  (String)(result.get(0)[0]));
		assertEquals("field content", "x Y z",  (String)(result.get(0)[1]));
 	}
	
	@Test
	public void insertsManyDataIntoComplexTable() throws SQLException, IOException 
	{
		// arrange
		final String createTableStatement = SqlStatementLoader.loadSqlStatement(TEST_CREATE_SQL_FILE, 1);
		SqlPojoMemoDB.execute( createTableStatement );
		final List<String> sqlStatements = SqlStatementLoader.loadAllSqlStatement(TEST_INSERT_SQL_FILE);
		
		// act
		for (String insertStatement : sqlStatements) {
			SqlPojoMemoDB.execute( insertStatement );
		}
		
		// assert
		List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute( "select * from TEN_SUM_FIELD" )).getDataRows();
		assertEquals("row number", 225,  result.size());
		assertEquals("value", "SalesPersonInformation", result.get(0)[1]);
		assertEquals("value", null, result.get(0)[3]);
		assertEquals("value", "1", result.get(0)[4].toString());
		assertEquals("value", getDateAsStringFromDate(new Date()), getDateAsStringFromDate(result.get(0)[5]));
		assertEquals("value", "System", result.get(0)[6]);
		assertEquals("value", getDateAsStringFromDate(new Date()), getDateAsStringFromDate(result.get(0)[7]));
		assertEquals("value", "System", result.get(0)[8]);
 	}
	
	
	// #############################################################################################
	//                           S E L E C T   S T A T E M E N T   T E S T S
	// #############################################################################################

	private String getDateAsStringFromDate(Object object) {
		final Date d = (Date) object;
		return DEFAULT_DATE_FORMATTER.format(d);
	}

	@Test
	public void selectsAllColumnsAndRowsInTable() throws SQLException 
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement =  "select * from TEST_TABLE_NAME";
		
		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		// assert
		assertEquals("row number", 4, result.size());
		assertEquals("column number", 4,  result.get(0).length);
		
		assertEquals("column number", "12",  "" + result.get(0)[0]);
		assertEquals("column number", "abc",  "" + result.get(0)[1]);
		assertEquals("column number", "34",  "" + result.get(1)[0]);
		assertEquals("column number", "def",  "" + result.get(1)[1]);
		assertEquals("column number", "56",  "" + result.get(2)[0]);
		assertEquals("column number", "null",  "" + result.get(2)[1]);
		assertEquals("column number", "null",  "" + result.get(3)[0]);
		assertEquals("column number", "ghi",  "" + result.get(3)[1]);
 	}	

	@Test
	public void selectsSubsetOfColumnsInTable() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement =  "select Name, From from TEST_TABLE_NAME";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("row number", 4, result.size());
		assertEquals("column number", 2,  result.get(0).length);

		assertEquals("value", "abc",  "" + result.get(0)[0]);
		assertEquals("value", "null",  "" + result.get(0)[1]);
		assertEquals("value", "def",  "" + result.get(1)[0]);
		assertEquals("value", "null",  "" + result.get(1)[1]);
		assertEquals("value", "null",  "" + result.get(2)[0]);
		assertEquals("value", "null",  "" + result.get(2)[1]);
		assertEquals("value", "ghi",  "" + result.get(3)[0]);
		assertEquals("value", "null",  "" + result.get(3)[1]);
	}

	@Test
	public void selectsSubsetOfRowsInTable() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement =  "select * from TEST_TABLE_NAME where Name='abc'";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("row number", 1, result.size());
		assertEquals("column number", 4,  result.get(0).length);

		assertEquals("value", "12",  "" + result.get(0)[0]);
		assertEquals("value", "abc",  "" + result.get(0)[1]);
	}

	@Test
	public void selectsSubsetOfColumnsAndRowsInTable() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement =  "select Name, From from TEST_TABLE_NAME where Name='abc'";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("row number", 1, result.size());
		assertEquals("column number", 2,  result.get(0).length);

		assertEquals("value", "abc",  "" + result.get(0)[0]);
		assertEquals("value", "null",  "" + result.get(0)[1]);
	}
	
	@Test
	public void throwsExceptionForUnkownColumnInWhereClause() throws SQLException 
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement =  "select Name, From from TEST_TABLE_NAME where UnknownColumn='abc'";
		
		try {
			// act			
			SqlPojoMemoDB.execute( selectStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Unknown column: UnknownColumn", e.getMessage());
		}
 	}

	@Test
	public void selectsDataRowForEqualDateAndID() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String insertStatement5 = "insert into TEST_TABLE_NAME (ID, Name, Until) VALUES (123, 'ghi', to_date('15.05.16','DD.MM.RR'))";
		SqlPojoMemoDB.execute( insertStatement5 );
		final String selectStatement =  "select * from TEST_TABLE_NAME where Until=to_date('15.05.16','DD.MM.RR') AND ID=123";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("row number", 1, result.size());
		assertEquals("column number", 4,  result.get(0).length);

		assertEquals("value", "Sun May 15 00:00:00 CEST 2016",  "" + result.get(0)[3]);
	}
	
	@Test
	public void selectsDataRowForUnequalDateAndID() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String insertStatement5 = "insert into TEST_TABLE_NAME (ID, Name, Until) VALUES (123, 'xxx', to_date('15.05.16','DD.MM.RR'))";
		SqlPojoMemoDB.execute( insertStatement5 );
		final String selectStatement1 =  "select * from TEST_TABLE_NAME where Until<>to_date('15.05.16','DD.MM.RR')";
		final String selectStatement2 =  "select * from TEST_TABLE_NAME where Name<>'xxx'";
		final String selectStatement3 =  "select * from TEST_TABLE_NAME";

		// act
		final List<Object[]> result1 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement1)).getDataRows();
		final List<Object[]> result2 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement2)).getDataRows();
		final List<Object[]> result3 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement3)).getDataRows();
		
		assertEquals("row number", 4, result1.size());
		assertEquals("row number", 4, result2.size());
		assertEquals("row number", 5, result3.size());
	}
	
	@Test
	public void selectsDateValuesAndConvertsThemAsString() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String insertStatement5 = "insert into TEST_TABLE_NAME (ID, Name, Until) VALUES (123, 'xxx', to_date('15.05.16','DD.MM.RR'))";
		SqlPojoMemoDB.execute( insertStatement5 );
		final String selectStatement =  "select to_char(Until,'dd.mm.yyyy hh24:mi:ss') from TEST_TABLE_NAME";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		// assert
		assertEquals("row number", 5, result.size());
		assertEquals("type of value", "String", result.get(4)[0].getClass().getSimpleName());
		assertEquals("date as String", "15.05.2016 00:00:00", (String) result.get(4)[0]);
	}

	@Test
	public void selectsDataUsingTableAlias() throws SQLException
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(2), NAME VARCHAR(5))";
		SqlPojoMemoDB.execute( createTableStatement );
		final String insertStatement = "insert into TEST_TABLE_NAME (ID, NAME) VALUES (123, 'name')";
		SqlPojoMemoDB.execute( insertStatement );
	    final String selectStatement = "select TT.ID, TT.NAME from TEST_TABLE_NAME TT";

		// act
	    final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("# rows", 1, result.size());
		assertEquals("column number", 2,  result.get(0).length);
		assertEquals("id", "123",  "" + result.get(0)[0]);
		assertEquals("name", "name",  "" + result.get(0)[1]);
	}

	@Test
	public void ordersSelectionResult() throws SQLException
	{
		// arrange
		SqlPojoMemoDB.execute( "create table TEST_TABLE_NAME (ID NUMBER(2), NAME VARCHAR(5), created date)" );
		SqlPojoMemoDB.execute( "insert into TEST_TABLE_NAME (ID, NAME, created) VALUES (10, 'B', to_date('20.01.16','DD.MM.RR'))" );
		SqlPojoMemoDB.execute( "insert into TEST_TABLE_NAME (ID, NAME, created) VALUES (30, 'C', to_date('20.03.16','DD.MM.RR'))" );
		SqlPojoMemoDB.execute( "insert into TEST_TABLE_NAME (ID, NAME, created) VALUES (20, 'A', to_date('20.02.16','DD.MM.RR'))" );
		final String selectStatement = "select TT.ID, TT.NAME, TT.created from TEST_TABLE_NAME TT";

		// act
		final List<Object[]> result1 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		final List<Object[]> result2 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by ID asc")).getDataRows();
		final List<Object[]> result3 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by ID desc")).getDataRows();
		final List<Object[]> result4 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by Name")).getDataRows();
		final List<Object[]> result5 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by Name desc")).getDataRows();
		final List<Object[]> result6 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by created")).getDataRows();
		final List<Object[]> result7 = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement + " order by created desc")).getDataRows();

		assertEquals("first ID", "10", "" + result1.get(0)[0]);
		assertEquals("last ID",  "20", "" + result1.get(2)[0]);  // not ordered

		assertEquals("first ID", "10", "" + result2.get(0)[0]);
		assertEquals("last ID",  "30", "" + result2.get(2)[0]); // ASC ordered

		assertEquals("first ID", "30", "" + result3.get(0)[0]);
		assertEquals("last ID",  "10", "" + result3.get(2)[0]); // DESC ordered

		assertEquals("first name", "A", "" + result4.get(0)[1]);
		assertEquals("last name",  "C", "" + result4.get(2)[1]);  // ASC ordered

		assertEquals("first name", "C", "" + result5.get(0)[1]);
		assertEquals("last name",  "A", "" + result5.get(2)[1]);  // DESC ordered

		assertEquals("first create", "Wed Jan 20 00:00:00 CET 2016", "" + result6.get(0)[2]);
		assertEquals("last create",  "Sun Mar 20 00:00:00 CET 2016", "" + result6.get(2)[2]);  // ASC ordered

		assertEquals("first create", "Sun Mar 20 00:00:00 CET 2016", "" + result7.get(0)[2]);
		assertEquals("last create",  "Wed Jan 20 00:00:00 CET 2016", "" + result7.get(2)[2]);  // DESC ordered
	}


	// #############################################################################################
	//                           U P D A T E    S T A T E M E N T   T E S T S
	// #############################################################################################

	@Test
	public void updatesEveryRowInTable() throws SQLException
	{
		// arrange
		final int numberOfDataRows = createDbStandardTestContent();
		final String updateStatement =  "update TEST_TABLE_NAME set \"Name\"='a new name', From=to_date('20.07.16','DD.MM.RR')";

		// act
		int result = (Integer) SqlPojoMemoDB.execute( updateStatement );

		// assert
		assertEquals("number of updated data rows", numberOfDataRows, result);
		
		final String selectStatement =  "select Name, From from TEST_TABLE_NAME";
		final List<Object[]> values = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("value", "a new name",  "" + values.get(0)[0]);
		assertEquals("value", "a new name",  "" + values.get(1)[0]);
		assertEquals("value", "a new name",  "" + values.get(2)[0]);
		assertEquals("value", "a new name",  "" + values.get(3)[0]);
		assertEquals("value", "Wed Jul 20 00:00:00 CEST 2016",  "" + values.get(0)[1]);
		assertEquals("value", "Wed Jul 20 00:00:00 CEST 2016",  "" + values.get(1)[1]);
		assertEquals("value", "Wed Jul 20 00:00:00 CEST 2016",  "" + values.get(2)[1]);
		assertEquals("value", "Wed Jul 20 00:00:00 CEST 2016",  "" + values.get(3)[1]);
	}
	
	@Test
	public void updatesSingleRowInTable() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String updateStatement =  "update TEST_TABLE_NAME set \"Name\"='a new name', From=to_date('20.07.16','DD.MM.RR') where ID=12";

		// act
		int result = (Integer) SqlPojoMemoDB.execute( updateStatement );

		// assert
		assertEquals("number of updated data rows", 1, result);
		
		final String selectStatement =  "select Name, From from TEST_TABLE_NAME";
		final List<Object[]> values = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("value", "a new name",  "" + values.get(0)[0]);
		assertEquals("value", "Wed Jul 20 00:00:00 CEST 2016",  "" + values.get(0)[1]);
	}
	
	@Test
	public void throwsExceptionForUnkownColumnInUpdateStatement() throws SQLException 
	{
		// arrange
		createDbStandardTestContent();
		final String updateStatement =  "update TEST_TABLE_NAME set UnkownColumn='a new name', From=to_date('20.07.16','DD.MM.RR') where ID=12";
		
		try {
			// act			
			SqlPojoMemoDB.execute( updateStatement );
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Unknown column: UnkownColumn", e.getMessage());
		}
 	}

	// #############################################################################################
	//                           D E L E T E    S T A T E M E N T   T E S T S
	// #############################################################################################

	
	@Test
	public void deletesOnlyRowsWithNullID() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement = "select * from TEST_TABLE_NAME where ID IS NULL";
		List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("number of data rows", 1, result.size());

		// act
		SqlPojoMemoDB.execute( "delete from TEST_TABLE_NAME where ID IS NULL" );

		// assert
		result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		assertEquals("number of data rows", 0, result.size());
	}
	
	@Test
	public void deletesOnlyRowsWithIDNotNull() throws SQLException
	{
		// arrange
		createDbStandardTestContent();
		final String selectStatement = "select * from TEST_TABLE_NAME where ID IS NOT NULL";
		List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		assertEquals("number of data rows", 3, result.size());

		// act
		SqlPojoMemoDB.execute( "delete from TEST_TABLE_NAME where ID IS NOT NULL" );

		// assert
		result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		assertEquals("number of data rows", 0, result.size());
	}

	
	// #############################################################################################
	//                 C R E A T E    S E Q U E N C E    S T A T E M E N T   T E S T S
	// #############################################################################################

	
	@Test
	public void createsSequence() throws SQLException
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH 123 CACHE 50 NOORDER NOCYCLE;";

		// act
		SqlPojoMemoDB.execute( createSequenceStatement );

		// assert
		final TableStore tableStore = (TableStore)SqlPojoMemoDB.getDbStatistics();
		final Sequence sequence = (Sequence) tableStore.getSequenceData("TEST_SEQ");
		assertNotNull(sequence);
		assertEquals("current value", "TEST_SEQ", sequence.getSequenceName());
		assertEquals("current value", 123, sequence.getCurrentValue());
	}

	@Test
	public void usesSequence() throws SQLException
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH 123 CACHE 50 NOORDER NOCYCLE;";
		final String createTableStatement =  "create table TEST_TABLE_NAME (ID NUMBER(2), Name VARCHAR(5), From DATE, Until DATE )";
		final String insertStatement = "insert into TEST_TABLE_NAME (Name, ID) VALUES ('abc', test_seq.nextval)";
		final String selectStatement = "select * from TEST_TABLE_NAME";
		
		SqlPojoMemoDB.execute( createSequenceStatement );
		SqlPojoMemoDB.execute( createTableStatement );

		// act
		SqlPojoMemoDB.execute( insertStatement );

		// assert
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		assertEquals("number of rows", 1, result.size());
		assertEquals("ID", 124L, result.get(0)[0]);
	}

	@Test
	public void throwsExectionForUnkownSequence() throws SQLException
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH 123 CACHE 50 NOORDER NOCYCLE;";
		final String createTableStatement =  "create table TEST_TABLE_NAME (ID NUMBER(2), Name VARCHAR(5), From DATE, Until DATE )";
		final String insertStatement = "insert into TEST_TABLE_NAME (Name, ID) VALUES ('abc', unkownSequence.nextval)";
		
		SqlPojoMemoDB.execute( createSequenceStatement );
		SqlPojoMemoDB.execute( createTableStatement );

		try {
			// act
			SqlPojoMemoDB.execute( insertStatement );
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// assert
			assertEquals("Error message", "Sequence 'unkownSequence' is unkown.", e.getMessage().trim());
		}
	}

	@Test
	public void returnsNextValUsingOracleDual() throws SQLException, NoSuchFieldException, IllegalAccessException
	{
        DbProperties.USE_ORACLE_DUAL_TABLE = true;
		SqlPojoMemoDB.reset();

		// arrange
		final int startValue1 = 7;
		final int startValue2 = 3;
		final String createSequenceStatement1 = "CREATE SEQUENCE \"TEST_SEQ1\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH " + startValue1 + " CACHE 50 NOORDER NOCYCLE;";
		final String createSequenceStatement2 = "CREATE SEQUENCE \"TEST_SEQ2\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH " + startValue2 + " CACHE 50 NOORDER NOCYCLE;";
		final String selectNextValStatement1 = "select TEST_SEQ1.nextval from dual";
		final String selectNextValStatement2 = "select TEST_SEQ2.nextval from dual";
		SqlPojoMemoDB.execute( createSequenceStatement1 );
		SqlPojoMemoDB.execute( createSequenceStatement2 );

		// act
		final List<Object[]> result1a = ((SelectionTable) SqlPojoMemoDB.execute(selectNextValStatement1)).getDataRows();
		final List<Object[]> result1b = ((SelectionTable) SqlPojoMemoDB.execute(selectNextValStatement1)).getDataRows();
		final List<Object[]> result2 = ((SelectionTable) SqlPojoMemoDB.execute(selectNextValStatement2)).getDataRows();

		// assert
		final String resultAsString1a = ((BigDecimal) result1a.get(0)[0]).toPlainString();
		final String resultAsString1b = ((BigDecimal) result1b.get(0)[0]).toPlainString();
		final String resultAsString2 = ((BigDecimal) result2.get(0)[0]).toPlainString();
		assertEquals("result", "" + (startValue1 + 1), resultAsString1a);
		assertEquals("result", "" + (startValue1 + 2), resultAsString1b);
		assertEquals("result", "" + (startValue2 + 1), resultAsString2);
	}
	
	
	// #############################################################################################
	//                                   J o i n    T E S T S
	// #############################################################################################

	@Test
	public void joins_Two_TablesUsingNonAnsiSyntax() throws SQLException
	{
		// arrange
		createDb_Join_SmallTestContent();
		final String selectStatement = "select T2.Name, T1.Description from TEST_TABLE_NAME_1 T1, TEST_TABLE_NAME_2 T2 where T1.ID=T2.ID";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		assertEquals("column number", 2,  result.get(0).length);
		assertEquals("row number", 1, result.size());
	}

	@Test
	public void joins_Two_TablesUsingNonAnsiSyntax_twoJoinConditions() throws SQLException
	{
		// arrange
		createDb_Join_SmallTestContent();
		final String selectStatement = "select T2.Name, T1.Description from TEST_TABLE_NAME_1 T1, TEST_TABLE_NAME_2 T2 where T1.ID=T2.ID and T1.Type1=T2.Type2";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		//assert
		assertEquals("row number", 0, result.size());
	}
	
	@Test
	public void joins_Three_TablesUsingNonAnsiSyntax() throws SQLException
	{
		// arrange
		createDb_Join_SmallTestContent();
		final String selectStatement = "select T3.Name, T2.Responsible, T1.CreationDate from TEST_TABLE_NAME_1 T1, TEST_TABLE_NAME_2 T2, TEST_TABLE_NAME_3 T3  where T1.ID=T2.ID and T2.Name=T3.Name";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		//assert
		assertEquals("column number", 3,  result.get(0).length);
		assertEquals("row number", 1, result.size());
	}
	
	
	@Test
	public void joins_TwoTables_UsingNonAnsiSyntaxWithTwoResultingDataRows() throws SQLException
	{
		// arrange
		createDb_Join_BigTestContent();
		final String selectStatement = "select T1.Name, T2.Name, T1.From, T2.Type from TEST_TABLE_NAME as T1, TEST_TABLE_NAME_2 as T2 where T1.ID=T2.ID";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		//assert
		assertEquals("column number", 4,  result.get(0).length);
		assertEquals("row number", 2, result.size());
	}

	@Test
	public void joins_TwoTables_UsingAnsiSyntax_andAdditionalWhereStatements() throws SQLException
	{
		createDb_Join_BigTestContent();
		
		final String selectStatement = "select T1.Name, T2.Name, T1.From, T2.Type from TEST_TABLE_NAME T1 JOIN TEST_TABLE_NAME_2 T2 on T1.ID=T2.ID where T1.Name is null";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		//assert
		assertEquals("column number", 4,  result.get(0).length);
		assertEquals("row number", 1, result.size());
	}

	@Test
	public void ordersSelectionResultOfJoinedTable() throws SQLException
	{
		// arrange
		createDb_Join_BigTestContent();
		SqlPojoMemoDB.execute( "insert into TEST_TABLE_NAME (ID) VALUES (12)" );
		SqlPojoMemoDB.execute( "insert into TEST_TABLE_NAME_2 (ID, Type) VALUES (12, 'T4')" );

		final String selectStatement = "select T1.Name, T2.Name, T1.From, T2.Type from TEST_TABLE_NAME T1 JOIN TEST_TABLE_NAME_2 T2 on T1.ID=T2.ID where T1.Name is null order by T2.Type DESC";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();

		//assert
		assertEquals("row number", 2, result.size());
		assertEquals("first name", "T4", "" + result.get(0)[3]);
		assertEquals("last name",  "T3", "" + result.get(1)[3]);  // ASC ordered
	}


	@Test
	public void joinsTablesAndUsesAsterixWithAlias() throws SQLException
	{
		// arrange
		createDb_Join_SmallTestContent();
		final String selectStatement = "select t3.* from TEST_TABLE_NAME_1 T1, TEST_TABLE_NAME_2 T2, TEST_TABLE_NAME_3 T3  where T1.ID=T2.ID and T2.Name=T3.Name";

		// act
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		//assert
		assertEquals("column number", 2,  result.get(0).length);
	}
	
	// #############################################################################################
	//                                   M I S C    T E S T S
	// #############################################################################################

	@Test
	@Ignore
	public void throwsExceptionForInsertValuesExceedingMaxLength() throws SQLException
	{
		// arrange
		final String createTableStatement = "create table TABLE_NAME (id Number(2), name varchar(5), amount Number(2,2))";
		SqlPojoMemoDB.execute( createTableStatement );
		
		final String insertStatement1 = "insert into TABLE_NAME (id) values (123)";
		final String insertStatement2 = "insert into TABLE_NAME (name) values ('abc')";
		final String insertStatement3 = "insert into TABLE_NAME (amount) values (110.123)";

        // act1
        try {
            SqlPojoMemoDB.execute(insertStatement1);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "", e.getMessage());
        }
        
        // act2
        try {
            SqlPojoMemoDB.execute(insertStatement2);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "", e.getMessage());
        }
        
        // act3
        try {
            SqlPojoMemoDB.execute(insertStatement3);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "", e.getMessage());
        }
	}

	@Test
	public void savesAndLoadsWithBooleanType() throws SQLException
	{
		// arrange
		final String createTableStatement = "create table TABLE_NAME (testColumn BOOLEAN)";
		final String insertStatement1 = "insert into TABLE_NAME (testColumn) values (TRue)";
		final String insertStatement2 = "insert into TABLE_NAME (testColumn) values (false)";
		final String selectStatement =  "select * from TABLE_NAME";

		// act
		SqlPojoMemoDB.execute( createTableStatement );
		SqlPojoMemoDB.execute( insertStatement1 );
		SqlPojoMemoDB.execute( insertStatement2 );
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		// assert
		assertEquals("column number", "true",  result.get(0)[0].toString());
		assertEquals("column number", "false",  result.get(1)[0].toString());
	}

	
	@Test
	public void understandsLowerAndUpperCaseInStatements() throws SQLException
	{
		// arrange
		final String createTableStatement = "cREATE tablE test_TABLE_NAME (iD NUmbeR(*), NamE VARchar(5))";
		final String insertStatement = "inSERT INto TEST_table_NAME (Id, NAme) VAluES (123, 'ghi')";
		final String selectStatement =  "seLEct * fROm TEST_TABLE_name WHEre id=123";

		// act
		SqlPojoMemoDB.execute( createTableStatement );
		SqlPojoMemoDB.execute( insertStatement );
		final List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
		
		// assert
		assertEquals("row number", 1, result.size());
		assertEquals("column number", 2,  result.get(0).length);
	}

    @Test
    public void handlesUnexpectedProblem_invalidMaximumLength() throws SQLException
    {
        // arrange
        final String createTableStatement = "create table X (ID Number(5a) )";

        // act
        try {
            SqlPojoMemoDB.execute(createTableStatement);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "Non parseable maximum length: '5A'. Concerned column: 'ID'.", e.getMessage());
        }
    }

    @Test
    public void handlesUnexpectedProblem_missingClosingParenthesis() throws SQLException
    {
        // arrange
        final String createTableStatement = "create table X (ID Number(5) primary key} ";

        // act
        try {
            SqlPojoMemoDB.execute(createTableStatement);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "Missing closing parenthesis in '(ID Number(5) primary key}'.", e.getMessage());
        }
    }

	// #############################################################################################
	//                                Private Methods
	// #############################################################################################
	

	private void createDb_Join_BigTestContent() throws SQLException {
		createDbStandardTestContent();
		final String createTableStatement =  "create table TEST_TABLE_NAME_2 (ID NUMBER(2), Name VARCHAR(5), Type VARCHAR(5), Description VARCHAR(15) )";
		final String insertStatement1 = "insert into TEST_TABLE_NAME_2 (ID, Name, Type, Description) VALUES (1, 'Name1', 'T1', 'Type1')";
		final String insertStatement2 = "insert into TEST_TABLE_NAME_2 (ID, Name, Type, Description) VALUES (34, 'Name2', 'T2', 'Type2')";
		final String insertStatement3 = "insert into TEST_TABLE_NAME_2 (ID, Name, Type, Description) VALUES (56, 'Name3', 'T3', 'Type3')";
		SqlPojoMemoDB.execute( createTableStatement );
		SqlPojoMemoDB.execute( insertStatement1 );
		SqlPojoMemoDB.execute( insertStatement2 );
		SqlPojoMemoDB.execute( insertStatement3 );
	}
	
	private void createDb_Join_SmallTestContent() throws SQLException 
	{
		final String createTableStatement1 =  "create table TEST_TABLE_NAME_1 (ID NUMBER(2), Description VARCHAR(50), Type1 VARCHAR(5), CreationDate DATE )";
		final String createTableStatement2 =  "create table TEST_TABLE_NAME_2 (ID NUMBER(2), Name VARCHAR(5), Type2 VARCHAR(5), Responsible VARCHAR(50) )";
		final String createTableStatement3 =  "create table TEST_TABLE_NAME_3 (Name VARCHAR(5), LastModificationDate DATE) )";

		SqlPojoMemoDB.execute( createTableStatement1 );
		SqlPojoMemoDB.execute( createTableStatement2 );
		SqlPojoMemoDB.execute( createTableStatement3 );
		
		final String insertStatement1 = "insert into TEST_TABLE_NAME_1 (ID, Type1, Description) VALUES (1, 'Type1', 'Description1'";
		final String insertStatement2 = "insert into TEST_TABLE_NAME_2 (ID, Name, Type2, Responsible) VALUES (1, 'Name2', 'Type2', 'Responsible2')";
		final String insertStatement3 = "insert into TEST_TABLE_NAME_3 (Name) VALUES ('Name2')";
		
		SqlPojoMemoDB.execute( insertStatement1 );
		SqlPojoMemoDB.execute( insertStatement2 );
		SqlPojoMemoDB.execute( insertStatement3 );
	}

	
	private int createDbStandardTestContent() throws SQLException 
	{
		final String createTableStatement =  "create table TEST_TABLE_NAME (ID NUMBER(2), Name VARCHAR(5), From DATE, Until DATE )";
		final String insertStatement1 = "insert into TEST_TABLE_NAME (Name, ID) VALUES ('abc', 12)";
		final String insertStatement2 = "insert into TEST_TABLE_NAME (ID, Name) VALUES (34, 'def')";
		final String insertStatement3 = "insert into TEST_TABLE_NAME (ID) VALUES (56)";
		final String insertStatement4 = "insert into TEST_TABLE_NAME (Name) VALUES ('ghi')";
		
		SqlPojoMemoDB.execute( createTableStatement );
		SqlPojoMemoDB.execute( insertStatement1 );
		SqlPojoMemoDB.execute( insertStatement2 );
		SqlPojoMemoDB.execute( insertStatement3 );
		SqlPojoMemoDB.execute( insertStatement4 );
		
		return 4; // number of insert statements!!
	}
	
}