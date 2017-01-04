package com.iksgmbh.sql.pojomemodb.connection;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This class represents an incomplete implementation of java.sql.ResultSetMetaData class.
 * It implements the some basic methods that are needed by current requirements.
 * Other methods may be implemented later on as needed.
 *
 * @author Reik Oberrath
 */
@SuppressWarnings("unused")
public class SqlPojoResultSetMetaData implements ResultSetMetaData
{
    private SelectionTable selectionTable;

    public SqlPojoResultSetMetaData(final SelectionTable aSelectionTable) {
        this.selectionTable = aSelectionTable;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return selectionTable.getNumberOfColumns();
    }

    @Override
    public String getColumnLabel(int orderNumber) throws SQLException
    {
        if (orderNumber < 1) {
            throw new IllegalArgumentException("Argument must not be lower than 1!");
        }

        return selectionTable.getNamesOfColumns().get(orderNumber - 1);
    }


    // ###########################################################################
    //                 not   implemented    dummy    methods
    // ###########################################################################


    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }
}
