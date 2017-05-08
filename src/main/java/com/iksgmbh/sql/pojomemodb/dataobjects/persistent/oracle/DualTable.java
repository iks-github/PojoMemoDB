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
package com.iksgmbh.sql.pojomemodb.dataobjects.persistent.oracle;

import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.OrderCondition;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.DUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NEXTVAL;

public class DualTable extends Table {

	public static final String DOT_NEXTVAL = "." + NEXTVAL;

	private HashMap<String, Sequence> sequenceMap;

	public DualTable(final HashMap<String, Sequence> aSequenceMap) {
		super(DUAL);
		this.sequenceMap = aSequenceMap;
	}
	
	@Override
	public List<Object[]> select(final List<String> selectedColumns,
								 final List<WhereCondition> whereConditions, List<OrderCondition> orderConditions) throws SQLDataException
	{
		if (selectedColumns == null || selectedColumns.size() == 0) {
			return new ArrayList<Object[]>();
		}

		if (selectedColumns.get(0).toLowerCase().endsWith(DOT_NEXTVAL.toLowerCase())) 
		{
			final int pos = selectedColumns.get(0).indexOf(DOT_NEXTVAL);
			final String sequenceName = selectedColumns.get(0).substring(0, pos);
			final Sequence sequence = sequenceMap.get(sequenceName);
			
			if (sequence == null) {
				throw new SQLDataException("Unkown sequence: " + sequenceName);
			}
			
			final Object[] arr = new Object[1];
			arr[0] = new BigDecimal( sequence.nextVal() );
			final List<Object[]> toReturn = new ArrayList<Object[]>();
			toReturn.add(arr);
			return toReturn;
		}
		
		return null;
	}	

}