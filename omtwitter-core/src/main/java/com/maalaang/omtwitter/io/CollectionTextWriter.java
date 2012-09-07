package com.maalaang.omtwitter.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class CollectionTextWriter {

	public static void writeMapStringInteger(Map<String,Integer> map, String file, boolean sort) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		Set<Entry<String,Integer>> set = map.entrySet();
		
		if (sort) {
			ArrayList<Entry<String,Integer>> list = new ArrayList<Entry<String,Integer>>(set);
			Collections.sort(list, new Comparator<Entry<String,Integer>>() {
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o2.getValue() - o1.getValue();
				}
			});
			for (Entry<String,Integer> e : list) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(String.format("%d\n", e.getValue()));
			}
		} else {
			for (Entry<String,Integer> e : set) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(String.format("%d\n", e.getValue()));
			}
		}
		
		bw.close();
	}

	public static void writeMapStringDouble(Map<String, Double> map, String file, boolean sort) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		
		if (sort) {
			Set<Entry<String,Double>> set = map.entrySet();
			ArrayList<Entry<String,Double>> list = new ArrayList<Entry<String,Double>>(set);
			Collections.sort(list, new Comparator<Entry<String,Double>>() {
				public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) {
					if (arg1.getValue() - arg0.getValue() >= 0) {
						return 1;
					} else {
						return -1;
					}
				}
			});
			for (Entry<String,Double> e : list) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(String.format("%.4f\n", e.getValue()));
			}
		} else {
			Set<Entry<String,Double>> set = map.entrySet();
			for (Entry<String,Double> e : set) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(String.format("%.4f\n", e.getValue()));
			}
		}
		
		bw.close();
	}
	
	public static void writeMapStringString(Map<String,String> map, String file, boolean sort) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		Set<Entry<String,String>> set = map.entrySet();
		
		if (sort) {
			ArrayList<Entry<String,String>> list = new ArrayList<Entry<String,String>>(set);
			Collections.sort(list, new Comparator<Entry<String,String>>() {
				public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			for (Entry<String,String> e : list) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(e.getValue());
				bw.write('\n');
			}
		} else {
			for (Entry<String,String> e : set) {
				bw.write(e.getKey());
				bw.write('\t');
				bw.write(e.getValue());
				bw.write('\n');
			}
			
		}
		
		bw.close();
	}

	public static void writeMapStringSetString(Map<String,Set<String>> map, String file, boolean sort) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		Set<Entry<String,Set<String>>> set = map.entrySet();
		
		if (sort) {
			ArrayList<Entry<String,Set<String>>> list = new ArrayList<Entry<String,Set<String>>>(set);
			Collections.sort(list, new Comparator<Entry<String,Set<String>>>() {
				public int compare(Entry<String, Set<String>> o1, Entry<String, Set<String>> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			for (Entry<String,Set<String>> e : list) {
				String key = e.getKey();
				bw.write(key);
				Set<String> strSet = e.getValue();
				for (String s : strSet) {
					bw.write('\t');
					bw.write(s);
				}
				bw.write('\n');
			}
		} else {
			for (Entry<String,Set<String>> e : set) {
				String key = e.getKey();
				bw.write(key);
				Set<String> strSet = e.getValue();
				for (String s : strSet) {
					bw.write('\t');
					bw.write(s);
				}
				bw.write('\n');
			}
		}
		
		bw.close();
	}

	public static void writeSetString(Set<String> set, String file, boolean sort) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		
		if (sort) {
			ArrayList<String> list = new ArrayList<String>(set);
			Collections.sort(list, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for (String s : list) {
				bw.write(s);
				bw.write('\n');
			}
		} else {
			for (String s : set) {
				bw.write(s);
				bw.write('\n');
			}
		}
		
		bw.close();
	}
}
