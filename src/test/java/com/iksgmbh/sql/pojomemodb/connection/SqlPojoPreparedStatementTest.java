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

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;

public class SqlPojoPreparedStatementTest {

	@Test
	public void creates() throws Exception 
	{
		// arrange
		final SqlPojoPreparedStatement statement = new SqlPojoPreparedStatement("select NextId(\"testmodell\")");
		SqlPojoMemoDB.execute("CREATE TABLE testmodell (ID int PRIMARY KEY NOT NULL)");
		SqlPojoMemoDB.execute("insert into testmodell (ID) values (5)");
		
		// act		
		ResultSet result = statement.executeQuery();
		result.first();
		statement.close();
		
		// arrange
		assertEquals(6, result.getInt(1));
	}
	
	@Test
	public void createsOutputSql() throws Exception 
	{
		// act
		final SqlPojoPreparedStatement statement = new SqlPojoPreparedStatement("select * from TEN_SUM_FIELD where ID=?");
		statement.setLong(1, 1);
		final String result = statement.buildOutputSql();
		statement.close();
	
		// assert
		assertEquals("result", "select * from TEN_SUM_FIELD where ID=1", result);
	}

}