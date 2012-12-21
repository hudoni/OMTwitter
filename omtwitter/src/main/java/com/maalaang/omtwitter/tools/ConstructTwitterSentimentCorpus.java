package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import com.maalaang.omtwitter.corpus.TwitterCorpusStat;
import com.maalaang.omtwitter.io.LogSystemStream;
import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileWriter;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.uima.pipeline.OMTwitterFixedFlowPipeline;

public class ConstructTwitterSentimentCorpus {
	private Properties prop = null;
	private Logger logger = null;
	
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			LogSystemStream.redirectErrToLog(Level.ERROR);
			
			ConstructTwitterSentimentCorpus con = new ConstructTwitterSentimentCorpus(prop);
			con.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConstructTwitterSentimentCorpus(Properties prop) {
		this.prop = prop;
		this.logger = Logger.getLogger(getClass());
	}
	
	public void run() throws Exception {
		constructSentiCorpusFromSearchCorpus();
		constructSentiCorpusFromSampleCorpus();
		constructBalancedSentiCorpus();
		
	}
	
	private void constructSentiCorpusFromSearchCorpus() throws InvalidXMLException, IOException, ResourceConfigurationException, ResourceInitializationException {
		OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();

		pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
		pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("raw.corpus.search.file"));
		pipeline.setReaderParameter("TwitterCorpusReader", "fields", "ID AUTHOR DATE QUERY TEXT");
		pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", "\\t");

		pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");

		pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");

