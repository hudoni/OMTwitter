package com.maalaang.omtwitter.tools;

public class ConstructTwitterSentimentCorpus {

	/*
			String sentimentFile1 = twitterDomainCorpusFile + ".senti";
			String sentimentFile2 = twitterSampleStreamCorpusFile + ".senti";
			String sentimentSmileyRemovedFile = twitterDomainCorpusFile + ".senti.smiley.removed.merged";
			String sentimentNoSmileyTweetFile = twitterDomainCorpusFile + ".senti.nosmiley";
			
//			helper.sentiment(twitterDomainCorpusFile, sentimentFile, false);
			
//			File sentiNoSmileyTweetFile = new File(sentimentNoSmileyTweetFile);
//			sentiNoSmileyTweetFile.delete();
//			helper.sentiment(new TwitterDBPediaCorpusReader(twitterDomainCorpusFile + ".refined.user.hashtag.similarity"), sentimentSmileyRemovedFile+".part1", sentimentNoSmileyTweetFile, true, false, true);
//			helper.sentiment(new TwitterTextDumpCorpusReader(twitterSampleStreamCorpusFile + ".refined.user.stopword"), sentimentSmileyRemovedFile+".part2", sentimentNoSmileyTweetFile, true, false, true);
//			helper.sentimentMerge(sentimentSmileyRemovedFile+".part1", sentimentSmileyRemovedFile+".part2", sentimentSmileyRemovedFile);
					
			String sentiWordNetDicFile = "data/sentiwordnet/SentiWordNet_3.0.0_20100908.stem.avg.dic";
			String stanfordSetUnigramScoreDicFile = "data/dic/stanford_set_unigram_score.dic";
			String unigramScoreDicFile = sentimentSmileyRemovedFile + ".unigram_score.dic";
			
			UnigramDic dic = new UnigramDic();
//			dic.buildDic(new TwitterDBPediaSentiCorpusReader(sentimentSmileyRemovedFile), unigramScoreDicFile);
			
//			double tweetNeutralFilterSWNParam = 0.043 - (0.043 * 0.2);
//			double tweetNeutralFilterCorpusParam = 0.31 - (0.31 * 0.2);
			double tweetNeutralFilterSWNParam = 0.0407 * 0.75;
			double tweetNeutralFilterCorpusParam = 0.3044 * 0.75;
			String sentimentNeutralTweetFile = sentimentNoSmileyTweetFile + ".filtered";
			TweetSentiCorpusNeutralFilteringWorkflow workflow3 = new TweetSentiCorpusNeutralFilteringWorkflow();
			workflow3.setProperty("TweetDBPediaSentiCorpusReader.TweetSet", sentimentNoSmileyTweetFile);
			workflow3.setProperty("TweetDBPediaSentiCorpusReader.TweetSetSize", "0");
			workflow3.setProperty("StanfordPosAnnotator.ModelFile", stanfodPosTaggerModel);
			workflow3.setProperty("SentiWordnetAvgScoreAnnotator.DictionaryFile", sentiWordNetDicFile);
			workflow3.setProperty("TwitterSentiScoreAnnotator.DictionaryFile", unigramScoreDicFile);
			workflow3.setProperty("TweetNeutralTweetWriter.TweetSet", sentimentNeutralTweetFile);
			workflow3.setProperty("TweetNeutralTweetWriter.Param1", String.valueOf(tweetNeutralFilterSWNParam));
			workflow3.setProperty("TweetNeutralTweetWriter.Param2", String.valueOf(tweetNeutralFilterCorpusParam));
			workflow3.setProperty("TweetXmiWriterCasConsumer.write", "false");
			workflow3.setProperty("TweetXmiWriterCasConsumer.OutputDirectory", "E:/Development/UIMA Annotation Result/xmi/TweetSentiCorpusNeutralFilteringWorkflow");
//			workflow3.run(true);	
			
			String sentimentCorpusFile = sentimentSmileyRemovedFile + ".neutral.added";
			String sentimentCorpusUnigramDicFile = sentimentCorpusFile + ".unigram_score.dic";
			
			helper.sentimentMerge(sentimentSmileyRemovedFile, sentimentNeutralTweetFile, sentimentCorpusFile);
			*/
}
