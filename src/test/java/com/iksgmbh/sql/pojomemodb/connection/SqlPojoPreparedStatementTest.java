package com.iksgmbh.sql.pojomemodb.connection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SqlPojoPreparedStatementTest {

	@Test
	public void createsOutputSql() throws Exception 
	{
		// act
		final SqlPojoPreparedStatement statement = new SqlPojoPreparedStatement("select * from TEN_SUM_FIELD where ID=?");
		statement.setLong(1, 1);
		final String result = statement.buildOutputSql();
	
		// assert
		assertEquals("result", "select * from TEN_SUM_FIELD where ID=1", result);
	}

}
