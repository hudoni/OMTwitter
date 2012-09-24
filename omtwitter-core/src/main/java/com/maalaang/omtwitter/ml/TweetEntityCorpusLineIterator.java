package com.maalaang.omtwitter.ml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import cc.mallet.types.Instance;

import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.model.OMTweet;

public class TweetEntityCorpusLineIterator implements Iterator<Instance> {
	private OMTwitterCorpusFileReader reader = null;
	private Logger logger = null;
	private long cnt = 0;
	
	private ArrayList<String> tokenList = null;
	private ArrayList<String> posTagList = null;
	private ArrayList<String> labelList = null;
	
	public TweetEntityCorpusLineIterator(String file, String fieldDelim, int[] fields) throws ClassNotFoundException, IOException {
		try {
			reader = new OMTwitterCorpusFileReader(file, fieldDelim, fields);
		} catch (IOException e) {
		}
		logger = Logger.getLogger(this.getClass().getName());
		
		tokenList = new ArrayList<String>();
		labelList = new ArrayList<String>();
		posTagList = new ArrayList<String>();
	}
	
	public boolean hasNext() {
		return reader.hasNext();
	}

	public Instance next() {
		OMTweet tweet = reader.next();
		
		String[] tokens = tweet.getText().split("\\s+");
		String prev = null;
		
		tokenList.clear();
		labelList.clear();
		posTagList.clear();
		
		StringBuilder sb = null;
		
		for (String t : tokens) {
			int idx = t.lastIndexOf('/');
			
			if (idx >= 0) {
				int idx1 = t.lastIndexOf('/', idx-1);
				
				String word = t.substring(0, idx1);
				String posTag = t.substring(idx1 + 1, idx);
				String label = t.substring(idx + 1);
				
				if (prev != null) {
					word = prev + " " + word;
					prev = null;
				}
				
				tokenList.add(word);
				labelList.add(label);
				posTagList.add(posTag);
				
				if (sb == null) {
					sb = new StringBuilder();
					sb.append(word);
				} else {
					sb.append(' ');
					sb.append(word);
				}
				
			} else {
				if (prev == null) {
					prev = t;
				} else {
					prev += " " + t;
				}
			}
		}

		
		String[][] data = new String[3][];
		data[0] = tokenList.toArray(new String[0]);
		data[1] = posTagList.toArray(new String[0]);
		data[2] = labelList.toArray(new String[0]);
		
		Instance inst = new Instance(data, null, null, null);
		cnt++;
		
		if (cnt % 100000 == 0) {
			logger.info(cnt + " labeled tweets were read");
		}
		
		return inst;
	}

	public void remove() {
	}
}
