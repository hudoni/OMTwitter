package com.maalaang.omtwitter.text;

import com.maalaang.omtwitter.model.OMTweet;

public class FilterHashtagUsage implements TweetFilter {
	private boolean filtered = false;
	
	public FilterHashtagUsage() {
	}

	public void initialize() {
	}

	public void next(OMTweet tweet, OMTweetToken[] tokenList) {
		filtered = false;

		String query = tweet.getQuery();
		if (query.charAt(0) != '#') {
			return;
		}

		int idx = 0;
		for (OMTweetToken tok : tokenList) {
			if (tok.getType() == OMTweetToken.TOKEN_TYPE_HASHTAG && tok.getText().equalsIgnoreCase(query)) {
				break;
			}
			idx++;
		}
		if (idx == tokenList.length) {
			throw new IllegalStateException("cannot find query hashtag in the tweet - query=" + query + ", tweet=" + tweet.getText());
		}

		int prevTokType;
		if (idx > 0) {
			prevTokType = tokenList[idx-1].getType();
		} else {
			prevTokType = OMTweetToken.TOKEN_TYPE_NONE;
		}

		int nextTokType;
		if (idx < tokenList.length - 1) {
			nextTokType = tokenList[idx+1].getType();
		} else {
			nextTokType = OMTweetToken.TOKEN_TYPE_NONE;
		}


		switch (nextTokType) {
		case OMTweetToken.TOKEN_TYPE_NONE:
		case OMTweetToken.TOKEN_TYPE_URL:
		case OMTweetToken.TOKEN_TYPE_USER:
		case OMTweetToken.TOKEN_TYPE_HASHTAG:
			filtered = true;
		}

		switch (prevTokType) {
		case OMTweetToken.TOKEN_TYPE_URL:
		case OMTweetToken.TOKEN_TYPE_HASHTAG:
			filtered = true;
		}
	}

	public boolean isFilteredOut() {
		return filtered; 
	}

	public void close() {
		
	}
}
