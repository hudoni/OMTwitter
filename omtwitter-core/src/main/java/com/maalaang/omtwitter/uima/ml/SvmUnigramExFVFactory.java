/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.maalaang.omtwitter.ml.SvmFeatureVector;
import com.maalaang.omtwitter.ml.SvmFeatureVector_Impl;
import com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class SvmUnigramExFVFactory implements SvmFVFactory {

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.ml.SvmFeatureVectorFactory#createFeatureVectorFromJCas(org.apache.uima.jcas.JCas)
	 */
	public SvmFeatureVector createFeatureVectorFromJCas(JCas jcas) {
		SvmFeatureVector fv = new SvmFeatureVector_Impl();
		
		FSIterator<Annotation> tscAnnIter = jcas.getAnnotationIndex(TwitterSentiCorpusAnnotation.type).iterator();
		
		while (tscAnnIter.hasNext()) {
			TwitterSentiCorpusAnnotation tscAnn = (TwitterSentiCorpusAnnotation) tscAnnIter.next();
			fv.setFeatureValue(tscAnn.getId(), 1.0);
		}

		return fv;
	}

}
