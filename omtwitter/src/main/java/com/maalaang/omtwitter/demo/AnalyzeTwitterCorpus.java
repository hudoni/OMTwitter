/**
 * 
 */
package com.maalaang.omtwitter.demo;

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


/**
 * @author Sangwon Park
 *
 */
public class AnalyzeTwitterCorpus {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
				
			LogSystemStream.redirectErrToLog(Level.ERROR);
			
			OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();
			
			pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
			pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("target.twitter.corpus.file"));
			pipeline.setReaderParameter("TwitterCorpusReader", "fields", prop.getProperty("target.twitter.corpus.fields"));
			pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", prop.getProperty("target.twitter.corpus.fields.delim"));
			
			pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");
			
			pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");
			
			pipeline.addAnnotator("NegExAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-negex-annotator.xml");
			pipeline.setAnnotatorParameter("NegExAnnotator", "negexWindowSize", 5);
			
			pipeline.addAnnotator("SentiWordNetAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-sentiment-score-annotator.xml");
			pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "sentiScoreDicObjectFile", prop.getProperty("swn.dic.object"));
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
			pipeline.setAnnotatorParameter("TwitterSentimentScoreAnnotator", "sentiScoreDicObjectFile", prop.getProperty("tsc.dic.object"));
			
			pipeline.addAnnotator("SvmSOClassificationAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-svm-classification-annotator.xml");
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmModelFile", prop.getProperty("svm.so.model"));
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmFVFactoryClassName", "com.maalaang.omtwitter.uima.ml.SvmScoreSumUnigramExFVFactory");
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmTargetUpdatorClassName", "com.maalaang.omtwitter.uima.ml.SvmSOTargetUpdator");
			
			pipeline.addAnnotator("SvmPNClassificationAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-svm-classification-annotator.xml");
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmModelFile", prop.getProperty("svm.pn.model"));
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmFVFactoryClassName", "com.maalaang.omtwitter.uima.ml.SvmScoreSumUnigramExFVFactory");
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmTargetUpdatorClassName", "com.maalaang.omtwitter.uima.ml.SvmPNTargetUpdator");
			
			pipeline.addAnnotator("CrfClassificationAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-crf-classification-annotator.xml");
			pipeline.setAnnotatorParameter("CrfClassificationAnnotator", "crfModelFile", prop.getProperty("crf.model"));
			
//			pipeline.addConsumer("XmiWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-xmi-write-consumer.xml");
//			pipeline.setConsumerParameter("XmiWriteConsumer", "outputDirectory", prop.getProperty("annotation.result.dir"));
			
			pipeline.addConsumer("OMTwitterResultWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-omtwitter-result-write-consumer.xml");
			pipeline.setConsumerParameter("OMTwitterResultWriteConsumer", "resultFile", prop.getProperty("omtwitter.result.file"));
			pipeline.setConsumerParameter("OMTwitterResultWriteConsumer", "skipTweetWithNoEntity", Boolean.parseBoolean(prop.getProperty("omtwitter.result.skip.no.entity")));
			pipeline.setConsumerParameter("OMTwitterResultWriteConsumer", "entityNoneLabel", prop.getProperty("omtwitter.result.entity.none.label"));
			
			pipeline.run(true);
			
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (ResourceConfigurationException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
