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
public class SvmSOTargetUpdator implements SvmTargetUpdator {

	public void updateTargetInJCas(JCas jcas, double value) {
		TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
		
		if (value >= 0) {
			tweetAnn.setPolarity(OMTweet.POLARITY_STR_SUBJECTIVE);
		} else {
			tweetAnn.setPolarity(OMTweet.POLARITY_STR_OBJECTIVE);
		}
	}

	public boolean classificationRequired(JCas jcas) {
		return true;
	}

}
