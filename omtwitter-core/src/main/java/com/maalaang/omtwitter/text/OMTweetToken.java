/**
 * 
 */
package com.maalaang.omtwitter.text;

/**
 * @author Sangwon Park
 *
 */
public interface OMTweetToken {
	
	public final static int TOKEN_TYPE_NORMAL = 0;
	public final static int TOKEN_TYPE_USER = 1;
	public final static int TOKEN_TYPE_HASHTAG = 2;
	public final static int TOKEN_TYPE_URL = 3;
	
	public final static String NORMALIZED_TEXT_USER = "USER";
	public final static String NORMALIZED_TEXT_URL = "URL";
	
	public int getType();

	public int getBegin();
	
	public int getEnd();
	
	public String getText();
	
	public String getNormalizedText();
}
