package com.iksgmbh.sql.pojomemodb.sqlparser;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.START_WITH;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;

import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlExecutor.ParsedSequenceData;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;

public class SqlPojoCreateSequenceParser extends AbstractSqlPojoMemoryParser 
{
	public SqlPojoCreateSequenceParser(final SqlPojoMemoDB memoryDb)  {
		this.memoryDb = memoryDb;
	}
	
	/**
	 * Defines the SQL command whose SQL statement is parsed by this parser
	 */
	@Override
	protected String getSqlCommand() {
		return SQLKeyWords.CREATE_SEQUENCE_COMMAND.toLowerCase();
	}
	
	public ParsedSequenceData parseCreateSequenceStatement(final String sql) throws SQLException 
	{
		final String sqlWithoutCommand = sql.substring(getSqlCommand().length()).trim();
		int pos = sqlWithoutCommand.indexOf(SPACE);
		String tmpString = sqlWithoutCommand.substring(0, pos);
		final String sequenceName = removeSurroundingQuotes(tmpString);
		
		pos = sqlWithoutCommand.toLowerCase().indexOf(START_WITH.toLowerCase());
		
		long currentValue;
		if (pos == -1) {
			currentValue = 0;
		} else {
			tmpString = sqlWithoutCommand.substring(pos + START_WITH.length()).trim();
			pos = tmpString.indexOf(SPACE);
			String currentValueAsString = tmpString.substring(0, pos).trim();
			
			try {
				currentValue = Long.valueOf(currentValueAsString);
			} catch (Exception e) {
				throw new SQLException("Unable to parse start value of sequence '" + sequenceName + "' to a number: " + currentValueAsString);
			}
		}
		
		return new ParsedSequenceData(sequenceName, currentValue );
	}
	
}
