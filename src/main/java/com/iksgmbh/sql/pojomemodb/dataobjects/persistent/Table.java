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
package com.iksgmbh.sql.pojomemodb.dataobjects.persistent;

import static com.iksgmbh.sql.pojomemodb.SQLKeyWords.TO_CHAR;
import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.iksgmbh.sql.pojomemodb.SqlPojoMemoDB;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.ColumnData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.data.TableData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.metadata.TableMetaData;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.ColumnStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.interfaces.statistics.TableStatistics;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.ApartValue;
import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.WhereCondition;
import com.iksgmbh.sql.pojomemodb.dataobjects.validator.Validator.ValidatorType;
import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil;

/**
 * Table implementation of the PojoMemoryDB. 
 * Its public methods belong to the three interfaces:
 * 
 * 1. TableStatistics (getter methods visible from outside the DB)
 * 2. TableMetaData   (methods to manage the table structure)
 * 3. TableData       (methods to manage the table content)
 * 
 * @author Reik Oberrath
 */
public class Table implements TableStatistics, TableMetaData, TableData
{
	protected final static Comparator<Column> COLUMN_SORT_COMPARATOR = createColumnSortComparator();
	
	protected String tableName;
	protected HashMap<String, Column> columnMap = new HashMap<String, Column>();
	protected List<String> sortedColumnNames = new ArrayList<String>();  // sorted by Column.orderNumber
	protected List<Object[]> dataRows = new ArrayList<Object[]>();  // objectArray is sorted by Column.orderNumber

	public Table(String tableName) {
		this.tableName = tableName.toUpperCase();
	}

	// #############################################################################################
	//                  P U B L I C    N O N - I N T E R F A C E    M E T H O D S
	// #############################################################################################
	
	public Column getColumn(String columnName) throws SQLDataException 
	{
		final Column column = columnMap.get(columnName.toUpperCase());
		
		if (column == null) {
			throw new SQLDataException("Unknown column: " + columnName);
		}
		
		return column;
	}

	public List<Object[]> getDataRows() {
		return dataRows;
	}

	public void setDataRows(List<Object[]> dataRows) {
		this.dataRows = dataRows;
	}
	
	// #########################################################################################
	//                       S T A T I S T I C S   M E T H O D S
	// #########################################################################################

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public int getNumberOfRows() {
		return dataRows.size();
	}
	
	@Override
	public int getNumberOfColumns() {
		return columnMap.size();
	}
	
	@Override
	public List<String> getNamesOfColumns() {
		return sortedColumnNames;
	}
	
	@Override
	public String getTypeOfColumn(final String columnName) throws SQLDataException {
		return getColumn(columnName).getColumnType();
	}
	
	@Override
	public boolean  isColumnNullable(final String columnName) throws SQLDataException 
	{
		final ColumnStatistics columnStatistics = columnMap.get(columnName);
		
		if (columnStatistics == null) {
			throw new SQLDataException("Unknown column: " + columnName);
		}
		
		return columnStatistics.isNullable();
	}


	// #########################################################################################
	//                         M E T A D A T A    M E T H O D S
	// #########################################################################################
	
	
	@Override
	public void createNewColumn(final String columnName, 
			                    final String columnType, 
			                    final boolean nullable,        
			                    final SqlPojoMemoDB memoryDB) throws SQLDataException  
	{
		final String upperCaseColumnName = columnName.toUpperCase();
		final int orderNumber = getNumberOfColumns() + 1;
		final Column column = new Column(upperCaseColumnName, columnType, nullable, orderNumber, memoryDB);
		columnMap.put(upperCaseColumnName, column);
		sortedColumnNames.add(upperCaseColumnName);
	}
	
	// #########################################################################################
	//                            D A T A     M E T H O D S
	// #########################################################################################
	
