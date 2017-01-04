package com.iksgmbh.sql.pojomemodb;

import com.iksgmbh.sql.pojomemodb.dataobjects.temporal.SelectionTable;
import com.iksgmbh.sql.pojomemodb.utils.FileUtil;

import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Checks different behavior of the DB for different values of the properties.
 *
 * @author Reik Oberrath
 */
public class DbPropertiesTest
{
    @Test
    public void usesOracleDualTableOnlyIfPropertyIsTrue() throws SQLException, NoSuchFieldException, IllegalAccessException
    {
        // test true
        SqlPojoMemoDB.reset();
        DbProperties.USE_ORACLE_DUAL_TABLE = true;
		List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute("select * from DUAL")).getDataRows();
        assertEquals("result size", 0, result.size());

        // test false
        DbProperties.USE_ORACLE_DUAL_TABLE = false;
        SqlPojoMemoDB.reset();

        try {
            // act
            SqlPojoMemoDB.execute("select * from DUAL");
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            // assert
            assertEquals("Error message", "Unknown table <DUAL>!", e.getMessage());
        }

    }

	@Test
    public void replacedEmptyStringByNullOnlyIfPropertyIsTrue() throws SQLException, NoSuchFieldException, IllegalAccessException
    {
        final String createStatement =  "create table TEST_TABLE_NAME (ID VARCHAR(10))";
        final String insertStatement =  "insert into TEST_TABLE_NAME (ID) VALUES ('')";
        final String selectStatement =  "select * from TEST_TABLE_NAME";

        // test true
        SqlPojoMemoDB.reset();
        DbProperties.REPLACE_EMPTY_STRING_BY_NULL = true;
        SqlPojoMemoDB.execute(createStatement);
        SqlPojoMemoDB.execute(insertStatement);
        List<Object[]> result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
        assertNull(result.get(0)[0]);

        // test false
        SqlPojoMemoDB.reset();
        DbProperties.REPLACE_EMPTY_STRING_BY_NULL = false;
        SqlPojoMemoDB.execute(createStatement);
        SqlPojoMemoDB.execute(insertStatement);
        result = ((SelectionTable) SqlPojoMemoDB.execute(selectStatement)).getDataRows();
        assertEquals("result", "", result.get(0)[0]);
    }

    @Test
    public void usesMySqlTypesIfMySqlIsSupported() throws SQLException, NoSuchFieldException, IllegalAccessException
    {
        final String createStatement =  "create table TEST_TABLE_NAME (ID INT)";

        // test true
        SqlPojoMemoDB.reset();
        DbProperties.SUPPORT_MYSQL = true;

        final String result = (String) SqlPojoMemoDB.execute(createStatement);
        assertEquals("result", "Table TEST_TABLE_NAME has been created.", result);

        // test false
        SqlPojoMemoDB.reset();
        DbProperties.SUPPORT_MYSQL = false;
        try {
            SqlPojoMemoDB.execute(createStatement);
            fail("Expected exception was not thrown!");
        } catch (Exception e) {
            assertEquals("Error message", "Unknown column type 'INT'. Concerned column: 'ID'.", e.getMessage());
        }
    }

    @Test
    public void loadsPropertiesFromPropertiesFile() throws Exception
    {
        // arrange
        DbProperties.SUPPORT_MYSQL = true;
        DbProperties.USE_ORACLE_DUAL_TABLE = true;
        DbProperties.REPLACE_EMPTY_STRING_BY_NULL = true;

        final File file = new File("./"+ DbProperties.PROPERTIES_FILE_NAME);

        FileUtil.createNewFileWithContent(file, "SUPPORT_MYSQL=false" + System.getProperty("line.separator") +
                                                "USE_ORACLE_DUAL_TABLE=false" + System.getProperty("line.separator") +
                                                "REPLACE_EMPTY_STRING_BY_NULL=false");

        // act
        int result = DbProperties.load();

        // cleanup
        file.delete();

        // assert
        assertFalse(DbProperties.SUPPORT_MYSQL);
        assertFalse(DbProperties.USE_ORACLE_DUAL_TABLE);
        assertFalse(DbProperties.REPLACE_EMPTY_STRING_BY_NULL);
        assertEquals("number of properties", 3, result);
    }

}
