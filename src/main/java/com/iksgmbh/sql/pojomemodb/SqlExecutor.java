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
package com.iksgmbh.sql.pojomemodb;

import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.TableData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Sequence;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.*;
import com.iksgmbh.sql.pojomemodb.sqlparser.*;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlExecutor 
{
	private SqlPojoMemoDB memoryDb;
	
	// parser for the different SQL commands
	private CreateTableParser createTableParser;
	private InsertIntoParser insertIntoParser;
	private SelectParser selectParser;
	private UpdateParser updateParser;
	private DeleteTableParser deleteParser;
	private CreateSequenceParser createSequenceParser;
	
	public SqlExecutor(final SqlPojoMemoDB sqlPojoMemoryDB) 
	{
		this.memoryDb = sqlPojoMemoryDB;
		createTableParser = new CreateTableParser(sqlPojoMemoryDB);
		insertIntoParser = new InsertIntoParser(sqlPojoMemoryDB);
		selectParser = new SelectParser(sqlPojoMemoryDB);
		updateParser = new UpdateParser(sqlPojoMemoryDB);
		deleteParser = new DeleteTableParser(sqlPojoMemoryDB);
		createSequenceParser = new CreateSequenceParser(sqlPojoMemoryDB);
	}
	
	public Object executeSqlStatement(final String sql) throws SQLException 
	{
		if (sql.toLowerCase().startsWith(SQLKeyWords.CREATE_TABLE_COMMAND))  {
			return executeCreateTableStatement(sql);
		}
		
		if (sql.toLowerCase().startsWith(SQLKeyWords.INSERT_INTO_COMMAND))  {
			return executeInsertStatement(sql);
		}
		
		if (sql.toLowerCase().startsWith(SQLKeyWords.SELECT_COMMAND))  {
			return executeSelectStatement(sql);
		}
		
		if (sql.toLowerCase().startsWith(SQLKeyWords.UPDATE_COMMAND))  {
			return executeUpdateStatement(sql);
		}
		
		if (sql.toLowerCase().startsWith(SQLKeyWords.DELETE_COMMAND))  {
			return executeDeleteStatement(sql);
		}
		
		if (sql.toLowerCase().startsWith(SQLKeyWords.CREATE_SEQUENCE_COMMAND))  {
			return executeCreateSequenceStatement(sql);
		}
		
		throw new SQLException("Unknown command in SQL statement: '" + sql + "'");
	}
	

	private String executeCreateTableStatement(final String sql) throws SQLException 
	{
		final TableMetaData tableMetaData = createTableParser.parseCreateTableStatement(sql);
		memoryDb.getTableStoreMetaData().addTable(tableMetaData );
		return "Table " + tableMetaData.getTableName() +  " has been created.";
	}
	
	private String executeInsertStatement(final String sql) throws SQLException 
	{
		final ParsedInsertData parsedInsertData = insertIntoParser.parseInsertStatement(sql);
		final List<ApartValue> values = buildApartValueList(parsedInsertData.columnNames, 
				                                            parsedInsertData.dataValues);
		memoryDb.getTableStoreData().getTableData(parsedInsertData.tableName).insertDataRow(values);
		
		return "Data row has been inserted into " + parsedInsertData.tableName +  ".";
	}

	List<ApartValue> buildApartValueList(final List<String> columnNames, 
			                             final List<String> dataValues) throws SQLDataException
	{
		if (columnNames.size() != dataValues.size()) {
			System.err.println("###############################################");
			System.err.println("Here the list of columnName-dataValue pairs:");
			System.err.println("----------------------------------------------");

			if (columnNames.size() < dataValues.size()) {
				for (int i = 0; i < columnNames.size(); i++) {
					System.err.println(columnNames.get(i) + " - " + dataValues.get(i));
				}
				System.err.println("===============================================");
				System.err.println("This is the list of the additional data values:");
				System.err.println("-----------------------------------------------");
				for (int i = columnNames.size(); i < dataValues.size(); i++) {
					System.err.println(dataValues.get(i));
				}
			} else {
				for (int i = 0; i < dataValues.size(); i++) {
					System.err.println(columnNames.get(i) + " - " + dataValues.get(i));
				}
				System.err.println("===============================================");
				System.err.println("This is the list of the additional column names:");
				System.err.println("-----------------------------------------------");
				for (int i = dataValues.size(); i < columnNames.size(); i++) {
					System.err.println(columnNames.get(i));
				}
			}
			System.err.println("-----------------------------------------------");
			System.err.println("###############################################");

			throw new SQLDataException("Unequal number of column names (" + columnNames.size() + ") and data values ("
					+ dataValues.size() + ").");
		}

		final List<ApartValue> toReturn = new ArrayList<ApartValue>();

		for (int i = 0; i < dataValues.size(); i++) {
			toReturn.add(new ApartValue(dataValues.get(i), columnNames.get(i)));
		}

		return toReturn;
	}

	private SelectionTable executeSelectStatement(final String sql) throws SQLException 
	{
		final ParsedSelectData parseResult = selectParser.parseSelectSql(sql);
        final TableData tableData;
        final List<Object[]> selectedData;

        if (parseResult.tableNames.size() == 1)
		{
			// simple select for a single table
			tableData = memoryDb.getTableStoreData().getTableData(parseResult.tableNames.get(0));
			
			if (parseResult.selectedColumns == null) {
				parseResult.selectedColumns = tableData.getNamesOfColumns();
			}

            selectedData = tableData.select(parseResult.selectedColumns, parseResult.whereConditions, parseResult.orderConditions);
		}
        else // build join table and select on it
        {
            tableData = buildJoinTable(parseResult);
            final List<WhereCondition> whereConditions = getOnlyNonJoinConditions(parseResult.whereConditions);
            selectedData = tableData.select(parseResult.selectedColumns, whereConditions, parseResult.orderConditions);
        }

        final SelectionTable toReturn = new SelectionTable((Table)tableData, parseResult.selectedColumns);
        toReturn.setDataRows(selectedData);
        return toReturn;
	}
	
	TableData buildJoinTable(final ParsedSelectData parseResult) throws SQLDataException 
	{
		final JoinTable joinTable = new JoinTable(memoryDb, parseResult.tableNames.get(0));
		final List<WhereCondition> joinConditions = getOnlyJoinConditions(parseResult.whereConditions);
		applyJoinConditions(joinTable, joinConditions);
		return joinTable;
	}

	private void applyJoinConditions(final JoinTable joinTable, 
			                         List<WhereCondition> joinConditions) throws SQLDataException 
	{
		final List<WhereCondition> conditionsUnableToApply = new ArrayList<WhereCondition>();
		
		while (conditionsUnableToApply.size() < joinConditions.size())  
		{
			conditionsUnableToApply.clear();
			for (WhereCondition joinCondition : joinConditions) 
			{
				boolean ok = joinTable.join(joinCondition);
				if ( ! ok )  {
					conditionsUnableToApply.add(joinCondition);
				}
			}
			
			joinConditions.clear();
			joinConditions.addAll(conditionsUnableToApply);
		}

		if (joinConditions.size() == 1) {
			throw new SQLDataException("Unable to apply join condition: " + joinConditions.get(0).toString());
		} 
		else if (joinConditions.size() > 1)
		{
			throw new SQLDataException("Unable to apply " + joinConditions.size() + " join conditions, e.g. " + joinConditions.get(0).toString());
		}
	}

	private List<WhereCondition> getOnlyNonJoinConditions(final List<WhereCondition> whereConditions) 
	{
		final List<WhereCondition> toReturn = new ArrayList<WhereCondition>();
		for (WhereCondition whereCondition : whereConditions) {
			if ( ! whereCondition.isJoinCondition() ) {
				toReturn.add(whereCondition);
			}
		}
		return toReturn;
	}

	private List<WhereCondition> getOnlyJoinConditions(final List<WhereCondition> whereConditions) 
	{
		final List<WhereCondition> toReturn = new ArrayList<WhereCondition>();
		for (WhereCondition whereCondition : whereConditions) {
			if ( whereCondition.isJoinCondition() ) {
				toReturn.add(whereCondition);
			}
		}
		return toReturn;
	}

	private int executeUpdateStatement(final String sql) throws SQLException 
	{
		final ParsedUpdateData parseResult = updateParser.parseUpdateStatement(sql);
		final TableData tableData = memoryDb.getTableStoreData().getTableData(parseResult.tableName);
		return tableData.update(parseResult.newValues, parseResult.whereConditions);
	}
	

	private int executeDeleteStatement(final String sql) throws SQLException 
	{
		final ParsedDeleteData parseResult = deleteParser.parseDeleteStatement(sql);
		final TableData tableData = memoryDb.getTableStoreData().getTableData(parseResult.tableName);
		return tableData.delete(parseResult.whereConditions);
	}
	
	private String executeCreateSequenceStatement(final String sql) throws SQLException 
	{
		final ParsedSequenceData result = createSequenceParser.parseCreateSequenceStatement(sql);
		final Sequence sequence = new Sequence(result.sequenceName);
		sequence.setCurrentValue(result.currentValue);
		memoryDb.getTableStoreMetaData().addSequence(sequence);
		return "Sequence " + result.sequenceName +  " has been created.";
	}

	
	
	// #########################################################################################
	//             D A T A   C L A S S E S    F O R   P A R S E D   D A T A
	// #########################################################################################
	
	
	public static class ParsedInsertData  
	{
		public String tableName;
		public List<ApartValue> values;
		public List<String> columnNames;
		public List<String> dataValues;
		
		public ParsedInsertData(final String tableName, 
				                final List<String> columnNames, 
				                final List<String> dataValues) 
		{
			this.tableName = tableName;
			this.columnNames = columnNames;
			this.dataValues = dataValues;
		}
	}
	
	public static class ParsedSelectData  
	{
		public List<String> tableNames;
		public List<String> selectedColumns;
		public List<WhereCondition> whereConditions;
		public List<OrderCondition> orderConditions;
		
		public ParsedSelectData(final List<String> tableNames, 
				                final List<String> selectedColumns, 
				                final List<WhereCondition> whereConditions,
								final List<OrderCondition> orderConditions)
		{
			this.tableNames = tableNames;
			this.selectedColumns = selectedColumns;
			this.whereConditions = whereConditions;
			this.orderConditions = orderConditions;
		}

		
		public ParsedSelectData(final String tableName, 
				                final List<String> selectedColumns, 
				                final List<WhereCondition> whereConditions) 
		{
			this.tableNames = new ArrayList<String>();
			tableNames.add(tableName);
			this.selectedColumns = selectedColumns;
			this.whereConditions = whereConditions;
		}	}
	
	public static class TableId 
	{
		private final static String AS_SEPARATOR = " " + SQLKeyWords.AS + " ";
		private String tableName;
		private String alias;
		
		public TableId(final String tableId) throws SQLException 
		{
			final String[] splitResult;
			if (tableId.toLowerCase().contains(AS_SEPARATOR.toLowerCase())) {
				splitResult = tableId.trim().toLowerCase().split(AS_SEPARATOR.toLowerCase());
			} else {
				splitResult = tableId.trim().split(StringParseUtil.SPACE);
			}
			
			if (splitResult.length == 2) {
				alias = splitResult[1].toUpperCase();
			} else if (splitResult.length != 1) {
				throw new SQLException("Non parseable table identification <" + tableId + ">!");
			}
			
			tableName = splitResult[0].toUpperCase();
		}

		public String getTableName() {
			return tableName;
		}

		public String getAlias() {
			return alias;
		}
		
		@Override
		public String toString() {
			return "TableId [tableName=" + tableName + ", alias=" + alias + "]";
		}
	}

	public static class ParsedUpdateData  
	{
		public String tableName;
		public List<ApartValue> newValues;
		public List<WhereCondition> whereConditions;
		
		public ParsedUpdateData(final String tableName, 
				                final List<ApartValue> newValues, 
				                final List<WhereCondition> whereConditions) 
		{
			this.tableName = tableName;
			this.newValues = newValues;
			this.whereConditions = whereConditions;
		}
	}
	
	public static class ParsedDeleteData  
	{
		public String tableName;
		public List<WhereCondition> whereConditions;
		
		public ParsedDeleteData(final String tableName, 
				                final List<WhereCondition> whereConditions) 
		{
			this.tableName = tableName;
			this.whereConditions = whereConditions;
		}
	}

	public static class ParsedSequenceData  
	{
		public String sequenceName;
		public long currentValue;
		
		public ParsedSequenceData(final String sequenceName, 
				                  final long currentValue) 
		{
			this.sequenceName = sequenceName;
			this.currentValue = currentValue;
		}
	}
	
}