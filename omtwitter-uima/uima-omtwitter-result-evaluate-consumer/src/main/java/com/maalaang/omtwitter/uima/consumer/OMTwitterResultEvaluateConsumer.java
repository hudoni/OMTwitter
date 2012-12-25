/**
 * 
 */
package com.maalaang.omtwitter.uima.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.uima.type.TokenAnnotation;
import com.maalaang.omtwitter.uima.type.TweetAnnotation;

/**
 * @author Sangwon Park
 *
 */
public class OMTwitterResultEvaluateConsumer extends CasConsumer_ImplBase {

	private final static String PARAM_EVALUATION_CORPUS_FILE = "evaluationCorpusFile";
	private final static String PARAM_EVALUATION_CORPUS_DELIM = "evaluationCorpusDelim";
	private final static String PARAM_EVALUATION_CORPUS_FIELDS = "evaluationCorpusFields";
	private final static String PARAM_PRINT_RESULT = "printResult";
	private final static String PARAM_NAMED_ENTITY_TAGS = "namedEntityTags";
	private final static String PARAM_LABEL_NONE = "labelNone";
	
	private OMTwitterCorpusFileReader evalCorpusReader = null; 
	private Logger logger = null;
	private boolean printResult = false;
	private String labelNone = null;
	private int labelNoneIdx = 0;
	
	private int cnt = 0;
	private int senti[][] = null;
	private Map<String,Integer> map = null;
	private int[][] stat = null;
	private int nerCnt = 0;
	private int nerCorrrect = 0;
	private int nerClassified = 0;
	private int[] classifiedEntityCnt = null; 
	private int[] answerEntityCnt = null; 
	

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		logger = getLogger();
		
		try {
			evalCorpusReader = new OMTwitterCorpusFileReader((String)getConfigParameterValue(PARAM_EVALUATION_CORPUS_FILE),
					(String)getConfigParameterValue(PARAM_EVALUATION_CORPUS_DELIM), OMTwitterCorpusFile.fieldNameToId((String)getConfigParameterValue(PARAM_EVALUATION_CORPUS_FIELDS), " "));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new ResourceInitializationException(e);
		}
		
		printResult = (Boolean)getConfigParameterValue(PARAM_PRINT_RESULT);
		
		String neTagsStr = (String)getConfigParameterValue(PARAM_NAMED_ENTITY_TAGS);
		if (neTagsStr == null) {
			throw new ResourceInitializationException();
		}
		
		labelNone = (String)getConfigParameterValue(PARAM_LABEL_NONE);
		
		String[] neTags = neTagsStr.split(" ");
		int idx = 0;
		map = new HashMap<String,Integer>();  
		
		for (String tag : neTags) {
			map.put(tag + "_B", idx++);
			map.put(tag + "_M", idx++);
			map.put(tag + "_E", idx++);
		}
		map.put(labelNone, idx++);
		labelNoneIdx = idx - 1;
		
		stat = new int[idx][3];
		senti = new int[3][3];
		classifiedEntityCnt = new int[idx/3];
		answerEntityCnt = new int[idx/3];
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
		
		TweetAnnotation tweetAnn = (TweetAnnotation)jcas.getAnnotationIndex(TweetAnnotation.type).iterator().next();
		OMTweet answerTweet = evalCorpusReader.next();
		
		if(!answerTweet.getId().equals(tweetAnn.getId())) {
			logger.log(Level.SEVERE, "target corpus and evaluation corpus don't match to each other - " + answerTweet.getId() + ", " + tweetAnn.getId());
			throw new ResourceProcessException();
		}
		
		String[] entity = extractEntityTags(answerTweet.getText());
		
		String classified = null;
		String prevClassified = null;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("\n[");
			sb.append(answerTweet.getPolarityString());
			sb.append("=>");
			sb.append(tweetAnn.getPolarity());
			sb.append("] ");
			sb.append(tweetAnn.getCoveredText());
			sb.append('\n');
			
			FSIterator<Annotation> tokenAnnIter = jcas.getAnnotationIndex(TokenAnnotation.type).iterator();
			TokenAnnotation tokenAnn = null;
			
			int i = 0;
			int prevClassifiedIdx = labelNoneIdx;
			int prevAnswerIdx = labelNoneIdx;
			String classifiedEntityStr = "";
			String answerEntityStr = "";
			
