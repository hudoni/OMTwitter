/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;
import com.maalaang.omtwitter.text.OMTweetTokenizer;

/**
 * @author Sangwon Park
 *
 */
public class TweetFilterPipeline {
	private ArrayList<TweetFilter> filterList = null;
	private String[] filterNames = null;
	private boolean[] filterResults = null;
	
	private Logger logger = null;
	
	public TweetFilterPipeline() {
		filterList = new ArrayList<TweetFilter>();
		logger = Logger.getLogger(getClass());
	}
	
	public void add(TweetFilter filter) {
		filterList.add(filter);
	}
	
	public void initialize() {
		filterList.trimToSize();
		filterNames = new String[filterList.size()];
		filterResults = new boolean[filterList.size()];
		
		int i = 0;
		for (TweetFilter f : filterList) {
			filterNames[i] = f.getClass().getSimpleName();
			
			logger.info("initialize " + filterNames[i]);
			f.initialize();
			
			i++;
		}
	}
	
	public boolean filter(OMTweet tweet) {
		List<OMTweetToken> tokenList = OMTweetTokenizer.tokenize(tweet.getText().toLowerCase());
		
		int i = 0;
		boolean res = false;
		boolean filtered = false;
		
		for (TweetFilter f : filterList) {
			f.next(tweet, tokenList);
			res = f.isFilteredOut();
			
			if (res) {
				logger.info(filterNames[i] + " filtered out - " + tweet);
				filtered = true;
			}
			
			filterResults[i++] = res;
		}
		
		return filtered;
	}
	
	public void close() {
		int i = 0;
		for (TweetFilter f : filterList) {
			logger.info("close " + filterNames[i]);
			f.close();
			i++;
		}
		filterList.clear();
	}
}
