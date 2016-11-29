package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata;

/**
 * List for methods to manage a sequence. 
 * 
 * @author Reik Oberrath
 */

public interface SequenceMetaData {
	long getCurrentValue();
	String getSequenceName();
}
