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
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Column;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;

import java.sql.SQLDataException;
import java.util.List;

/**
 * Table to store information about the selection data
 */
public class SelectionTable extends Table
{
	public SelectionTable(final Table parentTable,
                          final List<String> sortedColumnNames) throws SQLDataException
	{
		super(parentTable.getTableName());

        if ( ! parentTable.getTableName().equals(SQLKeyWords.DUAL) ) {
            for (String columnName : sortedColumnNames) {
                ColumnInitData columnInitData = createColumnInitData(parentTable.getColumn(columnName));
                this.createNewColumn(columnInitData, null);
            }
        }
	}

    private ColumnInitData createColumnInitData(Column column)
    {
        final ColumnInitData toReturn = new ColumnInitData(column.getColumnName());

        toReturn.columnType = column.getColumnType();
        toReturn.primaryKey = column.getPrimaryKeyId();
        toReturn.uniqueKey = column.getUniqueConstraintId();
        toReturn.nullable = column.isNullable();

        return toReturn;
    }

}