		pipeline.addAnnotator("SentiWordNetAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-sentiment-score-annotator.xml");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "sentiScoreDicObjectFile", "resource/generated/sentiwordnet/SentiWordNet_3.0.0_20100908.stem.average.dic.object");	
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "maxWindowSize", 5);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "useStemToFindDic", Boolean.TRUE);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "usePosToFindDic", Boolean.TRUE);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "posTagset", "BROWN_CORPUS");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "annotationTypeName", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameId", "id");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNamePositiveScore", "positiveScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameNegativeScore", "negativeScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameSubjectiveScore", "subjectiveScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameObjectiveScore", "objectiveScore");
		
		pipeline.addAnnotator("TwitterSentimentScoreAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-twitter-sentiment-score-annotator.xml");
		pipeline.setAnnotatorParameter("TwitterSentimentScoreAnnotator", "sentiScoreDicObjectFile", "resource/generated/senti_corpus/mobile_devices_20120426.tweet.senti.smiley.removed.merged.neutral.added.dic.object");	

		pipeline.addConsumer("TwitterSentimentCorpusWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-twitter-sentiment-corpus-write-consumer.xml");
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFile", prop.getProperty("senti.corpus.file.search"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFields", prop.getProperty("senti.corpus.search.fields"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFieldsDelim", prop.getProperty("senti.corpus.search.fields.delim"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "stopwordSetFile", prop.getProperty("stopword.set.file"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterUserNameWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.search.filter.user.name.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterUserNamePostLimit", Integer.parseInt(prop.getProperty("senti.corpus.search.filter.user.name.post.limit")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterStopwordThreshold", Integer.parseInt(prop.getProperty("senti.corpus.search.filter.stopword.threshold")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterCosineSimilarityWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.search.cosine.similarity.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterCosineSimilarityThreshold", Float.parseFloat(prop.getProperty("senti.corpus.search.cosine.similarity.threshold")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "subjectivityScoreWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.search.subjectivity.score.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "swnSubjectivityFactor", Float.parseFloat(prop.getProperty("senti.corpus.search.swn.subjectivity.factor")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "tscSubjectivityFactor", Float.parseFloat(prop.getProperty("senti.corpus.search.tsc.subjectivity.factor")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "swnSubjectivityScoreWindowStart", Float.parseFloat(prop.getProperty("senti.corpus.search.swn.subjectivity.score.window.start")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "tscSubjectivityScoreWindowStart", Float.parseFloat(prop.getProperty("senti.corpus.search.tsc.subjectivity.score.window.start")));
		
		pipeline.run(true, "ConstructSentiCorpusFromSearchCorpus.xml");
	}
	
	private void constructSentiCorpusFromSampleCorpus() throws InvalidXMLException, IOException, ResourceConfigurationException, ResourceInitializationException {
		OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();

		pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
		pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("raw.corpus.sample.file"));
		pipeline.setReaderParameter("TwitterCorpusReader", "fields", "ID AUTHOR DATE TEXT");
		pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", "\\t");

		pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");

		pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");

		pipeline.addAnnotator("SentiWordNetAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-sentiment-score-annotator.xml");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "sentiScoreDicObjectFile", "resource/generated/sentiwordnet/SentiWordNet_3.0.0_20100908.stem.average.dic.object");	
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "maxWindowSize", 5);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "useStemToFindDic", Boolean.TRUE);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "usePosToFindDic", Boolean.TRUE);
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "posTagset", "BROWN_CORPUS");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "annotationTypeName", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameId", "id");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNamePositiveScore", "positiveScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameNegativeScore", "negativeScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameSubjectiveScore", "subjectiveScore");
		pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "featureNameObjectiveScore", "objectiveScore");

		pipeline.addAnnotator("TwitterSentimentScoreAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-twitter-sentiment-score-annotator.xml");
		pipeline.setAnnotatorParameter("TwitterSentimentScoreAnnotator", "sentiScoreDicObjectFile", "resource/generated/senti_corpus/mobile_devices_20120426.tweet.senti.smiley.removed.merged.neutral.added.dic.object");	

		pipeline.addConsumer("TwitterSentimentCorpusWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-twitter-sentiment-corpus-write-consumer.xml");
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFile", prop.getProperty("senti.corpus.file.sample"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFields", prop.getProperty("senti.corpus.sample.fields"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "corpusFieldsDelim", prop.getProperty("senti.corpus.sample.fields.delim"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "stopwordSetFile", prop.getProperty("stopword.set.file"));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterUserNameWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.sample.filter.user.name.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterUserNamePostLimit", Integer.parseInt(prop.getProperty("senti.corpus.sample.filter.user.name.post.limit")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterStopwordThreshold", Integer.parseInt(prop.getProperty("senti.corpus.sample.filter.stopword.threshold")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterCosineSimilarityWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.sample.cosine.similarity.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "filterCosineSimilarityThreshold", Float.parseFloat(prop.getProperty("senti.corpus.sample.cosine.similarity.threshold")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "subjectivityScoreWindowSize", Integer.parseInt(prop.getProperty("senti.corpus.sample.subjectivity.score.window.size")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "swnSubjectivityFactor", Float.parseFloat(prop.getProperty("senti.corpus.sample.swn.subjectivity.factor")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "tscSubjectivityFactor", Float.parseFloat(prop.getProperty("senti.corpus.sample.tsc.subjectivity.factor")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "swnSubjectivityScoreWindowStart", Float.parseFloat(prop.getProperty("senti.corpus.sample.swn.subjectivity.score.window.start")));
		pipeline.setConsumerParameter("TwitterSentimentCorpusWriteConsumer", "tscSubjectivityScoreWindowStart", Float.parseFloat(prop.getProperty("senti.corpus.sample.tsc.subjectivity.score.window.start")));
		
		pipeline.run(true, "constructSentiCorpusFromSampleCorpus.xml");	
	}
	
	private void constructBalancedSentiCorpus() throws IOException {
		balanceSentiment(prop.getProperty("senti.corpus.file.search"), prop.getProperty("senti.corpus.search.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("senti.corpus.search.fields"), "\\s+"),
				prop.getProperty("senti.corpus.file"), prop.getProperty("senti.corpus.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("senti.corpus.fields"), "\\s+"), false);
		
		balanceSentiment(prop.getProperty("senti.corpus.file.sample"), prop.getProperty("senti.corpus.sample.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("senti.corpus.sample.fields"), "\\s+"),
				prop.getProperty("senti.corpus.file"), prop.getProperty("senti.corpus.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("senti.corpus.fields"), "\\s+"), true);
		
		printCorpusStat(prop.getProperty("senti.corpus.file"), prop.getProperty("senti.corpus.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("senti.corpus.fields"), "\\s+"));
	}
	
	private void balanceSentiment(String file1, String fieldDelim1, int[] fields1, String file2, String fieldDelim2, int[] fields2, boolean append) throws IOException {
		Map<Integer,Integer> sentiFreq = TwitterCorpusStat.sentimentFreq(file1, fieldDelim1, fields1);
		
		int[] freq = new int[3];
		
		freq[0] = sentiFreq.get(OMTweet.POLARITY_POSITIVE);
		freq[1] = sentiFreq.get(OMTweet.POLARITY_NEGATIVE);
		freq[2] = sentiFreq.get(OMTweet.POLARITY_NEUTRAL);
		
		int[][] indices = new int[3][];
		for (int i = 0; i < 3; i++) {
			indices[i] = null;
		}
		
		int sbjDiff;
		if ((sbjDiff = Math.min(freq[0], freq[1]) * 2) > freq[2]) {
			sbjDiff = (sbjDiff - freq[3]) / 2;
		} else {
			sbjDiff = 0;
		}
		
		
		if (freq[0] != freq[1] || sbjDiff > 0) {
			int senti1 = 0;
			int senti2 = 1;
			if (freq[0] < freq[1]) {
				senti1 = 1;
				senti2 = 0;
			}
			
			indices[senti1] = randomIndices(freq[senti1], freq[senti2] - sbjDiff, System.currentTimeMillis());
			freq[senti1] = freq[senti2] - sbjDiff;
			if (sbjDiff > 0) {
				indices[senti2] = randomIndices(freq[senti2], freq[senti2] - sbjDiff, System.currentTimeMillis());
				freq[senti2] = freq[senti2] - sbjDiff;
			}
		}
		
		int sbj = freq[0] + freq[1];
		if (sbj < freq[2]) {
			indices[2] = randomIndices(freq[2], sbj, System.currentTimeMillis());
			freq[2] = sbj;
		} else if (sbj > freq[2]) {
			throw new IllegalStateException();
		}
		
		int[] idx = new int[3];
		int[] cursor = new int[3];
		for (int i = 0; i < 3; i++) {
			idx[i] = 0;
			cursor[i] = 0;
		}
		
		OMTwitterCorpusFileReader reader = new OMTwitterCorpusFileReader(file1, fieldDelim1, fields1);
		OMTwitterCorpusFileWriter writer = new OMTwitterCorpusFileWriter(file2, fieldDelim2, fields2, append);
		
		int senti = 0;
		while (reader.hasNext()) {
			OMTweet tweet = reader.next();
			
			switch(tweet.getPolarity()) {
			case OMTweet.POLARITY_POSITIVE:
				senti = 0;
				break;
			case OMTweet.POLARITY_NEGATIVE:
				senti = 1;
				break;
			case OMTweet.POLARITY_NEUTRAL:
				senti = 2;
				break;
			}
			
			if (indices[senti] == null) {
				writer.write(tweet);
				
			} else if (cursor[senti] < indices[senti].length && indices[senti][cursor[senti]] == idx[senti]) {
				writer.write(tweet);
				cursor[senti]++;
			}
			
			idx[senti]++;
		}
		
		writer.close();
		reader.close();
	}
	
	private int[] randomIndices(int size1, int size2, long seed) {
		if (size1 <= size2) {
			throw new IllegalArgumentException();
		}
		
		Random random = new Random(seed);
		List<Integer> indices = new ArrayList<Integer>(size1);
		for (int i = 0; i < size1; i++) {
			indices.add(i);
		}
		
		int rand1;
		int rand2;
		int tmp;
		
		for (int i = 0; i < size1; i++) {
			rand1 = random.nextInt(size1);
			rand2 = random.nextInt(size1);
			tmp = indices.get(rand1);
			indices.set(rand1, indices.get(rand2));
			indices.set(rand2, tmp);
		}
		
		for (int i = 0; i < size1; i++) {
			rand2 = random.nextInt(size1);
			tmp = indices.get(i);
			indices.set(i, indices.get(rand2));
			indices.set(rand2, tmp);
		}
		
		indices = indices.subList(0, size2);
		Collections.sort(indices);
		
		int[] chosen = new int[size2];
		for (int i = 0; i < size2; i++) {
			chosen[i] = indices.get(i);
		}
		
		return chosen;
	}
	
	private void printCorpusStat(String corpusFile, String fieldDelim, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		Map<Integer,Integer> freq = TwitterCorpusStat.sentimentFreq(corpusFile, fieldDelim, fields);
		
		logger.info("+-------------------------------------------------------------+");
		logger.info(OMTweet.POLARITY_STR_POSITIVE + " tweets: " + freq.get(OMTweet.POLARITY_POSITIVE));
		logger.info(OMTweet.POLARITY_STR_NEGATIVE + " tweets: " + freq.get(OMTweet.POLARITY_NEGATIVE));
		logger.info(OMTweet.POLARITY_STR_NEUTRAL + " tweets: " + freq.get(OMTweet.POLARITY_NEUTRAL));
		logger.info("+-------------------------------------------------------------+");
		logger.info(OMTweet.POLARITY_STR_SUBJECTIVE + " tweets: " + freq.get(OMTweet.POLARITY_SUBJECTIVE));
		logger.info(OMTweet.POLARITY_STR_OBJECTIVE + " tweets: " + freq.get(OMTweet.POLARITY_OBJECTIVE));
		logger.info("+-------------------------------------------------------------+");
		logger.info(OMTweet.POLARITY_STR_NOT_SPECIFIED + " tweets: " + freq.get(OMTweet.POLARITY_NOT_SPECIFIED));
		logger.info("+-------------------------------------------------------------+");
	}
}
