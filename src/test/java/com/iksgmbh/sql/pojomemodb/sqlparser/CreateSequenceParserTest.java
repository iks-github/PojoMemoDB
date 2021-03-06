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
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSequenceData;


public class CreateSequenceParserTest {
	
	private CreateSequenceParser sut = new CreateSequenceParser(null);

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