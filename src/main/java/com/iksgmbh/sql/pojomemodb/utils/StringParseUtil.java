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

import org.apache.commons.lang3.StringUtils;

public class StringParseUtil 
{
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String EQUALSIGN = "=";
	public static final String APOSTROPY = "'";
	
	public static String removeSurroundingPrefixAndPostFix(final String input, 
			                                               final String prefix, 
			                                               final String postfix) 
	{
		if (input.startsWith(prefix))
		{
			if (input.endsWith(postfix)) {
				return input.substring(1, input.length()-1);
			} else {
				throw new IllegalArgumentException("Missing postfix: " + postfix);
			}
		}
		else
		{
			if (input.endsWith(postfix)) {
				throw new IllegalArgumentException("Missing prefix: " + prefix);
			}
		}
		
		return input;
	}

	/**
	 * Cuts off a piece of the input string from the beginning.
	 * The position where to cut is defined by delimiter.
	 * Delimiter is ignored if it es located between ignoreStartChar and ignoreEndChar. 
	 * 
	 * @param input
	 * @param ignoreStartChar
	 * @param ignoreEndChar
	 * @param delimiter
	 * @return
	 */
	public static InterimParseResult parseNextValue(final String input, 
			                                        final char ignoreStartChar,
			                                        final char ignoreEndChar,
			                                        final char delimiter)
	
	{
		final char[] charArray = input.toCharArray();
		boolean ignoreMode = false;
		for (int pos = 0; pos < charArray.length; pos++) 
		{
			char c = charArray[pos];
			
			if (c == delimiter) {
				if ( ! ignoreMode)  {
					return new InterimParseResult(input.substring(0,  pos).trim(), input.substring(pos+ 1).trim(), "" + delimiter);
				}
			}
			
			if ( ! ignoreMode && c == ignoreStartChar)  {
				ignoreMode = true;
			} else if (c == ignoreEndChar)  {
				ignoreMode = false;
			}
		}
		
		return new InterimParseResult(input, "", null);
	}
	

	/**
	 * Cuts off a piece of the input string from the beginning.
	 * The position where to cut is defined by the given delimiters.
	 * If more than delimiter are found, the next one 
	 * (i.e. the one with the smallest position) is used to cut.
	 * 
	 * @param input String
	 * @param delimiterArray collection of possible delimiters
	 * @return ParseResult
	 */
	public static InterimParseResult parseNextValue(final String input,
			                                        final String... delimiterArray) 
	{
		if (delimiterArray == null || delimiterArray.length == 0)  {
			throw new IllegalArgumentException("No delimiter defined!");
		}

		final CutPositionResult result = determineCutPosition(input, delimiterArray);
		
		if (result.cutPosition == -1) {
			return new InterimParseResult(input, "", null);
		}
		
		final String value = input.substring(0, result.cutPosition).trim();
		final String unparsedRest = input.substring(result.cutPosition + result.delimiter.length()).trim();
		
		return new InterimParseResult(value, unparsedRest, result.delimiter);
	}	
	
	/**
	 * Cuts off a piece of the input string from the beginning.
	 * The position where to cut is defined by the last occurence of the given delimiter.
	 * 
	 * @param input String
	 * @param delimiter
	 * @return ParseResult
	 */
	public static InterimParseResult parseNextValueByLastOccurrence(final String input,
			                                                        final String delimiter) 
	{
		if (StringUtils.isEmpty(delimiter))  {
			throw new IllegalArgumentException("No valid delimiter defined!");
		}
		
		if (StringUtils.isEmpty(input))  {
			return new InterimParseResult(input, "", null);
		}

		int cutPosition = input.toLowerCase().lastIndexOf(delimiter.toLowerCase());
		
		if (cutPosition == -1) {
			return new InterimParseResult(input, "", null);
		}
		
		final String value = input.substring(0, cutPosition).trim();
		final String unparsedRest = input.substring(cutPosition + delimiter.length() ).trim();
		
		return new InterimParseResult(value, unparsedRest, delimiter);
	}

