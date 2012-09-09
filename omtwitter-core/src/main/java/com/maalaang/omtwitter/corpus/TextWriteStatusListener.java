package com.maalaang.omtwitter.corpus;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import com.maalaang.omtwitter.model.OMTweet;

public class TextWriteStatusListener implements StatusListener {
	private final int LOG_TWEET_PROCESS_CNT = 1000;
	
	private Logger logger = null;
	private BufferedWriter bw = null;
	
	private int tweetCnt = 0;
	private String lang = null;
	private SimpleDateFormat dateFormat = null;
	
	public TextWriteStatusListener(String file, String lang) {
		this.logger = Logger.getLogger(getClass());
		try {
			this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		} catch (Exception e) {
			logger.error(e);
		}
		this.lang = lang;
		dateFormat = new SimpleDateFormat(OMTweet.DATE_FORMAT);
	}
	
	public void onStatus(Status status) {
		if (lang != null && status.getUser().getLang().equals(lang)) {
			try {
				// ID AUTHOR DATE TEXT
				bw.write(String.valueOf(status.getId()));
				bw.write('\t');
				bw.write(status.getUser().getName().replaceAll("\\s+", " "));
				bw.write('\t');
				bw.write(dateFormat.format(status.getCreatedAt()));
				bw.write('\t');
				bw.write(status.getText().replaceAll("\\s+", " "));
				bw.write('\n');
				bw.flush();
				
			} catch (IOException e) {
				logger.error(e);
			}
			
			if (tweetCnt % LOG_TWEET_PROCESS_CNT == 0) {
				logger.info(tweetCnt + ": " + status.getUser().getName() + "\t" + status.getText().replaceAll("\\s+", " "));
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
