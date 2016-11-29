package com.iksgmbh.sql.pojomemodb;

public class SQLKeyWords {

	// SQL commands
	public static final String CREATE_TABLE_COMMAND = "create table";
	public static final String CREATE_SEQUENCE_COMMAND = "create sequence";
	public static final String DELETE_COMMAND = "delete";
	public static final String INSERT_INTO_COMMAND = "insert into";
	public static final String SELECT_COMMAND = "select";
	public static final String UPDATE_COMMAND = "update";

	// SqlPojoCreateSequenceParser
	public static final String START_WITH = "start with";
	
	// SqlPojoCreateTableParser
	public static final String NOT_NULL_ENABLED = "NOT NULL ENABLE";
	public static final String CONSTRAINT = "CONSTRAINT";
	public static final String USING_INDEX = "USING_INDEX";

	// SqlPojoSelectParser
	public static final String ALL_COLUMNS = "*";
	
	// SqlPojoInsertIntoParser
	public static final String VALUES = "values";

	// SqlPojoUpdateParser
	public static final String SET = "set";

	// where clauses
	public static final String WHERE = "where";
	public static final String AND = "and";
	
	// ANSI JOIN Statements
	public static final String ON = "on";
	public static final String JOIN = "JOIN";
	public static final String INNER_JOIN = "INNER " + JOIN;
	
	// misc
	public static final String AS = "as";
	public static final String FROM = "from";
	public static final String NEXTVAL = "nextval";
	public static final String NULL = "null";
	public static final String SYSDATE = "sysdate";
	public static final String DUAL = "DUAL";

	// SQL functions
	public static final String TO_DATE = "to_date";
	public static final String TO_CHAR = "to_char";

	// Comparators
	public static final String COMPARATOR_EQUAL = "=";
	public static final String COMPARATOR_UNEQUAL = "<>";
	public static final String COMPARATOR_LARGER = ">";
	public static final String COMPARATOR_LOWER = "<";
	public static final String COMPARATOR_LARGER_EQUAL = ">=";
	public static final String COMPARATOR_LOWER_EQUAL = "<=";
	public static final String COMPARATOR_NOT_NULL = "IS NOT NULL";
	public static final String COMPARATOR_IS_NULL = "IS NULL";

}
