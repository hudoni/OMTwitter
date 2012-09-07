/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import org.apache.uima.jcas.JCas;

/**
 * @author Sangwon Park
 *
 */
public interface SvmTargetUpdator {
	
	public void updateTargetInJCas(JCas jcas, double value);
	
	public boolean classificationRequired(JCas jcas);

}
