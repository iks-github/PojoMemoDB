package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data;

/**
 * List for methods to manage a sequence. 
 * 
 * @author Reik Oberrath
 */

public interface SequenceData {
	long nextVal();
	void setCurrentValue(long value);
}
