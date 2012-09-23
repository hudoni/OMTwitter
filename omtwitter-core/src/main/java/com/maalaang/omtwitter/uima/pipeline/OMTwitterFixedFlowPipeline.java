/**
 * 
 */
package com.maalaang.omtwitter.uima.pipeline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumerDescription;
import org.apache.uima.collection.CollectionProcessingManager;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

/**
 * @author Sangwon Park
 *
 */
public class OMTwitterFixedFlowPipeline {
	private static final int ENTITY_CNT_FOR_LOG = 1000;
	private static final int THREAD_WAIT_INTERVAL = 5000;
	
	private CollectionReaderDescription readerDesc = null;
	
	private AnalysisEngineDescription aggDesc = null;
	
	private CollectionProcessingManager cpm = null;
	
	private List<String> annotatorList = null;
	
	private List<String> consumerList = null;
	
	private Logger logger = null;
	
	private long entityProcessCount = 0;
	
	private boolean isProcessing = false;
	
	public OMTwitterFixedFlowPipeline() {
		aggDesc = UIMAFramework.getResourceSpecifierFactory().createAnalysisEngineDescription();
		aggDesc.setPrimitive(false);
		
		annotatorList = new LinkedList<String>();
		consumerList = new LinkedList<String>();
		
		logger = UIMAFramework.getLogger();
	}
	 
	public void setReader(String name, InputStream descInputStream) throws IOException, InvalidXMLException {
		setReader(name, new XMLInputSource(descInputStream, null));
	}
	
	public void setReader(String name, String descName) throws IOException, InvalidXMLException {
		setReader(name, new XMLInputSource(getClass().getClassLoader().getResourceAsStream(descName), null));
	}
	
	private void setReader(String name, XMLInputSource in) throws IOException, InvalidXMLException {
		readerDesc = UIMAFramework.getXMLParser().parseCollectionReaderDescription(in);
	}
	
	public void setReaderParameter(String name, String param, Object value) {
		readerDesc.getMetaData().getConfigurationParameterSettings().setParameterValue(param, value);
	}
	
	public void setReaderParameter(String name, String group, String param, Object value) {
		readerDesc.getMetaData().getConfigurationParameterSettings().setParameterValue(group, param, value);
	}
	
	public void addAnnotator(String name, InputStream descInputStream) throws IOException, InvalidXMLException {
		addAnnotator(name, new XMLInputSource(descInputStream, null));
	}
	
	public void addAnnotator(String name, String descName) throws IOException, InvalidXMLException {
		addAnnotator(name, new XMLInputSource(getClass().getClassLoader().getResourceAsStream(descName), null));
	}
	
	private void addAnnotator(String name, XMLInputSource in) throws IOException, InvalidXMLException {
		AnalysisEngineDescription desc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put(name, desc);
		annotatorList.add(name);
	}
	
	public void setAnnotatorParameter(String name, String param, Object value) {
		AnalysisEngineDescription desc = (AnalysisEngineDescription) aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().get(name);
		desc.getMetaData().getConfigurationParameterSettings().setParameterValue(param, value);
	}
	
	public void setAnnotatorParameter(String name, String group, String param, Object value) {
		AnalysisEngineDescription desc = (AnalysisEngineDescription) aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().get(name);
		desc.getMetaData().getConfigurationParameterSettings().setParameterValue(group, param, value);
	}
	
	public void addConsumer(String name, InputStream descInputStream) throws IOException, InvalidXMLException {
		addConsumer(name, new XMLInputSource(descInputStream, null));
	}
	
	public void addConsumer(String name, String descName) throws IOException, InvalidXMLException {
		addConsumer(name, new XMLInputSource(getClass().getClassLoader().getResourceAsStream(descName), null));
	}
	
	private void addConsumer(String name, XMLInputSource in) throws IOException, InvalidXMLException {
		CasConsumerDescription desc = UIMAFramework.getXMLParser().parseCasConsumerDescription(in);
		aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().put(name, desc);
		consumerList.add(name);
	}
	
	public void setConsumerParameter(String name, String param, Object value) {
		CasConsumerDescription desc = (CasConsumerDescription) aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().get(name);
		desc.getMetaData().getConfigurationParameterSettings().setParameterValue(param, value);
	}
	
	public void setConsumerParameter(String name, String group, String param, Object value) {
		CasConsumerDescription desc = (CasConsumerDescription) aggDesc.getDelegateAnalysisEngineSpecifiersWithImports().get(name);
		desc.getMetaData().getConfigurationParameterSettings().setParameterValue(group, param, value);
	}
	
	public void run(boolean wait) throws ResourceConfigurationException, ResourceInitializationException {
		run(wait, true);
	}
	
	public void run(boolean wait, boolean writeAggDescFile) throws ResourceConfigurationException, ResourceInitializationException {
		run(wait, writeAggDescFile, this.getClass().getSimpleName() + ".xml");
	}
	
	public void run(boolean wait, boolean writeAggDescFile, String aggDescFile) throws ResourceConfigurationException, ResourceInitializationException {
		FixedFlow flow = UIMAFramework.getResourceSpecifierFactory().createFixedFlow();
		
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(annotatorList);
		list.addAll(consumerList);
		
		flow.setFixedFlow(list.toArray(new String[annotatorList.size() + consumerList.size()]));
		
		aggDesc.getAnalysisEngineMetaData().setName(this.getClass().getSimpleName());
		aggDesc.getAnalysisEngineMetaData().setFlowConstraints(flow);
		aggDesc.getAnalysisEngineMetaData().getOperationalProperties().setMultipleDeploymentAllowed(false);
		
		if (writeAggDescFile) {
			try {
				aggDesc.toXML(new OutputStreamWriter(new FileOutputStream(aggDescFile), "UTF-8"));
				logger.log(Level.INFO, "description file for the pipeline was created - " + aggDescFile);
			} catch (UnsupportedEncodingException e) {
				logger.log(Level.WARNING, e.getMessage());
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, e.getMessage());
			} catch (SAXException e) {
				logger.log(Level.WARNING, e.getMessage());
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
		}
		
		isProcessing = true;
		
		cpm = UIMAFramework.newCollectionProcessingManager();
		cpm.setAnalysisEngine(UIMAFramework.produceAnalysisEngine(aggDesc));
		cpm.process(UIMAFramework.produceCollectionReader(readerDesc));
		
		cpm.addStatusCallbackListener(new StatusCallbackListener() {
			public void resumed() {
				logger.log(Level.INFO, "CPM resumed");
			}
			
			public void paused() {
				logger.log(Level.INFO, "CPM paused");
			}
			
			public void initializationComplete() {
				logger.log(Level.INFO, "CPM initialization completed");
			}
			
			public void collectionProcessComplete() {
				logger.log(Level.INFO, "CPM processing completed");
				isProcessing = false;
			}
			
			public void batchProcessComplete() {
				logger.log(Level.INFO, "CPM batch process completed");
			}
			
			public void aborted() {
				logger.log(Level.SEVERE, "CPM aborted");
				isProcessing = false;
			}
			
			public void entityProcessComplete(CAS arg0, EntityProcessStatus arg1) {
				entityProcessCount++;
				if (entityProcessCount % ENTITY_CNT_FOR_LOG == 0) {
					logger.log(Level.INFO, "CPM entity process completed - " + entityProcessCount + " entities");
				}
			}
		});
			
		while (wait && isProcessing) {
			try {
				Thread.sleep(THREAD_WAIT_INTERVAL);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "sleep interrupted");
			}
		}
	}
}
