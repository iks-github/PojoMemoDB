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
package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;

import java.sql.SQLException;

/**
 * Stores information how the result is to be sorted.
 *  
 * @author Reik Oberrath
 */
public class OrderCondition
{
	private String columnName;
	private String direction;

	public OrderCondition(final String aColumnName,
						  final String aDirection) throws SQLException
	{
		this.columnName = aColumnName;
		this.direction = aDirection.toUpperCase();

		if ( ! direction.equals(SQLKeyWords.ASC)
			 &&
			 ! direction.equals(SQLKeyWords.DESC) )
		{
			throw new SQLException("Unknown order direction (" + direction + ") for column " + columnName + "!");
		}
	}
	
	public boolean isAscending() {
		return SQLKeyWords.ASC.equals(direction);
	}

	public String getColumnName() {
		return columnName;
	}

	public String getDirection() {
		return direction;
	}
}