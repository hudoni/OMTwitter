/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public interface TweetFilter {
	
	public void initialize();
	
	public void next(OMTweet tweet);
	
	public boolean isFilteredOut();
	
	public String getFilterName();
	
	public void close();

}
