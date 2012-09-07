package com.maalaang.omtwitter.corpus;

public class FilterHashtagUsage implements TweetFilter {

	/*
	public void tweetCorpusFilterByHashtagUsage(TwitterCorpusReader reader, String out) {
		try {
			FileWriter fw = new FileWriter(out);
			
			String prevUri = null;
			int filteredCnt = 0;
			int totalCnt = 0;
			
			while (reader.hasNext()) {
				reader.next();

				boolean filtered = false;

				String text = reader.getText().toLowerCase();
				String query = reader.getQuery().toLowerCase();

				if (query.charAt(0) == '#') {
					String[] tokens = text.split("\\s+");
					int hashtagIndex = 0;

					for (int i = 0; i < tokens.length; i++) {
						if (tokens[i].indexOf(query) != -1) {
							hashtagIndex = i;
							break;
						}
					}
					String prevToken = null;
					String nextToken = null;

					if (hashtagIndex > 0) {
						prevToken = tokens[hashtagIndex - 1];
					}
					if (hashtagIndex + 1 < tokens.length) {
						nextToken = tokens[hashtagIndex + 1];
					}

					if (nextToken == null || nextToken.indexOf("http://") != -1 || nextToken.indexOf("@") != -1 || nextToken.indexOf("#") != -1) {
						filtered = true;
						filteredCnt++;
					} else if (prevToken != null && (prevToken.indexOf("http://") != -1 || prevToken.indexOf("#") != -1)) {
						filtered = true;
						filteredCnt++;
					}
				}

				if (!filtered) {
					fw.write(reader.getQuery());
					fw.write('\t');
					fw.write(reader.getUser());
					fw.write('\t');
					fw.write(reader.getText());
					fw.write('\n');
//				} else {
//					logger.info("filtered by hashtag usage: " + reader.getUser() + "\t" + reader.getText());
					
				}
				totalCnt++;
			}

			fw.close();
			logger.info("'" + out + "', which was filtered by hashtag usage, has been created (" + filteredCnt + " / " + totalCnt + " are filtered out)");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
