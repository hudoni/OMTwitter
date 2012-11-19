/**
 * 
 */
package com.maalaang.omtwitter.uima.annotator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.resource.SentimentScore;
import com.maalaang.omtwitter.resource.SentimentScoreDictionary;
import com.maalaang.omtwitter.resource.SentimentScoreDictionaryFactory;
import com.maalaang.omtwitter.text.OMTweetToken;
import com.maalaang.omtwitter.text.OMTweetTokenizer;
import com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class TwitterSentimentScoreAnnotator extends JCasAnnotator_ImplBase {
	
	private Logger logger = null;
	
	private final static String PARAM_SENTI_SCORE_DIC_OBJ_FILE = "sentiScoreDicObjectFile";

	private SentimentScoreDictionary sentiScoreDic = null;
	
	private OMTweetTokenizer tweetTokenizer = null;
	
	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		logger = aContext.getLogger();
		tweetTokenizer = new OMTweetTokenizer();
		
		try {
			String dicFile = (String)aContext.getConfigParameterValue(PARAM_SENTI_SCORE_DIC_OBJ_FILE);
			InputStream is = getClass().getClassLoader().getResourceAsStream(dicFile);
			if (is == null) {
				is = new FileInputStream(dicFile);
			}
			sentiScoreDic = SentimentScoreDictionaryFactory.loadFromSerializedFile(is);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		logger.log(Level.INFO, "twitter sentiment score annotator initialized");
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		
		OMTweetToken[] tokenList = tweetTokenizer.tokenize(aJCas.getDocumentText());
		
		for (OMTweetToken tok : tokenList) {
			SentimentScore score = sentiScoreDic.find(tok.getNormalizedText());
			if (score != null) {
				TwitterSentiCorpusAnnotation ann = new TwitterSentiCorpusAnnotation(aJCas, tok.getBegin(), tok.getEnd());
				ann.setId(score.getId());
				ann.setPositiveScore(score.getPositiveScore());
				ann.setNegativeScore(score.getNegativeScore());
				ann.setSubjectiveScore(score.getSubjectiveScore());
				ann.setObjectiveScore(score.getObjectiveScore());
				ann.addToIndexes();
			}
		}
	}
}
