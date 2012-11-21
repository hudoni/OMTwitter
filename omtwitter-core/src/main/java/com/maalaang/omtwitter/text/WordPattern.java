/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sangwon Park
 *
 */
public class WordPattern {
	private static String patternPrefix = "$$_";
	
	private static Pattern[] patterns = new Pattern[] { Pattern.compile("^[0-9]+(\\.[0-9]+)*"), Pattern.compile("^(ix|iv|vi{0,3}|i{2,3})") };
	
	private static String[] patternNames = new String[] { patternPrefix + "NUMBER" , patternPrefix + "NUMBER_ROME" };	
	
	private static Pattern patternNumberRome = Pattern.compile("(ix|iv|vi{0,3}|i{2,3})");
	
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
	
	public static String replaceRomanToArabic(String word) {
		Matcher m = patternNumberRome.matcher(word);
		String res = word;
		while (m.find()) {
			String rome = m.group();
			int num = decodeRoman(rome);
			res = res.replaceFirst(rome, String.valueOf(num));
		}
		return res;
	}
	
	private static int decodeRoman(char letter) {
		switch(letter) {
			case 'm': return 1000;
			case 'd': return 500;
			case 'c': return 100;
			case 'l': return 50;
			case 'x': return 10;
			case 'v': return 5;
			case 'i': return 1;
			default: return 0;
		}
	}
	
	private static int decodeRoman(String roman) {
		int result = 0;
		roman = roman.toLowerCase();
		for(int i = 0;i < roman.length() - 1;i++) {
			if (decodeRoman(roman.charAt(i)) < decodeRoman(roman.charAt(i+1))) {
				result -= decodeRoman(roman.charAt(i));
			} else {
				result += decodeRoman(roman.charAt(i));
			}
		}
		result += decodeRoman(roman.charAt(roman.length()-1));
		return result;
	}
}