			while (tokenAnnIter.hasNext()) {
				tokenAnn = (TokenAnnotation) tokenAnnIter.next();
				
				classified = tokenAnn.getEntityLabel();
				String answer = entity[i];
				boolean correct = false;
				if (classified.equals(answer)) {
					correct = true;
				}
				
				int classifiedIdx = 0;
				int answerIdx = 0;
				try {
					answerIdx = map.get(answer);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "wrong annotation on the evaluation corpus - tweet id: " + answerTweet.getId() + ", answerTag=" + answer);
					logger.log(Level.SEVERE, e.getMessage());
					answerIdx = map.get(labelNone);
				}
				try {
					classifiedIdx = map.get(classified);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "wrong annotation from the NER - tweet id: " + answerTweet.getId() + ", classifiedTag=" + classified);
					logger.log(Level.SEVERE, e.getMessage());
					classifiedIdx = map.get(labelNone);
				}
				
				stat[classifiedIdx][0]++;
				stat[answerIdx][1]++;
				
				if (correct) {
					stat[classifiedIdx][2]++;
				}
				
				if (classifiedIdx != labelNoneIdx) {
					if (classifiedIdx/3 != prevClassifiedIdx/3) {
						classifiedEntityCnt[classifiedIdx/3]++;
						if (prevClassifiedIdx != labelNoneIdx) {
							sb.append('\t');
							sb.append(classifiedEntityStr);
							sb.append(" -> ");
							sb.append(prevClassified.substring(0, prevClassified.lastIndexOf('_')));
							sb.append('\n');
						}
						classifiedEntityStr = tokenAnn.getCoveredText();
					} else {
						classifiedEntityStr += " " + tokenAnn.getCoveredText();
					}
				} else if (prevClassifiedIdx != labelNoneIdx) {
					sb.append('\t');
					sb.append(classifiedEntityStr);
					sb.append(" -> ");
					sb.append(prevClassified.substring(0, prevClassified.lastIndexOf('_')));
					sb.append('\n');
					classifiedEntityStr = "";
				}
				prevClassifiedIdx = classifiedIdx;
				
				if (answerIdx != labelNoneIdx) {
					if (answerIdx/3 != prevAnswerIdx/3) {
						answerEntityCnt[answerIdx/3]++;
						answerEntityStr = tokenAnn.getCoveredText();
					} else {
						answerEntityStr += " " + tokenAnn.getCoveredText();
					}
				} else if (prevAnswerIdx != labelNoneIdx) {
					answerEntityStr = "";
				}
				
				prevAnswerIdx = answerIdx;
				prevClassified = classified;
				i++;
			}
			if (prevClassifiedIdx != labelNoneIdx) {
				sb.append('\t');
				sb.append(classifiedEntityStr);
				sb.append(" -> ");
				sb.append(prevClassified.substring(0, prevClassified.lastIndexOf('_')));
				sb.append('\n');
			}
			
			// senti
			String answerSenti = answerTweet.getPolarityString();
				
			boolean correct = false;
			String classifiedSenti = tweetAnn.getPolarity();
			if (classifiedSenti.equals(senti)) {
				correct = true;
			}
			
			int classifiedIdx = sentiIdx(classifiedSenti);
			int answerIdx = sentiIdx(answerSenti);
			
			senti[classifiedIdx][0]++;
			senti[answerIdx][1]++;
			if (classifiedIdx == answerIdx) {
				correct = true;
			}
			
			if (correct) {
				senti[classifiedIdx][2]++;
			}
			cnt++;
			
			logger.log(Level.INFO, sb.toString());
			
		} catch (CASRuntimeException e) {
			throw new ResourceProcessException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			Set<Entry<String,Integer>> set = map.entrySet();
			ArrayList<Entry<String,Integer>> list = new ArrayList<Entry<String,Integer>>();
			list.addAll(set);
			Collections.sort(list, new Comparator<Entry<String,Integer>>(){
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			
			/////////////////////////////////////////////
			System.out.println("# NER");
			
			for (Entry<String,Integer> e : list) {
				int idx = e.getValue();
				double prec = (stat[idx][0] != 0) ? (double)stat[idx][2] / (double)stat[idx][0] : -1;
				double recall = (stat[idx][1] != 0) ? (double)stat[idx][2] / (double)stat[idx][1] : -1;
				double f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
	//				System.out.format("%02d %15s %3d/%3d=%7.4f %3d/%3d=%7.4f %7.4f\n", idx, e.getKey(), stat[idx][2], stat[idx][0], prec, stat[idx][2], stat[idx][1], recall, f);
				System.out.format("%02d\t%s\t%d\t%.4f\t%.4f\t%.4f\n", idx, e.getKey(), stat[idx][1], prec, recall, f);
			}
			System.out.println();
			
			/////////////////////////////////////////////
			
			System.out.format("%10s\t%12s\t%12s\t%12s\n", "Index", "Type", "Answer", "Classified");
			for (int i = 0; i < list.size(); i+=3) {
				Entry<String,Integer> e = list.get(i);
				String s = e.getKey();
				int idx = e.getValue();
				if (idx != labelNoneIdx) {
					s = s.substring(0, s.lastIndexOf('_'));
					System.out.format("%010d\t%12s\t%12d\t%12d\n", idx/3, s, answerEntityCnt[idx/3], classifiedEntityCnt[idx/3]);
				} 
			}
			System.out.println();
			
			/////////////////////////////////////////////
			
			System.out.println("# senti");
			for (int i = 0; i < 3; i++) {
				int idx = i;
				double prec = (senti[idx][0] != 0) ? (double)senti[idx][2] / (double)senti[idx][0] : -1;
				double recall = (senti[idx][1] != 0) ? (double)senti[idx][2] / (double)senti[idx][1] : -1;
				double f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
	//				System.out.format("%s\t%d\t%.4f\t%.4f\t%.4f\n", sentiStr(idx), senti[idx][1], prec, recall, f);
				System.out.format("%02d %15s %3d/%3d =%6.4f  %3d/%3d =%6.4f %7.4f\n", idx, sentiStr(idx), senti[idx][2], senti[idx][0], prec, senti[idx][2], senti[idx][1], recall, f);
			}
			
			/////////////
			
			System.out.println("# senti: sbj & neu");
			double prec = (senti[0][0]+senti[1][0] != 0) ? (double)(senti[0][2]+senti[1][2]) / (double)(senti[0][0]+senti[1][0]) : -1;
			double recall = (senti[0][1]+senti[1][1] != 0) ? (double)(senti[0][2]+senti[1][2]) / (double)(senti[0][1]+senti[1][1]) : -1;
			double f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
			System.out.format("%02d %15s %3d/%3d =%6.4f  %3d/%3d =%6.4f %7.4f\n", 0, sentiStr(0)+"/"+sentiStr(1), senti[0][2]+senti[1][2], senti[0][0]+senti[1][0], prec, senti[0][2]+senti[1][2], senti[0][1]+senti[1][1], recall, f);
			
			int idx = 2;
			prec = (senti[idx][0] != 0) ? (double)senti[idx][2] / (double)senti[idx][0] : -1;
			recall = (senti[idx][1] != 0) ? (double)senti[idx][2] / (double)senti[idx][1] : -1;
			f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
			System.out.format("%02d %15s %3d/%3d =%6.4f  %3d/%3d =%6.4f %7.4f\n", idx, sentiStr(idx), senti[idx][2], senti[idx][0], prec, senti[idx][2], senti[idx][1], recall, f);
			
			//////////////
			
	//			System.out.println("# senti : pos & neg");
	//			prec = (senti[0][0]+senti[2][0] != 0) ? (double)(senti[0][2]+senti[2][2]) / (double)(senti[0][0]+senti[2][0]) : -1;
	//			recall = (senti[0][1]+senti[2][1] != 0) ? (double)(senti[0][2]+senti[2][2]) / (double)(senti[0][1]+senti[2][1]) : -1;
	//			f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
	//			System.out.format("%02d %15s %3d/%3d =%6.4f  %3d/%3d =%6.4f %7.4f\n", 0, sentiStr(0)+"/"+sentiStr(2), senti[0][2]+senti[2][2], senti[0][0]+senti[2][0], prec, senti[0][2]+senti[2][2], senti[0][1]+senti[2][1], recall, f);
	//			
	//			idx = 1;
	//			prec = (senti[idx][0] != 0) ? (double)senti[idx][2] / (double)senti[idx][0] : -1;
	//			recall = (senti[idx][1] != 0) ? (double)senti[idx][2] / (double)senti[idx][1] : -1;
	//			f = (prec + recall > 0) ? (2 * prec * recall / (prec + recall)) : -1;
	//			System.out.format("%02d %15s %3d/%3d =%6.4f  %3d/%3d =%6.4f %7.4f\n", idx, sentiStr(idx), senti[idx][2], senti[idx][0], prec, senti[idx][2], senti[idx][1], recall, f);
				
			evalCorpusReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.destroy();
	}
	
	private int sentiIdx(String s) {
		if (s.equals(OMTweet.POLARITY_STR_POSITIVE)) {
			return 0;
		}
		if (s.equals(OMTweet.POLARITY_STR_NEGATIVE)) {
			return 1;
		}
		if (s.equals(OMTweet.POLARITY_STR_NEUTRAL)) {
			return 2;
		}
		return -1;
	}
	
	private String sentiStr(int i) {
		switch(i) {
		case 0: return OMTweet.POLARITY_STR_POSITIVE;
		case 1: return OMTweet.POLARITY_STR_NEGATIVE;
		case 2: return OMTweet.POLARITY_STR_NEUTRAL;
		default: return null;
		}
	}
	
	private String[] extractEntityTags(String text) {
		String[] tokens = text.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].substring(tokens[i].lastIndexOf('/') + 1);
		}
		return tokens;
	}
}
