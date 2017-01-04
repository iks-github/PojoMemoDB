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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * This class represents an incomplete implementation of java.sql.Connection class.
 * It implements the most basic methods to make the SqlPojoMemoryDB running.
 * 
 * @author Reik Oberrath
 */
@SuppressWarnings("unused")
public class SqlPojoConnection implements Connection 
{
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new SqlPojoPreparedStatement(sql);
	}
	

	@Override
	public Statement createStatement() throws SQLException {
		return new SqlPojoPreparedStatement("");
	}


    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new SqlPojoDatabaseMetaData();
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
	public CallableStatement prepareCall(String sql) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void commit() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void rollback() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void close() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public boolean isClosed() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public String getCatalog() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
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
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public int getHoldability() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return false;
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		if (true) throw new RuntimeException("Not yet implemented!");
		
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


	// @Override not before Java 1.7
	public void abort(Executor arg0) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
	}


    // @Override not before Java 1.7
	public int getNetworkTimeout() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return 0;
	}


    // @Override not before Java 1.7
	public String getSchema() throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
		return null;
	}


    // @Override not before Java 1.7
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
	}

    // @Override not before Java 1.7
	public void setSchema(String arg0) throws SQLException {
		if (true) throw new RuntimeException("Not yet implemented!");
	}

	
}