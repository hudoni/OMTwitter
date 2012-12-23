package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.corpus.TwitterCorpusConstructor;
import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileWriter;
import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.FilterCosineSimilarity;
import com.maalaang.omtwitter.text.FilterTweetId;
import com.maalaang.omtwitter.text.FilterUserName;
import com.maalaang.omtwitter.text.TweetFilterPipeline;

public class CrawlTweetsBySearch {
	private Logger logger = null;

	public static void main(String[] args) {
		CrawlTweetsBySearch crawler = new CrawlTweetsBySearch();
		try {
			crawler.run(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CrawlTweetsBySearch() {
		logger = Logger.getLogger(getClass());
	}
	
	public void run(String propFile) throws UnsupportedEncodingException, FileNotFoundException, IOException, NumberFormatException, InterruptedException {
		Properties prop = new Properties();
		prop.load(new InputStreamReader(new FileInputStream(propFile), "UTF-8"));
		
		String entityNames = prop.getProperty("raw.corpus.search.query.entity");
		String props = prop.getProperty("raw.corpus.search.query.property");
		
		Set<String> querySet = searchQuery(entityNames.split(";"), props.split(";"));
		
		logger.info("start to crawl tweets from Twitter with " + querySet.size() + " queries: " + prop.getProperty("raw.corpus.search.file"));
		
		// search
		TwitterCorpusConstructor tcc = new TwitterCorpusConstructor();
		tcc.constructCorpusBySearch(querySet, prop.getProperty("raw.corpus.search.file"),
				Integer.parseInt(prop.getProperty("raw.corpus.search.rpp")), Integer.parseInt(prop.getProperty("raw.corpus.search.max")),
				prop.getProperty("raw.corpus.search.lang", null), Integer.parseInt(prop.getProperty("raw.corpus.search.interval")),
				Integer.parseInt(prop.getProperty("raw.corpus.search.retry.num")), Integer.parseInt(prop.getProperty("raw.corpus.search.retry.interval")));
		
		logger.info("start to filter collected tweets - " + prop.getProperty("raw.corpus.search.file.filtered"));
		
		// filtering
		int[] searchCorpusFields = new int[] { OMTwitterCorpusFile.FIELD_ID,
				OMTwitterCorpusFile.FIELD_AUTHOR,
				OMTwitterCorpusFile.FIELD_DATE,
				OMTwitterCorpusFile.FIELD_QUERY,
				OMTwitterCorpusFile.FIELD_TEXT };
		OMTwitterReader searchCorpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.search.file"), searchCorpusFields);
		
		TweetFilterPipeline searchCorpusFilterPipe = new TweetFilterPipeline();
		searchCorpusFilterPipe.add(new FilterTweetId());
		searchCorpusFilterPipe.add(new FilterUserName(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.window.size")),
				Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.post.limit"))));
		searchCorpusFilterPipe.add(new FilterCosineSimilarity(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.cosine.similarity.window.size")),
				Double.parseDouble(prop.getProperty("raw.corpus.search.filter.cosine.similarity.threshold"))));
		searchCorpusFilterPipe.initialize();
		
		OMTwitterCorpusFileWriter corpusWriter = new OMTwitterCorpusFileWriter(prop.getProperty("raw.corpus.search.file.filtered"), searchCorpusFields);
		int tweetTotalCnt = 0;
		int tweetWriteCnt = 0;
		
		while (searchCorpusReader.hasNext()) {
			OMTweet tweet = searchCorpusReader.next();
			tweetTotalCnt++;
			if (searchCorpusFilterPipe.check(tweet)) {
				corpusWriter.write(tweet);
				tweetWriteCnt++;
			}
		}
		
		searchCorpusFilterPipe.close();
		searchCorpusReader.close();
		corpusWriter.close();
		
		logger.info("total " + tweetTotalCnt + " tweets, " + tweetWriteCnt + " tweets were written, " + (tweetTotalCnt - tweetWriteCnt) + " tweets were filtered out"); 
	}
	
	private Set<String> searchQuery(String[] entityNames, String[] props) {
		HashSet<String> set = new HashSet<String>();
		for (String e : entityNames) {
			for (String p : props) {
				if (set.add(e + " " + p)) {
					logger.info("created query - " + e + " " + p);
				}
				set.add(e);
			}
		}
		return set;
	}
}
