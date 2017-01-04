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

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * This class represents an incomplete implementation of java.sql.PreparedStatement class.
 * It implements the most basic methods to make the SqlPojoMemoryDB running.
 * 
 * @author Reik Oberrath
 */
@SuppressWarnings("unused")
public class SqlPojoPreparedStatement implements PreparedStatement 
{
	private static final String QUESTION_MARK = "?";
	
	private String inputSql;
	private String[] replacements;
	private String[] sqlParts;

	public SqlPojoPreparedStatement(final String inputSql) {
		this.inputSql = inputSql;
		analyseSql();
	}


	/**
	 * Searches for ? placeholders and prepares their replacements.
	 */
	private void analyseSql() 
	{
		final String tmp = inputSql + " ";
		sqlParts = tmp.split("\\" + QUESTION_MARK);
		replacements = new String[sqlParts.length - 1];
	}

	@Override
	public void close() throws SQLException {
		// nothing to do
	}

	@Override
	public ResultSet executeQuery() throws SQLException 
	{
        String sql = buildOutputSql();
        final Object result = SqlPojoMemoDB.execute(sql);  // uses the SqlExecutor
		if (result instanceof SelectionTable)  {
			return new SqlPojoResultSet((SelectionTable)result);
		}
		return null;
	}

	@Override
	public boolean execute() throws SQLException {
		SqlPojoMemoDB.execute(inputSql);
		return true;
	}
	
	@Override
	public ResultSet executeQuery(final String sql) throws SQLException 
	{
		this.inputSql = sql;
		analyseSql();
        String outputSql = buildOutputSql();
        SelectionTable result = (SelectionTable) SqlPojoMemoDB.execute(outputSql);  // uses the SqlExecutor

		return new SqlPojoResultSet(result);
	}


    public String buildOutputSql()
	{
		final StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (i = 0; i < replacements.length; i++) {
			sb.append(sqlParts[i]).append(replacements[i]);
		}
		
		return sb.append(sqlParts[i++]).toString().trim();
	}


	@Override
	public int executeUpdate() throws SQLException {
		return ((Integer)SqlPojoMemoDB.execute(buildOutputSql())).intValue();
	}
	
	@Override
	public void setString(final int parameterIndex, final String s) throws SQLException {
		checkParameterIndex(parameterIndex);
		replacements[parameterIndex-1] = "'" + s + "'";
	}

	@Override
	public void setLong(int parameterIndex, long l) throws SQLException {
		checkParameterIndex(parameterIndex);
		replacements[parameterIndex-1] = "" + l;
	}

	@Override
	public void setInt(int parameterIndex, int i) throws SQLException {
		checkParameterIndex(parameterIndex);
		replacements[parameterIndex-1] = "" + i;
	}


    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        checkParameterIndex(parameterIndex);
        replacements[parameterIndex-1] = x.toString();
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        checkParameterIndex(parameterIndex);
        replacements[parameterIndex-1] = "" + x.getTime();
    }


    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        checkParameterIndex(parameterIndex);
        replacements[parameterIndex-1] = "" + x.getTime();
    }


    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        checkParameterIndex(parameterIndex);
        replacements[parameterIndex-1] = "" + x.getTime();
    }

	private void checkParameterIndex(int parameterIndex) throws SQLException 
	{
		if (replacements.length < parameterIndex || parameterIndex < 1)  {
			throw new SQLException("ParameterIndex out of range: " + parameterIndex);
		}
		
		if (replacements[parameterIndex-1] != null)  {
			throw new SQLException("Value for ParameterIndex '" + parameterIndex + "' has been already set.");
		}
	}


	// ###########################################################################
	//                 not   implemented    dummy    methods
	// ###########################################################################


	@Override
	public int executeUpdate(String sql) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public int getMaxFieldSize() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public int getMaxRows() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public void setMaxRows(int max) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public int getQueryTimeout() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void cancel() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
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
	public void setCursorName(String name) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public boolean execute(String sql) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public ResultSet getResultSet() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public int getUpdateCount() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public boolean getMoreResults() throws SQLException {
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
	public int getResultSetConcurrency() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public int getResultSetType() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public void addBatch(String sql) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void clearBatch() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public int[] executeBatch() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public Connection getConnection() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public boolean getMoreResults(int current) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public int getResultSetHoldability() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


	@Override
	public boolean isClosed() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}


	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public boolean isPoolable() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
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


	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void clearParameters() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void addBatch() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");

    }

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}


	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
	}


    // @Override not before Java 1.7
	public void closeOnCompletion() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
	}


    // @Override not before Java 1.7
	public boolean isCloseOnCompletion() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

}