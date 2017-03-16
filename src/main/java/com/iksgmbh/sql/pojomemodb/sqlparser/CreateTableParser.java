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

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.CONSTRAINT;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.PRIMARY_KEY;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.UNIQUE;
import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.USING_INDEX;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.CLOSING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.COMMA;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.OPENING_PARENTHESIS;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.SPACE;
import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.parseNextValue;

import java.sql.SQLDataException;
import java.sql.SQLException;

import com.iksgmbh.sql.pojomemodb.SQLKeyWords;
import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Column;
import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Table;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ColumnInitData;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.InterimParseResult;

public class CreateTableParser extends SqlPojoMemoParser
{
	private static final String[] EndOfColumnInfoKeyWords = {CONSTRAINT, USING_INDEX};

	public CreateTableParser(final SqlPojoMemoDB memoryDb) {
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

		if (!parseResult.unparsedRest.startsWith("(")) {
			throw new SQLException("Left parenthis missing...");
		}

        int numOpen = StringParseUtil.countOccurrencesOf(parseResult.unparsedRest, OPENING_PARENTHESIS.charAt(0));
        int numClose = StringParseUtil.countOccurrencesOf(parseResult.unparsedRest, CLOSING_PARENTHESIS.charAt(0));
        if ( numOpen > numClose) {
            throw new SQLException("Missing closing parenthesis in '" + parseResult.unparsedRest + "'.");
        }

        processColumnData(parseResult.unparsedRest.substring(1, parseResult.unparsedRest.length()-1), tableMetaData);

		return tableMetaData;
	}

	/**
	 * Parses column data and creates new columns in tableMetaData
	 *
	 * @param unparsedRest
	 * @param tableMetaData
	 * @return
	 * @throws SQLException
	 */
	private String processColumnData(String unparsedRest,
                                     final TableMetaData tableMetaData) throws SQLException
    {
		while ( ! isEndOfColumnDataReached(unparsedRest) )
        {
            if (unparsedRest.toUpperCase().startsWith(PRIMARY_KEY.toUpperCase())) {
                unparsedRest = createDefaultPrimaryConstraint(unparsedRest, tableMetaData);
            } else if (unparsedRest.toUpperCase().startsWith(UNIQUE.toUpperCase())) {
                unparsedRest = createDefaultUniqueConstraint(unparsedRest, tableMetaData);
            } else if (unparsedRest.toUpperCase().startsWith(CONSTRAINT.toUpperCase())) {

                if (unparsedRest.toUpperCase().contains(PRIMARY_KEY.toUpperCase())) {
                    unparsedRest = createPrimaryConstraint(unparsedRest, tableMetaData);
                }
                if (unparsedRest.toUpperCase().contains(UNIQUE.toUpperCase())) {
                    unparsedRest = createUniqueConstraint(unparsedRest, tableMetaData);
                }
            }
            else
            {
                unparsedRest = processColumnInformation(unparsedRest.trim(), tableMetaData);
            }
		}

		return unparsedRest;
	}

