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