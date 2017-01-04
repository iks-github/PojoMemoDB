package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;

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

}