    private String createPrimaryConstraint(String unparsedRest, TableMetaData tableMetaData) throws SQLException
    {
        String toReturn = unparsedRest.substring(CONSTRAINT.length()).trim();
        InterimParseResult parseResult = parseNextValue(toReturn, PRIMARY_KEY);

        if (StringParseUtil.isEmpty(parseResult.delimiter)) {
            throw new SQLException("Cannot parse to Primary Key Constraint: " + unparsedRest + " Expected something like 'CONSTRAINT PRIMARY_KEY_ID PRIMARY KEY (COLUMN_NAME)'.");
        }

        String primaryKeyId = removeSurroundingQuotes(parseResult.parsedValue);
        parseResult = parseNextValue(parseResult.unparsedRest, COMMA);
        String constraintColumnName = null;

        if (parseResult.delimiter == null) {
            toReturn = "";
            constraintColumnName = parseResult.parsedValue;
        }
        else
        {
            toReturn = parseResult.unparsedRest;
            constraintColumnName = parseResult.parsedValue;

            int pos = constraintColumnName.toUpperCase().indexOf(USING_INDEX.toUpperCase());
            if (pos > 0) {
                constraintColumnName = constraintColumnName.substring(0, pos).trim();
            }
        }

        if (constraintColumnName.startsWith(OPENING_PARENTHESIS)) {
            if (constraintColumnName.endsWith(CLOSING_PARENTHESIS)) {
                constraintColumnName = constraintColumnName.substring(1, constraintColumnName.length() - 1);
            }
            else
            {
                throw new SQLException("Cannot parse to primary key constraint: " + unparsedRest + ". Expected something like 'CONSTRAINT PRIMARY_KEY_ID PRIMARY KEY (COLUMN_NAME)'.");
            }
        }
        else
        {
            if (constraintColumnName.endsWith(CLOSING_PARENTHESIS)) {
                throw new SQLException("Cannot parse to primary key constraint: " + unparsedRest + ". Expected something like 'CONSTRAINT PRIMARY_KEY_ID PRIMARY KEY (COLUMN_NAME)'.");
            }
        }

        constraintColumnName = removeSurroundingQuotes(constraintColumnName);
        Column column = ((Table) tableMetaData).getColumn(constraintColumnName);
        column.setPrimaryKeyId(primaryKeyId);
        column.setNullable(false);  // Primary Key column must not be nullable

        return toReturn;
    }

    private String createUniqueConstraint(String unparsedRest, TableMetaData tableMetaData) throws SQLException
    {
        String toReturn = unparsedRest.substring(CONSTRAINT.length()).trim();
        InterimParseResult parseResult = parseNextValue(toReturn, SPACE+UNIQUE);

        if (StringParseUtil.isEmpty(parseResult.delimiter)) {
            throw new SQLException("Cannot parse to Primary Key Constraint: " + unparsedRest + " Expected something like 'CONSTRAINT UNIQUE_ID UNIQUE (COLUMN_NAME)'.");
        }

        String uniqueConstraintId = removeSurroundingQuotes(parseResult.parsedValue);
        parseResult = parseNextValue(parseResult.unparsedRest, CLOSING_PARENTHESIS);
        String constraintColumnName = null;

        if (parseResult.unparsedRest == null) {
            toReturn = "";
            constraintColumnName = parseResult.parsedValue + CLOSING_PARENTHESIS;
        }
        else
        {
            toReturn = parseResult.unparsedRest.trim();
            constraintColumnName = parseResult.parsedValue + CLOSING_PARENTHESIS;

            int pos = constraintColumnName.toUpperCase().indexOf(USING_INDEX.toUpperCase());
            if (pos > 0) {
                constraintColumnName = constraintColumnName.substring(0, pos).trim();
            }
        }

        if (constraintColumnName.startsWith(OPENING_PARENTHESIS)) {
            if (constraintColumnName.endsWith(CLOSING_PARENTHESIS)) {
                constraintColumnName = constraintColumnName.substring(1, constraintColumnName.length() - 1);
            }
            else
            {
                throw new SQLException("Cannot parse to primary key constraint: " + unparsedRest + ". Expected something like 'CONSTRAINT UNIQUE_ID UNIQUE KEY (COLUMN_NAME)'.");
            }
        }
        else
        {
            if (constraintColumnName.endsWith(CLOSING_PARENTHESIS)) {
                throw new SQLException("Cannot parse to primary key constraint: " + unparsedRest + ". Expected something like 'CONSTRAINT UNIQUE UNIQUE (COLUMN_NAME)'.");
            }
        }

        constraintColumnName = removeSurroundingQuotes(constraintColumnName);
        Column column = ((Table) tableMetaData).getColumn(constraintColumnName);
        column.setUniqueConstraintId(uniqueConstraintId);

        if (toReturn.startsWith(COMMA))
            toReturn = toReturn.substring(1);

        return toReturn.trim();
    }


