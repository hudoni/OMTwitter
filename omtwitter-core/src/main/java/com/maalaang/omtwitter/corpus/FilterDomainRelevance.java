/**
 * 
 */
package com.maalaang.omtwitter.corpus;

/**
 * @author Sangwon Park
 *
 */
public class FilterDomainRelevance implements TweetFilter {
	/*
	public void tweetCorpusFilterByRelevance(TwitterCorpusReader reader, String out, String scoreFile, double threshold, String splitPattern) {
		try {
			Map<String, Double> scoreMap = CollectionTextReader.readMapStringDouble(scoreFile);
			
			Set<String> stopwordSet = stopwordSet();
			int tweetCnt = 0;
			
			ArrayList<Double> scoreList = new ArrayList<Double>();
			
			while (reader.hasNext()) {
				reader.next();
				tweetCnt++;
				
				int wordCnt = 0;
				double score = 0.0;
				String text = reader.getText().toLowerCase();
				
				Matcher urlMatcher = pURL.matcher(text);
				text = urlMatcher.replaceAll("URL");
				Matcher userMatcher = pUser.matcher(text);
				text = userMatcher.replaceAll("USER");
				
				String[] tokens = text.split(splitPattern);
				Double value = null;
				
				for (String t : tokens) {
					if (stopwordSet.contains(t)) {
						continue;
					}
					wordCnt++;
					
					value = scoreMap.get(t);
					if (value != null) {
						score += value;
					}
				}
				if (wordCnt != 0) {
					score = score / wordCnt;
				} else {
					score = 0.0;
				}
				
				scoreList.add(score);
			}
			
			Collections.sort(scoreList);
			
			double thresholdScore = scoreList.get((int)(tweetCnt * threshold));
			
			logger.info("found threshold_score=" + thresholdScore + " (threshold=" + threshold + ")");
			logger.info("start filtering");
			
			reader.reset();
			FileWriter fw = new FileWriter(out);
			
			while (reader.hasNext()) {
				reader.next();
				
				int wordCnt = 0;
				double score = 0.0;
				String text = reader.getText().toLowerCase();
				
				Matcher urlMatcher = pURL.matcher(text);
				text = urlMatcher.replaceAll("URL");
				
				Matcher userMatcher = pUser.matcher(text);
				text = userMatcher.replaceAll("USER");
				
				String[] tokens = text.split(splitPattern);
				Double value = null;
					
				for (String t : tokens) {
					if (stopwordSet.contains(t)) {
						continue;
					}
					wordCnt++;
					
					value = scoreMap.get(t);
					if (value != null) {
						score += value;
					}
				}
				if (wordCnt != 0) {
					score = score / wordCnt;
				} else {
					score = 0.0;
				}
				
				if (score >= thresholdScore) {
					String query = reader.getQuery();
					if (query != null) {
						fw.write(reader.getQuery());
						fw.write('\t');
					}
					fw.write(reader.getUser());
					fw.write('\t');
					fw.write(reader.getText());
					fw.write('\n');
				} else {
					logger.info("filtered by relevance: " + reader.getQuery() + "\t" + reader.getText());
				}
			}
			
			reader.close();
			fw.close();
			
			logger.info("'" + out + "' filtered by relevance has been created (based on " + scoreFile + ")");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

}
