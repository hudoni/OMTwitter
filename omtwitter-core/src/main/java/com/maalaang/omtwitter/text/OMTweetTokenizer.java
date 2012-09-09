/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sangwon Park
 *
 */
public class OMTweetTokenizer {
	private final static String REGEX_TOKEN = "#\\w+|@\\w+|https?://\\S+|[\\w-'$%]+|[\\p{Punct}oOtT&&[^#@]]+";
	private final static Pattern patternToken = Pattern.compile(REGEX_TOKEN);
	private ArrayList<OMTweetToken> list = null;
	
	public OMTweetTokenizer() {
		list = new ArrayList<OMTweetToken>(128);
	}
	
	public OMTweetToken[] tokenize(String tweet) {
		
		Matcher matcher = patternToken.matcher(tweet);
		
		while (matcher.find()) {
			String token = matcher.group();
			int type = 0;
			if (token.charAt(0) == '#') {
				type = OMTweetToken.TOKEN_TYPE_HASHTAG;
			} else if (token.charAt(0) == '@') {
				type = OMTweetToken.TOKEN_TYPE_USER;
			} else if (token.startsWith("http")) {
				type = OMTweetToken.TOKEN_TYPE_URL;
			} else {
				type = OMTweetToken.TOKEN_TYPE_NORMAL;
			}
			list.add(new OMTweetToken_Impl(type, matcher.start(), matcher.end(), token));
		}
		
		OMTweetToken[] tokens = list.toArray(new OMTweetToken[list.size()]);
		list.clear();
		
		return tokens;
	}
}
