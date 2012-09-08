/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sangwon Park
 *
 */
public class OMTweetTokenizer {
	public final static String REGEX_URL = "(https?|file|ftp)://[\\S]+";
	public final static String REGEX_USER = "@\\w+";
	public final static String REGEX_TOKEN = "#?[\\w-]+('t)?|[\\p{Punct}oT]+";
	
	private static Pattern patternUrl = Pattern.compile(REGEX_URL);
	private static Pattern patternUser = Pattern.compile(REGEX_USER);
	private static Pattern patternToken = Pattern.compile(REGEX_TOKEN);
	
	public static List<OMTweetToken> tokenize(String tweet) {
		
		LinkedList<OMTweetToken> list = new LinkedList<OMTweetToken>();
		
		int tweetLen = tweet.length();
		
		Matcher matcherUrl = patternUrl.matcher(tweet);
		Matcher matcherUser = patternUser.matcher(tweet);
		Matcher matcherToken = patternToken.matcher(tweet);
		
		boolean urlFound = matcherUrl.find();
		boolean userFound = matcherUser.find();
		boolean tokenFound = matcherToken.find();
		
		int idx = 0;
		int type = 0;
		Matcher matcher = null;
		
		while (idx < tweetLen && (urlFound || userFound || tokenFound)) {
			if (urlFound && matcherUrl.start() <= matcherToken.start()) {
				matcher = matcherUrl;
				type = OMTweetToken.TOKEN_TYPE_URL;
				
			} else if (userFound && matcherUser.start() <= matcherToken.start()) {
				matcher = matcherUser;
				type = OMTweetToken.TOKEN_TYPE_USER;
				
			} else {
				matcher = matcherToken;
				String token = matcher.group();
				if (token.charAt(0) == '#') {
					type = OMTweetToken.TOKEN_TYPE_HASHTAG;
				} else {
					type = OMTweetToken.TOKEN_TYPE_NORMAL;
				}
			}
			
			list.add(new OMTweetToken_Impl(type, matcher.start(), matcher.end(), matcher.group()));
			idx = matcher.end();
			
			if (urlFound) {
				urlFound = matcherUrl.find(idx);
			}
			if (userFound) {
				userFound = matcherUser.find(idx);
			}
			if (tokenFound) {
				tokenFound = matcherToken.find(idx);
			}
		}	
		
		return list;
	}
}