	@Override
	public void insertDataRow(final List<ApartValue> values) throws SQLDataException 
	{
		final Object[] newDataRow = new Object[getNumberOfColumns()];
		final List<SQLDataException> exceptionList = new ArrayList<SQLDataException>();
		
		for (ApartValue apartValue : values) {
			
			try {
				integrateValue(apartValue, newDataRow);
			} catch (SQLDataException e) {
				exceptionList.add(e);
			}
		}

		checkForNullValues(newDataRow, exceptionList);

		if ( ! exceptionList.isEmpty() ) 
		{
			final StringBuilder  sb = new StringBuilder ();
			
			if (exceptionList.size() > 1)  {
				sb.append("Following " + exceptionList.size() + 
						  " validation problems occurred:").append(System.getProperty("line.separator"));
			}
			
			for (SQLDataException e : exceptionList) {
				sb.append(e.getMessage()).append(System.getProperty("line.separator"));
			}
			
			throw new SQLDataException(sb.toString());
		}
		

		dataRows.add(newDataRow);
	}

	@Override
	public List<Object[]> select(final List<String> selectedColumns, 
			                     final List<WhereCondition> whereConditions) throws SQLDataException 
	{
		final List<Object[]> tableData = createDataRowsClone();
		applySqlFunctions(tableData, selectedColumns);
		final List<Object[]> selectedDataRows = selectDataRows(whereConditions, tableData).selectedRows;
		return removeUnselectedColumns(selectedDataRows, selectedColumns);
	}

	@Override
	public int update(final List<ApartValue> newValues, 
			          final List<WhereCondition> whereConditions) throws SQLDataException 
	{
		final SelectionResult selectionResult = selectDataRows(whereConditions, dataRows);
		final List<Object[]> updatedRows = updateSelectedDataRows(selectionResult.selectedRows, newValues);
		return integrateDataRows(updatedRows, selectionResult.rowIndices);
	}


	@Override
	public int delete(final List<WhereCondition> whereConditions) throws SQLDataException {
		final SelectionResult selectionResult = selectDataRows(whereConditions, dataRows);
		return deleteDataRows(selectionResult.rowIndices);
	}
	
	// #############################################################################################
	//                           P R I V A T E   M E T H O D S
	// #############################################################################################
	
	/**
	 * Converts data values in those rows of the tableData where functions have to be applied and
	 * removes the called function from the column names in selectedColumns
	 * @param tableData 
	 * @param selectedColumns
	 * @throws SQLDataException
	 */
	private void applySqlFunctions(final List<Object[]> tableData, 
			                       final List<String> selectedColumns) throws SQLDataException 
	{
		
		for (int i = 0; i < selectedColumns.size(); i++) 
		{
			final String oldColumnName = selectedColumns.get(i);
			
			if (oldColumnName.startsWith(TO_CHAR))  
			{
				final String newColumnName = convertDateColumnInTableDataToString(tableData, oldColumnName);
				selectedColumns.set(i, newColumnName);
			} 
		}
	}

	/**
	 * Converts date values in a date column of the tableData.
	 * 
	 * @param tableData
	 * @param oldColumnName with function information
	 * @return newColumnName without function information
	 * @throws SQLDataException
	 */
	private String convertDateColumnInTableDataToString(final List<Object[]> tableData, 
			                                            final String oldColumnName) throws SQLDataException 
	{
		String tmp = oldColumnName.substring(TO_CHAR.length());
		tmp = StringParseUtil.removeSurroundingPrefixAndPostFix(tmp, "(", ")");
		String[] splitResult = tmp.split(",");
		final String newColumnName = splitResult[0].trim();
		String dateFormat = StringParseUtil.removeSurroundingPrefixAndPostFix(splitResult[1].trim(), "'", "'");
		final Column dateColumn = getColumn(newColumnName);
		
		convertDateColumnInTableDataToString(dateColumn, dateFormat, tableData);
		return newColumnName;
	}

	/**
	 * Converts date values in a date column of the tableData.
	 * 
	 * @param dateColumn
	 * @param dateFormat
	 * @param tableData
	 * @throws SQLDataException
	 */
	private void convertDateColumnInTableDataToString(final Column dateColumn, 
			                                          final String dateFormat,
			                                          final List<Object[]> tableData) throws SQLDataException 
	{
		final ValidatorType validationType = dateColumn.getValidationType();
		
		if (validationType != ValidatorType.DATE) {
			throw new SQLDataException("Function to_char only valid ");
		}
		
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.replace("mm", "MM").replace("mi", "mm").replace("hh24", "HH"));
		
