/**
 * 
 */
package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Level;

import com.maalaang.omtwitter.io.LogSystemStream;
import com.maalaang.omtwitter.ml.SvmTrainer;
import com.maalaang.omtwitter.uima.pipeline.OMTwitterFixedFlowPipeline;

/**
 * @author Sangwon Park
 *
 */
public class TrainPNSvmClassifier {
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
			pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", prop.getProperty("senti.corpus.file"));
			pipeline.setReaderParameter("TwitterCorpusReader", "fields", prop.getProperty("senti.corpus.fields"));
			pipeline.setReaderParameter("TwitterCorpusReader", "fieldsDelimiter", prop.getProperty("senti.corpus.fields.delim"));

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

			String annResultDir = prop.getProperty("annotation.result.dir", null);
			if (annResultDir != null) {
				pipeline.addConsumer("XmiWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-xmi-write-consumer.xml");
				pipeline.setConsumerParameter("XmiWriteConsumer", "outputDirectory", annResultDir);
			}

			pipeline.addConsumer("SvmTrainingDataWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-svm-training-data-write-consumer.xml");
			pipeline.setConsumerParameter("SvmTrainingDataWriteConsumer", "svmFVFactoryClassName", "com.maalaang.omtwitter.uima.ml.SvmScoreSumUnigramExFVFactory");
			pipeline.setConsumerParameter("SvmTrainingDataWriteConsumer", "svmTargetExtractorClassName", "com.maalaang.omtwitter.uima.ml.SvmPNTargetExtractor");
			pipeline.setConsumerParameter("SvmTrainingDataWriteConsumer", "svmTrainingDataFile", prop.getProperty("svm.training.data.file"));

			pipeline.run(true);

			SvmTrainer.train(prop.getProperty("svm.training.data.file"), prop.getProperty("svm.model"), SvmTrainer.numOfExamples(prop.getProperty("svm.training.data.file")));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
