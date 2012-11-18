/**
 * 
 */
package com.maalaang.omtwitter.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.maalaang.omtwitter.io.CollectionTextReader;
import com.maalaang.omtwitter.io.CollectionTextWriter;
import com.maalaang.omtwitter.ontology.DBPediaConstant;
import com.maalaang.omtwitter.ontology.DBPediaDomainOntologyStat;

/**
 * note: heap size -Xmx4000
 * @author Sangwon Park
 *
 */
public class BuildDBPediaDomainOntologyStat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DBPediaDomainOntologyStat stat = new DBPediaDomainOntologyStat();
			
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			Set<String> resources = CollectionTextReader.readSetString(prop.getProperty("resource.expanded.file"));
			Set<String> stopwords = CollectionTextReader.readSetString(prop.getProperty("stopword.set.file"));
			
			// properties
			Pattern propertyFilterPattern = Pattern.compile(prop.getProperty("property.filter.pattern"));
			
			Map<String,Integer> propertyFreqDomainMap = stat.propertyFreq(prop.getProperty("infobox.property.file"), resources, DBPediaConstant.DBPEDIA_INFOBOX_PROP_URI_PREFIX, propertyFilterPattern, false, prop.getProperty("property.freq.domain.file"));
			CollectionTextWriter.writeMapStringInteger(propertyFreqDomainMap, prop.getProperty("property.freq.domain.file"), true);
			
			Map<String,Integer> propertyFreqEntireMap = stat.propertyFreq(prop.getProperty("infobox.property.file"), null, DBPediaConstant.DBPEDIA_INFOBOX_PROP_URI_PREFIX, propertyFilterPattern, false, prop.getProperty("property.freq.entire.file"));
			CollectionTextWriter.writeMapStringInteger(propertyFreqEntireMap, prop.getProperty("property.freq.entire.file"), true);
			
			Map<String,Double> pisMap = stat.propertyImportanceScore(propertyFreqDomainMap, propertyFreqEntireMap, true);
			CollectionTextWriter.writeMapStringDouble(pisMap, prop.getProperty("property.importance.score.file"), true);
			
			Set<String> delegateProperties = stat.delegateProperties(pisMap, 0.1);
			CollectionTextWriter.writeSetString(delegateProperties, prop.getProperty("property.delegate.file"), true);
			
			// words
			Map<String,Integer> wordFreqDomainMap = stat.infoboxValueWordFreq(prop.getProperty("infobox.property.file"), stopwords, resources, prop.getProperty("word.freq.domain.file"));
			CollectionTextWriter.writeMapStringInteger(wordFreqDomainMap, prop.getProperty("word.freq.domain.file"), true);
			
			Map<String,Integer> wordFreqEntireMap = stat.infoboxValueWordFreq(prop.getProperty("infobox.property.file"), stopwords, null, prop.getProperty("word.freq.entire.file"));
			CollectionTextWriter.writeMapStringInteger(wordFreqEntireMap, prop.getProperty("word.freq.entire.file"), true);
			
			Map<String,Double> wrsMap = stat.infoboxValueWordRelevanceScore(wordFreqDomainMap, wordFreqEntireMap, true);
			CollectionTextWriter.writeMapStringDouble(wrsMap, prop.getProperty("word.relevance.score.file"), true);
			
			// value & properties
			Map<String,Integer> valuePropertyFreqMap = stat.infoboxValuePropertyFreq(prop.getProperty("domain.ontology.file"), prop.getProperty("value.property.freq.file"), delegateProperties, stopwords,
					Integer.parseInt(prop.getProperty("value.token.min")), Integer.parseInt(prop.getProperty("value.token.max")));
			CollectionTextWriter.writeMapStringInteger(valuePropertyFreqMap, prop.getProperty("value.property.freq.file"), false);
			
			stat.infoboxValueToPropertyMap(prop.getProperty("value.property.freq.file"), prop.getProperty("value.to.property.map.file"), delegateProperties, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
