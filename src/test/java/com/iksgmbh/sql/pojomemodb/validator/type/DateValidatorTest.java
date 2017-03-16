package com.iksgmbh.sql.pojomemodb.validator.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLDataException;
import java.util.Date;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.validator.type.DateTypeValidator;

/**
 * Created by XI325560 on 08.12.2016.
 */
public class DateValidatorTest {

    @Test
    public void comparesTwoDates() throws SQLDataException
    {
        final Date d1 = new Date();
        final Date d2 = new Date(d1.getTime() + 1000*60*60);  // plus one hour
        final Date d3 = new Date(d2.getTime());

        DateTypeValidator validator = new DateTypeValidator();

        assertTrue(validator.isValue1SmallerThanValue2(d1, d2));
        assertFalse(validator.isValue1SmallerThanValue2(d2, d1));
        assertNull(validator.isValue1SmallerThanValue2(d3, d2));
    }
}
