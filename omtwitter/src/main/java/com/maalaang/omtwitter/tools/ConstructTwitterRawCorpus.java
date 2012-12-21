package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.maalaang.omtwitter.corpus.TextWriteStatusListener;
import com.maalaang.omtwitter.corpus.TwitterCorpusConstructor;
import com.maalaang.omtwitter.corpus.TwitterCorpusStat;
import com.maalaang.omtwitter.corpus.TwitterQueryGenerator;
import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.CollectionTextWriter;
import com.maalaang.omtwitter.io.OMTwitterCorpusFile;

public class ConstructTwitterRawCorpus {

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			// search
			Model domainOntologyModel = ModelFactory.createDefaultModel();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(prop.getProperty("domain.ontology.file")), "UTF-8");
			domainOntologyModel.read(isr, null, "N-TRIPLE");
			isr.close();
			
			Set<String> stopwords = CollectionTextReader.readSetString(prop.getProperty("stopword.set.file"));
		
			Map<String,Set<String>> queryToResourceMap = TwitterQueryGenerator.generateQueries(domainOntologyModel, stopwords, prop.getProperty("query.gen.resource.label.lang"), Integer.parseInt(prop.getProperty("param.query.min.length")));
			CollectionTextWriter.writeMapStringSetString(queryToResourceMap, prop.getProperty("raw.corpus.search.query.map.file"), true);
			
			TwitterCorpusConstructor tcc = new TwitterCorpusConstructor();
			tcc.constructCorpusBySearch(queryToResourceMap.keySet(), prop.getProperty("raw.corpus.search.file"),
					Integer.parseInt(prop.getProperty("raw.corpus.search.rpp")), Integer.parseInt(prop.getProperty("raw.corpus.search.max")),
					prop.getProperty("raw.corpus.search.lang", null), Integer.parseInt(prop.getProperty("raw.corpus.search.interval")),
					Integer.parseInt(prop.getProperty("raw.corpus.search.retry.num")), Integer.parseInt(prop.getProperty("raw.corpus.search.retry.interval")));
			queryToResourceMap = null;
			
			int[] searchCorpusFields = new int[] { OMTwitterCorpusFile.FIELD_ID,
					OMTwitterCorpusFile.FIELD_AUTHOR,
					OMTwitterCorpusFile.FIELD_DATE,
					OMTwitterCorpusFile.FIELD_QUERY,
					OMTwitterCorpusFile.FIELD_TEXT };
			
			Map<String,Integer> searchUserStatusFreqMap = TwitterCorpusStat.userStatusFreq(prop.getProperty("raw.corpus.search.file"), "\\s+", searchCorpusFields);
			CollectionTextWriter.writeMapStringInteger(searchUserStatusFreqMap, prop.getProperty("raw.corpus.search.user.freq.file"), true);
			
			// sample stream
			tcc.openTwitterSampleStream(new TextWriteStatusListener(prop.getProperty("raw.corpus.sample.file"), prop.getProperty("raw.corpus.sample.lang")), 60*60*24);
			
			int[] sampleCorpusFields = new int[] { OMTwitterCorpusFile.FIELD_ID,
					OMTwitterCorpusFile.FIELD_AUTHOR,
					OMTwitterCorpusFile.FIELD_DATE,
					OMTwitterCorpusFile.FIELD_TEXT };
			
			Map<String,Integer> sampleUserStatusFreqMap = TwitterCorpusStat.userStatusFreq(prop.getProperty("raw.corpus.sample.file"), "\\s+", sampleCorpusFields);
			CollectionTextWriter.writeMapStringInteger(sampleUserStatusFreqMap, prop.getProperty("raw.corpus.sample.user.freq.file"), true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
