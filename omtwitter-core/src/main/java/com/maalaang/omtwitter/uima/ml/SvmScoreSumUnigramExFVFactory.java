/**
 * 
 */
package com.maalaang.omtwitter.uima.ml;

import java.util.HashSet;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.maalaang.omtwitter.ml.SvmFeatureVector;
import com.maalaang.omtwitter.ml.SvmFeatureVector_Impl;
import com.maalaang.omtwitter.uima.type.NegationAnnotation;
import com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation;
import com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class SvmScoreSumUnigramExFVFactory implements SvmFVFactory {

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.ml.SvmFeatureVectorFactory#createFeatureVectorFromJCas(org.apache.uima.jcas.JCas)
	 */
	public SvmFeatureVector createFeatureVectorFromJCas(JCas jcas) {
		SvmFeatureVector fv = new SvmFeatureVector_Impl();

		double posScoreSumSwn = 0.0;
		double negScoreSumSwn = 0.0;
		double posScoreSumTsc = 0.0;
		double negScoreSumTsc = 0.0;

		FSIterator<Annotation> negationAnnIter = jcas.getAnnotationIndex(NegationAnnotation.type).iterator();
		HashSet<SentiWordNetAnnotation> negatedSwnAnnSet = new HashSet<SentiWordNetAnnotation>();
		HashSet<TwitterSentiCorpusAnnotation> negatedTscAnnSet = new HashSet<TwitterSentiCorpusAnnotation>();

		while (negationAnnIter.hasNext()) {
			NegationAnnotation negation = (NegationAnnotation) negationAnnIter.next();

			FSIterator<Annotation> swnAnnIter = jcas.getAnnotationIndex(SentiWordNetAnnotation.type).subiterator(negation);
			while (swnAnnIter.hasNext()) {
				negatedSwnAnnSet.add((SentiWordNetAnnotation)swnAnnIter.next());
			}
			FSIterator<Annotation> tscAnnIter = jcas.getAnnotationIndex(TwitterSentiCorpusAnnotation.type).subiterator(negation);
			while (tscAnnIter.hasNext()) {
				negatedTscAnnSet.add((TwitterSentiCorpusAnnotation)tscAnnIter.next());
			}
		}

		FSIterator<Annotation> swnAnnIter = jcas.getAnnotationIndex(SentiWordNetAnnotation.type).iterator();
		while (swnAnnIter.hasNext()) {
			SentiWordNetAnnotation swnAnn = (SentiWordNetAnnotation)swnAnnIter.next();

			double posScore = swnAnn.getPositiveScore();
			double negScore = swnAnn.getNegativeScore();

			if (negatedSwnAnnSet.contains(swnAnn)) {
				double tmp = posScore;
				posScore = negScore;
				negScore = tmp;
			}

			posScoreSumSwn += posScore;
			negScoreSumSwn += negScore;
		}

		FSIterator<Annotation> tscAnnIter = jcas.getAnnotationIndex(TwitterSentiCorpusAnnotation.type).iterator();
		while (tscAnnIter.hasNext()) {
			TwitterSentiCorpusAnnotation tscAnn = (TwitterSentiCorpusAnnotation)tscAnnIter.next();

			double posScore = tscAnn.getPositiveScore();
			double negScore = tscAnn.getNegativeScore();

			if (negatedTscAnnSet.contains(tscAnn)) {
				double tmp = posScore;
				posScore = negScore;
				negScore = tmp;
			}

			posScoreSumTsc += posScore;
			negScoreSumTsc += negScore;

			fv.setFeatureValue(tscAnn.getId() + 2, 1.0);
		}

		fv.setFeatureValue(1, posScoreSumSwn - negScoreSumSwn);
		fv.setFeatureValue(2, posScoreSumTsc - negScoreSumTsc);

		return fv;
	}
}