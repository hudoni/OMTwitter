package com.maalaang.omtwitter.uima.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;

import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.uima.type.TweetAnnotation;

/**
 * @author Sangwon Park
 */
public class TwitterCorpusReader extends CollectionReader_ImplBase {
	
	public static final String PARAM_TWITTER_CORPUS_FILE = "twitterCorpusFile";
	public static final String PARAM_FIELDS = "fields";
	public static final String PARAM_FIELDS_DELIM = "fieldsDelimiter";
	
	private OMTwitterReader reader = null;
	
	private Logger logger = null;
	
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		logger = getLogger();
		
		try {
			String fieldsNameStr = (String) getConfigParameterValue(PARAM_FIELDS);
			String fieldsDelim = (String) getConfigParameterValue(PARAM_FIELDS_DELIM);
			
			String[] fieldNames = fieldsNameStr.split("\\s+");
			int[] fields = new int[fieldNames.length];
			
			for (int i = 0; i < fieldNames.length; i++) {
				fields[i] = OMTwitterCorpusFileReader.fieldNameToId(fieldNames[i]);
			}
			
			reader = new OMTwitterCorpusFileReader((String)getConfigParameterValue(PARAM_TWITTER_CORPUS_FILE), fieldsDelim, fields);
			
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		logger.log(Level.INFO, this.getClass().getSimpleName() + " initialized");
	}
	
	public void getNext(CAS cas) throws IOException, CollectionException {
		JCas jcas = null;
		
		try {
			jcas = cas.getJCas();
			
		} catch (CASException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new CollectionException(e);
		}
		
		OMTweet tweet = reader.next();
		
		jcas.setDocumentText(tweet.getText());
		
		TweetAnnotation tweetAnn = new TweetAnnotation(jcas);
		tweetAnn.setBegin(0);
		tweetAnn.setEnd(tweet.getText().length());
		tweetAnn.setId(tweet.getId());
		tweetAnn.setAuthor(tweet.getAuthor());
		tweetAnn.setDate(tweet.getDateString());
		tweetAnn.setQuery(tweet.getQuery());
		tweetAnn.setPolarity(tweet.getPolarityString());
		tweetAnn.addToIndexes();
		
		logger.log(Level.FINE, tweet.toString());
	}

	public void close() throws IOException {
		reader.close();
		reader = null;
	}

	public Progress[] getProgress() {
		return null;
	}

	public boolean hasNext() throws IOException, CollectionException {
		return reader.hasNext();
	}

}
