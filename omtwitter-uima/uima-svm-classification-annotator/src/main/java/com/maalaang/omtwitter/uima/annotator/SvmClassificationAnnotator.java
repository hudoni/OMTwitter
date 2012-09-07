/**
 * 
 */
package com.maalaang.omtwitter.uima.annotator;

import java.io.File;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.ml.SvmClassifier;
import com.maalaang.omtwitter.ml.SvmFeatureVector;
import com.maalaang.omtwitter.uima.ml.SvmFVFactory;
import com.maalaang.omtwitter.uima.ml.SvmTargetUpdator;

/**
 * @author Sangwon Park
 *
 */
public class SvmClassificationAnnotator extends JCasAnnotator_ImplBase {
	private static final String PARAM_SVM_MODEL_FILE = "svmModelFile";
	private static final String PARAM_SVM_FV_FACTORY_CLASS_NAME = "svmFVFactoryClassName";
	private static final String PARAM_SVM_TARGET_UPDATOR_CLASS_NAME = "svmTargetUpdatorClassName";
	
	private Logger logger = null;
	
	private SvmFVFactory fvFactory = null;
	private SvmTargetUpdator targetUpdator = null;
	private SvmClassifier svmClassifier = null;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		logger = aContext.getLogger();
		
		try {
			Class<?> fvFactoryClass = Class.forName((String) aContext.getConfigParameterValue(PARAM_SVM_FV_FACTORY_CLASS_NAME));
			fvFactory = (SvmFVFactory) fvFactoryClass.newInstance();
			
			Class<?> targetUpdatorClass = Class.forName((String) aContext.getConfigParameterValue(PARAM_SVM_TARGET_UPDATOR_CLASS_NAME));
			targetUpdator = (SvmTargetUpdator) targetUpdatorClass.newInstance();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		try {
			svmClassifier = new SvmClassifier(new File((String) aContext.getConfigParameterValue(PARAM_SVM_MODEL_FILE)));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		
		SvmFeatureVector fv = fvFactory.createFeatureVectorFromJCas(aJCas);
		double d = 0.0;
		
		if (targetUpdator.classificationRequired(aJCas)) {
			d = svmClassifier.classify(fv);
		}
		
		targetUpdator.updateTargetInJCas(aJCas, d);
	}

}
