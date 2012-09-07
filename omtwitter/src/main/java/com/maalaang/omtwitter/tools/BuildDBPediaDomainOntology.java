/**
 * 
 */
package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.CollectionTextWriter;
import com.maalaang.omtwitter.ontology.DBPediaDomainOntologyBuilder;

/**
 * @author Sangwon Park
 *
 */
public class BuildDBPediaDomainOntology {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DBPediaDomainOntologyBuilder builder = new DBPediaDomainOntologyBuilder();
			
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			Set<String> resources = CollectionTextReader.readSetString(prop.getProperty("resource.seed.file"));
			Set<String> targetProperties = CollectionTextReader.readSetString(prop.getProperty("property.target.file"));
			
			Map<String,Integer> stmtPatternFreqMap = builder.stmtPatternFrequency(resources, Integer.parseInt(prop.getProperty("query.interval")));
			CollectionTextWriter.writeMapStringInteger(stmtPatternFreqMap, prop.getProperty("resource.seed.pattern.freq.file"), true);
			
			builder.expandResources(stmtPatternFreqMap, resources, Double.parseDouble(prop.getProperty("param.seed.commonness")), targetProperties, Integer.parseInt(prop.getProperty("query.interval")));
			CollectionTextWriter.writeSetString(resources, prop.getProperty("resource.expanded.file"), true);
			
			Model domainOntologyModel = builder.retrieveStmtsForResources(resources, Integer.parseInt(prop.getProperty("query.interval")));
			
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(prop.getProperty("domain.ontology.file")), "UTF-8");
			domainOntologyModel.write(osw, "N-TRIPLE");
			osw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
