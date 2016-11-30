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