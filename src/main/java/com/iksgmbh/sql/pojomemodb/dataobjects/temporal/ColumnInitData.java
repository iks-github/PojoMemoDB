package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Column;

/**
 * @author Reik Oberrath
 */
public class ColumnInitData {

    public String columnName;
    public String columnType;
    public String defaultValue;
    public boolean nullable = true;    // this is default !
    public String primaryKey;
    public String uniqueKey;

    public ColumnInitData(String aName) {
        this.columnName = aName;
    }

	public ColumnInitData(Column column) 
	{
		columnName = column.getColumnName();
        columnType = column.getColumnType();
        defaultValue = column.getDefaultValue();
        nullable = column.isNullable();
        primaryKey = column.getPrimaryKeyId();
        uniqueKey = column.getUniqueConstraintId();
	}

}
