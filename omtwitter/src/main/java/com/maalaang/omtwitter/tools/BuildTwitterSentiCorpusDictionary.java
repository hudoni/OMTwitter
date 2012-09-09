package com.maalaang.omtwitter.tools;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.resource.TwitterSentiCorpusDictionary;
import com.maalaang.omtwitter.text.OMTweetToken;
import com.maalaang.omtwitter.text.OMTweetTokenizer;

public class BuildTwitterSentiCorpusDictionary {

	private Logger logger = null;
	
	private final static int INDEX_POS = 0;
	private final static int INDEX_NEG = 1;
	private final static int INDEX_NEU = 2;
	private final static int INDEX_ALL = 3;
	
	public final static int SORT_BY_POS_SCORE = 0;
	public final static int SORT_BY_NEG_SCORE = 1;
	public final static int SORT_BY_LEXICAL = 2;
	
	private OMTweetTokenizer tweetTokenizer = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BuildTwitterSentiCorpusDictionary builder = new BuildTwitterSentiCorpusDictionary();
			
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
		
			int[] corpusFields = new int[] { OMTwitterCorpusFile.FIELD_POLARITY,
					OMTwitterCorpusFile.FIELD_QUERY,
					OMTwitterCorpusFile.FIELD_AUTHOR,
					OMTwitterCorpusFile.FIELD_TEXT };
			
