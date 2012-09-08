package com.maalaang.omtwitter.corpus;

import java.util.List;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;
import com.maalaang.omtwitter.text.OMTweetTokenizer;

public class FilterHashtagUsage implements TweetFilter {
	private String filterName = null;
	private boolean filtered = false;
	
	public FilterHashtagUsage(String filterName) {
		this.filterName = filterName;
	}

	public void initialize() {
	}

	public void next(OMTweet tweet) {
		filtered = false;

		String query = tweet.getQuery();
		if (query.charAt(0) != '#') {
			return;
		}

		List<OMTweetToken> tokenList = OMTweetTokenizer.tokenize(tweet.getText());

		int idx = 0;
		for (OMTweetToken tok : tokenList) {
			if (tok.getType() == OMTweetToken.TOKEN_TYPE_HASHTAG && tok.getText().equalsIgnoreCase(query)) {
				break;
			}
			idx++;
		}
		if (idx == tokenList.size()) {
			throw new IllegalStateException("cannot find query hashtag in the tweet - query=" + query + ", tweet=" + tweet.getText());
		}

		int prevTokType;
		if (idx > 0) {
			prevTokType = tokenList.get(idx - 1).getType();
		} else {
			prevTokType = OMTweetToken.TOKEN_TYPE_NONE;
		}

		int nextTokType;
		if (idx < tokenList.size() - 1) {
			nextTokType = tokenList.get(idx + 1).getType();
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

	public String getFilterName() {
		return filterName;
	}

	public void close() {
		
	}
}
