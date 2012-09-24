package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Level;

import com.maalaang.omtwitter.io.LogSystemStream;
import com.maalaang.omtwitter.io.OMTwitterCorpusFile;
import com.maalaang.omtwitter.ml.CrfClassifier;

public class TrainCrfNamedEntityRecognizer {
	public final static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
				
			LogSystemStream.redirectErrToLog(Level.ERROR);
			
			CrfClassifier crf = new CrfClassifier();
			
			String[] trainingFiles = new String[1];
			trainingFiles[0] = prop.getProperty("ne.corpus.file");
			
			crf.train(trainingFiles, prop.getProperty("ne.corpus.fields.delim"), OMTwitterCorpusFile.fieldNameToId(prop.getProperty("ne.corpus.fields"), "\\s+"),
					prop.getProperty("crf.model"), prop.getProperty("crf.feature.dump.file"), Boolean.parseBoolean(prop.getProperty("crf.feature.dump")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
