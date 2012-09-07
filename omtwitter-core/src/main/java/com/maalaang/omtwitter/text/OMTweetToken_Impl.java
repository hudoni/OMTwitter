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
public class OMTweetToken_Impl implements OMTweetToken {
	
	public final static String REGEX_DUPLICATE = "(.)\\1{2,}";
	
	private int type = TOKEN_TYPE_NORMAL;
	private int begin = 0;
	private int end = 0;
	private String text = null;
	
	private static Pattern patternDuplicate = Pattern.compile(REGEX_DUPLICATE);
	
	public OMTweetToken_Impl(int type, int begin, int end, String text) {
		this.type = type;
		this.begin = begin;
		this.end = end;
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getText() {
		return text;
	}

	public String getNormalizedText() {
		switch (type) {
		case TOKEN_TYPE_USER:
			return NORMALIZED_TEXT_USER;
		case TOKEN_TYPE_URL:
			return NORMALIZED_TEXT_URL;
		case TOKEN_TYPE_HASHTAG:
			return text.toLowerCase(); 
		case TOKEN_TYPE_NORMAL:
			String normText = text.toLowerCase();
			Matcher matcherDuplicate = patternDuplicate.matcher(normText);
			
			while (matcherDuplicate.find()) {
				String duplicatePart = matcherDuplicate.group();
				normText = normText.replace(duplicatePart, duplicatePart.substring(0, 2));
				matcherDuplicate = patternDuplicate.matcher(normText);
			}
			
			return normText;
		default:
			throw new IllegalStateException();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OMTweetTokenDefault [type=" + type + ", begin=" + begin
				+ ", end=" + end + ", text=" + text + ", normalizedText=" + getNormalizedText() + "]";
	}

}
