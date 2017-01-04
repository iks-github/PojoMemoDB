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

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedDeleteData;


public class DeleteTableParserTest {
	
	private DeleteTableParser sut = new DeleteTableParser(null);

	@Test
	public void parsesDeleteAlllRowVariantA() throws SQLException 
	{
		// arrange
		final String deleteStatement =  "delete from TEST_TABLE_NAME";

		// act
		final ParsedDeleteData result = sut.parseDeleteStatement(deleteStatement);
		
		// assert
		assertEquals("tableName", "TEST_TABLE_NAME", result.tableName);
		assertEquals("number of where conditions", 0, result.whereConditions.size());
	}

	@Test
	public void parsesDeleteAlllRowVariantB() throws SQLException
	{
		// arrange
		final String deleteStatement =  "delete * from TEST_TABLE_NAME";

		// act
		final ParsedDeleteData result = sut.parseDeleteStatement(deleteStatement);

		// assert
		assertEquals("tableName", "TEST_TABLE_NAME", result.tableName);
		assertEquals("number of where conditions", 0, result.whereConditions.size());
	}

	@Test
	public void parsesDeleteStatementWithWhereConditions() throws SQLException
	{
		// arrange
		final String deleteStatement =  "delete from TEST_TABLE_NAME where ID IS NOT NULL and NAME IS NULL";

		// act
		final ParsedDeleteData result = sut.parseDeleteStatement(deleteStatement);

		// assert
		assertEquals("number of where conditions", 2,  result.whereConditions.size() );
		assertEquals("column name", "ID",  result.whereConditions.get(0).getColumnName() );
		assertEquals("Comparator", "IS NOT NULL",  result.whereConditions.get(0).getComparator() );
		assertEquals("value", null,  result.whereConditions.get(0).getValueAsString() );
		assertEquals("column name", "NAME",  result.whereConditions.get(1).getColumnName() );
		assertEquals("Comparator", "IS NULL",  result.whereConditions.get(1).getComparator() );
		assertEquals("value", null,  result.whereConditions.get(1).getValueAsString() );
	}

}