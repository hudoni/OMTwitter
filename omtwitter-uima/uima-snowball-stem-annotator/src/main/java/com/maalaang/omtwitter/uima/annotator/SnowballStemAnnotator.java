package com.maalaang.omtwitter.uima.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.tartarus.snowball.ext.englishStemmer;

import com.maalaang.omtwitter.uima.type.TokenAnnotation;

public class SnowballStemAnnotator extends JCasAnnotator_ImplBase {

	private Logger logger;
	private englishStemmer stemmer = null;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> tokenIterator = aJCas.getAnnotationIndex(TokenAnnotation.type).iterator();

		while (tokenIterator.hasNext()) {
			TokenAnnotation tokenAnn = (TokenAnnotation)tokenIterator.next();
			stemmer.setCurrent(tokenAnn.getCoveredText());
			stemmer.stem();
			tokenAnn.setStem(stemmer.getCurrent());
		}
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		this.logger = aContext.getLogger();
		this.stemmer = new englishStemmer();

		this.logger.log(Level.INFO, "snowball stem annotator initialized");
	}
}