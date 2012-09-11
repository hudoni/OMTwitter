package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import com.maalaang.omtwitter.io.LogSystemStream;
import com.maalaang.omtwitter.uima.pipeline.OMTwitterFixedFlowPipeline;

public class ConstructTwitterSentimentCorpus {
	private Properties prop = null;
	
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
//			LogSystemStream.redirectErrToLog(Level.DEBUG);
			
			ConstructTwitterSentimentCorpus con = new ConstructTwitterSentimentCorpus(prop);
			con.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConstructTwitterSentimentCorpus(Properties prop) {
		this.prop = prop;
	}
	
	public void run() throws Exception {
		writeSentiCorpusFromSearchCorpus();
		writeSentiCorpusFromSampleCorpus();
		mergeSentiCorpus();
	}
	
	private void writeSentiCorpusFromSearchCorpus() throws InvalidXMLException, IOException, ResourceConfigurationException, ResourceInitializationException {
		OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();

		pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
		pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("raw.corpus.search.file"));
		pipeline.setReaderParameter("TwitterCorpusReader", "fields", "ID AUTHOR DATE QUERY TEXT");
		pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", "\\t");

		pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");

		pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");

		pipeline.addAnnotator("NegExAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-negex-annotator.xml");
		pipeline.setAnnotatorParameter("NegExAnnotator", "negexWindowSize", 5);

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

		pipeline.addConsumer("XmiWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-xmi-write-consumer.xml");
		pipeline.setConsumerParameter("XmiWriteConsumer", "outputDirectory", "E:/Development/UIMA Annotation Result/xmi/200");

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
		
		pipeline.run(true);
	}
	
	private void writeSentiCorpusFromSampleCorpus() throws InvalidXMLException, IOException, ResourceConfigurationException, ResourceInitializationException {
		OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();

		pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
		pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("raw.corpus.sample.file"));
		pipeline.setReaderParameter("TwitterCorpusReader", "fields", "ID AUTHOR DATE QUERY TEXT");
		pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", "\\t");

		pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");

		pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");

		pipeline.addAnnotator("NegExAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-negex-annotator.xml");
		pipeline.setAnnotatorParameter("NegExAnnotator", "negexWindowSize", 5);

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

		pipeline.addConsumer("XmiWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-xmi-write-consumer.xml");
		pipeline.setConsumerParameter("XmiWriteConsumer", "outputDirectory", "E:/Development/UIMA Annotation Result/xmi/200");

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
		
		pipeline.run(true);	
	}
	
	private void mergeSentiCorpus() {
		
	}
}
