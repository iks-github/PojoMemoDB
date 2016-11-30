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
package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data;

import java.sql.SQLDataException;
import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;

/**
 * List of methods to handle the data values assigned to a column. 
 * 
 * @author Reik Oberrath
 */
public interface ColumnData extends ColumnStatistics {

	Object convertIntoColumnType(String valueAsString) throws SQLDataException;

	/**
	 * Validates validation type specific settings.
	 * Nullable check is not performed here!
	 */
	void validate(Object value) throws SQLDataException;

	/**
	 * Compares a data value against a WhereCondition value
	 * @param String comparator
	 * @param valueToCheck
	 * @return true if dataValueToCheck is valid 
	 * @throws SQLException 
	 */
	public boolean isWhereConditionMatched(String conditionValue, String comparator, Object dataValueToCheck) throws SQLDataException;	
}