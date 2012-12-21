/**
 * 
 */
package com.maalaang.omtwitter.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.maalaang.omtwitter.io.LogSystemStream;

/**
 * @author Sangwon Park
 *
 */
public class ExtractSampleCorpus {

	private Properties prop = null;
	private Logger logger = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			
			LogSystemStream.redirectErrToLog(Level.ERROR);
			
			ExtractSampleCorpus con = new ExtractSampleCorpus(prop);
			con.run(Integer.parseInt(prop.getProperty("sample.window")), Integer.parseInt(prop.getProperty("sample.number")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExtractSampleCorpus(Properties prop) {
		this.prop = prop;
		this.logger = Logger.getLogger(getClass());
	}
	
	public void run(int sampleWindow, int sampleNumber) throws Exception {
		String writeFile = prop.getProperty("corpus.file.out") + "." + sampleNumber + "." + sampleWindow;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("corpus.file.in")), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile), "UTF-8"));
		
		String line = null;
		int i = 0;
		long cnt = 0;
		
		logger.info("extract " + sampleNumber + " tweets in every " + sampleWindow + " tweets - " +prop.getProperty("corpus.file.in"));
				
		while ((line = br.readLine()) != null) {
			if (i == sampleWindow) {
				i = 0;
			}
			if (i++ < sampleNumber) {
				bw.write(line);
				bw.write('\n');
				cnt++;
			}
		}
		logger.info(cnt + " tweets were extracted - " + writeFile);
		
		bw.close();
		br.close();
	}

}
