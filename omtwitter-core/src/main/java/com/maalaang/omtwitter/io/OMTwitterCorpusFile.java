/**
 * 
 */
package com.maalaang.omtwitter.io;

/**
 * @author Sangwon Park
 *
 */
public class OMTwitterCorpusFile {
	
	public static final int FIELD_IGNORE = 0;
	public static final int FIELD_ID = 1;
	public static final int FIELD_AUTHOR = 2;
	public static final int FIELD_TEXT = 3;
	public static final int FIELD_DATE = 4;
	public static final int FIELD_POLARITY = 5;
	public static final int FIELD_QUERY = 6;
	
	public static final String[] FIELD_NAMES = { "IGNORE", "ID", "AUTHOR", "TEXT", "DATE", "POLARITY", "QUERY" };
	public static final String FIELD_EMPTY_STR = "NULL";
	
	public static final String FILE_CHARSET = "UTF-8";
	
	public static final String DEFAULT_FIELD_DELIM = "\t";
	public static final int[] DEFAULT_FIELDS = { FIELD_ID, FIELD_AUTHOR, FIELD_TEXT, FIELD_DATE, FIELD_POLARITY, FIELD_QUERY };
	
	public static int fieldNameToId(String fieldName) {
		for (int i = 0; i < FIELD_NAMES.length; i++) {
			if (FIELD_NAMES[i].equals(fieldName)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public static int[] fieldNameToId(String fieldName, String fieldNameDelim) {
		String[] fieldNames = fieldName.split(fieldNameDelim);
		int[] fields = new int[fieldNames.length];

		for (int i = 0; i < fieldNames.length; i++) {
			fields[i] = OMTwitterCorpusFileReader.fieldNameToId(fieldNames[i]);
		}
		return fields;
	}

}
