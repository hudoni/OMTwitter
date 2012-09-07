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
public class SvmSOTargetExtractor implements SvmTargetExtractor {

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.ml.SvmExampleTargetExtractor#extractTargetFromCas(org.apache.uima.cas.CAS)
	 */
	public int extractTargetFromJCas(JCas jcas) {
		TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
		String polarity = tweetAnn.getPolarity();
		
		if (polarity.equals(OMTweet.POLARITY_STR_POSITIVE) || polarity.equals(OMTweet.POLARITY_STR_NEGATIVE)) {
			return 1;
		} else if (polarity.equals(OMTweet.POLARITY_STR_NEUTRAL)) {
			return -1;
		} else {
			return 0;
		}
	}

}
