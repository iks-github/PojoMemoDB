package com.iksgmbh.sql.pojomemodb.validator;

import com.iksgmbh.sql.pojomemodb.dataobjects.persistent.Column;

import java.sql.SQLDataException;
import java.util.*;

/**
 * Validates contraints defined for a table or column
 *
 * @author Reik Oberrath
 */
public class ConstraintValidator
{
    public static List<SQLDataException> validateNullConstraints(final Object[] newDataRow,
                                                                 final List<String> sortedColumnNames,
                                                                 final HashMap<String, Column> columnMap)
    {
        final List<SQLDataException> toReturn = new ArrayList<SQLDataException>();

        for (String columnName : sortedColumnNames)
        {
            final Column column = columnMap.get(columnName);
            boolean nullable = column.isNullable();
            int index = column.getOrderNumber() - 1;

            if (newDataRow[index] == null && column.getDefaultValue() != null) {
                try {
                    newDataRow[index] = column.convertIntoColumnType( column.getDefaultValue() );
                } catch (SQLDataException e) {
                    toReturn.add(e);
                }
            }

            if (! nullable && newDataRow[index] == null) {
                toReturn.add(new SQLDataException("Null value not allowed for column '" + columnName + "'."));
            }
        }

        return toReturn;
    }


    public static Collection<? extends SQLDataException> validatePrimaryKeyConstraints(final List<Object[]> dataRows,
                                                                                       final Object[] newDataRow,
                                                                                       final List<String> sortedColumnNames,
                                                                                       final HashMap<String, Column> columnMap)
    {
        final List<SQLDataException> toReturn = new ArrayList<SQLDataException>();

        for (String columnName : sortedColumnNames)
        {
            final Column column = columnMap.get(columnName);
            if ( ! column.areDublicatesAllowed() )
            {
                Object newValue = newDataRow[column.getIndexInTable()];

                for (Object[] oldDataRow : dataRows)
                {
                    Object oldValue = oldDataRow[column.getIndexInTable()];

                    if (newValue != null && oldValue != null && newValue.equals(oldValue)) {
                        toReturn.add(new SQLDataException("Primary Key Constraint violated in column '" + columnName + "' with value '" + newValue + "'."));
                    }
                }
            }
        }

        return toReturn;
    }
}
