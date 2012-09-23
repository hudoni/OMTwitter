package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.maalaang.omtwitter.resource.SentiWordNetDictionary;

public class BuildSentiWordNetDictionary {

	private Logger logger = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BuildSentiWordNetDictionary buildSentiWordNetDictionary = new BuildSentiWordNetDictionary();
			
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			buildSentiWordNetDictionary.run(prop.getProperty("swn.dic.text"), prop.getProperty("swn.dic.object"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BuildSentiWordNetDictionary() {
		logger = Logger.getLogger(getClass());
	}
	
	public void run(String in, String out) throws IOException {
		logger.info("load SentiWordNet dictionary - " + in);
		SentiWordNetDictionary dic = new SentiWordNetDictionary();
		dic.load(in);
		logger.info("loaded");
		
		logger.info("write object file - " + out);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
		oos.writeObject(dic);
		oos.close();
		logger.info("done");
	}
}
