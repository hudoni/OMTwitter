/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

/**
 * @author Sangwon Park
 *
 */
public class FilterUserName implements TweetFilter {
	
	private HashMap<String,Integer> userPostFreqMap = null;
	private LinkedList<String> userNameQueue = null;
	private int windowSize = 0;
	private int userPostLimit = 0;
	private boolean filtered = false;
	
	public FilterUserName(int windowSize, int userPostLimit) {
		this.windowSize = windowSize;
		this.userPostLimit = userPostLimit;
	}

	public void initialize() {
		if (windowSize > 0) {
			userPostFreqMap = new HashMap<String,Integer>(windowSize);
			userNameQueue = new LinkedList<String>();
		} else {
			userPostFreqMap = new HashMap<String,Integer>();
		}
	}
	
	public void next(OMTweet tweet, List<OMTweetToken> tokenList) {
		String userName = tweet.getAuthor();
		Integer freq = null;
		
		if (windowSize > 0) {
			userNameQueue.addLast(userName);
			if (userNameQueue.size() > windowSize) {
				String removedUserName = userNameQueue.remove();
				freq = userPostFreqMap.get(removedUserName);
				if (freq == null) {
					throw new IllegalStateException();
				} else {
					userPostFreqMap.put(removedUserName, --freq);
				}
			}
		}
		
		freq = userPostFreqMap.get(userName);
		if (freq == null) {
			filtered = userPostLimit > 0 ? false : true;
			userPostFreqMap.put(userName, 1);
		} else {
			filtered = userPostLimit > freq ? false : true; 
			userPostFreqMap.put(userName, ++freq);
		}
	}

	public boolean isFilteredOut() {
		return filtered;
	}

	public void close() {
		userPostFreqMap.clear();
		userPostFreqMap = null;
		userNameQueue.clear();
		userNameQueue = null;
	}
}
