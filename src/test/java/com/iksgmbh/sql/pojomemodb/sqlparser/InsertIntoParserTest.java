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
package com.iksgmbh.sql.pojomemodb.sqlparser;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedInsertData;

public class InsertIntoParserTest {

	private InsertIntoParser sut = new InsertIntoParser(null);

	@Test
	public void parsesMysqlDateValues() throws SQLException 
	{
		// arrange
		final String insertStatement = "insert into TEST_TABLE_NAME (TIMESTAMP, DATE) VALUES (ts '2013-12-15 08:25:15'}, {d '2099-12-31'})";
		
		// act
		final ParsedInsertData result = sut.parseInsertStatement(insertStatement);
		
		// assert
		assertEquals("date value", "ts '2013-12-15 08:25:15'}", result.dataValues.get(0));
		assertEquals("date value", "{d '2099-12-31'}",  result.dataValues.get(1));
 	}
	
	
	@Test
	public void parsesInsertData() throws SQLException 
	{
		// arrange
		final String insertStatement = "insert into TEST_TABLE_NAME (NAME, ID) VALUES ('abc', 12)";
		
		// act
		final ParsedInsertData result = sut.parseInsertStatement(insertStatement);
		
		// assert
		assertEquals("table name", "TEST_TABLE_NAME",  result.tableName);
		assertEquals("number of data values", 2,  result.dataValues.size());
		assertEquals("data value", "'abc'",  result.dataValues.get(0));
		assertEquals("data value", "12",  result.dataValues.get(1));
		assertEquals("number of column names", 2,  result.columnNames.size());
		assertEquals("column name", "NAME",  result.columnNames.get(0));
		assertEquals("column name", "ID",  result.columnNames.get(1));
 	}
		
	@Test
	public void parsesApostophy() throws SQLException 
	{
		// arrange
		final String insertStatement = "insert into TEST_TABLE_NAME (NAME, TEXT, NUMBER) VALUES ('Bob', 'Don''t worry!', 5)";
		
		// act
		final ParsedInsertData result = sut.parseInsertStatement(insertStatement);
		
		// assert
		assertEquals("Number of column and value mismatch!", result.columnNames.size(), result.dataValues.size());
		assertEquals("Number of data values", 3, result.dataValues.size());
		assertEquals("data value", "'Don''t worry!'", result.dataValues.get(1));
 	}
	
	


}