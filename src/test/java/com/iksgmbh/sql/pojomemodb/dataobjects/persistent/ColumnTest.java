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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.util.Date;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;

public class ColumnTest 
{

	@Test
	public void applies_mysql_ts_function() throws SQLDataException {

		// arrange
		Column sut = new Column(createColumnInitData("Test", "Date"), 1, null);
		final String valueAsString = "{ts '2013-12-15 08:25:15'}";
		
		// act
		final Date result = (Date) sut.convertIntoColumnType(valueAsString);
		
		// assert
		assertEquals("date value", "Sun Dec 15 08:25:15 CET 2013", result.toString());
	}
	
	@Test
	public void applies_mysql_d_function() throws SQLDataException {

		// arrange
		Column sut = new Column(createColumnInitData("Test", "Date"), 1, null);
		final String valueAsString = "{d '2099-12-31'}";
		
		// act
		final Date result = (Date) sut.convertIntoColumnType(valueAsString);
		
		// assert
		assertEquals("date value", "Thu Dec 31 00:00:00 CET 2099", result.toString());
	}
	
	
	@Test
	public void applies_to_date_function() throws SQLDataException {

		// arrange
		Column sut = new Column(createColumnInitData("Test", "Date"), 1, null);
		final String valueAsString = "to_date('15.05.16','DD.MM.RR')";
		
		// act
		final Date result = (Date) sut.convertIntoColumnType(valueAsString);
		
		// assert
		assertEquals("date value", "Sun May 15 00:00:00 CEST 2016", result.toString());
	}
	
	@Test
	public void acceptsMaxLengthValuesForMysqlTypes() throws SQLDataException {

		// arrange
		Column sut = new Column(createColumnInitData("Test", "int(4)"), 1, null);
		final String valueOK = "1234";
		
		// act
		final BigDecimal result = (BigDecimal) sut.convertIntoColumnType(valueOK);
		
		// assert
		assertEquals("date value", "1234", result.toPlainString());
	}

    private ColumnInitData createColumnInitData(String colName, String colType) {
        ColumnInitData toReturn = new ColumnInitData(colName);
        toReturn.columnType = colType;
        return toReturn;
    }

}