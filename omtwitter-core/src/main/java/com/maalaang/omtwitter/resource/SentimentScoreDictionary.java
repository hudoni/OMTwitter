/**
 * 
 */
package com.maalaang.omtwitter.resource;

import java.io.Serializable;

/**
 * @author Sangwon Park
 *
 */
public interface SentimentScoreDictionary extends Serializable {
	
	public final static int POS_TAGSET_BROWN_CORPUS = 0;
	public final static int POS_TAGSET_PENN_TREE_BANK = 1;
	public final static int POS_TAGSET_WORD_NET = 2;
	
	public SentimentScore find(String expr);
	
	public SentimentScore find(String expr, String posTag, int posTagset);

}
