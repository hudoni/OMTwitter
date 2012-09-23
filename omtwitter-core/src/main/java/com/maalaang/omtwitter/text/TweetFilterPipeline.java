/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public class TweetFilterPipeline {
	private ArrayList<TweetFilter> filterList = null;
	private String[] filterNames = null;
	private boolean[] filterResults = null;
	private OMTweetTokenizer tweetTokenizer = null;
	
	private Logger logger = null;
	
	public TweetFilterPipeline() {
		filterList = new ArrayList<TweetFilter>();
		tweetTokenizer = new OMTweetTokenizer();
		logger = Logger.getLogger(getClass());
	}
	
	public void add(TweetFilter filter) {
		filterList.add(filter);
	}
	
	public void initialize() {
		filterList.trimToSize();
		filterNames = new String[filterList.size()];
		filterResults = new boolean[filterList.size()];
		
		logger.debug("initialize TweetFilterPipeline");
		int i = 0;
		for (TweetFilter f : filterList) {
			filterNames[i] = f.getClass().getSimpleName();
			
			logger.debug("initialize " + filterNames[i]);
			f.initialize();
			
			i++;
		}
	}
	
	/**
	 * Perform filtering with the filters registered.
	 * @param tweet
	 * @return true if the specified tweet is passed through all the filters; otherwise, false.
	 */
	public boolean check(OMTweet tweet) {
		OMTweetToken[] tokenList = tweetTokenizer.tokenize(tweet.getText().toLowerCase());
		
		if (logger.isDebugEnabled()) {
			for (OMTweetToken t : tokenList) {
				logger.debug(t);
			}
			
		}
		
		int i = 0;
		boolean res = false;
		boolean passed = true;
		
		for (TweetFilter f : filterList) {
			f.next(tweet, tokenList);
			res = f.isFilteredOut();
			
			if (res) {
				logger.debug(filterNames[i] + " filtered out - " + tweet);
				passed = false;
			}
			
			filterResults[i++] = res;
		}
		
		return passed;
	}
	
	public void close() {
		logger.debug("close TweetFilterPipeline");
		int i = 0;
		for (TweetFilter f : filterList) {
			logger.debug("close " + filterNames[i]);
			f.close();
			i++;
		}
		filterList.clear();
	}
}
