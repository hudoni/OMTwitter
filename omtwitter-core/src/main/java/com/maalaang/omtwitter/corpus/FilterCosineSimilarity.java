/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

/**
 * @author Sangwon Park
 *
 */
public class FilterCosineSimilarity implements TweetFilter {
	
	private LinkedList<RealVector> fvList = null;
	private Map<String,Integer> tokenIdMap = null;
	private int windowSize = 0;
	private double threshold = 0.0;
	private boolean filtered = false;
	private int tokenIndex = 0;
	
	public FilterCosineSimilarity(int windowSize, double threshold) {
		this.windowSize = windowSize;
		this.threshold = threshold;
	}
	
	public void initialize() {
		fvList = new LinkedList<RealVector>();
		tokenIdMap = new HashMap<String,Integer>();
	}

	public void next(OMTweet tweet, List<OMTweetToken> tokenList) {
		RealVector fv = tweetToFeatureVector(tweet, tokenList);
		filtered = false;
		
		for (RealVector fv1 : fvList) {
			try {
				if (fv.cosine(fv1) > threshold) {
					filtered = true;
					break;
				}
			} catch (MathArithmeticException e) {
			}
		}
		
		fvList.add(fv);
		if (fvList.size() > windowSize) {
			fvList.remove();
		}
	}

	public boolean isFilteredOut() {
		return filtered;
	}

	public void close() {
		fvList.clear();
		tokenIdMap.clear();
	}
	
	private RealVector tweetToFeatureVector(OMTweet tweet, List<OMTweetToken> tokenList) {
		RealVector fv = new OpenMapRealVector();
		
		for (OMTweetToken tok : tokenList) {
			String t = tok.getNormalizedText();
			
			Integer tokenId = tokenIdMap.get(t);
			if (tokenId == null) {
				tokenId = tokenIndex++;
				tokenIdMap.put(t, tokenId);
			}
			
			fv.setEntry(tokenId, fv.getEntry(tokenId) + 1.0);
		}
		
		return fv;
	}
}
