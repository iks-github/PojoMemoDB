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

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;

public class SqlPojoCreateTableParserTest {
	
	private SqlPojoCreateTableParser sut = new SqlPojoCreateTableParser(null);


	@Test
	public void parsesCreateStatement() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table \"TEST_TABLE_NAME\" (\"ID\" NUMBER(10,0) NOT NULL ENABLE, "
				                                                             + "NAME VARCHAR(50))";
		
		// act
		final TableMetaData result = sut.parseCreateTableStatement(createTableStatement);
		
		// assert
		assertEquals("tableName", "TEST_TABLE_NAME", result.getTableName());
		assertEquals("number of columns", 2, result.getNumberOfColumns());
		assertEquals("number of columns", "ID", result.getNamesOfColumns().get(0));
		assertEquals("number of columns", "NAME", result.getNamesOfColumns().get(1));
		assertEquals("number of columns", "NUMBER(10,0)", result.getTypeOfColumn("ID"));
		assertEquals("number of columns", "VARCHAR(50)", result.getTypeOfColumn("NAME"));
	}


}