    private String createDefaultPrimaryConstraint(String unparsedRest, TableMetaData tableMetaData) throws SQLException
    {
        String toReturn = unparsedRest.substring(PRIMARY_KEY.length()).trim();
        if (! toReturn.startsWith(OPENING_PARENTHESIS)) {
            throw new SQLException("Cannot parse to primary key constraint: " + unparsedRest + ". Expected something like 'PRIMARY KEY (COLUMN_NAME)'.");
        }

        int pos = toReturn.indexOf(CLOSING_PARENTHESIS);

        String constraintColumnName = toReturn.substring(1, pos);

        Column column = ((Table) tableMetaData).getColumn(constraintColumnName);
        column.setPrimaryKeyId(createDefaultPrimaryConstraintName(constraintColumnName));
        column.setNullable(false);  // Primary Key column must not be nullable

        return toReturn.substring(pos).trim();
    }

    private String createDefaultUniqueConstraint(String unparsedRest, TableMetaData tableMetaData) throws SQLException
    {
        String toReturn = unparsedRest.substring(UNIQUE.length()).trim();
        if (! toReturn.startsWith(OPENING_PARENTHESIS)) {
            throw new SQLException("Cannot parse to unique constraint: " + unparsedRest + ". Expected something like 'UNIQUE (COLUMN_NAME)'.");
        }

        int pos = toReturn.indexOf(CLOSING_PARENTHESIS);

        String constraintColumnName = toReturn.substring(1, pos);

        Column column = ((Table) tableMetaData).getColumn(constraintColumnName);
        column.setUniqueConstraintId(createDefaultUniqueConstraintName(constraintColumnName));

        return toReturn.substring(pos).trim();
    }


