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
	
	public static final String DEFAULT_FILE_CHARSET = "UTF8";
	
	public static final String DEFAULT_SPLIT_REGEX = "\t";
	
	public static final int FIELD_IGNORE = 0;
	public static final int FIELD_ID = 1;
	public static final int FIELD_AUTHOR = 2;
	public static final int FIELD_TEXT = 3;
	public static final int FIELD_DATE = 4;
	public static final int FIELD_POLARITY = 5;
	public static final int FIELD_QUERY = 6;
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
	
	public static final int[] DEFAULT_FIELDS = { FIELD_ID, FIELD_AUTHOR, FIELD_TEXT, FIELD_DATE, FIELD_POLARITY, FIELD_QUERY };
	
	public static final String[] FIELD_NAMES = { "IGNORE", "ID", "AUTHOR", "TEXT", "DATE", "POLARITY", "QUERY" };
	
	public static final String EMPTY_FIELD_STR = "NULL";
	
	private BufferedReader br = null;
	
	private String splitRegex = null;
	
	private int[] fields = null;
	
	private Logger logger = null;
	
	public OMTwitterCorpusFileReader(String file) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, DEFAULT_SPLIT_REGEX, DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileReader(String file, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, DEFAULT_SPLIT_REGEX, fields);
	}
	
	public OMTwitterCorpusFileReader(String file, String splitRegex) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, splitRegex, DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileReader(String file, String splitRegex, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this.br = new BufferedReader(new InputStreamReader(new FileInputStream(file), DEFAULT_FILE_CHARSET));
		this.fields = fields;
		this.splitRegex = splitRegex;
		this.logger = Logger.getLogger(this.getClass());
		
		logger.info("read from the twitter corpus file: " + file);
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
		
		String[] tokens = line.split(splitRegex);
		
		for (int i = 0; i < tokens.length && i < fields.length; i++) {
			if (tokens[i].equalsIgnoreCase(EMPTY_FIELD_STR)) {
				tokens[i] = null;
			}
			
			switch (fields[i]) {
			case FIELD_IGNORE:	
				break;
			case FIELD_ID:
				tweet.setId(tokens[i]);
				break;
			case FIELD_AUTHOR:
				tweet.setAuthor(tokens[i]);
				break;
			case FIELD_TEXT:
				tweet.setText(tokens[i]);
				break;
			case FIELD_DATE:
				tweet.setDate(tokens[i]);
				break;
			case FIELD_POLARITY:
				tweet.setPolarity(tokens[i]);
				break;
			case FIELD_QUERY:
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
			br.close();
			logger.info("the twitter corpus file was closed");
		} catch (IOException e) {
			logger.error(e);
		}
		br = null;
	}
	
	public static int fieldNameToId(String fieldName) {
		for (int i = 0; i < FIELD_NAMES.length; i++) {
			if (FIELD_NAMES[i].equals(fieldName)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}

}
