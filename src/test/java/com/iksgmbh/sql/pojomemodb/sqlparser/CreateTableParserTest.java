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

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import org.junit.Test;

import java.sql.SQLDataException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class CreateTableParserTest {
	
	private CreateTableParser sut = new CreateTableParser(null);


	@Test
	public void parsesCreateStatement() throws SQLException 
	{
		// arrange
		final String createTableStatement = "create table \"TEST_TABLE_NAME\" (\"ID\" NUMBER(10,0), "
				                                                             + "NAME VARCHAR(50))";
		
		// act
		final Table result = (Table) sut.parseCreateTableStatement(createTableStatement);
		
		// assert
		assertEquals("tableName", "TEST_TABLE_NAME", result.getTableName());
		assertEquals("number of columns", 2, result.getNumberOfColumns());
		assertEquals("number of columns", "ID", result.getNamesOfColumns().get(0));
		assertEquals("number of columns", "NAME", result.getNamesOfColumns().get(1));
		assertEquals("number of columns", "NUMBER(10,0)", result.getTypeOfColumn("ID"));
		assertEquals("number of columns", "VARCHAR(50)", result.getTypeOfColumn("NAME"));
		assertEquals("Nullable", "true", "" + result.getColumn("ID").isNullable());
		assertEquals("Nullable", "true", "" + result.getColumn("NAME").isNullable());
	}

	@Test
	public void parsesCreateStatement_withNotNullableFields() throws SQLException
	{
		// arrange
		final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10,0) NOT NULL ENABLE, NAME VARCHAR(50) not null)";

		// act
		final Table result = (Table) sut.parseCreateTableStatement(createTableStatement);

		// assert
		assertEquals("Nullable", "false", "" + result.getColumn("ID").isNullable());
		assertEquals("Nullable", "false", "" + result.getColumn("NAME").isNullable());
	}

	@Test
	public void parsesCreateStatement_withDefaultValue() throws SQLException
    {
		// arrange
		final String createTableStatement1 = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default 1) ";
        final String createTableStatement2 = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default 1, Name VARCHAR(10)) ";
        final String createTableStatement3 = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default 1, Name VARCHAR(10) default 'Bob') ";

		// act
		final Table table1 = (Table) sut.parseCreateTableStatement(createTableStatement1);
        final Table table2 = (Table) sut.parseCreateTableStatement(createTableStatement2);
        final Table table3 = (Table) sut.parseCreateTableStatement(createTableStatement3);

		// assert
        assertEquals("Default value", "1", table1.getColumn("ID").getDefaultValue());

        assertEquals("Default value", "1", table2.getColumn("ID").getDefaultValue());
        assertNull(table2.getColumn("Name").getDefaultValue());

		assertEquals("Default value", "1", table3.getColumn("ID").getDefaultValue());
        assertEquals("Default value", "'Bob'", table3.getColumn("Name").getDefaultValue());
	}

    @Test
    public void throwsExceptionForMissingDefaultValue() throws SQLException
    {
        // arrange
        final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default) ";

        try {
            // act
            SqlPojoMemoDB.execute(createTableStatement);
            fail("Expected exception was not thrown!");
        } catch (SQLDataException e) {
            assertEquals("Error message", "Missing default value for column 'ID'!", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionForDefaultValueOfWrongType() throws SQLException
    {
        // arrange
        final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default '1') ";

        try {
            // act
            SqlPojoMemoDB.execute(createTableStatement);
            fail("Expected exception was not thrown!");
        } catch (SQLDataException e) {
            assertEquals("Error message", "Value ''1'' is not valid for column ID!", e.getMessage());
        }
    }

    @Test
    public void parsesCreateStatement_withPrimaryKeyConstraint_inColumnDefinition() throws SQLException
    {
        // arrange
        final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0) PRIMARY KEY, Name varchar(10)) ";
        final String createTableStatement2 = "create table TEST_TABLE_NAME2 (ID NUMBER(10,0) not null Primary Key, Name varchar(10)) ";
        final String createTableStatement3 = "create table TEST_TABLE_NAME3 (ID NUMBER(10,0) primary key not null, Name varchar(10)) ";
        final String createTableStatement4 = "create table TEST_TABLE_NAME4 (ID NUMBER(10,0), Name varchar(10)) ";

        // act
        final Table table1 = (Table) sut.parseCreateTableStatement(createTableStatement1);
        final Table table2 = (Table) sut.parseCreateTableStatement(createTableStatement2);
        final Table table3 = (Table) sut.parseCreateTableStatement(createTableStatement3);
        final Table table4 = (Table) sut.parseCreateTableStatement(createTableStatement4);

        // assert
        assertEquals("primaryKeyId", "PK_ID", "" + table1.getColumn("ID").getPrimaryKeyId());
        assertEquals("primaryKeyId", "PK_ID", "" + table2.getColumn("ID").getPrimaryKeyId());
        assertEquals("primaryKeyId", "PK_ID", "" + table3.getColumn("ID").getPrimaryKeyId());
        assertEquals("primaryKeyId", "null", "" + table4.getColumn("ID").getPrimaryKeyId());

        assertEquals("nullable", "false", "" + table1.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table2.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table3.getColumn("ID").isNullable());
        assertEquals("nullable", "true", "" + table4.getColumn("ID").isNullable());

    }

    @Test
    public void parsesCreateStatement_withPrimaryKeyConstraint_separateDefinition() throws SQLException
    {
        // arrange
        final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0), Name varchar(10), PRIMARY KEY (ID)) ";
        final String createTableStatement2 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0), Name varchar(10), CONSTRAINT \"PRIMKEY_ID\" PRIMARY KEY (\"ID\")) ";
        final String createTableStatement3 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0), Name varchar(10), CONSTRAINT PKEY_ID PRIMARY KEY (ID), CONSTRAINT ignore that) ";

        // act
        final Table table1 = (Table) sut.parseCreateTableStatement(createTableStatement1);
        final Table table2 = (Table) sut.parseCreateTableStatement(createTableStatement2);
        final Table table3 = (Table) sut.parseCreateTableStatement(createTableStatement3);

        // assert
        assertEquals("primaryKeyId", "PK_ID", table1.getColumn("ID").getPrimaryKeyId());
        assertEquals("primaryKeyId", "PRIMKEY_ID", table2.getColumn("ID").getPrimaryKeyId());
        assertEquals("primaryKeyId", "PKEY_ID", table3.getColumn("ID").getPrimaryKeyId());

        assertEquals("nullable", "false", "" + table1.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table2.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table3.getColumn("ID").isNullable());
    }

    @Test
    public void throwsExceptionForDefaultValueWithPrimaryKey() throws SQLException
    {
        // arrange
        final String createTableStatement = "create table TEST_TABLE_NAME (ID NUMBER(10,0) default 1 Primary Key) ";

        try {
            // act
            SqlPojoMemoDB.execute(createTableStatement);
            fail("Expected exception was not thrown!");
        } catch (SQLException e) {
            assertEquals("Error message", "Primary Key column 'ID' must not define a default value!", e.getMessage());
        }
    }

    @Test
    public void parsesCreateStatement_withUniqueConstraint_separateDefinition() throws SQLException
    {
        // arrange
        final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0) Not Null, Name varchar(10), UNIQUE (ID)) ";
        final String createTableStatement2 = "create table TEST_TABLE_NAME2 (ID NUMBER(10,0), Name varchar(10), CONSTRAINT \"UNIQUE_ID\" UNIQUE (\"ID\")) ";
        final String createTableStatement3 = "create table TEST_TABLE_NAME3 (ID NUMBER(10,0), Name varchar(10), CONSTRAINT UNIC_ID UNIQUE (ID), CONSTRAINT ignore that) ";

        // act
        final Table table1 = (Table) sut.parseCreateTableStatement(createTableStatement1);
        final Table table2 = (Table) sut.parseCreateTableStatement(createTableStatement2);
        final Table table3 = (Table) sut.parseCreateTableStatement(createTableStatement3);

        // assert
        assertEquals("uniqueConstraintId", "UC_ID", table1.getColumn("ID").getUniqueConstraintId());
        assertEquals("uniqueConstraintId", "UNIQUE_ID", table2.getColumn("ID").getUniqueConstraintId());
        assertEquals("uniqueConstraintId", "UNIC_ID", table3.getColumn("ID").getUniqueConstraintId());

        assertEquals("nullable", "false", "" + table1.getColumn("ID").isNullable());
        assertEquals("nullable", "true", "" + table2.getColumn("ID").isNullable());
        assertEquals("nullable", "true", "" + table3.getColumn("ID").isNullable());
    }

    @Test
    public void parsesCreateStatement_withUniqueConstraint_inColumnDefinition() throws SQLException
    {
        // arrange
        final String createTableStatement1 = "create table TEST_TABLE_NAME1 (ID NUMBER(10,0) UNIQUE, Name varchar(10)) ";
        final String createTableStatement2 = "create table TEST_TABLE_NAME2 (ID NUMBER(10,0) not null UNIQUE, Name varchar(10)) ";
        final String createTableStatement3 = "create table TEST_TABLE_NAME3 (ID NUMBER(10,0) UNIQUE not null, Name varchar(10)) ";
        final String createTableStatement4 = "create table TEST_TABLE_NAME4 (ID NUMBER(10,0), Name varchar(10)) ";

        // act
        final Table table1 = (Table) sut.parseCreateTableStatement(createTableStatement1);
        final Table table2 = (Table) sut.parseCreateTableStatement(createTableStatement2);
        final Table table3 = (Table) sut.parseCreateTableStatement(createTableStatement3);
        final Table table4 = (Table) sut.parseCreateTableStatement(createTableStatement4);

        // assert
        assertEquals("uniqueConstraintId", "UC_ID", "" + table1.getColumn("ID").getUniqueConstraintId());
        assertEquals("uniqueConstraintId", "UC_ID", "" + table2.getColumn("ID").getUniqueConstraintId());
        assertEquals("uniqueConstraintId", "UC_ID", "" + table3.getColumn("ID").getUniqueConstraintId());
        assertEquals("uniqueConstraintId", "null", "" + table4.getColumn("ID").getUniqueConstraintId());

        assertEquals("nullable", "true", "" + table1.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table2.getColumn("ID").isNullable());
        assertEquals("nullable", "false", "" + table3.getColumn("ID").isNullable());
        assertEquals("nullable", "true", "" + table4.getColumn("ID").isNullable());

    }

}