/**
 * 
 */
package com.maalaang.omtwitter.uima.annotator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.resource.SentimentScore;
import com.maalaang.omtwitter.resource.SentimentScoreDictionary;
import com.maalaang.omtwitter.resource.SentimentScoreDictionaryFactory;
import com.maalaang.omtwitter.uima.type.SentenceAnnotation;
import com.maalaang.omtwitter.uima.type.TokenAnnotation;


/**
 * @author Sangwon Park
 *
 */
public class SentimentScoreAnnotator extends CasAnnotator_ImplBase {
	private final static String PARAM_MAX_WINDOW_SIZE = "maxWindowSize";
	private final static String PARAM_SENTI_SCORE_DIC_OBJ_FILE = "sentiScoreDicObjectFile";
	private final static String PARAM_USE_STEM_TO_FIND_DIC = "useStemToFindDic";
	private final static String PARAM_USE_POS_TO_FIND_DIC = "usePosToFindDic";
	private final static String PARAM_POS_TAGSET = "posTagset";

	private final static String PARAM_ANNOTATION_TYPE_NAME = "annotationTypeName";
	private final static String PARAM_FEATURE_NAME_ID = "featureNameId";
	private final static String PARAM_FEATURE_NAME_POSITIVE_SCORE = "featureNamePositiveScore";
	private final static String PARAM_FEATURE_NAME_NEGATIVE_SCORE = "featureNameNegativeScore";
	private final static String PARAM_FEATURE_NAME_SUBJECTIVE_SCORE = "featureNameSubjectiveScore";
	private final static String PARAM_FEATURE_NAME_OBJECTIVE_SCORE = "featureNameObjectiveScore";

	private final static int ANNOTATION_FEATURE_NUM = 5;

	public final static String POS_TAGSET_PENN_TREE_BANK = "PENN_TREE_BANK";
	public final static String POS_TAGSET_BROWN_CORPUS = "BROWN_CORPUS";
	public final static String POS_TAGSET_WORDNET = "WORDNET";
	
	private final static String TYPE_NAME_SENTENCE_ANNOTATION = "com.maalaang.omtwitter.uima.type.SentenceAnnotation";
	private final static String TYPE_NAME_TOKEN_ANNOTATION = "com.maalaang.omtwitter.uima.type.TokenAnnotation";

	private Logger logger = null;

	private int maxWindowSize = 0;

	private SentimentScoreDictionary sentiScoreDic = null;

	private boolean useStemToFindDic = false;
	private boolean usePosToFindDic = false;

	private int posTagset = 0;

	private String annotationTypeName = null;

	private Feature[] features = null;
	private String[] featureNames = null;
	
	private Type sentenceType = null;
	private Type tokenType = null;
	private Type sentiScoreType = null;
	

	private ArrayList<String> tokenList = null;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		logger = aContext.getLogger();

		maxWindowSize = (Integer) aContext.getConfigParameterValue(PARAM_MAX_WINDOW_SIZE);

		try {
			sentiScoreDic = SentimentScoreDictionaryFactory.loadFromSerializedFile((String) aContext.getConfigParameterValue(PARAM_SENTI_SCORE_DIC_OBJ_FILE));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}

		useStemToFindDic = (Boolean) aContext.getConfigParameterValue(PARAM_USE_STEM_TO_FIND_DIC);
		usePosToFindDic = (Boolean) aContext.getConfigParameterValue(PARAM_USE_POS_TO_FIND_DIC);

		try {
			Object posTagsetValue = aContext.getConfigParameterValue(PARAM_POS_TAGSET);;
			if (posTagsetValue != null) {
				posTagset = posTagsetId((String) posTagsetValue);
			}
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}

		annotationTypeName = (String) aContext.getConfigParameterValue(PARAM_ANNOTATION_TYPE_NAME);

		featureNames = new String[ANNOTATION_FEATURE_NUM];
		featureNames[0] = (String) aContext.getConfigParameterValue(PARAM_FEATURE_NAME_ID);
		featureNames[1] = (String) aContext.getConfigParameterValue(PARAM_FEATURE_NAME_POSITIVE_SCORE);
		featureNames[2] = (String) aContext.getConfigParameterValue(PARAM_FEATURE_NAME_NEGATIVE_SCORE);
		featureNames[3] = (String) aContext.getConfigParameterValue(PARAM_FEATURE_NAME_SUBJECTIVE_SCORE);
		featureNames[4] = (String) aContext.getConfigParameterValue(PARAM_FEATURE_NAME_OBJECTIVE_SCORE);

