package com.maalaang.omtwitter.corpus;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.model.OMTweet;

public class TwitterCorpusStat {
	public static Map<String,Integer> userStatusFreq(String corpusFile, String fieldDelim, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		OMTwitterCorpusFileReader reader = new OMTwitterCorpusFileReader(corpusFile, fieldDelim, fields);
		HashMap<String,Integer> map = new HashMap<String, Integer>();

		while (reader.hasNext()) {
			OMTweet tweet = reader.next();
			String user = tweet.getAuthor();
			Integer value = map.get(user);

			if (value != null) {
				map.put(user, value + 1);
			} else {
				map.put(user, 1);
			}
		}
		
		reader.close();
		
		return map;
	}
	
	public static Map<Integer,Integer> sentimentFreq(String corpusFile, String fieldDelim, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		OMTwitterCorpusFileReader reader = new OMTwitterCorpusFileReader(corpusFile, fieldDelim, fields);
		OMTweet tweet = null;
		int[] cnt = new int[6];
		while (reader.hasNext()) {
			tweet = reader.next();
			switch (tweet.getPolarity()) {
			case OMTweet.POLARITY_POSITIVE:
				cnt[0]++;
				cnt[3]++;
				break;
			case OMTweet.POLARITY_NEGATIVE:
				cnt[1]++;
				cnt[3]++;
				break;
			case OMTweet.POLARITY_NEUTRAL:
				cnt[2]++;
				cnt[4]++;
				break;
			case OMTweet.POLARITY_SUBJECTIVE:
				cnt[3]++;
				break;
			case OMTweet.POLARITY_OBJECTIVE:
				cnt[4]++;
				break;
			case OMTweet.POLARITY_NOT_SPECIFIED:
				cnt[5]++;
				break;
			}
		}
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>(6);
		map.put(OMTweet.POLARITY_POSITIVE, cnt[0]);
		map.put(OMTweet.POLARITY_NEGATIVE, cnt[1]);
		map.put(OMTweet.POLARITY_NEUTRAL, cnt[2]);
		map.put(OMTweet.POLARITY_SUBJECTIVE, cnt[3]);
		map.put(OMTweet.POLARITY_OBJECTIVE, cnt[4]);
		map.put(OMTweet.POLARITY_NOT_SPECIFIED, cnt[5]);
		
		reader.close();
		
		return map;
	}
}
