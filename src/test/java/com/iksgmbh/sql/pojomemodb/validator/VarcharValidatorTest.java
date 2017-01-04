package com.iksgmbh.sql.pojomemodb.validator;

import com.iksgmbh.sql.pojomemodb.validator.type.VarcharTypeValidator;
import org.junit.Test;

import java.sql.SQLDataException;

import static org.junit.Assert.*;

/**
 * Created by XI325560 on 08.12.2016.
 */
public class VarcharValidatorTest {

    @Test
    public void comparesTwoStrings() throws SQLDataException
    {
        final String valueAB = new String("AB");
        final String valueBC = new String("BC");
        final String string3 = new String("BC");

        VarcharTypeValidator validator = new VarcharTypeValidator("VARCHAR(10)");

        assertTrue(validator.isValue1SmallerThanValue2(valueAB, valueBC));
        assertFalse(validator.isValue1SmallerThanValue2(valueBC, valueAB));
        assertNull(validator.isValue1SmallerThanValue2(string3, valueBC));
    }
}
