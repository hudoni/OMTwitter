/**
 * 
 */
package com.maalaang.omtwitter.resource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sangwon Park
 *
 */
public class TwitterSentiCorpusDictionary implements SentimentScoreDictionary {
			
	public final static String REPLACEMENT_USER = "USER";
	public final static String REPLACEMENT_URL = "URL";
	

	private static final long serialVersionUID = 6349609426262551176L;
	
	private Map<String,SentimentScore> map = null;
	
	public TwitterSentiCorpusDictionary() {
		map = new HashMap<String,SentimentScore>();
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.resource.SentimentScoreDictionary#find(java.lang.String)
	 */
	public SentimentScore find(String expr) {
		return map.get(expr.toLowerCase());
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.resource.SentimentScoreDictionary#find(java.lang.String, java.lang.String, int)
	 */
	public SentimentScore find(String expr, String posTag, int posTagset) {
		return map.get(expr.toLowerCase());
	}
	
	public void load(String dicFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicFile), "UTF-8"));
		
		String line = null;
		String[] fields = null;
		int id = 0;
		
		while ((line = br.readLine()) != null) {
			if (line.charAt(0) == '#') {
				continue;
			}
			
			fields = line.split("\\t");
			
			SentimentScore score = new SentimentScore_Impl(id++, Double.parseDouble(fields[1]), Double.parseDouble(fields[2]));
			map.put(fields[0], score);
		}
		
		br.close();
	}
}
