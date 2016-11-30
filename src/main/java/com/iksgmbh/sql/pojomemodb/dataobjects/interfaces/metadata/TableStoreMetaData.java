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
package com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata;

import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStoreStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;

/**
 * List for methods to manage the content of the table store. 
 * 
 * @author Reik Oberrath
 */
public interface TableStoreMetaData extends TableStoreStatistics {

	void addTable(TableMetaData tableMetaData) throws SQLException;

	void dropAllTables();

	void dropTable(String tableName);

	void addSequence(Sequence sequence) throws SQLException;
	
	void dropAllSequences();

}