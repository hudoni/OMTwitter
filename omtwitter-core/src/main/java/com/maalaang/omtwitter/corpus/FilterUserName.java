/**
 * 
 */
package com.maalaang.omtwitter.corpus;

/**
 * @author Sangwon Park
 *
 */
public class FilterUserName implements TweetFilter {
/*
	public void tweetCorpusUserFilterList(TwitterCorpusReader reader, String out, int threshold) {
		try {
			HashMap<String,Integer> userFreqMap = new HashMap<String, Integer>();
			
			while (reader.hasNext()) {
				reader.next();
				String user = reader.getUser();
				if (user == null) {
					continue;
				}
				
				Integer value = userFreqMap.get(user);
				if (value != null) {
					userFreqMap.put(user, value + 1);
				} else {
					userFreqMap.put(user, 1);
				}
			}
			
			FileWriter fw = new FileWriter(out);
			TreeSet<Entry<String,Integer>> set = new TreeSet<Entry<String,Integer>>(new Comparator<Entry<String,Integer>>() {
				@Override
				public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
					int diff = arg1.getValue() - arg0.getValue();
					if (diff == 0) {
						return arg1.getKey().compareTo(arg0.getKey());
					}
					return diff;
				}
			});
			set.addAll(userFreqMap.entrySet());
			
			for (Entry<String, Integer> entry : set) {
				if (entry.getValue() > threshold) {
					fw.write(entry.getKey());
					fw.write('\n');
				}
			}
			fw.close();
			
			logger.info("user filtering list was written on '" + out + "' (threashold=" + threshold + ")");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void tweetCorpusFilterByUser(TwitterCorpusReader reader, String out, String userList) {
		try {
			Set<String> userFilterSet = CollectionTextReader.readSet(userList);
			
			FileWriter fw = new FileWriter(out);
			
			while (reader.hasNext()) {
				reader.next();
				
				String user = reader.getUser();
				String query = reader.getQuery();
				if (user != null && !userFilterSet.contains(user)) {
					if (query != null) {
						fw.write(query);
						fw.write('\t');
					}
					fw.write(user);
					fw.write('\t');
					fw.write(reader.getText());
					fw.write('\n');
//				} else {
//					logger.info("filted by user: " + user + "\t" + reader.getText());
				}
			}
			
			fw.close();
			logger.info("'" + out + "' filtered by user has been created (filtered by '" + userList + "')");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
