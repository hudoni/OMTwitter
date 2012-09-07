/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import org.apache.uima.jcas.JCas;

import com.maalaang.omtwitter.ml.SvmFeatureVector;


/**
 * @author Sangwon Park
 *
 */
public interface SvmFVFactory {
	
	public SvmFeatureVector createFeatureVectorFromJCas(JCas jcas);
	
}
