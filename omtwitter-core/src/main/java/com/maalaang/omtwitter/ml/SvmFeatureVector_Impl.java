/**
 * 
 */
package com.maalaang.omtwitter.ml;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Sangwon Park
 *
 */
public class SvmFeatureVector_Impl implements SvmFeatureVector {
	private Map<Integer,Double> fvMap = null;
	
	public SvmFeatureVector_Impl() {
		fvMap = new TreeMap<Integer,Double>();
	}
	
	public void setFeatureValue(int feature, double value) {
		fvMap.put(feature, value);
	}

	public double getFeatureValue(int feature) {
		return fvMap.get(feature);
	}
	
	public String toString() {
		StringBuilder sb = null;
		
		Set<Entry<Integer,Double>> entrySet = fvMap.entrySet();
		Double value = null;
		
		for (Entry<Integer,Double> e : entrySet) {
			if (sb == null) {
				sb = new StringBuilder();
			} else {
				sb.append(' ');
			}
			sb.append(e.getKey());
			sb.append(':');
			
			value = e.getValue();
			if (value == 1.0) {
				sb.append("1.0");
			} else {
				sb.append(String.format(Locale.US, "%.4f", value));
			}
		}
		
		return sb.toString();
	}

	public int size() {
		return fvMap.size();
	}

	public void toArrays(int[] features, double[] values) {
		Set<Entry<Integer,Double>> fvMapEntrySet = fvMap.entrySet();
		int i = 0;
		
		for (Entry<Integer, Double> e : fvMapEntrySet) {
			features[i] = e.getKey();
			values[i] = e.getValue();
			i++;
		}
	}

}
