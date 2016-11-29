package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import static org.junit.Assert.assertEquals;

import java.sql.SQLDataException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class ColumnTest 
{
	private Column sut;
	
	@Before
	public void setup() throws SQLDataException {
		sut = new Column("Test", "Date", true, 1, null);
	}
	
	@Test
	public void applies_to_date_function() throws SQLDataException {

		// arrange
		final String valueAsString = "to_date('15.05.16','DD.MM.RR')";
		
		// act
		final DateTime result = (DateTime) sut.convertIntoColumnType(valueAsString);
		
		// assert
		assertEquals("date value", "2016-05-15T00:00:00.000+02:00", result.toString());
	}

}
