package com.maalaang.omtwitter.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.model.OMTweet;
import com.maalaang.omtwitter.model.OMTweet_Impl;

public class OMTwitterCorpusFileReader implements OMTwitterReader {
	
	private BufferedReader br = null;
	
	private String fieldDelimiter = null;
	
	private int[] fields = null;
	
	private Logger logger = null;
	
	public OMTwitterCorpusFileReader(String file) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, OMTwitterCorpusFile.DEFAULT_FIELD_DELIM, OMTwitterCorpusFile.DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileReader(String file, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, OMTwitterCorpusFile.DEFAULT_FIELD_DELIM, fields);
	}
	
	public OMTwitterCorpusFileReader(String file, String fieldDelimiter) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, fieldDelimiter, OMTwitterCorpusFile.DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileReader(String file, String fieldDelimiter, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this.br = new BufferedReader(new InputStreamReader(new FileInputStream(file), OMTwitterCorpusFile.FILE_CHARSET));
		this.fields = fields;
		this.fieldDelimiter = fieldDelimiter;
		this.logger = Logger.getLogger(this.getClass());
		
		logger.info("read from twitter corpus file - " + file);
	}

	public boolean hasNext() {
		try {
			return br.ready();
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
	}

	public OMTweet next() {
		String line = null;
		
		try {
			line = br.readLine();
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
		
		if (line == null) {
			logger.info("reached to the end of the corpus file");
			return null;
		}
		
		OMTweet_Impl tweet = new OMTweet_Impl();
		
		String[] tokens = line.split(fieldDelimiter);
		
		for (int i = 0; i < tokens.length && i < fields.length; i++) {
			if (tokens[i].equalsIgnoreCase(OMTwitterCorpusFile.FIELD_EMPTY_STR)) {
				tokens[i] = null;
			}
			
			switch (fields[i]) {
			case OMTwitterCorpusFile.FIELD_IGNORE:	
				break;
			case OMTwitterCorpusFile.FIELD_ID:
				tweet.setId(tokens[i]);
				break;
			case OMTwitterCorpusFile.FIELD_AUTHOR:
				tweet.setAuthor(tokens[i]);
				break;
			case OMTwitterCorpusFile.FIELD_TEXT:
				tweet.setText(tokens[i]);
				break;
			case OMTwitterCorpusFile.FIELD_DATE:
				tweet.setDate(tokens[i]);
				break;
			case OMTwitterCorpusFile.FIELD_POLARITY:
				tweet.setPolarity(tokens[i]);
				break;
			case OMTwitterCorpusFile.FIELD_QUERY:
				tweet.setQuery(tokens[i]);
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		logger.debug("read a tweet: " + tweet);
		
		return tweet;
	}

	public void close() {
		try {
			logger.info("close twitter corpus file");
			br.close();
		} catch (IOException e) {
			logger.error(e);
		}
		br = null;
	}
	
	public static int fieldNameToId(String fieldName) {
		for (int i = 0; i < OMTwitterCorpusFile.FIELD_NAMES.length; i++) {
			if (OMTwitterCorpusFile.FIELD_NAMES[i].equals(fieldName)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}
}
