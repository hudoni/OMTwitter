/**
 * 
 */
package com.maalaang.omtwitter.corpus;

/**
 * @author Sangwon Park
 *
 */
public class FilterStopword {
	/*
	public void tweetCorpusFilterByStopword(TwitterCorpusReader reader, String out, int threshold) {
		try {
			Set<String> stopwordSet = stopwordSet();
			
			FileWriter fw = new FileWriter(out);
			
			while (reader.hasNext()) {
				reader.next();
				
				String query = reader.getQuery();
				String text = reader.getText().toLowerCase();
				text = pURL.matcher(text).replaceAll("URL");
				
				String[] tokens = text.split("\\s+");
				int stopwordCnt = 0;
				
				for (String tok : tokens) {
					if (stopwordSet.contains(tok)) {
						if (++stopwordCnt >= threshold) {
							break;
						}
					}
				}
				
				if (stopwordCnt >= threshold) {
					if (query != null) {
						fw.write(query);
						fw.write('\t');
					}
					fw.write(reader.getUser());
					fw.write('\t');
					fw.write(reader.getText());
					fw.write('\n');
				} else {
					logger.info("filtered by stopwords: " + reader.getText());
				}
			}
			
			fw.close();
			logger.info("'" + out + "' filtered by stopwords has been created (threshold=" + threshold + ")");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
