package com.iksgmbh.sql.pojomemodb.sqlparser;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedInsertData;

public class SqlPojoInsertIntoParserTest {

	private SqlPojoInsertIntoParser sut = new SqlPojoInsertIntoParser(null);

	
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
		
	
	


}
