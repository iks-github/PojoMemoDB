package com.iksgmbh.sql.pojomemodb.validator;

import com.iksgmbh.sql.pojomemodb.validator.type.DateTypeValidator;
import org.joda.time.DateTime;
import org.junit.Test;

import java.sql.SQLDataException;

import static org.junit.Assert.*;

/**
 * Created by XI325560 on 08.12.2016.
 */
public class DateValidatorTest {

    @Test
    public void comparesTwoDates() throws SQLDataException
    {
        final DateTime dt1 = new DateTime();
        final DateTime dt2 = dt1.plusHours(10);
        final DateTime dt3 = new DateTime(dt2.getMillis());

        DateTypeValidator validator = new DateTypeValidator();

        assertTrue(validator.isValue1SmallerThanValue2(dt1, dt2));
        assertFalse(validator.isValue1SmallerThanValue2(dt2, dt1));
        assertNull(validator.isValue1SmallerThanValue2(dt3, dt2));
    }
}