	private static CutPositionResult determineCutPosition(final String input, 
			                                              final String[] delimiterArray) 
	{
		String delimiter = "";
		int cutPosition = -1;
		
		for (String delimiterCandidate : delimiterArray) 
		{
			int pos = input.toLowerCase().indexOf(delimiterCandidate.toLowerCase());
			
			if (COMMA.equals(delimiterCandidate)) {
				pos = getNextValidCommaPosition(input);
			}
			if (SPACE.equals(delimiterCandidate)) {
				pos = getNextValidSpacePosition(input);
			}
			if (CLOSING_PARENTHESIS.equals(delimiterCandidate)) {
				pos = getNextValidClosingParenthesisPosition(input);
			}
			
			if (pos > -1)
			{
				if (cutPosition == -1 || pos < cutPosition)
				{
					delimiter = delimiterCandidate;
					cutPosition = pos;
				}
			}
		}
		
		return new CutPositionResult(delimiter, cutPosition);
	}

	private static int getNextValidClosingParenthesisPosition(String input) 
	{
		char[] charArray = input.toCharArray();
		int countApostrophies = 0;
		int pos = 0;
		
		for (char c : charArray) 
		{
			String ch = "" + c;
			if (ch.equals(APOSTROPY)) {
				countApostrophies++;
			}
			
			if (ch.equals(CLOSING_PARENTHESIS))  {
				if (countApostrophies%2 == 0)  {
					return pos;
				}
			}
			pos++;
		}
		
		return -1;
	}


	/**
	 * Finds the valid position to cut. Example:
	 * For input="Number(10,0)," the position of the second comma (12 not 9) is valid!
	 * 
	 * @param input
	 * @return next valid position of a comma to be used for cutting
	 */
	private static int getNextValidCommaPosition(final String input) 
	{
		if ( ! input.contains(COMMA) ) {
			return -1;
		}
		
		if (input.toUpperCase().startsWith("NUMBER")) 
		{
			int posOfClosingParenthesis = input.indexOf(CLOSING_PARENTHESIS);
			int posOfComma = input.indexOf(COMMA);
			
			if (posOfComma < posOfClosingParenthesis) {
				String inputPart = input.substring(posOfClosingParenthesis);
				posOfComma = inputPart.indexOf(COMMA);
				return posOfClosingParenthesis + posOfComma;
			}
		}
		
		return input.indexOf(COMMA);
	}

	/**
	 * Finds the valid position to cut. Example:
	 * For input="VARCHAR2(10 CHAR) not null enabled" the position of the second comma (17 not 11) is valid!
	 * 
	 * @param input
	 * @return next valid position of a space to be used for cutting
	 */
	private static int getNextValidSpacePosition(final String input) 
	{
		if ( ! input.contains(SPACE) ) {
			return -1;
		}
		
		if (input.toUpperCase().startsWith("VARCHAR")) 
		{
			int posOfOpeningParenthesis = input.indexOf(OPENING_PARENTHESIS);
			int posOfComma = input.indexOf(SPACE);
			
			if (posOfComma > posOfOpeningParenthesis) {
				int posOfClosingParenthesis = input.indexOf(CLOSING_PARENTHESIS);
				String inputPart = input.substring(posOfClosingParenthesis);
				posOfComma = inputPart.indexOf(SPACE);
				return posOfClosingParenthesis + posOfComma;
			}
		}
		return input.indexOf(SPACE);
	}

	public static class InterimParseResult 
	{
		@Override
		public String toString() {
			return "InterimParseResult [parsedValue=" + parsedValue + ", unparsedRest=" + unparsedRest + ", delimiter="
					+ delimiter + "]";
		}

		public String parsedValue;
		public String unparsedRest;
		public String delimiter;
		
		protected InterimParseResult(String lastParsedValue, String unparsedRest, String delimiter) {
			this.parsedValue = lastParsedValue;
			this.unparsedRest = unparsedRest;
			this.delimiter = delimiter;
		}
	}
	
	static class CutPositionResult 
	{
		String delimiter;
		int cutPosition;
		
		public CutPositionResult(String delimiter, int cutPosition) {
			this.cutPosition = cutPosition;
			this.delimiter = delimiter;
		}
	}	
}