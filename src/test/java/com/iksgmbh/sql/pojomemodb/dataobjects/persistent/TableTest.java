package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;

public class TableTest {

	private Table sut;

	@Before
	public void setup() throws SQLDataException {
		sut = new Table("Test");
	}
	
	@Test
	public void applies_to_char_function() throws SQLDataException {

		// arrange
		sut.createNewColumn("Column1", "Date", true, null);
		final List<ApartValue> values = new ArrayList<ApartValue>();
		ApartValue value = new ApartValue("to_date('15.05.16','DD.MM.RR')", "Column1");
		values.add(value);
		sut.insertDataRow(values);
		final List<String> selectedColumns = new ArrayList<String>();
		selectedColumns.add("to_char(Column1, 'dd.mm.yyyy hh24:mi:ss')");
		final List<WhereCondition> whereConditions = new ArrayList<WhereCondition>();
		
		// act
		final List<Object[]> result = sut.select(selectedColumns, whereConditions );
		
		// assert
		final String dateAsString = (String) result.get(0)[0];
		System.err.println("<" + dateAsString + ">");
		assertEquals("date value", "15.05.2016 00:00:00", dateAsString);
	}

	
	@Test
	public void buildsCloneOfDataRows() throws SQLDataException {

		// arrange
		sut.createNewColumn("Column1", "Date", true, null);
		sut.createNewColumn("Column2", "varchar(50)", true, null);
		sut.createNewColumn("Column3", "Number(10,2)", true, null);
		
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
	
}
