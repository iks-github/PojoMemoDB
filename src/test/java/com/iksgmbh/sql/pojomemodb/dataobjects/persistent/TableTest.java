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
package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.OrderCondition;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TableTest {

	private Table sut;

	@Before
	public void setup() throws SQLDataException {
		sut = new Table("Test");
	}
	
	@Test
	public void applies_to_char_function() throws SQLDataException {

		// arrange
        sut.createNewColumn(createColumnInitData("Column1", "Date"), null);
		final List<ApartValue> values = new ArrayList<ApartValue>();
		ApartValue value = new ApartValue("to_date('15.05.16','DD.MM.RR')", "Column1");
		values.add(value);
		sut.insertDataRow(values);
		final List<String> selectedColumns = new ArrayList<String>();
		selectedColumns.add("to_char(Column1, 'dd.mm.yyyy hh24:mi:ss')");
		final List<WhereCondition> whereConditions = new ArrayList<WhereCondition>();
		
		// act
		final List<Object[]> result = sut.select(selectedColumns, whereConditions, new ArrayList<OrderCondition>());
		
		// assert
		final String dateAsString = (String) result.get(0)[0];
		System.err.println("<" + dateAsString + ">");
		assertEquals("date value", "15.05.2016 00:00:00", dateAsString);
	}

	
	@Test
	public void buildsCloneOfDataRows() throws SQLDataException {

		// arrange
        sut.createNewColumn(createColumnInitData("Column1", "Date"), null);
        sut.createNewColumn(createColumnInitData("Column2", "varchar(50)"), null);
        sut.createNewColumn(createColumnInitData("Column3", "Number(10,2)"), null);

		final List<ApartValue> values = new ArrayList<ApartValue>();
		values.add(new ApartValue("to_date('15.05.16','DD.MM.RR')", "Column1"));
		values.add(new ApartValue("'Test'", "Column2"));
		values.add(new ApartValue("10.2", "Column3"));
		sut.insertDataRow(values);
		
		// act 1
		final List<Object[]> result1 = sut.createDataRowsClone();
		result1.get(0)[0] = null;
		result1.get(0)[1] = "New";
		result1.get(0)[2] = BigDecimal.ZERO;
		
		// act 2
		final List<Object[]> result2 = sut.createDataRowsClone();
				
		// assert
		assertEquals("number value", "10.2", ((BigDecimal)result2.get(0)[2]).toPlainString() );
		assertEquals("text value", "Test", result2.get(0)[1]);
		assertEquals("date value", "2016-05-15T00:00:00.000+02:00", result2.get(0)[0].toString());
	}



	@Test
	public void ordersNullValues() throws SQLException {

		// arrange test table
        sut.createNewColumn(createColumnInitData("Column1", "Date"), null);
        sut.createNewColumn(createColumnInitData("Column2", "varchar(50)"), null);
        sut.createNewColumn(createColumnInitData("Column3", "Number(10,2)"), null);

		List<ApartValue> values = new ArrayList<ApartValue>();
		values.add(new ApartValue("to_date('15.05.16','DD.MM.RR')", "Column1"));
		values.add(new ApartValue("'NotNull'", "Column2"));
		values.add(new ApartValue("10.2", "Column3"));
		sut.insertDataRow(values);  // column 2 is NOT null

		values = new ArrayList<ApartValue>();
		values.add(new ApartValue("to_date('15.05.16','DD.MM.RR')", "Column1"));
		values.add(new ApartValue("10.2", "Column3"));
		sut.insertDataRow(values); // column 2 is null

		// arrange select statement data
		final List<String> columns = new ArrayList<String>();
		columns.add("Column2");
		final List<OrderCondition> orderConditions = new ArrayList<OrderCondition>();
		orderConditions.add(new OrderCondition("Column2", SQLKeyWords.ASC));

		// act 1
		final List<Object[]> result1 = sut.select(columns, new ArrayList<WhereCondition>(), orderConditions);

		// act 2
		orderConditions.clear();
		orderConditions.add(new OrderCondition("Column2", SQLKeyWords.DESC));
		final List<Object[]> result2 = sut.select(columns, new ArrayList<WhereCondition>(), orderConditions);


		// assert
		assertNull(result1.get(0)[0]);
		assertEquals("Column2", "NotNull", result1.get(1)[0]);

		assertEquals("Column2", "NotNull", result2.get(0)[0]);
		assertNull(result2.get(1)[0]);

	}

    @Test
    public void usesDefaultValueIfDefined() throws SQLDataException {

        // arrange
        sut.createNewColumn(createColumnInitData("Column_With_Default", "VARCHAR(20)", "'DefaultValue'", null), null);
        sut.createNewColumn(createColumnInitData("Column_No_Default", "Date"), null);
        sut.createNewColumn(createColumnInitData("DateColumn", "Date", "sysdate", null), null);
        sut.createNewColumn(createColumnInitData("ID", "NUMBER"), null);

        final List<ApartValue> values = new ArrayList<ApartValue>();
        values.add(new ApartValue("1", "ID"));

        // act
        sut.insertDataRow(values);

        // assert
        final List<Object[]> result = sut.createDataRowsClone();
        assertEquals("value of ColumnWithDefault", "DefaultValue", result.get(0)[0]);
        assertNull(result.get(0)[1]);
        assertNotNull(result.get(0)[2]);
    }

    @Test
    public void throwsExceptionForDuplicatesInPrimaryKeyColumn() throws SQLDataException {

        // arrange
        sut.createNewColumn(createColumnInitData("Column_With_Default", "VARCHAR(20)", "'DefaultValue'", null), null);
        sut.createNewColumn(createColumnInitData("Column_No_Default", "Date"), null);
        sut.createNewColumn(createColumnInitData("DateColumn", "Date", "sysdate", null), null);
        sut.createNewColumn(createColumnInitData("ID", "NUMBER", null, "primaryKeyId"), null);

        final List<ApartValue> values = new ArrayList<ApartValue>();
        values.add(new ApartValue("1", "ID"));
        sut.insertDataRow(values);

        // act
        try {
            sut.insertDataRow(values);
            fail("Expected exception was not thrown!");
        } catch (SQLDataException e) {
            // assert
            assertEquals("Error message", "Primary Key Constraint violated in column 'ID' with value '1'.", e.getMessage().trim());
        }
    }

    private ColumnInitData createColumnInitData(String colName, String colType) {
        ColumnInitData toReturn = new ColumnInitData(colName);
        toReturn.columnType = colType;
        return toReturn;
    }

    private ColumnInitData createColumnInitData(String colName, String colType, String defaultValue, String primKey) {
        ColumnInitData toReturn = createColumnInitData(colName, colType);
        toReturn.defaultValue = defaultValue;
        toReturn.primaryKey = primKey;
        return toReturn;
    }

}
