package com.maalaang.omtwitter.io;

import java.awt.geom.IllegalPathStateException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.model.OMTweet;

public class OMTwitterCorpusFileWriter {
	
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
	
	private BufferedWriter bw = null;
	
	private String fieldDelimiter = null;
	
	private int[] fields = null;
	
	private Logger logger = null;
	
	public OMTwitterCorpusFileWriter(String file) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, DEFAULT_SPLIT_REGEX, DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileWriter(String file, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, DEFAULT_SPLIT_REGEX, fields);
	}
	
	public OMTwitterCorpusFileWriter(String file, String fieldDelimiter) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, fieldDelimiter, DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileWriter(String file, String fieldDelimiter, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), DEFAULT_FILE_CHARSET));
		this.fields = fields;
		this.fieldDelimiter = fieldDelimiter;
		this.logger = Logger.getLogger(this.getClass());
		
		logger.info("write twitter corpus file: " + file);
	}
	
	public void write(OMTweet tweet) throws IOException {
		for (int i = 0; i < fields.length; i++) {
			switch (fields[i]) {
			case FIELD_IGNORE:	
				bw.write(EMPTY_FIELD_STR);
				break;
			case FIELD_ID:
				bw.write(String.valueOf(tweet.getId()));
				break;
			case FIELD_AUTHOR:
				bw.write(tweet.getAuthor());
				break;
			case FIELD_TEXT:
				bw.write(tweet.getText());
				break;
			case FIELD_DATE:
				bw.write(tweet.getDate());
				break;
			case FIELD_POLARITY:
				bw.write(tweet.getPolarityString());
				break;
			case FIELD_QUERY:
				bw.write(tweet.getQuery());
				break;
			default:
				throw new IllegalPathStateException();
			}
			
			if (i < fields.length - 1) {
				bw.write(fieldDelimiter);
			} else {
				bw.write('\n');
			}
		}
		logger.debug("write a tweet: " + tweet);
	}

	public void close() {
		try {
			bw.close();
			logger.info("the twitter corpus file was closed");
		} catch (IOException e) {
			logger.error(e);
		}
		bw = null;
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
