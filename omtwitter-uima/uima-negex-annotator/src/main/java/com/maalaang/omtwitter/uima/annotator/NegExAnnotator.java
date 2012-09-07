package com.maalaang.omtwitter.uima.annotator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.google.code.negex.GenNegEx;
import com.maalaang.omtwitter.uima.type.NegationAnnotation;
import com.maalaang.omtwitter.uima.type.SentenceAnnotation;

public class NegExAnnotator extends JCasAnnotator_ImplBase {
	private static final String PARAM_NEGEX_WINDOW_SIZE = "negexWindowSize";
	
	private GenNegEx negEx = null; 
	private Pattern tokPattern = null;
	private int windowSize = 0;
	
	private Logger logger = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> senAnnIter = jcas.getAnnotationIndex(SentenceAnnotation.type).iterator();
		while (senAnnIter.hasNext()) {
			SentenceAnnotation sentAnn = (SentenceAnnotation)senAnnIter.next();
			String text = sentAnn.getCoveredText();
			
			int[] res = negEx.negScopeN(text);
			if (res == null) {
				continue;
			}
			
			/* correction of the window size of the negated expression */
			res[1] = res[0] + windowSize - 1;
			
			Matcher matcher = tokPattern.matcher(text);
			NegationAnnotation negAnn = new NegationAnnotation(jcas);
			negAnn.setEnd(sentAnn.getEnd());
			
			for (int cnt = 0; matcher.find(); cnt++) {
				if (cnt == res[0]) {
					negAnn.setBegin(sentAnn.getBegin() + matcher.start());
				} else if (cnt == res[1]) {
					negAnn.setEnd(sentAnn.getBegin() + matcher.end());
					break;
				}
			}
			negAnn.addToIndexes();
		}
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		negEx = new GenNegEx(true);
		windowSize = (Integer)aContext.getConfigParameterValue(PARAM_NEGEX_WINDOW_SIZE);
		logger = aContext.getLogger();
		tokPattern = Pattern.compile("[^\\s]+");
		
		logger.log(Level.INFO, "negated expression annotator initialized");
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
