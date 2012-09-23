package com.maalaang.omtwitter.uima.consumer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileWriter;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.model.OMTweet_Impl;
import com.maalaang.omtwitter.text.EmoticonProcessor;
import com.maalaang.omtwitter.text.FilterCosineSimilarity;
import com.maalaang.omtwitter.text.FilterStopword;
import com.maalaang.omtwitter.text.FilterUserName;
import com.maalaang.omtwitter.text.TweetFilterPipeline;
import com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation;
import com.maalaang.omtwitter.uima.type.TweetAnnotation;
import com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation;

public class TwitterSentimentCorpusWriteConsumer extends CasConsumer_ImplBase {
	private final static String PARAM_CORPUS_FILE = "corpusFile";
	private static final String PARAM_CORPUS_FIELDS = "corpusFields";
	private static final String PARAM_CORPUS_FIELDS_DELIM = "corpusFieldsDelim";
	
	private static final String PARAM_STOPWORD_SET_FILE = "stopwordSetFile";
	
	private static final String PARAM_FILTER_USER_NAME_WINDOW_SIZE = "filterUserNameWindowSize";
	private static final String PARAM_FILTER_USER_NAME_POST_LIMIT = "filterUserNamePostLimit";
	
	private static final String PARAM_FILTER_STOPWORD_THRESHOLD = "filterStopwordThreshold";
	
	private static final String PARAM_FILTER_COSINE_SIMILARITY_WINDOW_SIZE = "filterCosineSimilarityWindowSize";
	private static final String PARAM_FILTER_COSINE_SIMILARITY_THRESHOLD = "filterCosineSimilarityThreshold";
	
	private static final String PARAM_SUBJECTIVITY_SCORE_WINDOW_SIZE = "subjectivityScoreWindowSize";
	private static final String PARAM_SWN_SUBJECTIVITY_FACTOR = "swnSubjectivityFactor";
	private static final String PARAM_TSC_SUBJECTIVITY_FACTOR= "tscSubjectivityFactor";
	private static final String PARAM_SWN_SUBJECTIVITY_SCORE_WINDOW_START= "swnSubjectivityScoreWindowStart";
	private static final String PARAM_TSC_SUBJECTIVITY_SCORE_WINDOW_START= "tscSubjectivityScoreWindowStart";
	
	private SimpleDateFormat dateFormat = null;
	
	private OMTwitterCorpusFileWriter corpusWriter = null;
	private BufferedWriter bw = null;
	private EmoticonProcessor emoticonProcessor = null;
	private Logger logger = null;
	private TweetFilterPipeline filterPipe = null;

	private Set<String> stopwords = null;
	
	private int sbjScoreWindowSize = 0;
	private LinkedList<Double> swnSbjScoreWindow = null;
	private LinkedList<Double> tscSbjScoreWindow = null;
	private double swnSbjScoreWindowSum = 0.0;
	private double tscSbjScoreWindowSum = 0.0;
	private double swnSbjScoreWindowStart = 0.0;
	private double tscSbjScoreWindowStart = 0.0;
	private double swnSubjectivityFactor = 0.0;
	private double tscSubjectivityFactor = 0.0;
	
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		emoticonProcessor = new EmoticonProcessor();
		logger = getLogger();
		
		String fieldsNameStr = (String) getConfigParameterValue(PARAM_CORPUS_FIELDS);
		String fieldsDelim = (String) getConfigParameterValue(PARAM_CORPUS_FIELDS_DELIM);

		String[] fieldNames = fieldsNameStr.split("\\s+");
		int[] fields = new int[fieldNames.length];

		for (int i = 0; i < fieldNames.length; i++) {
			fields[i] = OMTwitterCorpusFileReader.fieldNameToId(fieldNames[i]);
		}
		
		try {
			corpusWriter = new OMTwitterCorpusFileWriter((String) getConfigParameterValue(PARAM_CORPUS_FILE), fieldsDelim, fields);
			stopwords = CollectionTextReader.readSetString((String) getConfigParameterValue(PARAM_STOPWORD_SET_FILE));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		dateFormat = new SimpleDateFormat(OMTweet.DATE_FORMAT);
		
		filterPipe = new TweetFilterPipeline();
		filterPipe.add(new FilterUserName((Integer) getConfigParameterValue(PARAM_FILTER_USER_NAME_WINDOW_SIZE), (Integer) getConfigParameterValue(PARAM_FILTER_USER_NAME_POST_LIMIT)));
		filterPipe.add(new FilterStopword(stopwords, (Integer) getConfigParameterValue(PARAM_FILTER_STOPWORD_THRESHOLD)));
		filterPipe.add(new FilterCosineSimilarity((Integer) getConfigParameterValue(PARAM_FILTER_COSINE_SIMILARITY_WINDOW_SIZE), (Float) getConfigParameterValue(PARAM_FILTER_COSINE_SIMILARITY_THRESHOLD)));
		filterPipe.initialize();
		
		swnSbjScoreWindow = new LinkedList<Double>();
		tscSbjScoreWindow = new LinkedList<Double>();
		
		sbjScoreWindowSize = (Integer) getConfigParameterValue(PARAM_SUBJECTIVITY_SCORE_WINDOW_SIZE);
		swnSubjectivityFactor = (Float) getConfigParameterValue(PARAM_SWN_SUBJECTIVITY_FACTOR);
		tscSubjectivityFactor = (Float) getConfigParameterValue(PARAM_TSC_SUBJECTIVITY_FACTOR);
		swnSbjScoreWindowStart = (Float) getConfigParameterValue(PARAM_SWN_SUBJECTIVITY_SCORE_WINDOW_START) * swnSubjectivityFactor;
		tscSbjScoreWindowStart = (Float) getConfigParameterValue(PARAM_TSC_SUBJECTIVITY_SCORE_WINDOW_START) * tscSubjectivityFactor;
		
	}
	
