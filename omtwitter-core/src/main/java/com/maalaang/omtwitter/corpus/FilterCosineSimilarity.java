/**
 * 
 */
package com.maalaang.omtwitter.corpus;

/**
 * @author Sangwon Park
 *
 */
public class FilterCosineSimilarity implements TweetFilter {
	/*

	public void tweetCorpusFilterByCosineSimilarity(TwitterCorpusReader reader, String out) throws IOException {
		FileWriter fw = new FileWriter(out);
		
		CosineSimiarity similarity = new CosineSimiarity();
		LinkedList<String[]> tweetList = new LinkedList<String[]>();
		HashSet<Integer> filteredSet = new HashSet<Integer>();
		
		reader.next();
		String prevQuery = reader.getQuery();		
		while (reader.hasNext()) {			String query = reader.getQuery();
			if (prevQuery.equals(query)) {
				tweetList.add(reader.getTokens());
				reader.next();				prevQuery = query;				
			} else {
				Matrix tweetMatrix = tweetFVMatrix(tweetList, query);
				Matrix similarityMatrix = similarity.transform(tweetMatrix);
				
				filteredSet.clear();
				
				for (int i = 0; i < similarityMatrix.getRowDimension(); i++) {
					if (!filteredSet.contains(i)) {
						for (int j = i + 1; j < similarityMatrix.getColumnDimension(); j++) {							if (!filteredSet.contains(j) && similarityMatrix.get(i, j) >= 0.7) {									filteredSet.add(j);
							}						}					}
				}
								int cnt = 0;				for (String[] s : tweetList) {					if (!filteredSet.contains(cnt)) {
						fw.write(s[0]);
						fw.write('\t');
						fw.write(s[1]);
						fw.write('\t');
						fw.write(s[2]);
						fw.write('\n');
//					} else {
//						logger.info("filtered by cosine similarity: " + s[1] + "\t" + s[2]);
					}					cnt++;
				}				tweetList.clear();								prevQuery = reader.getQuery();
			}	
		}		reader.close();
		fw.close();
	}
	*/
}
