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
package com.iksgmbh.sql.pojomemodb.utils;

import com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;
import org.junit.Test;

import static com.iksgmbh.sql.pojomemodb.utils.StringParseUtil.*;
import static org.junit.Assert.assertEquals;

public class StringParseUtilTest
{
    @Test
    public void test() throws Exception {
        String input1 = "NUMBER(10,0) default 1, Name VARCHAR(10)')";
        String input2 = "NUMBER(10,0) default 1)";

        InterimParseResult parseResult1 = StringParseUtil.parseNextValue(input1, SPACE, COMMA, CLOSING_PARENTHESIS + CLOSING_PARENTHESIS);
        InterimParseResult parseResult2 = StringParseUtil.parseNextValue(input2, SPACE, COMMA, CLOSING_PARENTHESIS + CLOSING_PARENTHESIS);

        assertEquals("default 1, Name VARCHAR(10)')", parseResult1.unparsedRest);
        assertEquals("default 1)", parseResult2.unparsedRest);
    }

    @Test
	public void removesSurroundingPrefixAndPostFixFromString() throws Exception {
		assertEquals("abc", StringParseUtil.removeSurroundingPrefixAndPostFix("'abc'", "'", "'"));
	}
	
	@Test
	public void parsesAPartFromALargerString() throws Exception 
	{
		// arrange
		final String delimiter1 = ";";
		final String delimiter2 = ",";
		
		// act
		final InterimParseResult interimParseResult1 = StringParseUtil.parseNextValue("a, b; c, d", delimiter1, delimiter2);
		final InterimParseResult interimParseResult2 = StringParseUtil.parseNextValue(interimParseResult1.unparsedRest, delimiter1, delimiter2);
		final InterimParseResult interimParseResult3 = StringParseUtil.parseNextValue(interimParseResult2.unparsedRest, delimiter1, delimiter2);
		final InterimParseResult interimParseResult4 = StringParseUtil.parseNextValue(interimParseResult3.unparsedRest, delimiter1, delimiter2);
		final InterimParseResult interimParseResult5 = StringParseUtil.parseNextValue(interimParseResult4.unparsedRest, delimiter1, delimiter2);
		
		//assert
		assertEquals("delimiter", delimiter2, interimParseResult1.delimiter);
		assertEquals("parsedValue", "a", interimParseResult1.parsedValue);
		assertEquals("unparsedRest", "b; c, d", interimParseResult1.unparsedRest);
		
		assertEquals("delimiter", delimiter1, interimParseResult2.delimiter);
		assertEquals("parsedValue", "b", interimParseResult2.parsedValue);
		assertEquals("unparsedRest", "c, d", interimParseResult2.unparsedRest);

		assertEquals("delimiter", delimiter2, interimParseResult3.delimiter);
		assertEquals("parsedValue", "c", interimParseResult3.parsedValue);
		assertEquals("unparsedRest", "d", interimParseResult3.unparsedRest);

		assertEquals("delimiter", null, interimParseResult4.delimiter);
		assertEquals("parsedValue", "d", interimParseResult4.parsedValue);
		assertEquals("unparsedRest", "", interimParseResult4.unparsedRest);
		
		assertEquals("delimiter", null, interimParseResult5.delimiter);
		assertEquals("parsedValue", "", interimParseResult5.parsedValue);
		assertEquals("unparsedRest", "", interimParseResult5.unparsedRest);
	}
	
	@Test
	public void parsesAPartFromALargerString_ignoringTextBetweenApostrophies() throws Exception 
	{
		// arrange
		final char delimiter = ',';
		final String value = "a; 'b, c'";
		final String unparsedRest = "d";
		final String input = value + delimiter + unparsedRest;
		final char apostrophy = '\'';
		
		// act
		InterimParseResult interimParseResult = StringParseUtil.parseNextValue(input, apostrophy, apostrophy, delimiter);
		
		// assert
		assertEquals("delimiter", ",", interimParseResult.delimiter);
		assertEquals("parsedValue", "a; 'b, c'", interimParseResult.parsedValue);
		assertEquals("unparsedRest", "d", interimParseResult.unparsedRest);
	}
	
	@Test
	public void parsesAPartFromALargerString_usingLastOccurrenceOfDelimeter() throws Exception 
	{
		// arrange
		final String delimiter = ",";
		final String value = "a, b, c";
		final String unparsedRest = "d";
		final String input = value + delimiter + unparsedRest;
		
		// act
		final InterimParseResult interimParseResult = StringParseUtil.parseNextValueByLastOccurrence(input, delimiter);
		
		// assert
		assertEquals("delimiter", delimiter, interimParseResult.delimiter);
		assertEquals("parsedValue", value, interimParseResult.parsedValue);
		assertEquals("unparsedRest", unparsedRest, interimParseResult.unparsedRest);
	}

    @Test
    public void countsOccurrencesOfChars() throws Exception
    {
        // act
        final int result1 = StringParseUtil.countOccurrencesOf("abABAab", 'A');
        final int result2 = StringParseUtil.countOccurrencesOf("abABAab", 'c');

        // assert
        assertEquals("number of occurrences", 2, result1);
        assertEquals("number of occurrences", 0, result2);

    }

}