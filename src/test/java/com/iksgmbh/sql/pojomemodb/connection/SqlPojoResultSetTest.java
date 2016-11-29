package com.iksgmbh.sql.pojomemodb.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.connection.SqlPojoResultSet;

public class SqlPojoResultSetTest 
{	
	@Test
	public void returnsFalseOnNextForNullResult() throws Exception 
	{
		// arrange
		final SqlPojoResultSet cut = new SqlPojoResultSet(null);

		// act
		boolean next = cut.next();
		cut.close();
		
		// assert
		assertFalse("false expected", next);
	}

	
	@Test
	public void returnsFalseOnNextForEmptyResult() throws Exception 
	{
		// arrange
		final List<Object[]> list = new ArrayList<Object[]>();
		final SqlPojoResultSet cut = new SqlPojoResultSet(list);

		// act
		boolean next = cut.next();
		cut.close();
		
		// assert
		assertFalse("false expected", next);
	}

	
	@Test
	public void returnsTrueOnNextForNonEmptyResult() throws Exception 
	{
		// arrange
		final List<Object[]> list = new ArrayList<Object[]>();
		final Object[] dataset = new Object[1];
		list.add(dataset);
		final SqlPojoResultSet cut = new SqlPojoResultSet(list);

		// act
		boolean next = cut.next();
		cut.close();
		
		// assert
		assertTrue("true expected", next);
	}

	// TODO test mit mehreren nexts
}
