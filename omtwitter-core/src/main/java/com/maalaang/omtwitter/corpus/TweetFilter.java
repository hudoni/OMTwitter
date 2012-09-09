/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

/**
 * @author Sangwon Park
 *
 */
public interface TweetFilter {
	
	public void initialize();
	
	public void next(OMTweet tweet, OMTweetToken[] tokenList);
	
	public boolean isFilteredOut();
	
	public void close();

}
