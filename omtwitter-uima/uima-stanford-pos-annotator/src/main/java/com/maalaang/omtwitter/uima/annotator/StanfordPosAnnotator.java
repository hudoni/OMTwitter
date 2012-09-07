/**
 * 
 */
package com.maalaang.omtwitter.uima.annotator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.uima.type.SentenceAnnotation;
import com.maalaang.omtwitter.uima.type.TokenAnnotation;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @author Sangwon Park
 *
 */
public class StanfordPosAnnotator extends JCasAnnotator_ImplBase {

	private MaxentTagger tagger = null;
	private Logger logger = null;
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<List<HasWord>> sentences =  MaxentTagger.tokenizeText(new StringReader(jcas.getDocumentText()));
		
		for (List<HasWord> sentence : sentences) {
			ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
			
			SentenceAnnotation sentenceAnn = new SentenceAnnotation(jcas);
			sentenceAnn.setBegin(taggedSentence.get(0).beginPosition());
			sentenceAnn.setEnd(taggedSentence.get(taggedSentence.size() - 1).endPosition());
			sentenceAnn.addToIndexes();
			
			for (TaggedWord word : taggedSentence) {
				TokenAnnotation tokenAnn = new TokenAnnotation(jcas);
				tokenAnn.setBegin(word.beginPosition());
				tokenAnn.setEnd(word.endPosition());
				tokenAnn.setPosTag(word.tag());
				tokenAnn.addToIndexes();
			}
		}
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
	    this.logger = aContext.getLogger();
		
		try {
			tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
			
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		this.logger.log(Level.INFO, "StanfordPosAnnotator initialized");
	}
}
