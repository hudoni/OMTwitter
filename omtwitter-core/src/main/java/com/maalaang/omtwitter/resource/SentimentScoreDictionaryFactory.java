/**
 * 
 */
package com.maalaang.omtwitter.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author Sangwon Park
 *
 */
public class SentimentScoreDictionaryFactory {
	public static SentimentScoreDictionary loadFromSerializedFile(String objectFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile));
		SentimentScoreDictionary dic = (SentimentScoreDictionary) ois.readObject();
		ois.close();
		return dic;
	}
	
	public static SentimentScoreDictionary loadFromSerializedFile(InputStream ObjectFileStream) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(ObjectFileStream);
		SentimentScoreDictionary dic = (SentimentScoreDictionary) ois.readObject();
		ois.close();
		return dic;
	}
	
	public static SentimentScoreDictionary loadFromDicFile(String dicFile) throws IOException {
		SentiWordNetDictionary dic = new SentiWordNetDictionary();
		dic.load(dicFile);
		return dic;
	}

}