			builder.buildDicFile(new OMTwitterCorpusFileReader(prop.getProperty("sentiCorpusFile"), corpusFields), prop.getProperty("sentiCorpusDicFile"));
			builder.createObjectFile(prop.getProperty("sentiCorpusDicFile"), prop.getProperty("sentiCorpusDicObjectFile"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public BuildTwitterSentiCorpusDictionary() {
		logger = Logger.getLogger(getClass());
		tweetTokenizer = new OMTweetTokenizer();
	}
	
	
	private Map<String,TokenFreq> buildFreqMap(OMTwitterReader reader, int[] countArray) {
		
		HashMap<String,TokenFreq> map = new HashMap<String, TokenFreq>();
		TokenFreq tokenFreq = null;
		
		while (reader.hasNext()) {
			OMTweet tweet = reader.next();
		
			int polarity = tweet.getPolarity();
			
			OMTweetToken[] tokenList = tweetTokenizer.tokenize(tweet.getText());
			for (OMTweetToken tok : tokenList) {
				String key = tok.getNormalizedText();
				
				if (map.containsKey(key)) {
					tokenFreq = map.get(key);
				} else {
					tokenFreq = new TokenFreq();
				}
			
				switch (polarity) {
				case OMTweet.POLARITY_POSITIVE:
					tokenFreq.pos++;
					countArray[INDEX_POS]++;
					break;
				case OMTweet.POLARITY_NEGATIVE:
					tokenFreq.neg++;
					countArray[INDEX_NEG]++;
					break;
				case OMTweet.POLARITY_NEUTRAL:
					tokenFreq.neu++;
					countArray[INDEX_NEU]++;
					break;
				case OMTweet.POLARITY_NOT_SPECIFIED:
					throw new IllegalStateException();
				}
			
				countArray[INDEX_ALL]++;
				map.put(key, tokenFreq);
			}
		}
	
		return map;
	}
	
	public void buildDicFile(OMTwitterReader reader, String out) throws IOException {
		Map<String,TokenFreq> map = null;
		int[] countArray = new int[4];
		double[] maxScoreArray = new double[2];
	
		map = buildFreqMap(reader, countArray);
	
		findMaxScore(map, countArray, maxScoreArray);
		writeDicFile(out, map, SORT_BY_LEXICAL, countArray, maxScoreArray);
		
		reader.close();
	}
	
	public void createObjectFile(String in, String out) throws IOException {
		logger.info("load Twitter Sentiment Corpus dictionary - " + in);
		TwitterSentiCorpusDictionary dic = new TwitterSentiCorpusDictionary();
		dic.load(in);
		logger.info("loaded");
	
		logger.info("write object file - " + out);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
		oos.writeObject(dic);
		oos.close();
		logger.info("done");
	}
	
	private void writeDicFile(String out, Map<String,TokenFreq> map, int sortFlag, int[] countArray, double[] maxScoreArray) throws IOException {
		TreeSet<Entry<String, TokenFreq>> sortedSet = new TreeSet<Map.Entry<String,TokenFreq>>(getFreqComparator(sortFlag, countArray));
		sortedSet.addAll(map.entrySet());
		
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
		for (Entry<String, TokenFreq> e : sortedSet) {
			TokenFreq f = e.getValue();
			fw.write(e.getKey());
			fw.write('\t');
			fw.write(String.format(Locale.ENGLISH, "%.4f", getPosScore(f.pos, f.neg, f.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]) / maxScoreArray[INDEX_POS]));
			fw.write('\t');
			fw.write(String.format(Locale.ENGLISH, "%.4f", getNegScore(f.pos, f.neg, f.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]) / maxScoreArray[INDEX_NEG]));
			fw.write('\n');
		}
		
		fw.close();
	}
	
	
	private void findMaxScore(Map<String,TokenFreq> map, int[] countArray, double[] maxScoreArray) {
		double maxPosScore = 0.0;
		double maxNegScore = 0.0;
		
		Set<Entry<String,TokenFreq>> set = map.entrySet();
		
		for (Entry<String,TokenFreq> e : set) {
			TokenFreq f = e.getValue();
			double posScore = getPosScore(f.pos, f.neg, f.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
			double negScore = getNegScore(f.pos, f.neg, f.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
			
			if (maxPosScore < posScore) {
				maxPosScore = posScore;
			}
			if (maxNegScore < negScore) {
				maxNegScore = negScore;
			}
		}
		
		maxScoreArray[INDEX_POS] = maxPosScore;
		maxScoreArray[INDEX_NEG] = maxNegScore;
	}
	
	private Comparator<Map.Entry<String,TokenFreq>> getFreqComparator(int sortByFlag, final int[] countArray) {
		Comparator<Map.Entry<String,TokenFreq>> comp = null;
		
		switch (sortByFlag) {
		case SORT_BY_POS_SCORE:
			comp = new Comparator<Map.Entry<String,TokenFreq>>() {
				public int compare(Entry<String, TokenFreq> o1, Entry<String, TokenFreq> o2) {
					TokenFreq f1 = o1.getValue();
					TokenFreq f2 = o2.getValue();
					double score1 = getPosScore(f1.pos, f1.neg, f1.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
					double score2 = getPosScore(f2.pos, f2.neg, f2.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
					
					if (score1 > score2) {
						return -1;
					} else if (score1 < score2) {
						return 1;
					} else {
						return o1.getKey().compareTo(o2.getKey());
					}
				}
			};
			break;
		case SORT_BY_NEG_SCORE:
			comp = new Comparator<Map.Entry<String,TokenFreq>>() {
				public int compare(Entry<String, TokenFreq> o1, Entry<String, TokenFreq> o2) {
					TokenFreq f1 = o1.getValue();
					TokenFreq f2 = o2.getValue();
					double score1 = getNegScore(f1.pos, f1.neg, f1.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
					double score2 = getNegScore(f2.pos, f2.neg, f2.neu, countArray[INDEX_POS], countArray[INDEX_NEG], countArray[INDEX_NEU]);
					
					if (score1 > score2) {
						return -1;
					} else if (score1 < score2) {
						return 1;
					} else {
						return o1.getKey().compareTo(o2.getKey());
					}
				}
			};
			break;			
		case SORT_BY_LEXICAL:
			comp = new Comparator<Map.Entry<String,TokenFreq>>() {
				public int compare(Entry<String, TokenFreq> o1, Entry<String, TokenFreq> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			};
			break;		
		}
		return comp;
	}
	
	public static double getPosScore(int posCnt, int negCnt, int neuCnt, int posTotalCnt, int negTotalCnt, int neuTotalCnt) {
		return ((double)posCnt / (double)posTotalCnt) * ((negTotalCnt + neuTotalCnt) / (double)(negCnt + neuCnt + 10));
	}
	
	public static double getNegScore(int posCnt, int negCnt, int neuCnt, int posTotalCnt, int negTotalCnt, int neuTotalCnt) {
		return ((double)negCnt / (double)negTotalCnt) * ((posTotalCnt + neuTotalCnt) / (double)(posCnt + neuCnt + 10));
	}
	
	private class TokenFreq {
		public int pos = 0;
		public int neg = 0;
		public int neu = 0;
	}
}
