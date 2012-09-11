/**
 * 
 */
package com.maalaang.omtwitter.uima.consumer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.ml.SvmFeatureVector;
import com.maalaang.omtwitter.uima.ml.SvmTargetExtractor;
import com.maalaang.omtwitter.uima.ml.SvmFVFactory;

/**
 * @author Sangwon Park
 *
 */
public class SvmTrainingDataWriteConsumer extends CasConsumer_ImplBase {
	private static final String PARAM_SVM_FV_FACTORY_CLASS_NAME = "svmFVFactoryClassName";
	private static final String PARAM_SVM_TARGET_EXTRACTOR_CLASS_NAME = "svmTargetExtractorClassName";
	private static final String PARAM_SVM_TRAINING_DATA_FILE = "svmTrainingDataFile";
	
	private Logger logger = null;
	
	private SvmFVFactory fvFactory = null;
	
	private SvmTargetExtractor targetExtractor = null;
	
	private BufferedWriter bw = null;

	/* (non-Javadoc)
	 * @see org.apache.uima.collection.CasConsumer_ImplBase#initialize()
	 */
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		logger = getLogger();
		
		try {
			Class<?> fvFactoryClass = Class.forName((String)getConfigParameterValue(PARAM_SVM_FV_FACTORY_CLASS_NAME));
			fvFactory = (SvmFVFactory) fvFactoryClass.newInstance();
			
			Class<?> targetExtractorClass = Class.forName((String)getConfigParameterValue(PARAM_SVM_TARGET_EXTRACTOR_CLASS_NAME));
			targetExtractor = (SvmTargetExtractor) targetExtractorClass.newInstance();
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((String)getConfigParameterValue(PARAM_SVM_TRAINING_DATA_FILE)), "UTF-8"));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
	}

	public void processCas(CAS aCAS) throws ResourceProcessException {
		JCas jcas = null;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}
		
		SvmFeatureVector fv = fvFactory.createFeatureVectorFromJCas(jcas);
		int target = targetExtractor.extractTargetFromJCas(jcas);
		
		try {
			bw.write(String.valueOf(target));
			bw.write(' ');
			bw.write(fv.toString());
			bw.write('\n');
			bw.flush();
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.collection.CasConsumer_ImplBase#destroy()
	 */
	@Override
	public void destroy() {
		try {
			bw.close();
			bw = null;
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		super.destroy();
	}

}
