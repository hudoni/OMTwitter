package com.maalaang.omtwitter.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.LogSystemStream;
import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.text.FilterCosineSimilarity;
import com.maalaang.omtwitter.text.FilterDomainRelevance;
import com.maalaang.omtwitter.text.FilterHashtagUsage;
import com.maalaang.omtwitter.text.FilterStopword;
import com.maalaang.omtwitter.text.FilterUserName;
import com.maalaang.omtwitter.text.TweetFilterPipeline;
import com.maalaang.omtwitter.text.WordPattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ConstructTwitterNamedEntityCorpus {
	
	private MaxentTagger tagger = null;
	private BufferedWriter bw = null;
	private Properties prop = null;
	private Logger logger = null;
	private Map<String,String> valueToPropertyMap = null;
	private Map<String,String> propertyToLabelMap = null;
	private int valueMinToken = 0;
	private int valueMaxToken = 0;
	private String nonelabel = null;
	private double mergeRate = 0.0;
	private Set<String> searchQuerySet = null;
	private String searchQueryToProperty = null;
	
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			LogSystemStream.redirectErrToLog(Level.ERROR);
			
			ConstructTwitterNamedEntityCorpus con = new ConstructTwitterNamedEntityCorpus(prop);
			con.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConstructTwitterNamedEntityCorpus(Properties prop) throws ClassNotFoundException, IOException {
		this.prop = prop;
		this.tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
		this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prop.getProperty("ne.corpus.file")), "UTF-8"));
		this.logger = Logger.getLogger(getClass());
		this.valueToPropertyMap = valueToPropertyMap(prop.getProperty("value.to.property.map.file"));
		this.propertyToLabelMap = CollectionTextReader.readMapStringString(prop.getProperty("ne.corpus.property.label.map.file"));
		this.valueMinToken = Integer.parseInt(prop.getProperty("value.token.min"));
		this.valueMaxToken = Integer.parseInt(prop.getProperty("value.token.max"));
		this.nonelabel = prop.getProperty("ne.corpus.label.none");
		this.mergeRate = Double.parseDouble(prop.getProperty("ne.corpus.merge.rate"));
		this.searchQuerySet = searchQuerySet(prop.getProperty("raw.corpus.search.query.map.file"));
		this.searchQueryToProperty = prop.getProperty("raw.corpus.search.query.to.property");
	}
	
	public void run() throws IOException {
		
		Map<String,Double> wrsMap = CollectionTextReader.readMapStringDouble(prop.getProperty("word.relevance.score.file"));
		Set<String> stopwords = CollectionTextReader.readSetString(prop.getProperty("stopword.set.file"));
		
		// search corpus
		int[] searchCorpusFields = new int[] { OMTwitterCorpusFile.FIELD_ID,
				OMTwitterCorpusFile.FIELD_AUTHOR,
				OMTwitterCorpusFile.FIELD_DATE,
				OMTwitterCorpusFile.FIELD_QUERY,
				OMTwitterCorpusFile.FIELD_TEXT };
		OMTwitterReader searchCorpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.search.file"), searchCorpusFields);
		
		TweetFilterPipeline searchCorpusFilterPipe = new TweetFilterPipeline();
		searchCorpusFilterPipe.add(new FilterUserName(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.window.size")),
				Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.post.limit"))));
		searchCorpusFilterPipe.add(new FilterHashtagUsage());
		searchCorpusFilterPipe.add(new FilterCosineSimilarity(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.cosine.similarity.window.size")),
				Double.parseDouble(prop.getProperty("raw.corpus.search.filter.cosine.similarity.threshold"))));
		searchCorpusFilterPipe.add(new FilterDomainRelevance(wrsMap, stopwords,
				Double.parseDouble(prop.getProperty("raw.corpus.search.filter.domain.relevance.relevance.factor")),
				Integer.parseInt(prop.getProperty("raw.corpus.search.filter.domain.relevance.window.size")),
				Double.parseDouble(prop.getProperty("raw.corpus.search.filter.domain.relevance.start.window.score"))));
		searchCorpusFilterPipe.initialize();
		
		int searchCorpusWriteCnt = 0;
		
		while (searchCorpusReader.hasNext()) {
			OMTweet tweet = searchCorpusReader.next();
			if (searchCorpusFilterPipe.check(tweet)) {
				writeNEAnnotatedTweet(tweet);
				searchCorpusWriteCnt++;
			}
		}
		
		searchCorpusFilterPipe.close();
		searchCorpusReader.close();
		
		// sample corpus
		int[] sampleCorpusFields = new int[] { OMTwitterCorpusFile.FIELD_ID,
				OMTwitterCorpusFile.FIELD_AUTHOR,
				OMTwitterCorpusFile.FIELD_DATE,
				OMTwitterCorpusFile.FIELD_TEXT };
		OMTwitterReader sampleCorpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.sample.file"), sampleCorpusFields);
		
		TweetFilterPipeline sampleCorpusFilterPipe = new TweetFilterPipeline();
		sampleCorpusFilterPipe.add(new FilterUserName(Integer.parseInt(prop.getProperty("raw.corpus.sample.filter.user.name.window.size")),
				Integer.parseInt(prop.getProperty("raw.corpus.sample.filter.user.name.post.limit"))));
		sampleCorpusFilterPipe.add(new FilterStopword(stopwords, Integer.parseInt(prop.getProperty("raw.corpus.sample.filter.stopword.threshold"))));
		sampleCorpusFilterPipe.add(new FilterCosineSimilarity(Integer.parseInt(prop.getProperty("raw.corpus.sample.filter.cosine.similarity.window.size")),
				Double.parseDouble(prop.getProperty("raw.corpus.sample.filter.cosine.similarity.threshold"))));
		sampleCorpusFilterPipe.add(new FilterDomainRelevance(wrsMap, stopwords,
				Double.parseDouble(prop.getProperty("raw.corpus.sample.filter.domain.relevance.relevance.factor")),
				Integer.parseInt(prop.getProperty("raw.corpus.sample.filter.domain.relevance.window.size")),
				Double.parseDouble(prop.getProperty("raw.corpus.sample.filter.domain.relevance.start.window.score")),
				true));
		sampleCorpusFilterPipe.initialize();
		
		int sampleCorpusWriteCnt = 0;
		int sampleCorpusWriteLimit = (int)(searchCorpusWriteCnt * mergeRate);
		
		while (sampleCorpusReader.hasNext()) {
			OMTweet tweet = sampleCorpusReader.next();
			if (sampleCorpusFilterPipe.check(tweet)) {
				writeNEAnnotatedTweet(tweet);
				if (++sampleCorpusWriteCnt > sampleCorpusWriteLimit) {
					break;
				}
			}
		}
		
		sampleCorpusFilterPipe.close();
		sampleCorpusReader.close();
	}
	
	private void writeNEAnnotatedTweet(OMTweet tweet) throws IOException {
		String text = tweet.getText();
		List<List<HasWord>> sentences =  MaxentTagger.tokenizeText(new StringReader(text));
		int wordIdx = 0;
		
		String query = null;
		
		bw.write(tweet.getId());
		bw.write('\t');
		bw.write(tweet.getAuthor());
		bw.write('\t');
		bw.write(tweet.getDateString());
		bw.write('\t');
		if ((query = tweet.getQuery()) != null) {
			bw.write(query);
			bw.write('\t');
		} else {
			bw.write(OMTwitterCorpusFile.FIELD_EMPTY_STR);
			bw.write('\t');
		}
		
		for (List<HasWord> sentence : sentences) {
			ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
			
			// to exclude escaping characters from PTBLexer; it can be improved
			for (TaggedWord word : taggedSentence) {
				word.setWord(text.substring(word.beginPosition(), word.endPosition()));
			}
			
			String[] neTags = tagNESentence(taggedSentence);
			int neTagsIdx = 0;
			
			for (TaggedWord word : taggedSentence) {
				if (wordIdx++ > 0)
					bw.write(' ');
				bw.write(word.word());
				bw.write('/');
				bw.write(word.tag());
				bw.write('/');
				bw.write(neTags[neTagsIdx]);
				
				neTagsIdx++;
			}
		}
		bw.write('\n');
		bw.flush();
	}
	
	private String[] tagNESentence(List<TaggedWord> sent) {
		String[] tags = new String[sent.size()];
		String[] normWords = new String[sent.size()];
		
		int idx = 0;
		for (TaggedWord word : sent) {
			normWords[idx++] = WordPattern.normalize(word.word());
		}
		
		int start;
		int end;
		int endMin;
		int endMax;
		String key = null;
		String tag = null;
		
		for (int i = 0; i < tags.length; i++) {
			start = i;
			if ((endMin = i + valueMinToken) > tags.length) {
				for (int k = start; k < tags.length; i++) {
					tags[k] = nonelabel;
				}
				break;
			}
			if ((endMax = i + valueMaxToken) > tags.length) {
				endMax = tags.length;
			}
			
			for (end = endMax; end >= endMin; end--) {
				for (int j = start; j < end; j++) {
					if (j == start) {
						key = normWords[j];
					} else {
						key += " " + normWords[j];
					}
				}
				if (searchQueryToProperty != null && searchQuerySet.contains(key)) {
					tag = searchQueryToProperty;
					logger.debug(key + " > " + searchQueryToProperty);
					break;
				} else if ((tag = valueToPropertyMap.get(key)) != null) {
					logger.debug(key + " > " + tag);
					break;
				}
			}
			
			if (tag == null) {
				tags[i] = nonelabel;
			} else {
				tag = propertyToLabelMap.get(tag);
				int k = start;
				tags[k++] = tag + "_B";
				for ( ; k < end - 1; k++) {
					tags[k] = tag + "_M";
				}
				if (k == end - 1) {
					tags[k] = tag + "_E";
				}
				
				i = end - 1;
			}
		}
		
		return tags;
	}
	
	private Set<String> searchQuerySet(String file) throws IOException {
		HashSet<String> set = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			
			String w = tokens[0].replaceAll("\"", "");
			String w1 = WordPattern.replaceRomanToArabic(w);
			set.add(w.replaceFirst("#", ""));
			set.add(w1.replaceFirst("#", ""));
			
			w = tokenizeAndConcatText(w, " ");
			w1 = tokenizeAndConcatText(w1, " ");
			set.add(w.replaceFirst("#", ""));
			set.add(w1.replaceFirst("#", ""));
		}
		br.close();
		
		for (String s : set) {
			System.out.println(s);
		}
		
		return set;
	}
	
	private static Map<String, String> valueToPropertyMap(String file) throws IOException {
		HashMap<String, String> map = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			map.put(tokens[0].trim(), tokens[1].trim());
			map.put(tokenizeAndConcatText(tokens[0].trim(), " "), tokens[1].trim());
			System.out.println(tokens[0].trim());
			System.out.println(tokenizeAndConcatText(tokens[0].trim(), " "));
		}
		br.close();
		
		return map;
	}
	
	private static String tokenizeAndConcatText(String text, String s) {
		List<List<HasWord>> sentences =  MaxentTagger.tokenizeText(new StringReader(text));
		StringBuffer sb = null;
		for (List<HasWord> sentence : sentences) {
			for (HasWord word : sentence) {
				if (sb == null) {
					sb = new StringBuffer();
					sb.append(word.word());
				} else {
					sb.append(s);
					sb.append(word.word());
				}
			}
		}
		return sb.toString().replaceAll("\\\\/", "/").trim();
	}
	
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
