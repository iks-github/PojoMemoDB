package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.COMMA;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.NOT_NULL_ENABLED;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.CONSTRAINT;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.USING_INDEX;

import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class SqlPojoCreateTableParser extends AbstractSqlPojoMemoryParser
{	
	private static final String[] EndOfColumnInfoKeyWords = { CONSTRAINT, USING_INDEX };
	
	public SqlPojoCreateTableParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}

	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.CREATE_TABLE_COMMAND.toLowerCase();
	}
	
	public TableMetaData parseCreateTableStatement(final String sql) throws SQLException
	{
		final InterimParseResult parseResult = parseTableName(sql);
		final String tableName = removeSurroundingQuotes(parseResult.parsedValue);
		final TableMetaData tableMetaData = new Table(tableName);
		
		if (! parseResult.unparsedRest.startsWith("(") ) {
			throw new SQLException("Left parenthis missing...");
		}
		
		parseColumnData(parseResult.unparsedRest.substring(1), tableMetaData);

		return tableMetaData;
	}

	private String parseColumnData(String unparsedRest, final TableMetaData tableMetaData) throws SQLException 
	{
		while ( ! isEndOfColumnDataReached(unparsedRest) )  {
			unparsedRest = parseColumnInformation(unparsedRest.trim(), tableMetaData);
	    }
		
		return unparsedRest;
	}
	
	private boolean isEndOfColumnDataReached(final String unparsedRest) 
	{
		if ( unparsedRest.length() == 0 || unparsedRest.startsWith(CLOSING_PARENTHESIS) ) {
			return true;
		}
		
		for (String keyword : EndOfColumnInfoKeyWords) {
			if (unparsedRest.toLowerCase().startsWith(keyword.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * TODO
	 * @param columnInformation
	 * @param tableMetaData
	 * @return
	 * @throws SQLException
	 */
	private String parseColumnInformation(String columnInformation, 
			                             final TableMetaData tableMetaData) 
			                             throws SQLException
	{
		// parse column name
		InterimParseResult parseResult = parseNextValue(columnInformation, SPACE);
		if (parseResult.parsedValue == null) {
			throw new SQLException("Column name not found but expected");
		}
		final String columnName = removeSurroundingQuotes( parseResult.parsedValue );
		
		// parse column type
		parseResult = parseNextValue(parseResult.unparsedRest, SPACE, COMMA, CLOSING_PARENTHESIS+CLOSING_PARENTHESIS);
		if (parseResult.parsedValue == null) {
			throw new SQLException("Column type not found but expected");
		}
		final String columnType = checkColumnType(parseResult.parsedValue);
		
		// parse column nullable state
		String toReturn = parseResult.unparsedRest; 
		final boolean nullable = ! toReturn.toLowerCase().startsWith(NOT_NULL_ENABLED.toLowerCase());
		if (! nullable) {
			toReturn = toReturn.substring(NOT_NULL_ENABLED.length() + 1).trim();
		}
		
		// collect parse results
		tableMetaData.createNewColumn( columnName, columnType, nullable, memoryDb );

		return toReturn;
	}

	private String checkColumnType(final String columnType) {
		if (columnType.contains(OPENING_PARENTHESIS) && ! columnType.contains(CLOSING_PARENTHESIS))
		{
			return columnType + CLOSING_PARENTHESIS;
		}
		return columnType;
	}
	
}
