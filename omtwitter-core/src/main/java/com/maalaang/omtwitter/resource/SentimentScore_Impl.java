/**
 * 
 */
package com.maalaang.omtwitter.resource;

/**
 * @author Sangwon Park
 *
 */
public class SentimentScore_Impl implements SentimentScore {
	private static final long serialVersionUID = -4436519395943924151L;
	
	private int id;
	private double posScore;
	private double negScore;
	
	public SentimentScore_Impl() {
		this(0, 0.0, 0.0);
	}
	
	public SentimentScore_Impl(int id, double pos, double neg) {
		this.id = id;
		this.posScore = pos;
		this.negScore = neg;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public static double calSubjectiveScore(double pos, double neg) {
		return pos + neg;
	}
	
	public static double calObjectiveScore(double pos, double neg) {
		return 1 - (pos + neg);
	}

	public void setPositiveScore(double score) {
		posScore = score;
	}

	public double getPositiveScore() {
		return posScore;
	}

	public void setNegativeScore(double score) {
		negScore = score;
	}

	public double getNegativeScore() {
		return negScore;
	}

	public void setSubjectiveScore(double score) {
		/* do nothing */
	}

	public double getSubjectiveScore() {
		return calSubjectiveScore(posScore, negScore);
	}

	public void setObjectiveScore(double score) {
		/* do nothing */
	}

	public double getObjectiveScore() {
		return calObjectiveScore(posScore, negScore);
	}

}
