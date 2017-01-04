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
package com.iksgmbh.sql.pojomemodb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Allows to configure some behaviour of the memory database.
 * 
 * @author Reik Oberrath
 */
public class DbProperties {

    public static String PROPERTIES_FILE_NAME = "SqlPojoMemoDb.properties";

	public static Boolean REPLACE_EMPTY_STRING_BY_NULL = true;
	public static Boolean USE_ORACLE_DUAL_TABLE = true;
	public static Boolean SUPPORT_MYSQL = true;

    /**
     * Loads properties from file and overwrites default if properties are found in file.
     * @return number of properties loaded from file.
     */
    public static int load()
    {
        final File propertiesFile = new File(PROPERTIES_FILE_NAME);
        try {
            System.out.println(propertiesFile.getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (propertiesFile.exists())
            {
                int counter = 0;
                Properties properties = new Properties();
                FileReader fileReader = new FileReader(propertiesFile);
                properties.load(fileReader);

                String value = properties.getProperty("REPLACE_EMPTY_STRING_BY_NULL");
                if ( value != null) {
                    REPLACE_EMPTY_STRING_BY_NULL = value.equalsIgnoreCase("true");
                    counter++;
                }

                value = properties.getProperty("USE_ORACLE_DUAL_TABLE");
                if ( value != null) {
                    USE_ORACLE_DUAL_TABLE = value.equalsIgnoreCase("true");
                    counter++;
                }

                value = properties.getProperty("SUPPORT_MYSQL");
                if ( value != null) {
                    SUPPORT_MYSQL = value.equalsIgnoreCase("true");
                    counter++;
                }

                fileReader.close();
                return counter;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}