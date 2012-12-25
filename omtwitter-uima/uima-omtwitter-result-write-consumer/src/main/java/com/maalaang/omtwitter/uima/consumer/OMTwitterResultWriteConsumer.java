/**
 * 
 */
package com.maalaang.omtwitter.uima.consumer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.maalaang.omtwitter.model.OMTweet;
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
	private final static String PARAM_PRINT_RESULT = "printResult";
	
	private final static int ENTITY_MAX = 128;
	
	private BufferedWriter bw = null;
	private Logger logger = null;
	private boolean skipTweetWithNoEntity = false;
	private String entityNoneLabel = null;
	private int[][] entityIdxList = null;
	private String[] entityLabelList = null;
	private boolean printResult = false;
	private int typeIdxSize = 0;
	private Set<String> entitySet = null;
	
	private final static String targetEntityType = "mobile_device";
	private Map<String,Integer> typeIdxMap = null;
	private Map<String,Integer[][]> targetEntityCntMap = null;
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
		entityIdxList = new int[ENTITY_MAX][2];
		entityLabelList = new String[ENTITY_MAX];
		printResult = (Boolean) getConfigParameterValue(PARAM_PRINT_RESULT);
		
		typeIdxMap = new HashMap<String,Integer>();
		targetEntityCntMap = new HashMap<String,Integer[][]>();
		
		typeIdxMap.put("battery", typeIdxSize++);
		typeIdxMap.put("camera", typeIdxSize++);
		typeIdxMap.put("carrier", typeIdxSize++);
		typeIdxMap.put("connectivity", typeIdxSize++);
		typeIdxMap.put("display", typeIdxSize++);
		typeIdxMap.put("generation", typeIdxSize++);
		typeIdxMap.put("input", typeIdxSize++);
		typeIdxMap.put("manufacturer", typeIdxSize++);
		typeIdxMap.put("memory", typeIdxSize++);
		typeIdxMap.put("networks", typeIdxSize++);
		typeIdxMap.put("os", typeIdxSize++);
		typeIdxMap.put("power", typeIdxSize++);
		typeIdxMap.put("processor", typeIdxSize++);
		typeIdxMap.put("size", typeIdxSize++);
		typeIdxMap.put("storage", typeIdxSize++);
		typeIdxMap.put("type", typeIdxSize++);
		typeIdxMap.put("weight", typeIdxSize++);
		
		entitySet = new HashSet<String>();
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
		String prevLabel = null;
		
		int idx = 0;
		boolean isEntity = false;
		entitySet.clear();
		
		FSIterator<Annotation> tokenAnnIt = jcas.getAnnotationIndex(TokenAnnotation.type).iterator();
		while (tokenAnnIt.hasNext()) {
			TokenAnnotation tokenAnn = (TokenAnnotation) tokenAnnIt.next();
			String label = tokenAnn.getEntityLabel();
			
			if (entityNoneLabel.equals(label)) {
				if (isEntity) {
					idx++;
					isEntity = false;
				}
			} else if (label.endsWith("_B")) {
				if (isEntity) {
					idx++;
				}
				isEntity = true;
				entityIdxList[idx][0] = tokenAnn.getBegin();
				entityIdxList[idx][1] = tokenAnn.getEnd();
				entityLabelList[idx] = label.substring(0, label.length() - 2);
			} else {
				entityIdxList[idx][1] = tokenAnn.getEnd();
			}
		}
		
		try {
			if (idx > 0 || !skipTweetWithNoEntity) {
				TweetAnnotation tweetAnn = (TweetAnnotation) jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
				StringBuffer sb = new StringBuffer();
				String text = tweetAnn.getCoveredText();
				sb.append('\n');
				sb.append(text);
				sb.append('\n');
				
				int polarityIndex = polarityToIndex(tweetAnn.getPolarity());
				
				for (int i = 0; i < idx; i++) {
					String entity = text.substring(entityIdxList[i][0], entityIdxList[i][1]);
					sb.append('\t');
					sb.append(entity);
					sb.append(" -> ");
					sb.append(entityLabelList[i]);
					sb.append('\n');
					
					if (entityLabelList[i].equals(targetEntityType)) {
//						entitySet.add(entity);
						String q = tweetAnn.getQuery().toLowerCase();
						if (q.indexOf("galaxy") >= 0) {
							entitySet.add("Samsung Galaxy S III");
						} else if (q.indexOf("ipad") >= 0) {
							entitySet.add("iPad Mini");
						}
					}
				}
				sb.append('\t');
				sb.append(tweetAnn.getPolarity());
				sb.append("\n");
				
				String res = sb.toString();
				bw.write(res);
				bw.flush();
				
				if (printResult) {
					logger.log(Level.INFO, res);
				}
				
				for (int i = 0; i < idx; i++) {
					try {
						if (!entityLabelList[i].equals(entityNoneLabel) && !entityLabelList[i].equals(targetEntityType)) {
							for (String entity : entitySet) {
								Integer[][] cnt = targetEntityCntMap.get(entity);
								if (cnt == null) {
									cnt = new Integer[typeIdxSize][3];
									for (int j = 0; j < typeIdxSize; j++) {
										cnt[j][0] = 0;
										cnt[j][1] = 0;
										cnt[j][2] = 0;
									}
									targetEntityCntMap.put(entity, cnt);
								}
								int typeIdx = typeIdxMap.get(entityLabelList[i]);
								cnt[typeIdx][polarityIndex]++; 
							}
						}
					} catch (Exception e) {
						logger.log(Level.SEVERE, "error on entity counting - " + entityLabelList[i]);
					}
				}
			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceProcessException(e);
		}
	}

	@Override
	public void destroy() {
		Set<Entry<String,Integer[][]>> set1 = targetEntityCntMap.entrySet();
		Set<Entry<String,Integer>> set2 = typeIdxMap.entrySet();
		String res = null;
		printResult = true;
		
		try {
			for (int j = 0; j < 3; j++) {
				int polarityIdx = j;
				for (Entry<String,Integer[][]> e1 : set1) {
					StringBuffer sb = new StringBuffer();
					sb.append("\n# ");
					sb.append(e1.getKey());
					sb.append(" :: ");
					sb.append(polarityString(polarityIdx));
					sb.append('\n');
					
					Integer[][] cnt = e1.getValue();
					for (Entry<String,Integer> e2 : set2) {
						int num = cnt[e2.getValue()][polarityIdx];
						sb.append(String.format("\t%-15s%4d ", e2.getKey(), num));
						for (int k = 0; k < num; k++) {
							sb.append('|');
						}
						sb.append('\n');
					}
					sb.append('\n');
					
					res = sb.toString();
					bw.write(res);
					bw.flush();
					if (printResult) {
						logger.log(Level.INFO, res);
					}
				}
			}
			
			for (Entry<String,Integer[][]> e1 : set1) {
				Integer[][] cnt = e1.getValue();
				
				long totalSum = 0;
				for (Entry<String,Integer> e2 : set2) {
					int idx = e2.getValue();
					totalSum += cnt[idx][0];
					totalSum += cnt[idx][1];
					totalSum += cnt[idx][2];
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append("\n# ");
				sb.append(e1.getKey());
				sb.append('\n');
				sb.append(String.format("\t%-15s%13s%13s%13s%13s", "property", "positive", "negative", "neutral", "sum"));
				sb.append('\n');
				
				for (Entry<String,Integer> e2 : set2) {
					int idx = e2.getValue();
					int posCnt = cnt[idx][0];
					int negCnt = cnt[idx][1];
					int neuCnt = cnt[idx][2];
					int sum = posCnt + negCnt + neuCnt;
					if (sum != 0) {
						sb.append(String.format("\t%-15s%6.2f%%(%4d)%6.2f%%(%4d)%6.2f%%(%4d)%6.2f%%(%4d)",
								e2.getKey(), (double)posCnt/(double)sum, posCnt, (double)negCnt/(double)sum, negCnt, (double)neuCnt/(double)sum, neuCnt, (double)sum/(double)totalSum, sum));
					} else {
						sb.append(String.format("\t%-15s%6.2f%%(%4d)%6.2f%%(%4d)%6.2f%%(%4d)%6.2f%%(%4d)",
								e2.getKey(), 0.0, 0, 0.0, 0, 0.0, 0, 0.0, 0));
					}
					sb.append('\n');
				}
				sb.append('\n');
				res = sb.toString();
				bw.write(res);
				bw.flush();
				if (printResult) {
					logger.log(Level.INFO, res);
				}
			}
		
			bw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage());
		}
		super.destroy();
	}
	
	private int polarityToIndex(String polarity) {
		if (polarity.equals(OMTweet.POLARITY_STR_POSITIVE)) {
			return 0;
		} else if (polarity.equals(OMTweet.POLARITY_STR_NEGATIVE)) {
			return 1;
		} else if (polarity.equals(OMTweet.POLARITY_STR_NEUTRAL)) {
			return 2;
		}
		return -1;
	}
	
	private String polarityString(int idx) {
		switch (idx) {
		case 0:
			return "POS";
		case 1:
			return "NEG";
		case 2:
			return "NEU";
		}
		return null;
	}
}
