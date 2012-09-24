/**
 * 
 */
package com.maalaang.omtwitter.uima.consumer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.uima.type.TokenAnnotation;
import com.maalaang.omtwitter.uima.type.TweetAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class OMTwitterResultWriteConsumer extends CasConsumer_ImplBase {
	
	private final static String PARAM_RESULT_FILE = "resultFile";
	private final static String PARAM_SKIP_TWEET_WITH_NO_ENTITY = "skipTweetWithNoEntity";
	private final static String PARAM_ENTITY_NONE_LABEL = "entityNoneLabel";
	
	private final static int ENTITY_MAX = 128;
	
	private BufferedWriter bw = null;
	private Logger logger = null;
	private boolean skipTweetWithNoEntity = false;
	private String entityNoneLabel = null;
//	private String[][] entityList = null;
	private int[][] entityIdxList = null;
	private String[] entityLabelList = null;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		logger = getLogger();
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((String) getConfigParameterValue(PARAM_RESULT_FILE)), "UTF-8"));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		skipTweetWithNoEntity = (Boolean) getConfigParameterValue(PARAM_SKIP_TWEET_WITH_NO_ENTITY);
		entityNoneLabel = (String) getConfigParameterValue(PARAM_ENTITY_NONE_LABEL);
//		entityList = new String[ENTITY_MAX][2];
		entityIdxList = new int[ENTITY_MAX][2];
		entityLabelList = new String[ENTITY_MAX];
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
	 */
	public void processCas(CAS aCAS) throws ResourceProcessException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}
		
		boolean in = false;
		String entityLabel = null;
		
		int idx = 0;
		
		FSIterator<Annotation> tokenAnnIt = jcas.getAnnotationIndex(TokenAnnotation.type).iterator();
		while (tokenAnnIt.hasNext()) {
			TokenAnnotation tokenAnn = (TokenAnnotation) tokenAnnIt.next();
			String label = tokenAnn.getEntityLabel();
			
			if (entityNoneLabel.equals(label)) {
				if (in) {
					idx++;
					in = false;
				}
			} else if (!in) {
				entityIdxList[idx][0] = tokenAnn.getBegin();
				entityIdxList[idx][1] = tokenAnn.getEnd();
				
				entityLabel = tokenAnn.getEntityLabel();
				entityLabelList[idx] = entityLabel;
				in = true;
			} else if (entityLabel.equals(label)) {
				entityIdxList[idx][1] = tokenAnn.getEnd();
				
			} else {
				entityIdxList[++idx][0] = tokenAnn.getBegin();
				entityIdxList[idx][1] = tokenAnn.getEnd();
				
				entityLabel = label;
				entityLabelList[idx] = entityLabel;
			}
		}
		
		if (in) {
			idx++;
			in = false;
		}
		
		try {
			if (idx > 0 || !skipTweetWithNoEntity) {
				TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
				String text = tweetAnn.getCoveredText();
				bw.write(text);
				bw.write('\n');
				for (int i = 0; i < idx; i++) {
					bw.write('\t');
					bw.write(text.substring(entityIdxList[i][0], entityIdxList[i][1]));
					bw.write(" -> ");
					bw.write(entityLabelList[i]);
					bw.write('\n');
				}
				bw.write('\t');
				bw.write(tweetAnn.getPolarity());
				bw.write("\n\n");
				bw.flush();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}

	}

	@Override
	public void destroy() {
		try {
			bw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		super.destroy();
	}

}
