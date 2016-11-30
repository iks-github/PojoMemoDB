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
package com.iksgmbh.sql.pojomemodb.connection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.testutils.SqlStatementLoader;

public class SqlPojoConnectionTest
{
	private static final String TEST_CREATE_SQL_FILE = "src/test/resources/create.sql";
	private static final String TEST_INSERT_SQL_FILE = "src/test/resources/insert.sql";
	
	@Before
	public void setup() {
		SqlPojoMemoDB.reset();
	}

	@Test
	public void createsPreparedStatementWithResultSetForAllDataInTable() throws IOException, SQLException 
	{
		// arrange
		final String createStatement = SqlStatementLoader.loadSqlStatement(TEST_CREATE_SQL_FILE, 1);
		SqlPojoMemoDB.execute( createStatement );
		
		final List<String> sqlStatements = SqlStatementLoader.loadAllSqlStatement(TEST_INSERT_SQL_FILE);
		for (String insertStatement : sqlStatements) {
			SqlPojoMemoDB.execute( insertStatement );
		}		
		
		// act 
		final PreparedStatement prepareStatement = SqlPojoMemoDB.getConnection().prepareStatement("select * from TEN_SUM_FIELD");
		final ResultSet result = prepareStatement.executeQuery();
		
		// assert
		assertEquals("result", 225, countSizeOfResultSet(result));
	}

	@Test
	public void createsPreparedStatementWithResultSetForOneDataRow() throws IOException, SQLException 
	{
		// arrange
		final String createStatement = SqlStatementLoader.loadSqlStatement(TEST_CREATE_SQL_FILE, 1);
		SqlPojoMemoDB.execute( createStatement );
		
		final List<String> sqlStatements = SqlStatementLoader.loadAllSqlStatement(TEST_INSERT_SQL_FILE);
		for (String insertStatement : sqlStatements) {
			SqlPojoMemoDB.execute( insertStatement );
		}		
		
		// act 
		final PreparedStatement prepareStatement = SqlPojoMemoDB.getConnection().prepareStatement("select * from TEN_SUM_FIELD where ID=1");
		final ResultSet result = prepareStatement.executeQuery();
		
		// assert
		assertEquals("result", 1, countSizeOfResultSet(result));
	}
	
	@Test
	public void createsPreparedStatementWithResultSetAndReadsDataFromIt() throws IOException, SQLException 
	{
		// arrange
		final String createStatement = SqlStatementLoader.loadSqlStatement(TEST_CREATE_SQL_FILE, 1);
		SqlPojoMemoDB.execute( createStatement );
		
		final List<String> sqlStatements = SqlStatementLoader.loadAllSqlStatement(TEST_INSERT_SQL_FILE);
		for (String insertStatement : sqlStatements) {
			SqlPojoMemoDB.execute( insertStatement );
		}		
		
		// act 
		final PreparedStatement prepareStatement = SqlPojoMemoDB.getConnection().prepareStatement("select * from TEN_SUM_FIELD where ID=?");
		prepareStatement.setLong(1, 1);
		final ResultSet result = prepareStatement.executeQuery();
		result.next();
		
		// assert
		assertEquals("result", 1, result.getLong(1));
	}
	
	
	private Object countSizeOfResultSet(final ResultSet result) throws SQLException {
		int count = 0;
		while (result.next())  {
			count++;
		}
		return count;
	}

}