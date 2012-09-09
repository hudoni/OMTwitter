package com.maalaang.omtwitter.corpus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileWriter;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.model.OMTweet_Impl;

public class TextWriteStatusListener implements StatusListener {
	private final int LOG_TWEET_PROCESS_CNT = 1000;
	
	private Logger logger = null;
	private OMTwitterCorpusFileWriter corpusWriter = null;
	
	private int tweetCnt = 0;
	private String lang = null;
	
	public TextWriteStatusListener(String file, String lang) {
		this.logger = Logger.getLogger(getClass());
		try {
			this.corpusWriter = new OMTwitterCorpusFileWriter(file, new int[] {
					OMTwitterCorpusFile.FIELD_ID,
					OMTwitterCorpusFile.FIELD_AUTHOR,
					OMTwitterCorpusFile.FIELD_DATE,
					OMTwitterCorpusFile.FIELD_TEXT
			});
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.lang = lang;
	}
	
	public void onStatus(Status status) {
		OMTweet tweet = new OMTweet_Impl(String.valueOf(status.getId()), status.getUser().getName().replaceAll("\\s+", " "), status.getCreatedAt(), status.getText().replaceAll("\\s+", " "));
		if (lang != null && status.getUser().getLang().equals(lang)) {
			try {
				corpusWriter.write(tweet);
			} catch (IOException e) {
				logger.error(e);
			}
			
			if (tweetCnt % LOG_TWEET_PROCESS_CNT == 0) {
				logger.info(tweetCnt + ": " + tweet.getAuthor() + "\t" + tweet.getText());
			}
			
			tweetCnt++;
		}
	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		logger.debug("got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		logger.debug("got track limitation notice:" + numberOfLimitedStatuses);
	}

	public void onScrubGeo(long userId, long upToStatusId) {
		logger.debug("got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	}

	public void onException(Exception ex) {
		logger.error(ex);
	}
}
