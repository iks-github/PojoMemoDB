package com.iksgmbh.sql.pojomemodb.validator.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLDataException;

import org.junit.Test;

import com.iksgmbh.sql.pojomemodb.validator.type.BooleanTypeValidator;

/**
 * Created by XI325560 on 08.12.2016.
 */
public class BooleanTypeValidatorTest {

    @Test
    public void comparesTwoBooleans() throws SQLDataException
    {
        final Boolean b1 = Boolean.TRUE;
        final Boolean b2 = Boolean.TRUE;
        final Boolean b3 = Boolean.FALSE;

        BooleanTypeValidator validator = new BooleanTypeValidator();

        assertNull(validator.isValue1SmallerThanValue2(b1, b2));
        assertFalse(validator.isValue1SmallerThanValue2(b2, b3));
        assertTrue(validator.isValue1SmallerThanValue2(b3, b1));
    }
}
