/**
 * 
 */
package com.maalaang.omtwitter.resource;

import java.io.Serializable;

/**
 * @author Sangwon Park
 *
 */
public interface SentimentScore extends Serializable {
	void setId(int id);
	int getId();
	
	void setPositiveScore(double score);
	double getPositiveScore();
	
	void setNegativeScore(double score);
	double getNegativeScore();
	
	void setSubjectiveScore(double score);
	double getSubjectiveScore();
	
	void setObjectiveScore(double score);
	double getObjectiveScore();
	
}
