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

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedInsertData;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSelectData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.JoinTable;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.OrderCondition;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SqlExecutorTest {

	private SqlPojoMemoDB db = SqlPojoMemoDB.getInstance();
	private SqlExecutor sut = new SqlExecutor(db);
	
	@Before
	public void setup() {
		DbProperties.SUPPORT_MYSQL=true;
		SqlPojoMemoDB.reset();
	}
	
	@Test
	public void buildsSelectionTableForMysqlNextId() throws SQLException 
	{
		// arrange
		final String selectStatement = "select NextId(\"testmodell\")";
		SqlPojoMemoDB.execute("CREATE TABLE testmodell (ID int PRIMARY KEY NOT NULL)");
		SqlPojoMemoDB.execute("insert into testmodell (ID) values (5)");
		
		// act		
		SelectionTable result = (SelectionTable) sut.executeSqlStatement(selectStatement);
		
		// arrange
		assertNotNull(result);
		BigDecimal nextId= (BigDecimal) result.getDataRows().get(0)[0];
		assertEquals(6, nextId.intValue());
	}
	
	@Test
	public void throwsExceptionForMissingPrimaryKeyColumnForMysqlNextId() throws SQLException 
	{
		// arrange
		final String selectStatement = "select NextId(\"testmodell\")";
		SqlPojoMemoDB.execute("CREATE TABLE testmodell (ID int NOT NULL)");
		SqlPojoMemoDB.execute("insert into testmodell (ID) values (5)");	
		
		try {
			// act			
			sut.executeSqlStatement(selectStatement);	
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "No primary key column found in table 'testmodell'.", e.getMessage());
		}
	}	
	
	@Test
	public void throwsExceptionForNonNumberPrimaryKeyColumnForMysqlNextId() throws SQLException 
	{
		// arrange
		final String selectStatement = "select NextId(\"testmodell\")";
		SqlPojoMemoDB.execute("CREATE TABLE testmodell (ID varchar(10) PRIMARY KEY NOT NULL)");
		SqlPojoMemoDB.execute("insert into testmodell (ID) values ('a')");	
		
		try {
			// act			
			sut.executeSqlStatement(selectStatement);	
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Primary key of table 'testmodell' is no number.", e.getMessage());
		}
	}
	
	@Test
	public void throwsExceptionForUnequalNumberOfColumnNamesAndDataValues() throws SQLException 
	{
		// arrange
		final List<String> columnNames = new ArrayList<String>();
		columnNames.add("A");
		columnNames.add("B");
		final List<String> dataValues = new ArrayList<String>();
		dataValues.add("1");
		final ParsedInsertData parsedInsertData = new ParsedInsertData("TEST_TABLE_NAME", columnNames, dataValues);
		
		try {
			// act			
			sut.buildApartValueList(parsedInsertData.columnNames, parsedInsertData.dataValues);			
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Unequal number of column names (2) and data values (1).", e.getMessage());
		}
 	}	

	@Test
	public void throwsExceptionForNonJoinableConditions() throws SQLException 
	{
		// arrange
		final List<String> columnNames = new ArrayList<String>();
		columnNames.add("C1");
		columnNames.add("C2");
		columnNames.add("C3");
		columnNames.add("C4");		
		final List<String> tableNames = new ArrayList<String>();
		tableNames.add("T1");
		tableNames.add("T2");
		tableNames.add("T3");
		tableNames.add("T4");
		final List<WhereCondition> joinConditions = new ArrayList<WhereCondition>();
		joinConditions.add(new WhereCondition("T1.C1", SQLKeyWords.COMPARATOR_EQUAL, "T2.C2"));
		joinConditions.add(new WhereCondition("T3.C3", SQLKeyWords.COMPARATOR_EQUAL, "T4.C4")); // the bridge between these joinCondition is missing !
		final ParsedSelectData selectData = new ParsedSelectData(tableNames, columnNames, joinConditions, new ArrayList<OrderCondition>());
		
		TableMetaData table = new Table("T1");
        table.createNewColumn(createColumnInitData("C1", "Number(5)"), null);
		db.getTableStoreMetaData().addTable(table);
		table = new Table("T2");
        table.createNewColumn(createColumnInitData("C2", "Number(5)"), null);
		db.getTableStoreMetaData().addTable(table);
		
		try {
			// act			
			sut.buildJoinTable(selectData);			
			fail("Expected exception was not thrown!");
		} catch (SQLDataException e) {			
			assertEquals("Error message", "Unable to apply join condition: WhereCondition [T3.C3 = T4.C4]", e.getMessage());
		}
 	}	

	
	@Test
	public void buildsJoinTableColumns() throws SQLException 
	{
		// arrange
		final List<String> columnNames = new ArrayList<String>();
		columnNames.add("C1");
		columnNames.add("C2");
		columnNames.add("C3");
		final List<String> tableNames = new ArrayList<String>();
		tableNames.add("T1");
		tableNames.add("T2");
		tableNames.add("T3");
		final List<WhereCondition> joinConditions = new ArrayList<WhereCondition>();
		joinConditions.add(new WhereCondition("T1.C1", SQLKeyWords.COMPARATOR_EQUAL, "T2.C2"));
		joinConditions.add(new WhereCondition("T2.C2", SQLKeyWords.COMPARATOR_EQUAL, "T3.C3"));
		final ParsedSelectData selectData = new ParsedSelectData(tableNames, columnNames, joinConditions, new ArrayList<OrderCondition>());
		
		TableMetaData table = new Table("T1");
        table.createNewColumn(createColumnInitData("C1", "Number(5)"), null);
        table.createNewColumn(createColumnInitData("C2", "Number(5)"), null);
		db.getTableStoreMetaData().addTable(table);
		table = new Table("T2");
        table.createNewColumn(createColumnInitData("C1", "Number(5)"), null);
        table.createNewColumn(createColumnInitData("C2", "Number(5)"), null);
		db.getTableStoreMetaData().addTable(table);
		table = new Table("T3");
		table.createNewColumn(createColumnInitData("C1", "Number(5)"), null);
        table.createNewColumn(createColumnInitData("C2", "Number(5)"), null);
		db.getTableStoreMetaData().addTable(table);
		
		// act			
		final JoinTable joinTable = (JoinTable) sut.buildJoinTable(selectData);			
		
		// assert
		final List<String> namesOfColumns = joinTable.getNamesOfColumns();
		assertEquals("column name", "T1.C1", namesOfColumns.get(0));
		assertEquals("column name", "T1.C2", namesOfColumns.get(1));
		assertEquals("column name", "T2.C1", namesOfColumns.get(2));
		assertEquals("column name", "T2.C2", namesOfColumns.get(3));
		assertEquals("column name", "T3.C1", namesOfColumns.get(4));
		assertEquals("column name", "T3.C2", namesOfColumns.get(5));
 	}	

    private ColumnInitData createColumnInitData(String colName, String colType) {
        ColumnInitData toReturn = new ColumnInitData(colName);
        toReturn.columnType = colType;
        return toReturn;
    }
}