    private boolean isEndOfColumnDataReached(final String unparsedRest) {
		if (unparsedRest.length() == 0 || unparsedRest.startsWith(CLOSING_PARENTHESIS)) {
			return true;
		}

        if (unparsedRest.contains(PRIMARY_KEY) || unparsedRest.contains(UNIQUE)) {
            return false;
        }

		for (String keyword : EndOfColumnInfoKeyWords) {
			if (unparsedRest.toLowerCase().startsWith(keyword.toLowerCase())) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Parses column data and creates a new column in tableMetaData
	 *
	 * @param columnInformation
	 * @param tableMetaData
	 * @return
	 * @throws SQLException
	 */
	private String processColumnInformation(final String columnInformation,
											final TableMetaData tableMetaData)
			                                throws SQLException
    {
		// parse column name
		InterimParseResult parseResult = parseNextValue(columnInformation, SPACE);
		if (parseResult.parsedValue == null) {
			throw new SQLException("Column name not found but expected");
		}
		final ColumnInitData columnInitData = new ColumnInitData( removeSurroundingQuotes(parseResult.parsedValue) );

		// parse column type
		parseResult = parseNextValue(parseResult.unparsedRest, OPENING_PARENTHESIS.charAt(0), CLOSING_PARENTHESIS.charAt(0), COMMA.charAt(0));
		if (parseResult.parsedValue == null) {
			throw new SQLException("Column type not found but expected");
		}
        columnInitData.columnType = correctParenthesisIfNecessary(parseResult.parsedValue);
        String toReturn = parseResult.unparsedRest;

        parseNullableConstraint(columnInitData);
        parseDefaultValue(columnInitData);
        parseUniqueConstraint(columnInitData);  // call this parse method in the end !
        parsePrimaryKeyConstraint(columnInitData);  // call this parse method in the end !

		// collect parse results
		tableMetaData.createNewColumn(columnInitData, memoryDb);

		return toReturn;
	}

    private void parseUniqueConstraint(ColumnInitData columnInitData)
    {
        int pos = columnInitData.columnType.toUpperCase().indexOf(SQLKeyWords.UNIQUE.toUpperCase());
        if (pos > -1) {
            columnInitData.columnType = (columnInitData.columnType.substring(0, pos) + columnInitData.columnType.substring(pos + SQLKeyWords.UNIQUE.length())).trim();
            columnInitData.uniqueKey = createDefaultUniqueConstraintName(columnInitData.columnName);
        }
    }

    private void parsePrimaryKeyConstraint(ColumnInitData columnInitData) throws SQLException
    {
        int pos = columnInitData.columnType.toUpperCase().indexOf(SQLKeyWords.PRIMARY_KEY.toUpperCase());
        if (pos > -1) {
            columnInitData.nullable = false;   // primary key must not be null !
            columnInitData.columnType = (columnInitData.columnType.substring(0, pos) + columnInitData.columnType.substring(pos + SQLKeyWords.PRIMARY_KEY.length())).trim();
            columnInitData.primaryKey = createDefaultPrimaryConstraintName(columnInitData.columnName);
        }

        if ( ! StringParseUtil.isEmpty(columnInitData.primaryKey)
             && columnInitData.nullable) {
            throw new SQLException("Primary Key column '" + columnInitData.columnName + "' must not be set to nullable=true!");
        }
    }

    private String createDefaultPrimaryConstraintName(String columnName) {
        return "PK_" + columnName;
    }

    private String createDefaultUniqueConstraintName(String columnName) {
        return "UC_" + columnName;
    }


    private void parseNullableConstraint(ColumnInitData columnInitData)
    {
        int pos = columnInitData.columnType.toUpperCase().indexOf(SQLKeyWords.NOT_NULL_ENABLED.toUpperCase());
        if (pos > -1) {
            columnInitData.nullable = false;
            columnInitData.columnType = columnInitData.columnType.substring(0, pos) + columnInitData.columnType.substring(pos + SQLKeyWords.NOT_NULL_ENABLED.length());
        }
        else
        {
            pos = columnInitData.columnType.toUpperCase().indexOf(SQLKeyWords.NOT_NULL.toUpperCase());
            if (pos > -1) {
                columnInitData.nullable = false;
                columnInitData.columnType = columnInitData.columnType.substring(0, pos) + columnInitData.columnType.substring(pos + SQLKeyWords.NOT_NULL.length());
            }
        }

        columnInitData.columnType = columnInitData.columnType.trim();
    }

    private void parseDefaultValue(ColumnInitData columnInitData) throws SQLException
    {
        if (columnInitData.columnType.toUpperCase().contains(SQLKeyWords.DEFAULT.toUpperCase()))
        {
            if ( columnInitData.columnType.toUpperCase().contains(SQLKeyWords.PRIMARY_KEY.toUpperCase())) {
                throw new SQLException("Primary Key column '" + columnInitData.columnName + "' must not define a default value!");
            }

            InterimParseResult parseResult = parseNextValue(columnInitData.columnType, SQLKeyWords.DEFAULT);
            columnInitData.defaultValue = parseResult.unparsedRest;
            columnInitData.columnType = parseResult.parsedValue;
            if (StringParseUtil.isEmpty(columnInitData.defaultValue) && ! StringParseUtil.isEmpty(parseResult.delimiter))
                throw new SQLDataException("Missing default value for column '" + columnInitData.columnName + "'!");
        }
    }

    private String correctParenthesisIfNecessary(final String columnType)
    {
        final int numOpenP = StringParseUtil.countOccurrencesOf(columnType, OPENING_PARENTHESIS.charAt(0));
        final int numCloseP = StringParseUtil.countOccurrencesOf(columnType, CLOSING_PARENTHESIS.charAt(0));

		if (numOpenP > numCloseP) {
			return columnType + CLOSING_PARENTHESIS;
		}

		if (numOpenP < numCloseP) {
			return columnType.substring(0, columnType.length() - 1);
		}

		return columnType;
	}
}