package com.maalaang.omtwitter.corpus;

import java.util.HashMap;
import java.util.Map;

import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;

public class TwitterCorpusStat {
	public static Map<String,Integer> userStatusFreq(OMTwitterReader reader) {
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
		
		return map;
	}
}
