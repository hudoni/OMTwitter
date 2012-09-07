/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import org.apache.uima.jcas.JCas;

/**
 * @author Sangwon Park
 *
 */
public interface SvmTargetExtractor {
	
	public int extractTargetFromJCas(JCas jcas);
	
}
