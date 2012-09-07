/**
 * 
 */
package com.maalaang.omtwitter.text;

/**
 * @author Sangwon Park
 *
 */
public class InfoboxValueTokenizer {
	public static final String REGEX_WORD_SPLIT = "[\\s\\p{Punct}&&[^\\-]]+";
	public static final String REGEX_VALUE_SPLIT = "[,;]+";
	
	public static String[] tokenizeToWord(String s) {
		return s.split(REGEX_WORD_SPLIT);
	}
	
	public static String[] tokenizeToValues(String s) {
		return s.replaceAll("\\s+", " ").split(REGEX_VALUE_SPLIT);
	}
}
