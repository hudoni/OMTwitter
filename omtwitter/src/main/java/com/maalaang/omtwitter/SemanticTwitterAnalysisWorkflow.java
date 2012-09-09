/**
 * 
 */
package com.maalaang.omtwitter;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.collection.CasConsumerDescription;
import org.apache.uima.collection.CollectionProcessingManager;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Logger;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

/**
 * @author Sangwon Park
 */
public class SemanticTwitterAnalysisWorkflow {

	private Logger logger = null;

//	private final String CR_TWEET_DESC = "src/sentitwitter/uima/cr/TweetStanfordSetReader.xml";
//	private final String CR_TWEET_DESC = "src/sentitwitter/uima/cr/TweetCrawledSetReader.xml";
	private final String CR_TWEET_DESC = "src/sentitwitter/uima/cr/TweetDBPediaSentiCorpusReader.xml";
	private final String AE_POS_DESC = "src/sentitwitter/uima/ae/StanfordPosAnnotator.xml";
	private final String AE_STEM_DESC = "src/org/tartarus/snowball/SnowballAnnotator.xml";
	private final String AE_SWN_DESC = "src/sentitwitter/uima/ae/SentiWordnetAvgScoreAnnotator.xml";
	private final String AE_NEG_DESC = "src/sentitwitter/uima/ae/NegExAnnotator.xml";
	private final String AE_TWEET_SENTI_DESC = "src/sentitwitter/uima/ae/TwitterSentiScoreAnnotator.xml";
	private final String AE_SVM_SBJ_DESC = "src/sentitwitter/uima/ae/SVMSubjectivityAnnotator.xml";
	private final String AE_SVM_SENTI_DESC = "src/sentitwitter/uima/ae/SVMSentimentAnnotator.xml";
	private final String AE_CRF_ENTITY_DESC = "src/sentitwitter/uima/ae/CRFEntityAnnotator.xml";
	private final String CC_ANNOTATION_WRITER_DESC = "src/sentitwitter/uima/cc/TweetXmiWriterCasConsumer.xml";
	private final String CC_EVAlUATOR_DESC = "src/sentitwitter/uima/cc/NerAndSaEvaluator.xml";
	private boolean processing = true;
	
	private Properties prop = null;
	
