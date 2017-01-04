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

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedUpdateData;


public class UpdateParserTest {
	
	private UpdateParser sut = new UpdateParser(null);

	@Test
	public void parsesStatementWithWhereCondition() throws SQLException
	{
		// arrange
		final String updateStatement =  "update TEST_TABLE_NAME set \"Name\"='a new name', From=to_date('20.07.16','DD.MM.RR') where ID=12";

		// act
		final ParsedUpdateData result = sut.parseUpdateStatement( updateStatement );

		// assert
		assertEquals("tableName", "TEST_TABLE_NAME", result.tableName);
		assertEquals("number of new values", 2, result.newValues.size());
		assertEquals("column name", "Name",  result.newValues.get(0).getColumnName() );
		assertEquals("value", "'a new name'",  result.newValues.get(0).getValueAsString() );
		assertEquals("column name", "From",  result.newValues.get(1).getColumnName() );
		assertEquals("value", "to_date('20.07.16','DD.MM.RR')",  result.newValues.get(1).getValueAsString() );
		assertEquals("number of where conditions", 1,  result.whereConditions.size() );
		assertEquals("column name", "ID",  result.whereConditions.get(0).getColumnName() );
		assertEquals("Comparator", "=",  result.whereConditions.get(0).getComparator() );
		assertEquals("value", "12",  result.whereConditions.get(0).getValueAsString() );
	}	

}