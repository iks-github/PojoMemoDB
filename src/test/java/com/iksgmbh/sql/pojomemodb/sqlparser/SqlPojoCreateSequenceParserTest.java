package com.iksgmbh.sql.pojomemodb.sqlparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSequenceData;


public class SqlPojoCreateSequenceParserTest {
	
	private SqlPojoCreateSequenceParser sut = new SqlPojoCreateSequenceParser(null);

	@Test
	public void parsesCreateSequenceStatement() throws SQLException 
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH 123 CACHE 50 NOORDER NOCYCLE;";
		
		// act
		final ParsedSequenceData result = sut.parseCreateSequenceStatement(createSequenceStatement);
		
		// assert
		assertEquals("tableName", "TEST_SEQ", result.sequenceName);
		assertEquals("currentValue", 123, result.currentValue);
	}

	@Test
	public void throwsExceptionForInvalidStartValue() throws SQLException 
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1 START WITH INVALID CACHE 50 NOORDER NOCYCLE;";
		
		try {
			// act
			sut.parseCreateSequenceStatement(createSequenceStatement);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// assert
			assertEquals("Error message", "Unable to parse start value of sequence 'TEST_SEQ' to a number: INVALID", e.getMessage());
		}
	}

	@Test
	public void parsesCreateSequenceStatementWithoutStartValue() throws SQLException 
	{
		// arrange
		final String createSequenceStatement = "CREATE SEQUENCE \"TEST_SEQ\" MINVALUE 0 MAXVALUE 9999999999 INCREMENT BY 1;";
		
		// act
		final ParsedSequenceData result = sut.parseCreateSequenceStatement(createSequenceStatement);
		
		// assert
		assertEquals("tableName", "TEST_SEQ", result.sequenceName);
		assertEquals("currentValue", 0, result.currentValue);
	}
	
}