	/**
	 * @param args
	 * @throws InvalidXMLException 
	 * @throws IOException 
	 * @throws ResourceInitializationException 
	 * @throws CpeDescriptorException 
	 * @throws ResourceConfigurationException 
	 */
	public static void main(String[] args) {
		try {
			SemanticTwitterAnalysisWorkflow workflow = new SemanticTwitterAnalysisWorkflow();
//			workflow.run(false);
			workflow.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	public SemanticTwitterAnalysisWorkflow() throws FileNotFoundException, IOException {
		logger = UIMAFramework.getLogger();
//		prop = new Properties();
//		prop.load(new FileInputStream("src/sentitwitter/workflow/SemanticTwitterAnalysisWorkflow.properties"));
	}
	
	public void setProperty(String property, String value) {
		prop.setProperty(property, value);
	}
	
	public boolean isProcessing() {
		return processing;
	}
	
	public void run() throws IOException {
		XMLInputSource in = new XMLInputSource(ClassLoader.getSystemResource("uima-twitter-corpus-reader.xml"));
		
	}
	
	
	public void run(boolean waitUntilCompleted) throws IOException, InvalidXMLException, SAXException, ResourceInitializationException, ResourceConfigurationException {
		XMLInputSource in = new XMLInputSource(CR_TWEET_DESC);
		CollectionReaderDescription crTweetDesc = UIMAFramework.getXMLParser().parseCollectionReaderDescription(in);
//		crTweetDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("TweetSet", prop.getProperty("TweetStanfordSetReader.TweetSet"));
//		crTweetDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("TweetSet", prop.getProperty("TweetCrawledSetReader.TweetSet"));
		crTweetDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("TweetSet", prop.getProperty("TweetDBPediaSentiCorpusReader.TweetSet"));
		crTweetDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("TweetSetSize", Integer.parseInt(prop.getProperty("TweetDBPediaSentiCorpusReader.TweetSetSize")));

		in = new XMLInputSource(AE_POS_DESC);
		AnalysisEngineDescription aePosDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aePosDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("ModelFile", prop.getProperty("StanfordPosAnnotator.ModelFile"));
		
		in = new XMLInputSource(AE_STEM_DESC);
		AnalysisEngineDescription aeStemDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		
		in = new XMLInputSource(AE_SWN_DESC);
		AnalysisEngineDescription aeSwnDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aeSwnDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("DictionaryFile", prop.getProperty("SentiWordnetAvgScoreAnnotator.DictionaryFile"));
		
		in = new XMLInputSource(AE_NEG_DESC);
		AnalysisEngineDescription aeNegDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		
		in = new XMLInputSource(AE_TWEET_SENTI_DESC);
		AnalysisEngineDescription aeTweetSentiDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aeTweetSentiDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("DictionaryFile", prop.getProperty("TwitterSentiScoreAnnotator.DictionaryFile"));
		
		in = new XMLInputSource(AE_SVM_SBJ_DESC);
		AnalysisEngineDescription aeSvmSbjDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aeSvmSbjDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("ModelFile", prop.getProperty("SVMSubjectivityAnnotator.ModelFile"));
		aeSvmSbjDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("DictionaryFile", prop.getProperty("SVMSubjectivityAnnotator.DictionaryFile"));

		in = new XMLInputSource(AE_SVM_SENTI_DESC);
		AnalysisEngineDescription aeSvmSentiDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aeSvmSentiDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("ModelFile", prop.getProperty("SVMSentimentAnnotator.ModelFile"));
		aeSvmSentiDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("DictionaryFile", prop.getProperty("SVMSentimentAnnotator.DictionaryFile"));
		
		in = new XMLInputSource(AE_CRF_ENTITY_DESC);
		AnalysisEngineDescription aeCrfEntityDesc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aeCrfEntityDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("ModelFile", prop.getProperty("CRFEntityAnnotator.ModelFile"));
		
		in = new XMLInputSource(CC_EVAlUATOR_DESC);
		CasConsumerDescription ccEvaluatorDesc = UIMAFramework.getXMLParser().parseCasConsumerDescription(in);
		ccEvaluatorDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("EvaluationCorpus", prop.getProperty("NerAndSaEvaluator.EvaluationCorpus"));
		
		
		FixedFlow flow = UIMAFramework.getResourceSpecifierFactory().createFixedFlow();      
		
		AnalysisEngineDescription aggDesc = UIMAFramework.getResourceSpecifierFactory().createAnalysisEngineDescription();
		aggDesc.setPrimitive(false);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("POSTagger", aePosDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("Stemmer", aeStemDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("SentiWordnet", aeSwnDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("Negation", aeNegDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("TweetScore", aeTweetSentiDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("SvmSbj", aeSvmSbjDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("SvmSenti", aeSvmSentiDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("CrfEntity", aeCrfEntityDesc);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("Evaluator", ccEvaluatorDesc);
		
		boolean writeXmi = prop.getProperty("TweetXmiWriterCasConsumer.write").equals("true") ? true : false;
		if (writeXmi) {
			in = new XMLInputSource(CC_ANNOTATION_WRITER_DESC);
			CasConsumerDescription ccAnnotationWriterDesc = UIMAFramework.getXMLParser().parseCasConsumerDescription(in);
			ccAnnotationWriterDesc.getMetaData().getConfigurationParameterSettings().setParameterValue("OutputDirectory", prop.getProperty("TweetXmiWriterCasConsumer.OutputDirectory"));
			aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put("AnnotationWriter", ccAnnotationWriterDesc);
			flow.setFixedFlow(new String[] { "POSTagger", "Stemmer", "SentiWordnet", "Negation", "TweetScore", "SvmSbj", "SvmSenti", "CrfEntity", "Evaluator", "AnnotationWriter"});
			
		} else {
			flow.setFixedFlow(new String[] { "POSTagger", "Stemmer", "SentiWordnet", "Negation", "TweetScore", "SvmSbj", "SvmSenti", "CrfEntity", "Evaluator"});
		}

		aggDesc.getAnalysisEngineMetaData().setName("SentiTwitterWorkflow");
		aggDesc.getAnalysisEngineMetaData().setFlowConstraints(flow);
		aggDesc.getAnalysisEngineMetaData().getOperationalProperties().setMultipleDeploymentAllowed(false);
		aggDesc.toXML(new FileWriter("src/sentitwitter/workflow/SemanticTwitterAnalysisWorkflow.xml"));

		CollectionProcessingManager mCPM;
		mCPM = UIMAFramework.newCollectionProcessingManager();
		
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggDesc);
		mCPM.setAnalysisEngine(ae);
		
		mCPM.process(UIMAFramework.produceCollectionReader(crTweetDesc));
//		mCPM.addStatusCallbackListener(new CPMCallbackListener());
		
		if (waitUntilCompleted) {
			while (true) {
				if (isProcessing()) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				} else {
					break;
				}
			}
		}
	}
	
//	private class CPMCallbackListener implements StatusCallbackListener {
//		private int processedEntityCnt = 0;
//		
//		@Override
//		public void initializationComplete() {
//			logger.log(Level.INFO, "Collection Processing Manager is initialized");
//		}
//
//		@Override
//		public void batchProcessComplete() {
//		}
//
//		@Override
//		public void collectionProcessComplete() {
//			processing = false;
//		}
//
//		@Override
//		public void paused() {
//		}
//
//		@Override
//		public void resumed() {
//		}
//
//		@Override
//		public void aborted() {
//			logger.log(Level.INFO, "Collection Processing Manager is aborted");
//		}
//
//		@Override
//		public void entityProcessComplete(CAS arg0, EntityProcessStatus arg1) {
//			processedEntityCnt++;
//			
//			if (processedEntityCnt % 1000 == 0) {
//				logger.log(Level.INFO, processedEntityCnt + " entities are processed");
//			}
//		}
//	}
}
