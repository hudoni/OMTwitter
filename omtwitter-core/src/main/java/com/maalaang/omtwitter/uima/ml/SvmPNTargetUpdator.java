/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import org.apache.uima.jcas.JCas;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.uima.type.TweetAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class SvmPNTargetUpdator implements SvmTargetUpdator {

	public void updateTargetInJCas(JCas jcas, double value) {
		TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
		
		if (OMTweet.POLARITY_STR_OBJECTIVE.equals(tweetAnn.getPolarity())) {
			tweetAnn.setPolarity(OMTweet.POLARITY_STR_NEUTRAL);
		} else if (value >= 0) {
			tweetAnn.setPolarity(OMTweet.POLARITY_STR_POSITIVE);
		} else {
			tweetAnn.setPolarity(OMTweet.POLARITY_STR_NEGATIVE);
		}
	}

	public boolean classificationRequired(JCas jcas) {
		TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
		if (OMTweet.POLARITY_STR_OBJECTIVE.equals(tweetAnn.getPolarity())) {
			return false;
		} else {
			return true;
		}
	}

}
