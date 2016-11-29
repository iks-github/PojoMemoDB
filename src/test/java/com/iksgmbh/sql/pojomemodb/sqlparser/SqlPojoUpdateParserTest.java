package com.iksgmbh.sql.pojomemodb.sqlparser;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedUpdateData;


public class SqlPojoUpdateParserTest {
	
	private SqlPojoUpdateParser sut = new SqlPojoUpdateParser(null);

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
