/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

/**
 * @author Sangwon Park
 *
 */
public class FilterDomainRelevance implements TweetFilter {
	
	private Map<String,Double> wrsMap = null;
	private Set<String> stopwords = null;
	private double relevanceFactor = 0.0;
	
	private int windowSize = 0;
	private double startWindowScore = 0.0;
	private boolean irrelevance = false;
	
	private LinkedList<Double> windowQueue = null;
	private double windowScoreSum = 0.0;
	
	private boolean filtered = false;
	private int processCnt = 0;
	private boolean useWindowScore = false;
	
	public FilterDomainRelevance(Map<String,Double> wrsMap, Set<String> stopwords, double relevanceFactor, int windowSize, double startWindowScore) {
		this(wrsMap, stopwords, relevanceFactor, windowSize, startWindowScore, false);
	}
	
	public FilterDomainRelevance(Map<String,Double> wrsMap, Set<String> stopwords, double relevanceFactor, int windowSize, double startWindowScore, boolean irrelevance) {
		this.wrsMap = wrsMap;
		this.stopwords = stopwords;
		this.relevanceFactor = relevanceFactor;
		this.windowSize = windowSize;
		this.startWindowScore = startWindowScore;
		this.irrelevance = irrelevance;
	}

	public void initialize() {
		windowQueue = new LinkedList<Double>();
	}

	public void next(OMTweet tweet, List<OMTweetToken> tokenList) {
		double rs = relevanceScore(tokenList);
		double threshold = useWindowScore && windowSize > 0 ? windowScoreSum / (double) windowSize : startWindowScore;
		threshold *= relevanceFactor;
		
		filtered = false;
		
		if (!irrelevance) {
			if (rs < threshold)
				filtered = true;
		} else {
			if (rs > threshold)
				filtered = true;
		}
		
		windowScoreSum += rs;
		windowQueue.addLast(rs);
		
		if (windowQueue.size() > windowSize) {
			windowScoreSum -= windowQueue.remove();
		}
		
		if (!useWindowScore && ++processCnt >= windowSize) {
			useWindowScore = true;
		}
		
	}

	public boolean isFilteredOut() {
		return filtered;
	}

	public void close() {
		windowQueue.clear();
		windowQueue = null;
	}
	
	private double relevanceScore(List<OMTweetToken> tokenList) {
		int tokenCnt = tokenList.size();
		double sum = 0.0;
		Double wrs = null;
		
		for (OMTweetToken tok : tokenList) {
			switch (tok.getType()) {
			case OMTweetToken.TOKEN_TYPE_HASHTAG:
				wrs = wrsMap.get(tok.getText().substring(1));
				if (wrs != null) {
					sum += wrs;
				}
				break;
				
			case OMTweetToken.TOKEN_TYPE_NORMAL:
				if (!stopwords.contains(tok.getText())) {
					if ((wrs = wrsMap.get(tok.getText())) != null) {
						sum += wrs;
					}
				}
				break;
			}
		}
		
		return tokenCnt != 0 ? sum / (double) tokenCnt : 0.0;
	}
}
