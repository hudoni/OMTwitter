package com.maalaang.omtwitter.corpus;

import java.util.List;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.OMTweetToken;

public class FilterHashtagUsage implements TweetFilter {
	private boolean filtered = false;
	
	public FilterHashtagUsage() {
	}

	public void initialize() {
	}

	public void next(OMTweet tweet, List<OMTweetToken> tokenList) {
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

	public void close() {
		
	}
}
