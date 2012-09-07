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
public class SentiWordNetDictionary implements SentimentScoreDictionary {
	
	private static final long serialVersionUID = 44054974772288452L;
	
	private static char NON_WORDNET_POS_TAG = 'x';
	
	private Map<String,SentimentScore> nounMap = null; 
	private Map<String,SentimentScore> adjMap = null;
	private Map<String,SentimentScore> verbMap = null;
	private Map<String,SentimentScore> adverbMap = null;
	
	public SentiWordNetDictionary() {
		this.nounMap = new HashMap<String,SentimentScore>();
		this.adjMap = new HashMap<String,SentimentScore>();
		this.verbMap = new HashMap<String,SentimentScore>();
		this.adverbMap = new HashMap<String,SentimentScore>();
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.resource.SentimentScoreDictionary#find(java.lang.String, java.lang.String, int)
	 */	
	public SentimentScore find(String expr) {
		SentimentScore score = null;
		double posScore = 0.0;
		double negScore = 0.0;
		int foundCnt = 0;
		int id = 0;
		
		if ((score = adverbMap.get(expr)) != null) {
			posScore += score.getPositiveScore();
			negScore += score.getNegativeScore();
			foundCnt++;
			id = score.getId();
		}
		
		if ((score = adjMap.get(expr)) != null) {
			posScore += score.getPositiveScore();
			negScore += score.getNegativeScore();
			foundCnt++;
			id = score.getId();
		}
		
		if ((score = verbMap.get(expr)) != null) {
			posScore += score.getPositiveScore();
			negScore += score.getNegativeScore();
			foundCnt++;
			id = score.getId();
		}
		
		if ((score = nounMap.get(expr)) != null) {
			posScore += score.getPositiveScore();
			negScore += score.getNegativeScore();
			foundCnt++;
			id = score.getId();
		}
		
		if (foundCnt > 0) {
			return new SentimentScore_Impl(id, posScore, negScore);
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.resource.SentimentScoreDictionary#find(java.lang.String, java.lang.String, int)
	 */	
	public SentimentScore find(String expr, String posTag, int posTagset) {
		Map<String,SentimentScore> map = null;
		char wordnetPosTag;
		
		switch (posTagset) {
		case POS_TAGSET_BROWN_CORPUS:
			wordnetPosTag = wordnetPosTagFromBrown(posTag);
			break;
		case POS_TAGSET_PENN_TREE_BANK:
			wordnetPosTag = wordnetPosTagFromPennTreebank(posTag);
			break;
		case POS_TAGSET_WORD_NET:
			wordnetPosTag = Character.toLowerCase(posTag.charAt(0));
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		if ((map = mapForPosTag(wordnetPosTag)) != null) {
			return map.get(expr);
		} else {
			return null;
		}
	}
	
	public void load(String dicFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicFile), "UTF-8"));
		Map<String,SentimentScore> map = null;
		
		String line = null;
		String[] fields = null;
		int id = 0;
		
		while ((line = br.readLine()) != null) {
			if (line.charAt(0) == '#') {
				continue;
			}
			
			fields = line.split("\\t");
			
			if ((map = mapForPosTag(fields[0].charAt(0))) != null) {
				SentimentScore score = new SentimentScore_Impl(id++, Double.parseDouble(fields[2]), Double.parseDouble(fields[3]));
				map.put(fields[1], score);
			} else {
				throw new IllegalStateException();
			}
		}
		
		br.close();
	}
	
	private Map<String,SentimentScore> mapForPosTag(char wordnetPosTag) {
		switch (wordnetPosTag) {
		case 'n':
			return nounMap;
		case 'a':
			return adjMap;
		case 'r':
			return adverbMap;
		case 'v':
			return verbMap;
		default:
			return null;
		}
	}
	
	public static char wordnetPosTagFromBrown(String posTag) {
		switch (posTag.charAt(0)) {
		case 'n':
		case 'N':
			return 'n';
			
		case 'j':
		case 'J':
			return 'a';
			
		case 'r':
		case 'R':
			return 'r';
			
		case 'v':
		case 'V':
			return 'v';
			
		default:
			return NON_WORDNET_POS_TAG;
		}
	}
	
	public static char wordnetPosTagFromPennTreebank(String posTag) {
		switch (posTag.charAt(0)) {
		case 'n':
		case 'N':
			return 'n';
			
		case 'j':
		case 'J':
			return 'a';
			
		case 'r':
		case 'R':
			return 'r';
			
		case 'v':
		case 'V':
			return 'v';
			
		default:
			return NON_WORDNET_POS_TAG;
		}
	}
}
