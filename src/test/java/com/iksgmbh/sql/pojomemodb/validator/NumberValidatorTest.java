package com.iksgmbh.sql.pojomemodb.validator;

import com.iksgmbh.sql.pojomemodb.validator.type.NumberTypeValidator;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLDataException;

import static org.junit.Assert.*;

/**
 * Created by XI325560 on 08.12.2016.
 */
public class NumberValidatorTest {

    @Test
    public void comparesTwoNumbers() throws SQLDataException
    {
        final BigDecimal value10 = new BigDecimal("10");
        final BigDecimal value20 = new BigDecimal("20");
        final BigDecimal number3 = new BigDecimal("20");

        NumberTypeValidator validator = new NumberTypeValidator("NUMBER");

        assertTrue(validator.isValue1SmallerThanValue2(value10, value20));
        assertFalse(validator.isValue1SmallerThanValue2(value20, value10));
        assertNull(validator.isValue1SmallerThanValue2(number3, value20));
    }
}
