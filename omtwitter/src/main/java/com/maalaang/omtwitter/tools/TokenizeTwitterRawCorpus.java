package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileWriter;
import com.maalaang.omtwitter.model.OMTweet;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TokenizeTwitterRawCorpus {
	private Logger logger = null;

	public static void main(String[] args) {
		TokenizeTwitterRawCorpus crawler = new TokenizeTwitterRawCorpus();
		try {
			crawler.run(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TokenizeTwitterRawCorpus() {
		logger = Logger.getLogger(getClass());
	}
	
	public void run(String propFile) throws UnsupportedEncodingException, FileNotFoundException, IOException, NumberFormatException, InterruptedException {
		Properties prop = new Properties();
		prop.load(new InputStreamReader(new FileInputStream(propFile), "UTF-8"));
		
		logger.info("tokenize tweets - " + prop.getProperty("raw.corpus.file"));
		
		int fields[] = OMTwitterCorpusFile.fieldNameToId(prop.getProperty("raw.corpus.fields"), " ");
		
		OMTwitterCorpusFileReader corpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.file"), fields);
		OMTwitterCorpusFileWriter corpusWriter = new OMTwitterCorpusFileWriter(prop.getProperty("raw.corpus.file.tokenized"), fields);
		int tweetTotalCnt = 0;
		
		while (corpusReader.hasNext()) {
			OMTweet tweet = corpusReader.next();
			String text = tweet.getText();
			tweet.setText(tokenizeAndConcatText(text, " "));
			corpusWriter.write(tweet);
			tweetTotalCnt++;
		}
		
		corpusReader.close();
		corpusWriter.close();
		
		logger.info("total " + tweetTotalCnt + " tweets were written - " + prop.getProperty("raw.corpus.file.tokenized"));
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
}
