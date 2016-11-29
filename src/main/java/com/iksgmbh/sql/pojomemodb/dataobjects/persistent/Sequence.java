package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.SequenceData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.SequenceMetaData;

public class Sequence implements SequenceData, SequenceMetaData
{
	private String name;
	private long currentValue;
	
	public Sequence(String name) {
		this.name = name;
	}

	@Override
	public long nextVal() {
		currentValue++;
		return currentValue;
	}

	@Override
	public long getCurrentValue() {
		return currentValue;
	}

	@Override
	public void setCurrentValue(final long newValue) {
		currentValue = newValue;
		
	}

	@Override
	public String getSequenceName() {
		return name;
	}

}
