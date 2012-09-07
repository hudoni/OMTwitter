package com.maalaang.omtwitter.corpus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class ObjectWriteStatusListener implements StatusListener {
	private final int OBJECT_LIST_WRITE_INTERVAL = 1000;
	
	private Logger logger = null;
	private LinkedList<Status> statusList = null;
	private int tweetCnt = 0;
	private int saveCnt = 0;
	
	private String lang = null;
	private String dir = null;
	private String fileName = null;
	
	public ObjectWriteStatusListener(String dir, String fileName, String lang) {
		statusList = new LinkedList<Status>();
		
		this.logger = Logger.getLogger(getClass());
		this.dir = dir;
		this.fileName = fileName;
		this.lang = lang;
	}
	
	public void onStatus(Status status) {
		if (lang != null && status.getUser().getLang().equals(lang)) {
			tweetCnt++;
			logger.info(tweetCnt + ": " + status.getUser().getName() + "\t" + status.getText().replaceAll("\\s+", " "));
			statusList.add(status);
			
			if (tweetCnt % OBJECT_LIST_WRITE_INTERVAL == 0) {
				try {
					FileOutputStream fos = new FileOutputStream(String.format("%s/%s_%04d", dir, fileName, saveCnt++) + ".object");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(statusList);
					oos.close();
					fos.close();
					statusList.clear();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		logger.debug("got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		logger.debug("got track limitation notice:" + numberOfLimitedStatuses);
	}

	public void onScrubGeo(long userId, long upToStatusId) {
		logger.debug("got scrub geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	}

	public void onException(Exception ex) {
		logger.error(ex);
	}
}
