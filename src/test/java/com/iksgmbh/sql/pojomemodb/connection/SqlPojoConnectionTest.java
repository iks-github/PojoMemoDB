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
import com.iksgmbh.sql.pojomemodb.utils.SqlStatementLoader;

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
