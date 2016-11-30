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
package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.*;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.FROM;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.WHERE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.COMMA;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValueByLastOccurrence;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSelectData;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.TableId;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.sqlparser.helper.WhereConditionParser;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class SqlPojoSelectParser extends AbstractSqlPojoMemoryParser
{
	public SqlPojoSelectParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}

	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.SELECT_COMMAND.toLowerCase();
	}


	public ParsedSelectData parseSelectSql(final String sql) throws SQLException 
	{
		InterimParseResult parseResult = parseNextValue(sql, WHERE);  
		final String whereClause = parseResult.unparsedRest;
		final String selectClause = parseResult.parsedValue;
		final List<WhereCondition> whereConditions = WhereConditionParser.doYourJob(whereClause);
		
		parseResult = parseNextValue(selectClause, SPACE);  // cuts command
		
		if (! parseResult.unparsedRest.toLowerCase().contains(FROM.toLowerCase())) {
			throw new SQLException("Missing FROM declaration in select statement: " + sql);
		}

		parseResult = parseNextValueByLastOccurrence(parseResult.unparsedRest, FROM);
		final List<String> selectedColumns = parseColumnList(parseResult.parsedValue);
		
		final List<TableId> selectedTables;
		
		if (sql.contains(JOIN))  
		{
			selectedTables = parseAnsiJoinStatement(parseResult.unparsedRest, whereConditions);
		} 
		else 
		{
			selectedTables = parseTableList(parseResult.unparsedRest);
		}
		
		if (selectedTables.size() == 1) {
			// simple select on only one table
			removeTableIdFromColumnNamesIfPresent(selectedTables.get(0), selectedColumns); 
		} else {
			replaceAliasInWhereClause(whereConditions, selectedTables);
			replaceAliasInSelectClause(selectedColumns, selectedTables);
		}
		
		ParsedSelectData toReturn = new ParsedSelectData(buildTableNameList(selectedTables), selectedColumns, whereConditions);
		checkForUnkownAliases(toReturn);
		return toReturn;
	}

	/**
	 * Parses a part of the SQL join statement that follows ANSI syntax
	 * @param joinSqlPart 
	 * @param whereConditions
	 * @return List of JoinTables and join conditions in whereConditions 
	 * @throws SQLException 
	 */
	private List<TableId> parseAnsiJoinStatement(final String joinSqlPart, 
			                                     final List<WhereCondition> whereConditions) throws SQLException  
	{
		InterimParseResult parseResult = parseNextJoinTable(joinSqlPart);
		
		final List<TableId> toReturn = new ArrayList<TableId>();
		toReturn.add( new TableId(parseResult.parsedValue) );

		try {			
			while (parseResult.delimiter != null)  {
				parseResult = parseJoinInstruction(parseResult.unparsedRest, whereConditions, toReturn);
			}
		} catch (SQLDataException e) {
			throw new SQLDataException(e.getMessage() + joinSqlPart);
		}
				
		return toReturn;
	}

	private InterimParseResult parseJoinInstruction(final String unparsedRest,
			                                         final List<WhereCondition> whereConditions,
			                                         final List<TableId> toReturn) throws SQLException 
	{
		InterimParseResult parseResult = parseNextJoinTable(unparsedRest);
		
		InterimParseResult tmpParseResult = parseNextValue(parseResult.parsedValue, ON);		
		
		if (tmpParseResult.delimiter == null) {
			throw new SQLDataException("Missing " + ON.toUpperCase() + " keyword: ");
		}
		
		toReturn.add( new TableId(tmpParseResult.parsedValue) );
		
		final String onClause = tmpParseResult.unparsedRest;  
		List<WhereCondition> joinConditions = WhereConditionParser.doYourJob(onClause);
		whereConditions.addAll(joinConditions);
		
		return parseResult;
	}

	private InterimParseResult parseNextJoinTable(final String joinSqlPart) 
	{
		InterimParseResult parseResult = parseNextValue(joinSqlPart, INNER_JOIN);
		
		if (parseResult.delimiter == null) {
			parseResult = parseNextValue(joinSqlPart, JOIN); // treat Join as InnerJoin
		}
		return parseResult;
	}

	/**
	 * Throws an exception if a join condition or where conditions
	 * contains an ALIAS name that is undefined.
	 * 
	 * @param parsedSelectData
	 * @throws SQLDataException
	 */
	private void checkForUnkownAliases(final ParsedSelectData parsedSelectData) throws SQLDataException 
	{
		final List<WhereCondition> whereConditions = parsedSelectData.whereConditions;
		for (WhereCondition whereCondition : whereConditions) {
			checkForUnkownAliases(whereCondition.getColumnName(), parsedSelectData.tableNames);
			checkForUnkownAliases(whereCondition.getValueAsString(), parsedSelectData.tableNames);
		}
		
		final List<String> selectedColumns = parsedSelectData.selectedColumns;
		if (selectedColumns != null) {
			for (String columnName : selectedColumns) {
				checkForUnkownAliases(columnName, parsedSelectData.tableNames);
			}
		}
	}
	

	private void replaceAliasInSelectClause(final List<String> selectedColumns, 
			                                final List<TableId> selectedTables) 
	{
		if (selectedColumns != null) 
		{
			int i = -1;
			for (String columnName : selectedColumns) 
			{
				i++;
				for (TableId tableId : selectedTables) 
				{
					final String firstPartOfColumnId = tableId.getAlias() + ".";
					if (columnName.startsWith(firstPartOfColumnId)) {
						selectedColumns.set(i, columnName.replace(firstPartOfColumnId, tableId.getTableName() + "."));
					}
				}
			}
		}
	}


	private void checkForUnkownAliases(final String columnId, 
			                           final List<String> tableNames) throws SQLDataException 
	{
		if (columnId != null && columnId.contains(".") && ! columnId.contains("'") && ! columnId.endsWith("." + SQLKeyWords.NEXTVAL))  
		{
			boolean isKnown = false;
			
			for (String tableName : tableNames) {
				if (columnId.startsWith(tableName + ".")) {
					isKnown = true;
				}
			}
			
			if (! isKnown) {
				throw new SQLDataException("Unkown column id <" + columnId + "> detected.");
			}
		}
		
	}

	private void replaceAliasInWhereClause(final List<WhereCondition> whereConditions, 
			                                   final List<TableId> tableIdList) throws SQLException 
	{
		final List<WhereCondition> toReturn = new ArrayList<WhereCondition>();
		for (WhereCondition whereCondition : whereConditions) {
			toReturn.add(new WhereCondition(replaceAliases(whereCondition.getColumnName(), tableIdList),
					                        whereCondition.getComparator(), 
					                        replaceAliases(whereCondition.getValueAsString(), tableIdList)));
		}
		whereConditions.clear();
		whereConditions.addAll(toReturn);
	}

	private String replaceAliases(final String valueAsString, 
			                      final List<TableId> tableIdList) 
	{
		if (valueAsString == null) 
			return null;
		
		String toReturn = valueAsString;
		for (TableId tableId : tableIdList) {
			toReturn = toReturn.replace(tableId.getAlias() + ".", tableId.getTableName() + ".");
		}
		return toReturn;
	}

	private List<TableId> parseTableList(final String tableNameData) throws SQLException 
	{
		final List<TableId> toReturn = new ArrayList<SqlExecutor.TableId>();
		final String[] splitResult = tableNameData.split(COMMA);
		for (String string : splitResult) {
			toReturn.add(new TableId(string.trim()));
		}
		
		return toReturn;
	}

	private List<String> buildTableNameList(final List<TableId> selectedTables) 
	{
		final List<String> toReturn = new ArrayList<String>();
		for (TableId tableId : selectedTables) {
			toReturn.add(tableId.getTableName());
		}
		return toReturn;
	}

	private void removeTableIdFromColumnNamesIfPresent(final TableId tableId, 
			                                           final List<String> selectedColumns) throws SQLDataException 
	{
		if (selectedColumns == null) return;
		
		for (int i = 0; i < selectedColumns.size(); i++) 
		{
			final String oldColumnName = selectedColumns.get(i);
			final String alias = tableId.getAlias();
			
			if (alias != null && oldColumnName.startsWith(alias + ".")) {
				// remove alias
				String newColumnName = oldColumnName.substring(alias.length() + 1);
				selectedColumns.set(i, newColumnName);
			} else if (oldColumnName.toUpperCase().startsWith(tableId.getTableName() + ".")) {
				// remove table name
				String newColumnName = oldColumnName.substring(tableId.getTableName().length() + 1);
				selectedColumns.set(i, newColumnName);

			} else if (! StringUtils.isEmpty(alias)){
				throw new SQLDataException("Column name <" + oldColumnName + "> misses table alias.");
			}
		}
	}

	private List<String> parseColumnList(final String columnNameData) 
	{
		if (ALL_COLUMNS.equals(columnNameData)) {
			return null;
		} 
		
		final List<String> toReturn = new ArrayList<String>();
		InterimParseResult parseResult = parseNextValue(columnNameData, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0));
		toReturn.add(parseResult.parsedValue);
		
		if ( StringUtils.isEmpty(parseResult.unparsedRest)) {
			// do nothing
		} else if ( ! parseResult.unparsedRest.contains(COMMA)) {
			toReturn.add(parseResult.unparsedRest);
		} else {			
			while ( ! StringUtils.isEmpty( parseResult.unparsedRest) )
			{
				parseResult = parseNextValue(parseResult.unparsedRest, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0));
				toReturn.add(parseResult.parsedValue);
			}
		}
		
		return toReturn;
	}

}