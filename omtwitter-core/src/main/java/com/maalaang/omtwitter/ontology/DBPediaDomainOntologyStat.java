/**
 * 
 */
package com.maalaang.omtwitter.ontology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.maalaang.omtwitter.io.CollectionTextWriter;
import com.maalaang.omtwitter.text.InfoboxValueTokenizer;
import com.maalaang.omtwitter.text.WordPattern;

/**
 * @author Sangwon Park
 *
 */
public class DBPediaDomainOntologyStat {
	private final int STMT_SEGMENT_SIZE = 2000000;
	private final int INFOBOX_VALUE_WORD_LEN_MAX = 20;
	private final int INFOBOX_VALUE_WORD_LEN_MIN = 2;
	private final int INFOBOX_PROP_NAME_LEN_MIN = 2;
	
	private final String STMT_LANG = "en";
	
	private Logger logger = null;

	public DBPediaDomainOntologyStat() {
		logger = Logger.getLogger(getClass());
	}

	/**
	 * Note: Words are extracted from the literals of infobox properties by InfoboxValueTokenizer.tokenizeToWord()
	 * @param infoboxPropertiesFile
	 * @param stopwords
	 * @param resources
	 * @param infoboxValueUnigramFreqMapFile
	 * @return
	 * @throws IOException
	 */
	public Map<String,Integer> infoboxValueWordFreq(String infoboxPropertiesFile, Set<String> stopwords, Set<String> resources, String infoboxValueUnigramFreqMapFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infoboxPropertiesFile), "UTF-8"));
		String line = null;

		int segmentCnt = 0;

		long tokenCnt = 0;
		long literalCnt = 0;
		long statementCnt = 0;

		boolean doProcessing = true;

		while (doProcessing) {
			Model model = ModelFactory.createDefaultModel();
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			int remainedStmtNum = STMT_SEGMENT_SIZE;

			while ((line = br.readLine()) != null && remainedStmtNum > 0) {
				try {
					statementCnt++;		
					model.read(new StringReader(line), null, "N-TRIPLE");
					remainedStmtNum--;
				} catch (Exception e) {
					logger.info("exception on reading (skipped) - " + line);
				}
			}
			if (line == null) {
				doProcessing = false;
			}

			StmtIterator iter = model.listStatements();

			while (iter.hasNext()) {
				Statement stmt = iter.next();
				if (resources != null && !resources.contains(stmt.getSubject().getURI())) {
					continue;
				}

				RDFNode obj = stmt.getObject();
				if (obj.isLiteral()) {
					literalCnt++;
					String[] tokens = InfoboxValueTokenizer.tokenizeToWord(stmt.getString().toLowerCase());

					for (String t : tokens) {
						if (t.length() > INFOBOX_VALUE_WORD_LEN_MAX || t.length() < INFOBOX_VALUE_WORD_LEN_MIN) {
							continue;
						}
						if (stopwords.contains(t)) {
							continue;
						}							

						Integer value = map.get(t);
						if (value == null) {
							map.put(t, 1);
						} else {
							map.put(t, ++value);
						}
						tokenCnt++;
					}
				}
			}
			String tmpFileName = String.format("%s.%03d", infoboxValueUnigramFreqMapFile, segmentCnt++);
			CollectionTextWriter.writeMapStringInteger(map, tmpFileName, false);
			logger.info("write temp file - " + tmpFileName);
		}
		br.close();

		Map<String,Integer> map = freqMapFileMerge(infoboxValueUnigramFreqMapFile, segmentCnt, true);

		logger.info("total " + statementCnt + " statements processed");
		logger.info("total " + tokenCnt + " tokens observed");
		logger.info("total " + literalCnt + " literals observed");

		return map;
	}

	private Map<String,Integer> freqMapFileMerge(String infoboxValueUnigramFreqMapFile, int segmentNumber, boolean deleteTmpFiles) throws IOException {
		String line = null;
		HashMap<String,Integer> map = new HashMap<String,Integer>();


		for (int i = 0; i < segmentNumber; i++) {
			String tmpFileName = String.format("%s.%03d", infoboxValueUnigramFreqMapFile, i);
			File tmpFile = new File(tmpFileName);

			logger.info("merge data in " + tmpFileName);

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tmpFile), "UTF-8"));

			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				Integer value = map.get(tokens[0]);
				if (value == null) {
					map.put(tokens[0], Integer.parseInt(tokens[1]));
				} else {
					map.put(tokens[0], value + Integer.parseInt(tokens[1]));
				}
			}

			br.close();

			if (deleteTmpFiles) {
				logger.info("delete temp file - " + tmpFileName);
				tmpFile.delete();
			}
		}
		return map;
	}

	public Map<String,Double> infoboxValueWordRelevanceScore(Map<String,Integer> wordFreqDomainMap, Map<String,Integer> wordFreqEntireMap, boolean normalize) {
		Map<String,Double> map = new HashMap<String,Double>();
		Collection<Integer> values = null;

		int freqSumDomain = 0;
		int freqSumEntire = 0;
		double maxValue = 0.0;

		values = wordFreqDomainMap.values();
		for (Integer v : values) {
			freqSumDomain += v;
		}

		values = wordFreqEntireMap.values();
		for (Integer v : values) {
			freqSumEntire += v;
		}

		Set<Entry<String,Integer>> wordFreqDomainSet = wordFreqDomainMap.entrySet();
		for (Entry<String,Integer> e : wordFreqDomainSet) {
			String key = e.getKey();
			Integer o1 = e.getValue();
			Integer o2 = wordFreqEntireMap.get(key);

			if (o2 == null) {
				logger.warn("'" + key + "' doesn't exist in the infobox value map " + wordFreqEntireMap);
			} else if (o2 < o1) {
				logger.warn("the infobox value maps are not consistent about '" + key + "'");
			}

			double value = ((double) o1 / (double) freqSumDomain) * Math.log((double)(freqSumEntire - freqSumDomain) / (double)(o2 - o1 + 1));
			if (value > maxValue) {
				maxValue = value;
			}
			map.put(key, value);
		}

		if (normalize) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				map.put(key, map.get(key) / maxValue);
			}
		}
		
		return map;
	}
	
	public Map<String,Integer> propertyFreq(String infoboxPropertiesFile, Set<String> resources, String prefix, Pattern filterPattern, boolean useLocalname, String propertyFreqMapFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infoboxPropertiesFile), "UTF-8"));
		String line = null;

		int segmentCnt = 0;
		long statementCnt = 0;

		boolean doProcessing = true;
		HashSet<String> propSet = new HashSet<String>();

		while (doProcessing) {
			Model model = ModelFactory.createDefaultModel();
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			int remainedStmtNum = STMT_SEGMENT_SIZE;

			while ((line = br.readLine()) != null && remainedStmtNum > 0) {
				try {
					statementCnt++;		
					model.read(new StringReader(line), null, "N-TRIPLE");
					remainedStmtNum--;
				} catch (Exception e) {
					logger.info("exception on reading (skipped) - " + line);
				}
			}
			if (line == null) {
				doProcessing = false;
			}

			ResIterator resIt = model.listSubjects();
			while (resIt.hasNext()) {
				Resource res = resIt.next();
				if (resources != null && !resources.contains(res.getURI())) {
					continue;
				}
					
				StmtIterator stmtIt = res.listProperties();
				while (stmtIt.hasNext()) {
					Statement stmt = stmtIt.next();
					Property p = stmt.getPredicate();
					
					String pUri = p.getURI();
					String pLocalName = p.getLocalName();
					
					if (pLocalName.length() < INFOBOX_PROP_NAME_LEN_MIN || ((prefix != null) && !pUri.startsWith(prefix))) {
						continue;
					}
					
					String pName = null;
					if (useLocalname) {
						pName = pLocalName;
					} else {
						pName = pUri;
					}
	
					Matcher matcher = filterPattern.matcher(pName.toLowerCase());
					if (!matcher.find() && pName.length() > 1) {
						if (!propSet.contains(pName)) {
							Integer value = map.get(pName);
							if (value == null) {
								map.put(pName, 1);
							} else {
								map.put(pName, ++value);
							}
							propSet.add(pName);
						}
					}
				}
				
				propSet.clear();
			}
			
			String tmpFileName = String.format("%s.%03d", propertyFreqMapFile, segmentCnt++);
			CollectionTextWriter.writeMapStringInteger(map, tmpFileName, false);
			logger.info("write temp file - " + tmpFileName);
		}
		br.close();
	
		Map<String,Integer> map = freqMapFileMerge(propertyFreqMapFile, segmentCnt, true);
		logger.info("total " + statementCnt + " statements processed");
	
		return map;
	}		
	
	public Map<String,Double> propertyImportanceScore(Map<String,Integer> propertyFreqDomainMap, Map<String,Integer> propertyFreqEntireMap, boolean normalize) {
		Map<String,Double> map = new HashMap<String,Double>();
		Collection<Integer> values = null;

		int freqSumDomain = 0;
		int freqSumEntire = 0;
		double maxValue = 0.0;

		values = propertyFreqDomainMap.values();
		for (Integer v : values) {
			freqSumDomain += v;
		}

		values = propertyFreqEntireMap.values();
		for (Integer v : values) {
			freqSumEntire += v;
		}

		Set<Entry<String,Integer>> propertyFreqDomainSet = propertyFreqDomainMap.entrySet();
		for (Entry<String,Integer> e : propertyFreqDomainSet) {
			String key = e.getKey();
			Integer o1 = e.getValue();
			Integer o2 = propertyFreqEntireMap.get(key);

			if (o2 == null) {
				logger.warn("'" + key + "' doesn't exist in the infobox value map " + propertyFreqEntireMap);
			} else if (o2 < o1) {
				logger.warn("the infobox value maps are not consistent about '" + key + "'");
			}

			double value = ((double) o1 / (double) freqSumDomain) * Math.log((double)(freqSumEntire - freqSumDomain) / (double)(o2 - o1 + 1));
			if (value > maxValue) {
				maxValue = value;
			}
			map.put(key, value);
		}

		if (normalize) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				map.put(key, map.get(key) / maxValue);
			}
		}
		
		return map;
	}
			
	public Set<String> delegateProperties(Map<String,Double> propertyFreqMap, double paramDelegateProperty) throws IOException {
		ArrayList<Entry<String,Double>> entryList = new ArrayList<Entry<String,Double>>(propertyFreqMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<String,Double>>() {
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				if (o2.getValue() - o1.getValue() >= 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		int limit = (int)(entryList.size() * paramDelegateProperty);
		HashSet<String> set = new HashSet<String>(limit);
		for (int i = 0; i < limit; i++) {
			set.add(entryList.get(i).getKey());
		}
		
		return set;
	}

	public Map<String,Integer> infoboxValuePropertyFreq(String ontologyFile, String propertyValueFreqFile, Set<String> delegateProperties, Set<String> stopwords, int minToken, int maxToken) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ontologyFile), "UTF-8"));
		String line = null;

		int segmentCnt = 0;
		long literalCnt = 0;
		long statementCnt = 0;

		boolean doProcessing = true;

		while (doProcessing) {
			Model model = ModelFactory.createDefaultModel();
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			int remainedStmtNum = STMT_SEGMENT_SIZE;

			while ((line = br.readLine()) != null && remainedStmtNum > 0) {
				try {
					statementCnt++;		
					model.read(new StringReader(line), null, "N-TRIPLE");
					remainedStmtNum--;
				} catch (Exception e) {
					logger.info("exception on reading (skipped) - " + line);
				}
			}
			if (line == null) {
				doProcessing = false;
			}
			
			StmtIterator iter = model.listStatements();

			while (iter.hasNext()) {
				Statement stmt = iter.next();
				
				if (delegateProperties != null && !delegateProperties.contains(stmt.getPredicate().getURI())) {
					continue;
				}
				
				RDFNode obj = stmt.getObject();
				
				if (obj.isLiteral()) {
					String lang = stmt.getLanguage();
					String propertyName = stmt.getPredicate().getLocalName();
					literalCnt++;
					
					if (STMT_LANG.equals(lang) || lang.length() == 0) {
						String[] valueTokens = InfoboxValueTokenizer.tokenizeToValues(stmt.getString().toLowerCase());
						
						for (String s : valueTokens) {
							s = s.trim();
							
							if (s.length() < 1)
								continue;
							
							if (stopwords.contains(s))
								continue;
							
							String[] tokens = s.split(" ");
							if (tokens.length > maxToken || tokens.length < minToken)
								continue;
							
							WordPattern.normalize(tokens);
							
							boolean hasWord = false;
							for (String t : tokens) {
								if (WordPattern.isPatternName(t) == false) {
									hasWord = true;
									break;
								}
							}
							if (!hasWord)
								continue;
							
							StringBuilder sb = null;
							for (String t : tokens) {
								if (sb == null) {
									sb = new StringBuilder();
									sb.append(t);
								} else {
									sb.append(' ');
									sb.append(t);
								}
							}
							String normValue = sb.toString();
							
							if (normValue.matches("^\\p{Punct}+"))
								continue;
							
							String key = propertyName + ";" + normValue;
							Integer value = map.get(key);
							if (value == null) {
								map.put(key, 1);
							} else {
								map.put(key, ++value);
							}
						}
					}
				}
			}
			String tmpFileName = String.format("%s.%03d", propertyValueFreqFile, segmentCnt++);
			CollectionTextWriter.writeMapStringInteger(map, tmpFileName, false);
			logger.info("write temp file - " + tmpFileName);
		}
		br.close();

		Map<String,Integer> map = freqMapFileMerge(propertyValueFreqFile, segmentCnt, true);

		logger.info("total " + statementCnt + " statements processed");
		logger.info("total " + literalCnt + " literals observed");

		return map;
	}

	public void infoboxValueToPropertyMap(String valuePropertyFreqMapFile, String valueToPropertyMapFile, Set<String> delegateProperties, boolean sort) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(valuePropertyFreqMapFile), "UTF-8"));
		String line = null;

		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		Object[] propFreq = null;

		while ((line = br.readLine()) != null) {
			String[] tokens = line.trim().split("[\t;]+");
			if (tokens.length != 3) {
				continue;
			}

			if (!delegateProperties.contains(DBPediaConstant.DBPEDIA_INFOBOX_PROP_URI_PREFIX + tokens[0])) {
				continue;
			}

			propFreq = map.get(tokens[1]);
			if (propFreq == null) {
				propFreq = new Object[2];
				propFreq[0] = tokens[0];
				propFreq[1] = Integer.parseInt(tokens[2]);
				map.put(tokens[1], propFreq);
			} else {
				Integer num = Integer.parseInt(tokens[2]);
				if (!((String)propFreq[0]).equals("name") && num > (Integer)propFreq[1]) {
					propFreq[0] = tokens[0];
					propFreq[1] = num;
				}
			}
		}
		
		br.close();

		for (String p : delegateProperties) {
			String propName = p.substring(p.lastIndexOf('/') + 1);

			propFreq = map.get(propName);
			if (propFreq == null) {
				propFreq = new Object[2];
				propFreq[0] = propName;
				propFreq[1] = 1;
				map.put(propName, propFreq);
			} else {
				propFreq[0] = propName;
				propFreq[1] = 1;
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueToPropertyMapFile), "UTF-8"));
		Set<Entry<String,Object[]>> set = map.entrySet();

		if (sort) {
			ArrayList<Entry<String,Object[]>> list = new ArrayList<Entry<String,Object[]>>(set);
			Collections.sort(list, new Comparator<Entry<String,Object[]>>() {
				public int compare(Entry<String, Object[]> o1, Entry<String, Object[]> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			
			for (Entry<String,Object[]> e : list) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write((String)e.getValue()[0]);
				bw.write('\n');
			}
			
		} else {
			for (Entry<String,Object[]> e : set) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write((String)e.getValue()[0]);
				bw.write('\n');
			}
		}

		bw.close();
	}
}
