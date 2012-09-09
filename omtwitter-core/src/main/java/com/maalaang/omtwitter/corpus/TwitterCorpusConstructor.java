/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StatusListener;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public class TwitterCorpusConstructor {
	private Logger logger = null;
	private static String FILE_DATE_FORMAT = "yyyyMMdd_HHmmss";
	
	public TwitterCorpusConstructor() {
		logger = Logger.getLogger(getClass());
	}
	
	/**
	 * Construct raw corpus by searching Twitter based on the generated queries.
	 * Fields: ID AUTHOR DATE QUERY TEXT
	 * @param queries
	 * @param rawCorpusFile
	 * @param rpp
	 * @param max
	 * @param lang
	 * @param interval
	 * @param retryNum
	 * @param retryInterval
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void constructCorpusBySearch(Set<String> queries, String rawCorpusFile, int rpp, int max, String lang, int interval, int retryNum, int retryInterval) throws IOException, InterruptedException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rawCorpusFile), "UTF-8"));
		long resultTweetTotalCnt = 0;
		
		int queryTotalNum = queries.size();
		int queryProcessCnt = 0;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(OMTweet.DATE_FORMAT);
		
		for (String query : queries) {
			List<QueryResult> res = null;
			int tryCount = retryNum;
			
			while (tryCount-- > 0) {
				try {
					res = searchTwitter(query, rpp, max, lang);
					break;
				} catch (TwitterException e) {
					logger.error(e);
					logger.info("failed to serach : remaining try = " + tryCount);
					Thread.sleep(retryInterval);
				}
			}
			
			// ID AUTHOR DATE QUERY TEXT
			int resultTweetCnt = 0;
			for (QueryResult qr : res) {
				List<Tweet> list = qr.getTweets();
				for (Tweet t : list) {
					bw.write(String.valueOf(t.getId()));
					bw.write('\t');
					bw.write(t.getFromUser().replaceAll("\\s+", " "));
					bw.write('\t');
					bw.write(dateFormat.format(t.getCreatedAt()));
					bw.write('\t');
					bw.write(query);
					bw.write('\t');
					bw.write(t.getText().replaceAll("\\s+", " "));
					bw.write('\n');
					resultTweetCnt++;
				}
			}
			resultTweetTotalCnt += resultTweetCnt;
			logger.info("[" + (++queryProcessCnt) + "/" + queryTotalNum + "] " + resultTweetCnt + " tweets are returned. total=" + resultTweetTotalCnt);
			
			bw.flush();
			Thread.sleep(interval);
		}
		bw.close();
		logger.info("done");
	}
	
	public void searchTwitterCountHashtag(String query, int rpp, int max, String lang) throws TwitterException, IOException {
		List<QueryResult> res = searchTwitter(query, rpp, max, lang);
		HashMap<String, Integer> tagCountMap = new HashMap<String, Integer>();
		for (QueryResult qr : res) {
			List<Tweet> list = qr.getTweets();
			for (Tweet t : list) {
				HashtagEntity[] tags = t.getHashtagEntities();
				if (tags != null) {
					for (int i = 0; i < tags.length; i++) {
						String tag = tags[i].getText().toLowerCase();
						Integer cnt = tagCountMap.get(tag);
						
						if (cnt == null) {
							tagCountMap.put(tag, 0);
						} else {
							tagCountMap.put(tag, cnt + 1);
						}
					}
				}
			}
		}
		
		Set<Entry<String,Integer>> set = tagCountMap.entrySet();
		for (Entry<String,Integer> e : set) {
			logger.info(String.format("%04d\t%s", e.getValue(), e.getKey()));
		}
	}
	
	public void searchTwitterWriteObject(String dir, String fileName, String query, int rpp, int max, String lang) throws TwitterException, IOException {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(FILE_DATE_FORMAT);
	    
		List<QueryResult> res = searchTwitter(query, rpp, max, lang);
		writeQueryResultListObject(res, String.format("%s/%s_%s_%s.object", dir, fileName, sdf.format(cal.getTime()), lang));
	}
	
	public void searchTwitterWriteText(String file, String query, int rpp, int max, String lang) throws TwitterException, IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		List<QueryResult> res = searchTwitter(query, rpp, max, lang);
		int i = 0;
		
		for (QueryResult qr : res) {
			List<Tweet> tweetList = qr.getTweets();
			for (Tweet tweet : tweetList) {
				bw.write(query);
				bw.write('\t');
				bw.write(tweet.getFromUser());
				bw.write('\t');
				bw.write(tweet.getText().replaceAll("\\s", " ").trim());
				bw.write('\n');
				logger.info(++i + "\t@" + tweet.getFromUser() + ": " + tweet.getText());
			}
		}
		bw.close();
	}
	
	public void printTweetsFromStoredResults(String file) throws IOException, ClassNotFoundException {
		ArrayList<QueryResult> res = loadQueryResult(file);
		int i = 1;
		for (QueryResult r : res) {
			List<Tweet> tweets = r.getTweets();
			for (Tweet t : tweets) {
				logger.info(i++ + "\t@" + t.getFromUser() + ": " + t.getText());
			}
		}
	}
	
	public List<QueryResult> searchTwitter(String query, int rpp, int max, String lang) throws TwitterException {
		logger.info("query to the twitter.com: query=" + query + " rpp=" + rpp + " max=" + max + " lang=" + lang);
		
		if (rpp < 1 || rpp > 100) {
			throw new TwitterException("rpp should be between 1 and 100");
		} else if (max < rpp || max > 1500) {
			throw new TwitterException("max should be between rpp <= max <= 1500");
		}
		
		Twitter twitter = new TwitterFactory().getInstance();
		ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();

		Query q = new Query();
		q.setQuery(query);
		q.setRpp(rpp);
		if (lang != null) {
			q.setLang(lang);
		}

		QueryResult result = twitter.search(q);
		queryResultList.add(result);
		
		int cnt = result.getResultsPerPage();

		for ( ; cnt < max; cnt += result.getResultsPerPage()) {
			if (result.getResultsPerPage() != q.getRpp()) {
				break;
			}
			q.setPage(result.getPage() + 1);
			q.setMaxId(result.getMaxId());

			result = twitter.search(q);
			if (result == null) {
				break;
			}
			queryResultList.add(result);
		}

		return queryResultList;
	}
	
	private void writeQueryResultListObject(List<QueryResult> resultList, String filePath) throws IOException{
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(resultList);
		oos.close();
		fos.close();
		
		logger.info("query results were stored to '" + filePath + "'");
	}
	
	public ArrayList<QueryResult> loadQueryResult(String filePath) throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		@SuppressWarnings("unchecked")
		ArrayList<QueryResult> list = (ArrayList<QueryResult>) ois.readObject();
		
		ois.close();
		fis.close();
		
		logger.info("Query results were loaded from '" + filePath + "': " + list.size() + " results");
		
		return list;
	}

	public void openTwitterTrackStream(List<String> track, StatusListener listener, final int seconds) {
		final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		String[] trackArray = track.toArray(new String[track.size()]);
		
		if (seconds != 0) {
			Thread th = new Thread() {
				public void run() {
					try {
						Thread.sleep(1000*seconds);
						logger.info("shutdown twitter stream - " + seconds + " seconds passed");
						twitterStream.shutdown();
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			};
			th.start();
			
			logger.info("open twitter filter stream for " + seconds + " hours");
			twitterStream.filter(new FilterQuery(0, null, trackArray));
			
			try {
				th.join();
			} catch (InterruptedException e) {
				logger.error(e);
			}
			
		} else {
			logger.info("open twitter filter stream");
			twitterStream.filter(new FilterQuery(0, null, trackArray));
		}
	}
	
	public void openTwitterSampleStream(StatusListener listener, final int seconds) {
		final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		
		if (seconds != 0) {
			Thread th = new Thread() {
				public void run() {
					try {
						Thread.sleep(1000*seconds);
						logger.info("shutdown twitter stream - " + seconds + " seconds passed");
						twitterStream.shutdown();
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			};
			th.start();
			
			logger.info("open twitter sample stream for " + seconds + " hours");
			twitterStream.sample();
			
			try {
				th.join();
			} catch (InterruptedException e) {
				logger.error(e);
			}
			
		} else {
			logger.info("open twitter sample stream");
			twitterStream.sample();
		}
	}
}