		features = new Feature[ANNOTATION_FEATURE_NUM];

		tokenList = new ArrayList<String>();
		
		logger.log(Level.INFO, "sentiment score annotator initialized");

	}

	public static int posTagsetId(String posTagset) {
		if (POS_TAGSET_BROWN_CORPUS.equals(posTagset)) {
			return SentimentScoreDictionary.POS_TAGSET_BROWN_CORPUS;
		} else if (POS_TAGSET_PENN_TREE_BANK.equals(posTagset)) {
			return SentimentScoreDictionary.POS_TAGSET_PENN_TREE_BANK;
		} else if (POS_TAGSET_WORDNET.equals(posTagset)) {
			return SentimentScoreDictionary.POS_TAGSET_WORD_NET;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void createAnnotation(CAS aCas, SentimentScore score, int begin, int end) {
		AnnotationFS ann = aCas.createAnnotation(sentiScoreType, begin, end);
		ann.setIntValue(features[0], score.getId());
		ann.setDoubleValue(features[1], score.getPositiveScore());
		ann.setDoubleValue(features[2], score.getNegativeScore());
		ann.setDoubleValue(features[3], score.getSubjectiveScore());
		ann.setDoubleValue(features[4], score.getObjectiveScore());
		aCas.addFsToIndexes(ann);
	}

	@Override
	public void typeSystemInit(TypeSystem aTypeSystem) throws AnalysisEngineProcessException {
		super.typeSystemInit(aTypeSystem);
		
		sentenceType = aTypeSystem.getType(TYPE_NAME_SENTENCE_ANNOTATION);
		tokenType = aTypeSystem.getType(TYPE_NAME_TOKEN_ANNOTATION);
		sentiScoreType = aTypeSystem.getType(annotationTypeName);
		
		for (int i = 0; i < ANNOTATION_FEATURE_NUM; i++) {
			features[i] = sentiScoreType.getFeatureByBaseName(featureNames[i]);
		}
	}

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		
		TokenAnnotation tokenAnn = null;
		SentimentScore score = null;
		FSIterator<AnnotationFS> sentenceAnnIt = aCAS.getAnnotationIndex(sentenceType).iterator();
		
		while (sentenceAnnIt.hasNext()) {
			SentenceAnnotation sentAnn = (SentenceAnnotation) sentenceAnnIt.next();
			tokenList.clear();
			
			FSIterator<AnnotationFS> tokenAnnIt = aCAS.getAnnotationIndex(tokenType).subiterator(sentAnn);
			if (useStemToFindDic) {
				while (tokenAnnIt.hasNext()) {
					tokenAnn = (TokenAnnotation) tokenAnnIt.next();
					tokenList.add(tokenAnn.getCoveredText());
				}
			} else {
				while (tokenAnnIt.hasNext()) {
					tokenAnn = (TokenAnnotation) tokenAnnIt.next();
					tokenList.add(tokenAnn.getStem());
				}
			}
			tokenAnnIt.moveToFirst();

			for (int i = 0; i < tokenList.size(); i++) {
				tokenAnn = (TokenAnnotation) tokenAnnIt.get();
				String posTag = tokenAnn.getPosTag();

				int lastIndex = i + maxWindowSize - 1;
				if (lastIndex >= tokenList.size()) {
					lastIndex = tokenList.size() - 1;
				}

				for ( ; i <= lastIndex; lastIndex--) {
					StringBuilder expr = new StringBuilder();
					expr.append(tokenList.get(i));

					for (int j = i + 1; j <= lastIndex; j++) {
						expr.append(' ');
						expr.append(tokenList.get(j));
					}

					String key = expr.toString().toLowerCase().replaceAll("(-|_)", " ");

					if (usePosToFindDic) {
						score = sentiScoreDic.find(key, posTag, posTagset);
					} else {
						score = sentiScoreDic.find(key);
					}
					
					if (score != null) {
						break;
					}
				}

				if (score != null) {
					int begin = tokenAnn.getBegin();
					for ( ; i < lastIndex; i++) {
						tokenAnnIt.moveToNext();
					}
					int end = ((TokenAnnotation) tokenAnnIt.get()).getEnd();

					createAnnotation(aCAS, score, begin, end);
				}

				if (tokenAnnIt.hasNext()) {
					tokenAnnIt.moveToNext();
				}
			}
		}	
	}
}
