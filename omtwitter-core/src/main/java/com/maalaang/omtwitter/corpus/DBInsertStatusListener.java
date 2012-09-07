package com.maalaang.omtwitter.corpus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.User;

/**
 * Insert status to MySQL database.
 * Table Schema should be compatible with [tweetId, tweetText, date, userId, userLang]
 * 
 * @author Sangwon Park
 */
public class DBInsertStatusListener implements StatusListener {
	private final int MAX_CONNECT_FAIL_NUM = 5;
	private Logger logger = null;
	private DateFormat dateFormat = null;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	
	private String host = null;
	private String db = null;
	private String table = null;
	private String user = null;
	private String passwd = null;
	
	public DBInsertStatusListener(String host, String db, String table, String user, String passwd) {
		this.logger = Logger.getLogger(getClass());
		dateFormat = new SimpleDateFormat(OMTwitterCorpusFileReader.DATE_FORMAT);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect();
			setPreparedStatement();
		} catch (ClassNotFoundException e) {
			logger.error(e);
		}
		
		this.host = host;
		this.db = db;
		this.table = table;
		this.user = user;
		this.passwd = passwd;
	}
	
	private void connect() {
		boolean connected = false;
		int connectFailCnt = 0;
		
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		while (!connected) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?useUnicode=true&characterEncoding=utf8", user, passwd);
				break;
			} catch (SQLException e) {
				logger.error(e);
				if (connectFailCnt++ > MAX_CONNECT_FAIL_NUM) {
					logger.error("try limit of mysql connection exceeded");
					break;
				}
			}
		}
	}
	
	private void setPreparedStatement() {
		try {
			pstmt = conn.prepareStatement("INSERT INTO " + table + " VALUES (?,?,?,?,?)");
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	public void onStatus(Status status) {
		User user = status.getUser();
		
		try {
			pstmt.setLong(1, status.getId());
			pstmt.setString(2, status.getText());
			pstmt.setString(3, dateFormat.format(status.getCreatedAt()));
			pstmt.setLong(4, user.getId());
			pstmt.setString(5, user.getLang());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error on Tweet: " + status.getText());
			logger.error(e);
			connect();
			setPreparedStatement();
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
