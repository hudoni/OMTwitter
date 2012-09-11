/**
 * 
 */
package com.maalaang.omtwitter.text;

import com.maalaang.omtwitter.model.OMTweet;

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
