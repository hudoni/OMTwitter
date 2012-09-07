package com.maalaang.omtwitter.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionTextReader {
	public static Map<String, Integer> readMapStringInteger(String file) throws IOException {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			System.out.flush();
			String[] tokens = line.split("\t");
			map.put(tokens[0].trim(), Integer.parseInt(tokens[1].trim()));
		}
		br.close();
		
		return map;
	}

	public static Map<String, Double> readMapStringDouble(String file) throws IOException {
		HashMap<String, Double> map = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			map.put(tokens[0].trim(), Double.parseDouble(tokens[1].trim()));
		}
		br.close();
		
		return map;
	}
	
	public static Map<String, String> readMapStringString(String file) throws IOException {
		HashMap<String, String> map = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = null;
		
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			map.put(tokens[0].trim(), tokens[1].trim());
		}
		br.close();
		
		return map;
	}
	
	public static Map<String, Set<String>> readMapStringSetString(String file) throws IOException {
		HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < tokens.length; i++) {
				set.add(tokens[i].trim());
			}
			map.put(tokens[0].trim(), set);
		}
		br.close();
		
		return map;
	}
	
	public static Set<String> readSetString(String file) throws IOException {
		HashSet<String> set = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			set.add(line.trim());
		}
		br.close();
		
		return set;
	}
}
