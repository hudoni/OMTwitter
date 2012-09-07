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
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;

public class ConstructTwitterRawCorpus {

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"))
			
			// search
			Model domainOntologyModel = ModelFactory.createDefaultModel();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(prop.getProperty("domain.ontology.file")), "UTF-8");
			domainOntologyModel.read(isr, null, "N-TRIPLE");
			isr.close();
			
			Set<String> stopwords = CollectionTextReader.readSetString(prop.getProperty("stopword.set.file"));
		
			Map<String,Set<String>> queryToResourceMap = TwitterQueryGenerator.generateQueries(domainOntologyModel, stopwords, prop.getProperty("query.gen.resource.label.lang"), Integer.parseInt(prop.getProperty("query.gen.min.token")));
			CollectionTextWriter.writeMapStringSetString(queryToResourceMap, prop.getProperty("raw.corpus.search.query.file"), true);
			
			TwitterCorpusConstructor tcc = new TwitterCorpusConstructor();
			tcc.constructCorpusBySearch(queryToResourceMap.keySet(), prop.getProperty("raw.corpus.search.file"),
					Integer.parseInt(prop.getProperty("raw.corpus.search.rpp")), Integer.parseInt(prop.getProperty("raw.corpus.search.max")),
					prop.getProperty("raw.corpus.search.lang", null), Integer.parseInt(prop.getProperty("raw.corpus.search.interval")),
					Integer.parseInt(prop.getProperty("raw.corpus.search.retry.num")), Integer.parseInt(prop.getProperty("raw.corpus.search.retry.interval")))
			queryToResourceMap = null;
			
			int[] searchCorpusFields = new int[] { OMTwitterCorpusFileReader.FIELD_ID,
					OMTwitterCorpusFileReader.FIELD_AUTHOR,
					OMTwitterCorpusFileReader.FIELD_DATE,
					OMTwitterCorpusFileReader.FIELD_QUERY,
					OMTwitterCorpusFileReader.FIELD_TEXT };
			
			Map<String,Integer> searchUserStatusFreqMap = TwitterCorpusStat.userStatusFreq(new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.search.file"), searchCorpusFields));
			CollectionTextWriter.writeMapStringInteger(searchUserStatusFreqMap, prop.getProperty("raw.corpus.search.user.freq.file"), true);
			
			// sample stream
			tcc.openTwitterSampleStream(new TextWriteStatusListener(prop.getProperty("raw.corpus.sample.file"), prop.getProperty("raw.corpus.sample.lang")), 60*60*24);
			
			int[] sampleCorpusFields = new int[] { OMTwitterCorpusFileReader.FIELD_ID,
					OMTwitterCorpusFileReader.FIELD_AUTHOR,
					OMTwitterCorpusFileReader.FIELD_DATE,
					OMTwitterCorpusFileReader.FIELD_TEXT };
			
			Map<String,Integer> sampleUserStatusFreqMap = TwitterCorpusStat.userStatusFreq(new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.sample.file"), sampleCorpusFields));
			CollectionTextWriter.writeMapStringInteger(sampleUserStatusFreqMap, prop.getProperty("raw.corpus.sample.user.freq.file"), true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
