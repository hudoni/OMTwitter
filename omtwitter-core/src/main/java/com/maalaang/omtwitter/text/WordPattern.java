/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.regex.Pattern;

/**
 * @author Sangwon Park
 *
 */
public class WordPattern {
	private static String patternPrefix = "$$_";
	
	private static Pattern[] patterns = new Pattern[] { Pattern.compile("^[0-9]+(\\.[0-9]+)*"), Pattern.compile("^(ix|iv|v?i{0,3}|i{2,3})") };
	
	private static String[] patternNames = new String[] { patternPrefix + "NUMBER" , patternPrefix + "NUMBER_ROME" };	
	
	public static String normalize(String value) {
		value = value.toLowerCase();
		int i = 0;
		for (Pattern p : patterns) {
			if (p.matcher(value).matches()) {
				return patternNames[i];
			}
			i++;
		}
		return value;
	}
	
	public static String[] normalize(String[] values) {
		for (int i = 0; i < values.length; i++) {
			values[i] = normalize(values[i]);
		}
		return values;
	}
	
	public static boolean isPatternName(String word) {
		return word.startsWith(patternPrefix);
	}
	
}
