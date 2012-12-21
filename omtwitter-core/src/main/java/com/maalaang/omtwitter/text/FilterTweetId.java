/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.HashSet;
import java.util.Set;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public class FilterTweetId implements TweetFilter {
	
	private Set<Long> idSet = null;
	private boolean filtered = false;
	
	public FilterTweetId() {
	}

	public void initialize() {
		idSet = new HashSet<Long>();
	}
	
	public void next(OMTweet tweet, OMTweetToken[] tokenList) {
		filtered = idSet.add(Long.parseLong(tweet.getId())) ? false : true;
	}

	public boolean isFilteredOut() {
		return filtered;
	}

	public void close() {
		idSet.clear();
	}
}
