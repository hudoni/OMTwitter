package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.maalaang.omtwitter.corpus.FilterCosineSimilarity;
import com.maalaang.omtwitter.corpus.FilterDomainRelevance;
import com.maalaang.omtwitter.corpus.FilterHashtagUsage;
import com.maalaang.omtwitter.corpus.FilterUserName;
import com.maalaang.omtwitter.corpus.TweetFilterPipeline;
import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.OMTwitterCorpusFileReader;
import com.maalaang.omtwitter.io.OMTwitterReader;
import com.maalaang.omtwitter.model.OMTweet;

public class ConstructTwitterNamedEntityCorpus {
	
	public static void main(String[] args) {
		
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			Map<String,Double> wrsMap = CollectionTextReader.readMapStringDouble(prop.getProperty("word.relevance.score.file"));
			Set<String> stopwords = CollectionTextReader.readSetString(prop.getProperty("stopword.set.file"));
			
			int[] searchCorpusFields = new int[] { OMTwitterCorpusFileReader.FIELD_ID,
					OMTwitterCorpusFileReader.FIELD_AUTHOR,
					OMTwitterCorpusFileReader.FIELD_DATE,
					OMTwitterCorpusFileReader.FIELD_QUERY,
					OMTwitterCorpusFileReader.FIELD_TEXT };
			OMTwitterReader searchCorpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.search.file"), searchCorpusFields);
			
			TweetFilterPipeline filterPipe = new TweetFilterPipeline();
			filterPipe.add(new FilterUserName(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.window.size")), Integer.parseInt(prop.getProperty("raw.corpus.search.filter.user.name.post.limit"))));
			filterPipe.add(new FilterHashtagUsage());
			filterPipe.add(new FilterCosineSimilarity(Integer.parseInt(prop.getProperty("raw.corpus.search.filter.cosine.similarity.window.size")), Integer.parseInt(prop.getProperty("raw.corpus.search.filter.cosine.similarity.threshold"))));
			filterPipe.add(new FilterDomainRelevance(wrsMap, stopwords, Double.parseDouble(prop.getProperty("raw.corpus.search.filter.domain.relevance.relevance.factor")), Integer.parseInt(prop.getProperty("raw.corpus.search.filter.domain.relevance.window.size")), Double.parseDouble(prop.getProperty("raw.corpus.search.filter.domain.relevance.start.window.score"))));
			
			while (searchCorpusReader.hasNext()) {
				OMTweet tweet = searchCorpusReader.next();
				if (filterPipe.filter(tweet)) {
				}
			}
			
			int[] sampleCorpusFields = new int[] { OMTwitterCorpusFileReader.FIELD_ID,
					OMTwitterCorpusFileReader.FIELD_AUTHOR,
					OMTwitterCorpusFileReader.FIELD_DATE,
					OMTwitterCorpusFileReader.FIELD_TEXT };
			OMTwitterReader sampleCorpusReader = new OMTwitterCorpusFileReader(prop.getProperty("raw.corpus.sample.file"), sampleCorpusFields);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			// corpus refiner
//			helper.tweetCorpusUserFilterList(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile), twitterDomainCorpusFile + ".user.filter.list", 1);
//			helper.tweetCorpusFilterByUser(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile), twitterDomainCorpusFile + ".refined.user", twitterDomainCorpusFile + ".user.filter.list");
//			helper.tweetCorpusFilterByHashtagUsage(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile + ".refined.user"), twitterDomainCorpusFile + ".refined.user.hashtag");//			helper.tweetCorpusFilterByCosineSimilarity(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile + ".refined.user.hashtag"), twitterDomainCorpusFile + ".refined.user.hashtag.similarity");
//			helper.tweetCorpusFilterByRelevance(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile + ".refined.user.hashtag.similarity"), twitterDomainCorpusFile + ".refined.user.hashtag.similarity.relevance.0.8", dbpediaInfoboxValueInDomainScoreMapFile, 0.8, splitPattern);
//			
//			helper.tweetCorpusUserStat(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile), twitterSampleStreamCorpusFile + ".user.stat");
//			helper.tweetCorpusUserFilterList(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile), twitterSampleStreamCorpusFile + ".user.filter.list", 1);
//			helper.tweetCorpusFilterByUser(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile), twitterSampleStreamCorpusFile + ".refined.user", twitterSampleStreamCorpusFile + ".user.filter.list");
//			helper.tweetCorpusFilterByStopword(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile + ".refined.user"), twitterSampleStreamCorpusFile + ".refined.user.stopword", 3);
//			
//			double f = 0.75;
//			String dataset1 = twitterDomainCorpusFile + ".refined.user.hashtag.similarity.relevance.0.75";
//			String dataset2 = twitterSampleStreamCorpusFile + ".refined.user.stopword.irrelevance.0.75";
//			String trainingset1 = dataset1 + ".entity";
//			String trainingset2 = dataset2 + ".entity";
//			String trainingset = trainingset1 + ".training";
//			String modelfile = "data/mallet/tweet.relevance.0.75.model";
//			
//			String stanfodPosTaggerModel = "data/stanford_nlp/pos_tagger_model/left3words-wsj-0-18.tagger";
			
//			helper.tweetCorpusFilterByRelevance(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile + ".refined.user.hashtag.similarity"), dataset1, dbpediaInfoboxValueInDomainScoreMapFile, f, splitPattern);
//			DBPediaTweetCorpusConstructWorkflow workflow1 = new DBPediaTweetCorpusConstructWorkflow();
//			workflow1.setProperty("TweetDBPediaCorpusReader.TweetSet", dataset1);
//			workflow1.setProperty("TweetDBPediaCorpusReader.QueryToResourceMap", twitterQueryFile2);
//			workflow1.setProperty("StanfordPosAnnotator.ModelFile", stanfodPosTaggerModel);
//			workflow1.setProperty("DBPediaNGramPropAnnotator.OntologyFile1", dbpediaOntologyInDomainFile);
//			workflow1.setProperty("DBPediaNGramPropAnnotator.OntologyFile2", dbpediaInfoboxPropertyDefFile);
//			workflow1.setProperty("DBPediaNGramPropAnnotator.NGram", "5");
//			workflow1.setProperty("DBPediaNGramPropAnnotator.TwitterQueryFile", twitterQueryFile2);
//			workflow1.setProperty("DBPediaNGramPropAnnotator.PropertyListFile", dbpediaPropertyInDomainFilteredListFile);
//			workflow1.setProperty("DBPediaNGramPropAnnotator.WriteDebugFile", "true");
//			workflow1.setProperty("DBPediaNGramPropAnnotator.DebugFile", trainingset1 + ".debug");
//			workflow1.setProperty("DBPediaNGramPropAnnotator.Annotating", "true");
//			workflow1.setProperty("DBPediaNGramPropAnnotator.WordToPropertyMapFile", dbpediaInfoboxValueToPropertyInDomainMapFile);
//			workflow1.setProperty("TweetEntityCorpusWriter.TweetEntityCorpus", trainingset1);
//			workflow1.setProperty("TweetXmiWriterCasConsumer.write", "false");
//			workflow1.setProperty("TweetXmiWriterCasConsumer.OutputDirectory", "");
//			workflow1.run(true);	
			
//			helper.tweetCorpusFilterByIrrelevance(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile + ".refined.user.stopword"), dataset2, dbpediaInfoboxValueInDomainScoreMapFile, f, splitPattern);
//			helper.tweetCorpusReplaceQueryWord(new TwitterTextDumpCorpusReader(dataset2), dataset2 + ".querynull", "NULL");
//			dataset2 += ".querynull";
//			DBPediaTweetCorpusConstructWorkflow workflow2 = new DBPediaTweetCorpusConstructWorkflow();
//			workflow2.setProperty("TweetDBPediaCorpusReader.TweetSet", dataset2);
//			workflow2.setProperty("TweetDBPediaCorpusReader.QueryToResourceMap", twitterQueryFile2);
//			workflow2.setProperty("StanfordPosAnnotator.ModelFile", stanfodPosTaggerModel);
//			workflow2.setProperty("DBPediaNGramPropAnnotator.OntologyFile1", dbpediaOntologyInDomainFile);
//			workflow2.setProperty("DBPediaNGramPropAnnotator.OntologyFile2", dbpediaInfoboxPropertyDefFile);
//			workflow2.setProperty("DBPediaNGramPropAnnotator.NGram", "5");
//			workflow2.setProperty("DBPediaNGramPropAnnotator.TwitterQueryFile", twitterQueryFile2);
//			workflow2.setProperty("DBPediaNGramPropAnnotator.PropertyListFile", dbpediaPropertyInDomainFilteredListFile);
//			workflow2.setProperty("DBPediaNGramPropAnnotator.WriteDebugFile", "true");
//			workflow2.setProperty("DBPediaNGramPropAnnotator.DebugFile", trainingset2 + ".debug");
//			workflow2.setProperty("DBPediaNGramPropAnnotator.Annotating", "false");
//			workflow2.setProperty("TweetEntityCorpusWriter.TweetEntityCorpus", trainingset2);
//			workflow2.setProperty("TweetXmiWriterCasConsumer.write", "false");
//			workflow2.setProperty("TweetXmiWriterCasConsumer.OutputDirectory", "");
//			workflow2.run(true);	
			
			int nfold = 5;
//			helper.mergeFilesByLinesToNFolds(trainingset1, trainingset2, nfold, trainingset);
	}

}
