/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.util.List;
import java.util.Set;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

/**
 * @author Sangwon Park
 *
 */
public class FilterStopword implements TweetFilter {
	
	private Set<String> stopwords = null;
	private int stopwordNum = 0;
	
	private boolean filtered = false;
	
	public FilterStopword(Set<String> stopwords, int stopwordNum) {
		this.stopwords = stopwords;
		this.stopwordNum = stopwordNum;
	}
	
	public void initialize() {
		
	}

	public void next(OMTweet tweet, List<OMTweetToken> tokenList) {
		int cnt = 0;
		for (OMTweetToken tok : tokenList) {
			if (stopwords.contains(tok)) {
				if (++cnt > stopwordNum) {
					filtered = true;
					break;
				}
			}
		}
		filtered = false;
	}

	public boolean isFilteredOut() {
		return filtered;
	}

	public void close() {
		
	}
}
