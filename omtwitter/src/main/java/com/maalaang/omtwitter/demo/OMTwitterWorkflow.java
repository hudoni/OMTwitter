/**
 * 
 */
package com.maalaang.omtwitter.demo;

import java.io.IOException;

import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import com.maalaang.omtwitter.uima.pipeline.OMTwitterFixedFlowPipeline;


/**
 * @author Sangwon Park
 *
 */
public class OMTwitterWorkflow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			OMTwitterFixedFlowPipeline pipeline = new OMTwitterFixedFlowPipeline();
			
			pipeline.setReader("TwitterCorpusReader", "com/maalaang/omtwitter/uima/reader/uima-twitter-corpus-reader.xml");
			pipeline.setReaderParameter("TwitterCorpusReader", "twitterCorpusFile", "corpus/mobile_devices_20120426.tweet.senti.smiley.removed.merged.neutral.added");
			pipeline.setReaderParameter("TwitterCorpusReader", "fields", "IGNORE QUERY AUTHOR TEXT");
			pipeline.setReaderParameter("TwitterCorpusReader", "splitRegex", "\\t");
			
			pipeline.addAnnotator("StanfordPosAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-stanford-pos-annotator.xml");
			
			pipeline.addAnnotator("SnowballStemAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-snowball-stem-annotator.xml");
			
			pipeline.addAnnotator("NegExAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-negex-annotator.xml");
			pipeline.setAnnotatorParameter("NegExAnnotator", "negexWindowSize", 5);
			
			pipeline.addAnnotator("SentiWordNetAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-sentiment-score-annotator.xml");
			pipeline.setAnnotatorParameter("SentiWordNetAnnotator", "sentiScoreDicObjectFile", "resource/sentiwordnet/SentiWordNet_3.0.0_20100908.stem.average.dic.object");	
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
			pipeline.setAnnotatorParameter("TwitterSentimentScoreAnnotator", "sentiScoreDicObjectFile", "resource/twittersenticorpus/mobile_devices_20120426.tweet.senti.smiley.removed.merged.neutral.added.dic.object");	
			
			pipeline.addAnnotator("SvmSOClassificationAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-svm-classification-annotator.xml");
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmModelFile", "resource/svmclassifier/mobile_devices_20120426.tweet.senti.smiley.removed.merged.neutral.added.so.model");
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmFVFactoryClassName", "com.maalaang.omtwitter.uima.ml.SvmScoreSumUnigramExFVFactory");
			pipeline.setAnnotatorParameter("SvmSOClassificationAnnotator", "svmTargetUpdatorClassName", "com.maalaang.omtwitter.uima.ml.SvmSOTargetUpdator");
			
			pipeline.addAnnotator("SvmPNClassificationAnnotator", "com/maalaang/omtwitter/uima/annotator/uima-svm-classification-annotator.xml");
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmModelFile", "resource/svmclassifier/mobile_devices_20120426.tweet.senti.smiley.removed.merged.pn.model");
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmFVFactoryClassName", "com.maalaang.omtwitter.uima.ml.SvmScoreSumUnigramExFVFactory");
			pipeline.setAnnotatorParameter("SvmPNClassificationAnnotator", "svmTargetUpdatorClassName", "com.maalaang.omtwitter.uima.ml.SvmPNTargetUpdator");
			
			pipeline.addConsumer("XmiWriteConsumer", "com/maalaang/omtwitter/uima/consumer/uima-xmi-write-consumer.xml");
			pipeline.setConsumerParameter("XmiWriteConsumer", "outputDirectory", "E:/Development/UIMA Annotation Result/xmi/200");
			
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
