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
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;

public class SqlPojoResultSetTest 
{
	private SqlPojoResultSet cut;
	private List<Object[]> list = new ArrayList<Object[]>();

	@Before
	public void setup() throws SQLDataException {
		cut = createCut(list);
	}
    
	@Test
	public void closesResultSet() throws Exception 
	{
        // act
        boolean result1 = cut.isClosed();
        cut.close();
        boolean result2 = cut.isClosed();
        
		// assert
		assertFalse("false expected", result1);
		assertTrue("true expected", result2);
	}

	@Test
	public void returnsTrueOnFirst() throws Exception 
	{
        // arrange
		Object[] datarow = new Object[1];
		String testContent = "TestContent";
		datarow[0] = testContent;
		list.add( datarow );
		cut = createCut(list);

		// act
		boolean result = cut.first();
		Object object = cut.getObject(1);
		
		// assert
		assertTrue("true expected", result);
		assertEquals(testContent, object);
	}
	
	@Test
	public void returnsFalseOnFirstForNullResult() throws Exception 
	{
		// act
		boolean result = cut.first();
		
		// assert
		assertFalse("false expected", result);
	}
	
	
	@Test
	public void returnsFalseOnNextForNullResult() throws Exception 
	{
		// act
		boolean next = cut.next();
		
		// assert
		assertFalse("false expected", next);
	}

	
	@Test
	public void returnsFalseOnNextForEmptyResult() throws Exception 
	{
        // act
		boolean next = cut.next();
		
		// assert
		assertFalse("false expected", next);
	}

	
	@Test
	public void returnsTrueOnNextForNonEmptyResult() throws Exception 
	{
		// arrange
		list.add(new Object[1]);
        final SqlPojoResultSet cut = createCut(list);

        // act
		boolean next = cut.next();
		
		// assert
		assertTrue("true expected", next);
	}

	@Test
	public void returnsTrueOnNextExceptEndOfList() throws Exception 
	{
		// arrange
		list.add(new Object[1]);
		list.add(new Object[1]);
		list.add(new Object[1]);
        final SqlPojoResultSet cut = createCut(list);

		// act
		boolean next1 = cut.next();
		boolean next2 = cut.next();
		boolean next3 = cut.next();
		boolean next4 = cut.next();
		
		// assert
		assertTrue("true expected", next1);
		assertTrue("true expected", next2);
		assertTrue("true expected", next3);
		assertFalse("false expected", next4);
	}

	@Test
	public void returnsTypeSpecificNullValues() throws Exception 
	{
		// arrange
		final Object[] dataset1 = new Object[1];
		dataset1[0] = null;
		list.add(dataset1);
		final Object[] dataset2 = new Object[1];
		dataset2[0] = null;
		list.add(dataset2);
		final Object[] dataset3 = new Object[1];
		dataset3[0] = null;
		list.add(dataset3);
		final Object[] dataset4 = new Object[1];
		dataset4[0] = null;
		list.add(dataset4);
        final SqlPojoResultSet cut = createCut(list);

		// act
		cut.next();
		Date d = cut.getDate(1);
		cut.next();
		String errorMessage = null;
		try {
			cut.getLong(1);
		} catch (Exception e) {
			errorMessage = e.getMessage();
		}
		
		cut.next();
		String s = cut.getString(1);
		cut.next();
		BigDecimal bd = cut.getBigDecimal(1);
		
		// assert
		assertEquals("field content", dataset1[0], d);
		assertEquals("field content", "null value in db cannot be parsed into an long value.", errorMessage);
		assertEquals("field content", dataset3[0], s);
		assertEquals("field content", dataset4[0], bd);
	}
	
	@Test
	public void returnsCorrectTypeSpecificValues() throws Exception 
	{
		// arrange
		final Object[] dataset1 = new Object[1];
		dataset1[0] = new Date();
		list.add(dataset1);
		final Object[] dataset2 = new Object[1];
		dataset2[0] = BigDecimal.ONE;
		list.add(dataset2);
		final Object[] dataset3 = new Object[1];
		dataset3[0] = "test";
		list.add(dataset3);
		final Object[] dataset4 = new Object[1];
		dataset4[0] = BigDecimal.ONE;
		list.add(dataset4);
        final SqlPojoResultSet cut = createCut(list);

		// act
		cut.next();
		Date d = cut.getDate(1);
		cut.next();
		long l = cut.getLong(1);
		cut.next();
		String s = cut.getString(1);
		cut.next();
		BigDecimal bd = cut.getBigDecimal(1);
		
		// assert
		assertEquals("field content", dataset1[0], d);
		assertEquals("field content", "1", ""+l);
		assertEquals("field content", dataset3[0], s);
		assertEquals("field content", dataset4[0], bd);
	}

    private SqlPojoResultSet createCut(final List<Object[]> dataRows) throws SQLDataException
    {
        final Table table = new Table("TestTable");
        final ColumnInitData columnInitData = new ColumnInitData("TestCol");
        columnInitData.columnType = "VARCHAR(100)";
		table.createNewColumn(columnInitData, null);
        ArrayList<String> sortedColumnNames = new ArrayList<String>();
        sortedColumnNames.add("TestCol");
		final SelectionTable selectionTable = new SelectionTable(table, sortedColumnNames);
        selectionTable.setDataRows(dataRows);
        return new SqlPojoResultSet(selectionTable);
    }
   
}