		for (int row = 0; row < tableData.size(); row++) 
		{
			final Object[] columnData = tableData.get(row);
			Object value = columnData[dateColumn.getIndexInTable()];
			
			if (value != null) {
				final DateTime dt = (DateTime) value;
				final String dateAsString = sdf.format(dt.toDate());
				columnData[dateColumn.getIndexInTable()] = dateAsString;
			}
		}
	}
	
	private int deleteDataRows(final List<Integer> rowIndicesToDelete) 
	{
		final List<Object[]> newDataRows = new ArrayList<Object[]>();
		
		int index = -1;
		for (Object[] dataRow : dataRows) {
			index++;
			if ( ! rowIndicesToDelete.contains(index) )  {
				newDataRows.add(dataRow);
			}
		}
		
		dataRows = newDataRows;
		return rowIndicesToDelete.size();
	}

	private int integrateDataRows(final List<Object[]> updatedDataRows, 
			                      final List<Integer> rowIndices) 
	{
		if (updatedDataRows.size() != rowIndices.size()) {  // security check
			throw new RuntimeException("Data Integrity is violated!");  
		}
		
		
		for (int i = 0; i < rowIndices.size(); i++) 
		{
			int rowIndex = rowIndices.get(i);
			dataRows.set(rowIndex, updatedDataRows.get(i));
		}
		
		return updatedDataRows.size();
	}

	private List<Object[]> updateSelectedDataRows(final List<Object[]> selectedDataRows, 
			                                      final List<ApartValue> newValues) throws SQLDataException 
	{
		for (ApartValue apartValue : newValues) 
		{
			final Column column = getColumn(apartValue.getColumnName());
			final Object value = column.convertIntoColumnType(apartValue.getValueAsString());
			column.validate(value);
			for (Object[] dataRow : selectedDataRows) {
				dataRow[column.getOrderNumber()-1] = value;
			}
		}
		
		return selectedDataRows;
	}

	private void checkForNullValues(final Object[] newDataRow, 
			                        final List<SQLDataException> exceptionList)
	{
		final List<String> namesOfColumns = getNamesOfColumns();
		for (String columnName : namesOfColumns) 
		{
			Column column = columnMap.get(columnName);
			boolean nullable = column.isNullable();
			int index = column.getOrderNumber() - 1;
			
			if (! nullable && newDataRow[index] == null) {
				exceptionList.add(new SQLDataException("Null value not allowed for column '" + columnName + "'."));
			}
		}
	}
	
	private void integrateValue(final ApartValue apartValue, 
			                    final Object[] newDataRow) throws SQLDataException 
	{
		final ColumnData column = getColumn(apartValue.getColumnName()); 
		final Object value = column.convertIntoColumnType(apartValue.getValueAsString());
		column.validate(value);
		newDataRow[column.getOrderNumber() - 1] = value;
	}

	private List<Object[]> removeUnselectedColumns(final List<Object[]> selectDataRows, 
			                                       final List<String> selectedColumns) throws SQLDataException 
	{
		final List<Object[]> dataRowsWithSelectedColumns = new ArrayList<Object[]>();
		
		for (Object[] fullDataRow : selectDataRows)
		{
			Object[] dataRowWithReducedColumns = new Object[selectedColumns.size()];
			int indexInReducedDataRow = 0;
			
			for (String selectedColumnName : selectedColumns) 
			{
				Column column = getColumn(selectedColumnName);
				int indexInFullDataRow = column.getOrderNumber() - 1;
				dataRowWithReducedColumns[indexInReducedDataRow] = fullDataRow[indexInFullDataRow];
				indexInReducedDataRow++;
			}
			
			dataRowsWithSelectedColumns.add(dataRowWithReducedColumns);
		}
		
		
		return dataRowsWithSelectedColumns;
	}

	private SelectionResult selectDataRows(final List<WhereCondition> whereConditions,
			                               final List<Object[]> dataRows) throws SQLDataException 
	{
		final List<Object[]> selectedDataRows = new ArrayList<Object[]>();  // objectArray is sorted by Column.orderNumber
		
		if (whereConditions.size() == 0) {
			if (dataRows != null) selectedDataRows.addAll(dataRows);
			return new SelectionResult(selectedDataRows, createFullIndicesList());
		}
		
		final Integer[] matchedConditionCount = applyWhereConditions(whereConditions); 
		final List<Integer> indices = new ArrayList<Integer>();
		
		for (int dataRowIndex = 0; dataRowIndex < getNumberOfRows(); dataRowIndex++) 
		{
			int numberOfMatchedConditions = matchedConditionCount[dataRowIndex];
			if (numberOfMatchedConditions == whereConditions.size())  
			{
				indices.add(dataRowIndex);
				selectedDataRows.add(dataRows.get(dataRowIndex));
			}
		}

		return new SelectionResult(selectedDataRows, indices);
	}

	/**
	 * Applies all conditions to all data rows and returns rows that match all conditions.
	 * 
	 * @param whereConditions
	 * @return IntegerArray [number of rules that matched this data row] sorted by the index of data row in dataRows 
	 * @throws SQLDataException
	 */
	private Integer[] applyWhereConditions(final List<WhereCondition> whereConditions) throws SQLDataException 
	{
		final Integer[] matchedConditionCount = new Integer[dataRows.size()];
		
		for (int dataRowIndex = 0; dataRowIndex < getNumberOfRows(); dataRowIndex++) {
			matchedConditionCount[dataRowIndex] = 0;
		}
		
		for (WhereCondition condition : whereConditions) 
		{
			int dataRowIndex = 0;
			for (Object[] dataRow : dataRows) 
			{
				if ( isWhereConditionMatched(dataRow, condition) ) {
					int numberOfMatchedConditions = matchedConditionCount[dataRowIndex];
					matchedConditionCount[dataRowIndex] = ++numberOfMatchedConditions;
				}
				dataRowIndex++;
			}
		}
		return matchedConditionCount;
	}
	
	private List<Integer> createFullIndicesList() 
	{
		final List<Integer> toReturn = new ArrayList<Integer>();
		if (dataRows != null) {			
			for (int i = 0; i < dataRows.size(); i++) {
				toReturn.add(i);
			}
		}
		return toReturn;
	}
	

	private boolean isWhereConditionMatched(final Object[] dataRow, 
			                                final WhereCondition condition) throws SQLDataException 
	{
		final Column column = getColumn(condition.getColumnName());
		final Object valueToCheck = dataRow[column.getOrderNumber()-1];
		return column.isWhereConditionMatched(condition.getValueAsString(), condition.getComparator(), valueToCheck);
	}
		
	private static Comparator<Column> createColumnSortComparator() 
	{
		return new Comparator<Column>() 
		{
			@Override public int compare(Column c1, Column c2) {
				if (c1.getOrderNumber() > c2.getOrderNumber()) {
					return 1;
				}
				if (c1.getOrderNumber() < c2.getOrderNumber()) {
					return -1;
				}
				return 0;
			}
		};
	}
	
	List<Object[]> createDataRowsClone()
	{
		if (dataRows == null) {
			return null;
		}
		
		final List<Object[]> toReturn = new ArrayList<Object[]>();
		
		for (Object[] objects : dataRows) 
		{
			final Object[] clonedColumnData = new Object[objects.length];
			
			for (int i = 0; i < clonedColumnData.length; i++) 
			{
				if (objects[i] == null)  {
					clonedColumnData[i] = null;
				} else if (objects[i] instanceof String) {
					clonedColumnData[i] = new String((String) objects[i]);
				} else if (objects[i] instanceof DateTime) {
					clonedColumnData[i] = new DateTime(((DateTime) objects[i]).toDate());
				} else if (objects[i] instanceof BigDecimal) {
					clonedColumnData[i] = new BigDecimal(((BigDecimal) objects[i]).toPlainString());
				} else if (objects[i] instanceof Long) {
					clonedColumnData[i] = new Long(((Long) objects[i]).longValue());
				} else {
					throw new RuntimeException("Unknown data type: " + objects[i].getClass());
				}
			}
			
			toReturn.add(clonedColumnData);
		}
		
		return toReturn;
	}

	class SelectionResult 
	{
		List<Object[]> selectedRows;
		List<Integer> rowIndices;    // position in dataRows object
		
		public SelectionResult(List<Object[]> selectedRows, List<Integer> rowIndices) {
			this.selectedRows = selectedRows;
			this.rowIndices = rowIndices;
		}
		
	}

	public int removeAllContent() {
		final int toReturn = getNumberOfRows();
		dataRows = new ArrayList<Object[]>();
		return toReturn;
	}
}