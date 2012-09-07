/**
 * 
 */
package com.maalaang.omtwitter.ontology;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Sangwon Park
 *
 */
public class DBPediaDomainOntologyBuilder {
	private Logger logger = null;
	private final int LITERAL_KEY_MAX_LEN = 20;
	
	public DBPediaDomainOntologyBuilder() {
		logger = Logger.getLogger(getClass().getName());
	}

	/**
	 * Collects statements about the resources and calculate the frequency of each predicate-object pattern.
	 * @param resources a set of resources to find predicate-object patterns of their statements
	 * @param interval milliseconds for the time interval between queries to DBPedia 
	 * @return a map for predicate-object patterns and their frequencies
	 * @throws InterruptedException
	 */
	public Map<String,Integer> stmtPatternFrequency(Set<String> resources, int interval) throws InterruptedException {
		HashMap<String,Integer> map = new HashMap<String, Integer>();

		for (String id : resources) {
			String q = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"SELECT ?p ?o WHERE {" +
					" <" + id + "> ?p ?o " + 
					"}";
			logger.info("find statement about <" + id + "> - query to dbpedia: " + q);

			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPediaConstant.DBPEDIA_SPARQL_ENDPOINT, query);

			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				try {
					QuerySolution qs = results.next();
					Resource p = qs.getResource("p");
					RDFNode o = qs.get("o");
					String oKey = null;

					if (o.isResource()) {
						oKey = o.asResource().getURI();
					} else {
						oKey = o.asLiteral().getValue().toString().replaceAll("\\s+", " ");
						if (oKey.length() > LITERAL_KEY_MAX_LEN) {
							oKey = oKey.substring(0, LITERAL_KEY_MAX_LEN);
						}
					}

					String stmt = p.getURI() + " " + oKey;

					logger.debug(stmt);

					Integer freq = map.get(stmt);
					if (freq != null) {
						map.put(stmt, ++freq);
					} else {
						map.put(stmt, 1);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}

			qexec.close() ;

			if (interval > 0) {
				logger.info("wait for " + (interval / 1000) + " seconds");
				Thread.sleep(interval);
			}
		}
		
		return map;
	}
	
	/**
	 * Find statements matched with the predicate-object pattern by querying to DBPedia SparQL end-point and add them to the resource set.
	 * @param stmtPatternFreqMap
	 * @param resources
	 * @param paramSeedCommonness
	 * @param targetProperties
	 * @param interval
	 * @throws InterruptedException
	 */
	public void expandResources(Map<String,Integer> stmtPatternFreqMap, Set<String> resources, double paramSeedCommonness, Set<String> targetProperties, int interval) throws InterruptedException {
		int threshold = (int) Math.ceil(resources.size() * paramSeedCommonness);
		logger.info("expand resources - pattern_frequency_threshold=" + threshold);
		
		Set<Entry<String,Integer>> entrySet = stmtPatternFreqMap.entrySet();
		for (Entry<String,Integer> entry : entrySet) {
			if (entry.getValue() >= threshold) {
				String stmtStr = entry.getKey();
				int idx = stmtStr.indexOf(' ');
				String prop = stmtStr.substring(0, idx);
				String obj = stmtStr.substring(idx+1);
				if (!obj.startsWith(DBPediaConstant.DBPEDIA_RESOURCE_URI_PREFIX))
					continue;
				
				if (targetProperties != null) {
					if (targetProperties.contains(prop)) {
						addMatchedResources(resources, prop, obj);
						Thread.sleep(interval);
					}
				} else {
					addMatchedResources(resources, prop, obj);
					Thread.sleep(interval);
				}
			}
		}
		
		logger.info("total " + resources.size() + " resources in the resource set");
	}
	
	private void addMatchedResources(Set<String> resources, String p, String o) {
		logger.info("find resources matched with the pattern - <s> < " + p + "> <" + o + ">");
		
		String q = "SELECT ?s WHERE { " +
				" ?s <" + p + "> <" + o + "> " + 
				"}";
		
		logger.info("query to dbpedia: " + q);
		
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPediaConstant.DBPEDIA_SPARQL_ENDPOINT, query);
		ResultSet results = qexec.execSelect();		
		int cnt = 0;
		
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			RDFNode s = qs.get("s");
			if (s.isResource()) {
				String uri = s.asResource().getURI();
				resources.add(uri);
				cnt++;
			}
		}
		
		logger.info(cnt + " matched resources are added");
	}
	
	public Model retrieveStmtsForResources(Set<String> resources, int interval) throws IOException, InterruptedException {
		Model model = ModelFactory.createDefaultModel();
		int totalCnt = 0;
		
		for (String uri : resources) {
			Resource s = model.createResource(uri);
			
			String q = "SELECT ?p ?o WHERE { " +
					" <" + uri + "> ?p ?o . " + 
					"}";
			
			logger.info("query to dbpedia - " + q);
			
			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPediaConstant.DBPEDIA_SPARQL_ENDPOINT, query);

			ResultSet results = null;
			while (results == null) {
				try {
					results = qexec.execSelect();
				} catch (Exception e) {
					logger.error(e);
					logger.info("wait for " + (interval / 1000) + " seconds");
					Thread.sleep(interval);
				}		
			}
			
			int cnt = 0;
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				Resource propRes = qs.getResource("p");
				Property prop = model.createProperty(propRes.getURI());
				model.add(s, prop, qs.get("o"));
				cnt++;
			}
			totalCnt += cnt;
			logger.info(cnt + " triples were added");
			logger.info("wait for " + (interval / 1000) + " seconds");
			Thread.sleep(interval);
		}
		
		logger.info("total " + totalCnt + " triples were added");
		
		return model;
	}
}
