/**
 * 
 */
package com.maalaang.omtwitter.corpus;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author Sangwon Park
 *
 */
public class TwitterQueryGenerator {
	
	public static Map<String,Set<String>> generateQueries(Model domainOntologyModel, Set<String> stopwords, String lang, int minToken) throws IOException {
		Property labelProperty = domainOntologyModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
		HashMap<String,Set<String>> map = new HashMap<String,Set<String>>();
		
		ResIterator resIt = domainOntologyModel.listSubjects();
		while (resIt.hasNext()) {
			Resource res = resIt.next();
			String uri = res.getURI();
			StmtIterator stmtIter = res.listProperties(labelProperty);
			
			while (stmtIter.hasNext()) {
				Statement stmt = stmtIter.next();
				if (lang != null && lang.equals(stmt.getLanguage())) {
					String label = stmt.getLiteral().getString();
					
					label = label.toLowerCase().replaceAll("\\(|\\)|,", "");
					String[] tokens = label.split("\\s+");
					
					for (int i = 0; i < tokens.length; i++) {
						if (tokens.length != 1 && tokens.length - i < minToken) {
							break;
						}
						String strNgram = null;
						for (int j = i; j < tokens.length; j++) {
							if (tokens[j].matches("[\\W]+")) {
								continue;
							}
							
							if (j == i) {
								strNgram = tokens[j];
							} else {
								strNgram += " " + tokens[j];
							}
						}
						if (stopwords.contains(strNgram)) {
							continue;
						}
						if (strNgram.matches("[0-9\\W]+")) {
							continue;
						}
						if (strNgram.length() < 5) {
							continue;
						}
						strNgram = strNgram.replaceFirst("\\.+$", "").replaceAll("-", "");
						
						String query = "\"" + strNgram + "\"";
						String hashtag = "#" + strNgram.replaceAll("[\\p{Punct}\\s]+", "");
						
						Set<String> value = map.get(query);
						if (value == null) {
							HashSet<String> set = new HashSet<String>();
							set.add(uri);
							map.put(query, set);
						} else {
							value.add(uri);
						}
						value = map.get(hashtag);
						if (value == null) {
							HashSet<String> set = new HashSet<String>();
							set.add(uri);
							map.put(hashtag, set);
						} else {
							value.add(uri);
						}
					}
				}
			}
		}
		
		domainOntologyModel.close();
		return map;
	}	
}