	public void processCas(CAS aCAS) throws ResourceProcessException {
		try {
			JCas jcas = aCAS.getJCas();
			TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
			
			// calculate subjectivity scores
			double swnSbjScoreSum = 0.0;
			AnnotationIndex<Annotation> swnAnnIndex = jcas.getAnnotationIndex(SentiWordNetAnnotation.type);
			FSIterator<Annotation> swnAnnIter = swnAnnIndex.iterator();
			while (swnAnnIter.hasNext()) {
				SentiWordNetAnnotation swnAnn = (SentiWordNetAnnotation) swnAnnIter.next();
				swnSbjScoreSum += swnAnn.getPositiveScore() + swnAnn.getNegativeScore();
			}
			int swnAnnSize = swnAnnIndex.size();
			double swnSbjScore = swnAnnSize > 0 ? swnSbjScoreSum / swnAnnSize : -1.0;
			
			double tscSbjScoreSum = 0.0;
			AnnotationIndex<Annotation> tscAnnIndex = jcas.getAnnotationIndex(TwitterSentiCorpusAnnotation.type);
			FSIterator<Annotation> tscAnnIter = tscAnnIndex.iterator();
			while (tscAnnIter.hasNext()) {
				TwitterSentiCorpusAnnotation tscAnn = (TwitterSentiCorpusAnnotation) tscAnnIter.next();
				tscSbjScoreSum += tscAnn.getPositiveScore() + tscAnn.getNegativeScore();
			}
			int tscAnnSize = tscAnnIndex.size();
			double tscSbjScore = tscAnnSize > 0 ? tscSbjScoreSum / tscAnnSize : -1.0;
			
			logger.log(Level.FINE, "swnSbjScore=" + swnSbjScore + ", tscSbjScore=" + tscSbjScore);
			
			// do filtering
			Date date = null;
			try {
				date = dateFormat.parse(tweetAnn.getDate());
			} catch (Exception e) {
				logger.log(Level.WARNING, "failed to parse a date - " + e.getMessage());
			}
			OMTweet tweet = new OMTweet_Impl(tweetAnn.getId(), tweetAnn.getAuthor(), date, tweetAnn.getCoveredText(), tweetAnn.getQuery());
			
			if (!filterPipe.check(tweet)) {
				maintainSbjScoreWindow(swnSbjScore, tscSbjScore);
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "[SKIP] filtered out - " + tweet);
				}
				return;
			}
			
			// write positive & negative tweets judged by smiley and frowny emoticons
			emoticonProcessor.updatePolarityByEmoticon(tweet);
			switch (tweet.getPolarity()) {
			case OMTweet.POLARITY_POSITIVE:
			case OMTweet.POLARITY_NEGATIVE:
				emoticonProcessor.removeEmoticon(tweet);
				corpusWriter.write(tweet);
				maintainSbjScoreWindow(swnSbjScore, tscSbjScore);
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "[" + tweet.getPolarityString() + "] write tweet  - " + tweet);
				}
				return;
			}
			
			// write neutral tweets filtered based on subjectivity scores compared with the scores of the windows
			if (swnSbjScore > 0.0 && tscSbjScore > 0.0) {
				if (swnSbjScoreWindow.size() < sbjScoreWindowSize) {
					if (swnSbjScore < swnSbjScoreWindowStart && tscSbjScore < tscSbjScoreWindowStart) {
						tweet.setPolarity(OMTweet.POLARITY_NEUTRAL);
						corpusWriter.write(tweet);
					}
				} else if (swnSbjScore < swnSubjectivityFactor * swnSbjScoreWindowSum / swnSbjScoreWindow.size() &&
						tscSbjScore < tscSubjectivityFactor * tscSbjScoreWindowSum / tscSbjScoreWindow.size()) {
					tweet.setPolarity(OMTweet.POLARITY_NEUTRAL);
					corpusWriter.write(tweet);
				}
				maintainSbjScoreWindow(swnSbjScore, tscSbjScore);
			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}
	}

	public void destroy() {
		filterPipe.close();
		try {
			bw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		super.destroy();
	}
	
	
	/**
	 * Maintain the windows for the scores of past tweets
	 * @param swnSbjScore
	 */
	private void maintainSbjScoreWindow(double swnSbjScore, double tscSbjScore) {
		if (swnSbjScore > 0.0) {
			swnSbjScoreWindow.add(swnSbjScore);
			swnSbjScoreWindowSum += swnSbjScore;
			if (swnSbjScoreWindow.size() > sbjScoreWindowSize) {
				swnSbjScoreWindowSum -= swnSbjScoreWindow.remove();
			}
		}
		
		if (tscSbjScore > 0.0) {
			tscSbjScoreWindow.add(tscSbjScore);
			tscSbjScoreWindowSum += tscSbjScore;
			if (tscSbjScoreWindow.size() > sbjScoreWindowSize) {
				tscSbjScoreWindowSum -= tscSbjScoreWindow.remove();
			}
		}
	}

}
