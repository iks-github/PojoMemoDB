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
package com.iksgmbh.sql.pojomemodb.connection;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;
import org.joda.time.DateTime;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * This class represents an incomplete implementation of java.sql.ResultSet class.
 * It implements the most basic methods to make the SqlPojoMemoryDB running.
 * 
 * @author Reik Oberrath
 */
@SuppressWarnings("unused")
public class SqlPojoResultSet implements ResultSet 
{
    private SelectionTable selectionTable;
    private final List<Object[]> selectedData;
    private int resultCursorPosition = -1;
	private int maximumIndex;

	public SqlPojoResultSet(final SelectionTable aSelectionTable)
    {
		this.selectionTable = aSelectionTable;
        this.selectedData = aSelectionTable.getDataRows();

		if (aSelectionTable == null) {
			maximumIndex = -1;
		} else {
			maximumIndex = selectionTable.getNumberOfRows() - 1;
		}
	}


	@Override
	public boolean next() throws SQLException 
	{
        boolean toReturn = (selectedData != null && selectedData.size() > 0 && resultCursorPosition < maximumIndex);
		resultCursorPosition++;
		return toReturn;
	}
	

	@Override
	public void close() throws SQLException {
		// nothing to close
	}


	@Override
	public long getLong(int columnOrderNumber) throws SQLException {
		try {
			final BigDecimal decimal = (BigDecimal) selectedData.get(resultCursorPosition)[columnOrderNumber-1];
			if (decimal == null) {
				throw new NullPointerException("null value in db cannot be parsed into an int value.");
			}
			return decimal.longValue();
		} catch (ClassCastException e) {
			throwsTypeMismatchException(e);
			return -1;
		}
	}
	
	@Override
	public int getInt(int columnOrderNumber) throws SQLException {
		return (int) getLong(columnOrderNumber);
	}


	@Override
	public String getString(int columnOrderNumber) throws SQLException {
		try {
			return (String) selectedData.get(resultCursorPosition)[columnOrderNumber-1];
		} catch (ClassCastException e) {
			throwsTypeMismatchException(e);
			return null;
		}
	}
	
	@Override
	public Date getDate(int columnOrderNumber) throws SQLException {
		try {
			final DateTime dateTime = (DateTime) selectedData.get(resultCursorPosition)[columnOrderNumber-1];
			if (dateTime == null) {
				throw new NullPointerException("null value in db cannot be parsed into an Date value.");
			}
			return new java.sql.Date(dateTime.getMillis());
		} catch (ClassCastException e) {
			throwsTypeMismatchException(e);
			return null;
		}
	}

    @Override
    public Time getTime(int columnOrderNumber) throws SQLException {
        try {
            final DateTime dateTime = (DateTime) selectedData.get(resultCursorPosition)[columnOrderNumber-1];
            if (dateTime == null) {
                throw new NullPointerException("null value in db cannot be parsed into an Date value.");
            }
            return new java.sql.Time(dateTime.getMillis());
        } catch (ClassCastException e) {
            throwsTypeMismatchException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int columnOrderNumber) throws SQLException {
        try {
            final DateTime dateTime = (DateTime) selectedData.get(resultCursorPosition)[columnOrderNumber-1];
            if (dateTime == null) {
                throw new NullPointerException("null value in db cannot be parsed into an Date value.");
            }
            return new java.sql.Timestamp(dateTime.getMillis());
        } catch (ClassCastException e) {
            throwsTypeMismatchException(e);
            return null;
        }
    }
	
	private void throwsTypeMismatchException(final ClassCastException e) throws SQLException {
		throw new SQLException("Wrong data type while accessing data in the result set.", e);
	}


    @Override
    public ResultSetMetaData getMetaData() throws SQLException {

        return new SqlPojoResultSetMetaData(selectionTable);
    }

    @Override
    public Object getObject(int columnOrderNumber) throws SQLException
    {
        String columnName = selectionTable.getNamesOfColumns().get(columnOrderNumber - 1);
        String columnType = selectionTable.getTypeOfColumn(columnName);

        if ( "TIMESTAMP".equals(columnType) ) {
            return getTimestamp(columnOrderNumber);
        }

        if ( "DATE".equals(columnType) ) {
            return getDate(columnOrderNumber);
        }

        if ( "TIME".equals(columnType) ) {
            return getTime(columnOrderNumber);
        }

        return selectedData.get(resultCursorPosition)[columnOrderNumber - 1];
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        int columnOrderNumber = findColumn(columnLabel);
        return getObject(columnOrderNumber);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        List<String> namesOfColumns = selectionTable.getNamesOfColumns();
        int columnOrderNumber = 1;
        for (String columnName : namesOfColumns)  {
            if (columnLabel.equals(columnName))  {
                return columnOrderNumber;
            }
            columnOrderNumber++;
        }

        return -1;
    }


    // ###########################################################################
	//                 not   implemented    dummy    methods
	// ###########################################################################

		


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

	@Override
	public boolean wasNull() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public String getCursorName() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean isFirst() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean isLast() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void beforeFirst() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void afterLast() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public boolean first() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean last() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public int getRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean previous() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public int getFetchDirection() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public int getFetchSize() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public int getType() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public int getConcurrency() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void insertRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void deleteRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void refreshRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void moveToInsertRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public Statement getStatement() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public int getHoldability() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");

	}


    // @Override not before Java 1.7
	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


    // @Override not before Java 1.7
	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


}