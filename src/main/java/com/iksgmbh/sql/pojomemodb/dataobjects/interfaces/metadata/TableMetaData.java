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

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;

import java.sql.SQLDataException;

/**
 * List for methods to manage the structure of a table. 
 * 
 * @author Reik Oberrath
 */
public interface TableMetaData extends TableStatistics {

	void createNewColumn(ColumnInitData columnInitData, SqlPojoMemoDB memoryDb) throws SQLDataException;

}