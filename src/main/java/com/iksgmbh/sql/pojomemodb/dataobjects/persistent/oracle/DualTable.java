package com.iksgmbh.sql.pojomemodb.dataobjects.persistent.oracle;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.DUAL;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NEXTVAL;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;

public class DualTable extends Table {

	public static final String DOT_NEXTVAL = "." + NEXTVAL;

	private HashMap<String, Sequence> sequenceMap;

	public DualTable(final HashMap<String, Sequence> aSequenceMap) {
		super(DUAL);
		this.sequenceMap = aSequenceMap;
	}
	
	@Override
	public List<Object[]> select(final List<String> selectedColumns, 
			                     final List<WhereCondition> whereConditions) throws SQLDataException 
	{
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
