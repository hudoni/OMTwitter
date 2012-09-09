/**
 * 
 */
package com.maalaang.omtwitter.model;

import java.util.Date;

/**
 * @author Sangwon Park
 */
public interface OMTweet {
	
	public static final int POLARITY_POSITIVE = 0;
	public static final int POLARITY_NEGATIVE = 1;
	public static final int POLARITY_NEUTRAL = 2;
	public static final int POLARITY_SUBJECTIVE = 4;
	public static final int POLARITY_OBJECTIVE = 5;
	public static final int POLARITY_NOT_SPECIFIED = 6;
	
	public static final String POLARITY_STR_POSITIVE = "POS";
	public static final String POLARITY_STR_NEGATIVE = "NEG";
	public static final String POLARITY_STR_NEUTRAL = "NEU";
	public static final String POLARITY_STR_SUBJECTIVE = "SBJ";
	public static final String POLARITY_STR_OBJECTIVE = "OBJ";
	public static final String POLARITY_STR_NOT_SPECIFIED = "NOT SPECIFIED";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
	
	public void setId(String id);
	public String getId();
	
	public void setAuthor(String author);
	public String getAuthor();
	
	public void setText(String text);
	public String getText();
	
	public void setDate(Date date);
	public void setDate(String date);
	public Date getDate();
	public String getDateString();
	
	public void setPolarity(int polarity);
	public void setPolarity(String polarity);
	public int getPolarity();
	public String getPolarityString();
	
	public void setQuery(String query);
	public String getQuery();
	
}
