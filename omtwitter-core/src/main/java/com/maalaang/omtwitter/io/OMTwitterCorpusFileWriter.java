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
	
	private BufferedWriter bw = null;
	
	private String fieldDelimiter = null;
	
	private int[] fields = null;
	
	private Logger logger = null;
	
	public OMTwitterCorpusFileWriter(String file) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, OMTwitterCorpusFile.DEFAULT_FIELD_DELIM, OMTwitterCorpusFile.DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileWriter(String file, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, OMTwitterCorpusFile.DEFAULT_FIELD_DELIM, fields);
	}
	
	public OMTwitterCorpusFileWriter(String file, String fieldDelimiter) throws UnsupportedEncodingException, FileNotFoundException {
		this(file, fieldDelimiter, OMTwitterCorpusFile.DEFAULT_FIELDS);
	}
	
	public OMTwitterCorpusFileWriter(String file, String fieldDelimiter, int[] fields) throws UnsupportedEncodingException, FileNotFoundException {
		this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), OMTwitterCorpusFile.FILE_CHARSET));
		this.fields = fields;
		this.fieldDelimiter = fieldDelimiter;
		this.logger = Logger.getLogger(this.getClass());
		
		logger.info("write twitter corpus file: " + file);
	}
	
	public void write(OMTweet tweet) throws IOException {
		for (int i = 0; i < fields.length; i++) {
			switch (fields[i]) {
			case OMTwitterCorpusFile.FIELD_IGNORE:	
				bw.write(OMTwitterCorpusFile.FIELD_EMPTY_STR);
				break;
			case OMTwitterCorpusFile.FIELD_ID:
				bw.write(String.valueOf(tweet.getId()));
				break;
			case OMTwitterCorpusFile.FIELD_AUTHOR:
				bw.write(tweet.getAuthor());
				break;
			case OMTwitterCorpusFile.FIELD_TEXT:
				bw.write(tweet.getText());
				break;
			case OMTwitterCorpusFile.FIELD_DATE:
				bw.write(tweet.getDateString());
				break;
			case OMTwitterCorpusFile.FIELD_POLARITY:
				bw.write(tweet.getPolarityString());
				break;
			case OMTwitterCorpusFile.FIELD_QUERY:
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
		bw.flush();
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
	
}
