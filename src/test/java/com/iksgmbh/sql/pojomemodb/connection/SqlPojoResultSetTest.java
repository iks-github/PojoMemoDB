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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

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

	@Test
	public void returnsTrueOnNextExceptEndOfList() throws Exception 
	{
		// arrange
		final List<Object[]> list = new ArrayList<Object[]>();
		final Object[] dataset1 = new Object[1];
		list.add(dataset1);
		final Object[] dataset2 = new Object[1];
		list.add(dataset2);
		final Object[] dataset3 = new Object[1];
		list.add(dataset3);
		final SqlPojoResultSet cut = new SqlPojoResultSet(list);

		// act
		boolean next1 = cut.next();
		boolean next2 = cut.next();
		boolean next3 = cut.next();
		boolean next4 = cut.next();
		cut.close();
		
		// assert
		assertTrue("true expected", next1);
		assertTrue("true expected", next2);
		assertTrue("true expected", next3);
		assertFalse("false expected", next4);
	}
	
	@Test
	public void returnsCorrectTypeSpecificValues() throws Exception 
	{
		// arrange
		final List<Object[]> list = new ArrayList<Object[]>();
		final Object[] dataset1 = new Object[1];
		dataset1[0] = new DateTime();
		list.add(dataset1);
		final Object[] dataset2 = new Object[1];
		dataset2[0] = BigDecimal.ZERO;
		list.add(dataset2);
		final Object[] dataset3 = new Object[1];
		dataset3[0] = "test";
		list.add(dataset3);
		final SqlPojoResultSet cut = new SqlPojoResultSet(list);

		// act
		cut.next();
		Date d = cut.getDate(1);
		cut.next();
		long l = cut.getLong(1);
		cut.next();
		String s = cut.getString(1);
		cut.close();
		
		// assert
		assertEquals("field content", ((DateTime)dataset1[0]).getMillis(), d.getTime());
		assertEquals("field content", ((BigDecimal)dataset2[0]).toPlainString(), ""+l);
		assertEquals("field content", dataset3[0], s);
	}
	
}