package com.maalaang.omtwitter.uima.consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;


public class XmiWriteConsumer extends CasConsumer_ImplBase {
	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

	private File outputDir = null;
	private long entityCnt = 0;
	private Logger logger = null;

	public void initialize() throws ResourceInitializationException {
		logger = getLogger();
		
		outputDir = new File((String)getConfigParameterValue(PARAM_OUTPUT_DIRECTORY));
		if (!outputDir.exists()) {
			logger.log(Level.SEVERE, "cannot find the output directory - " + outputDir.getAbsolutePath());
			throw new ResourceInitializationException();
		}
	}

	public void processCas(CAS aCAS) throws ResourceProcessException {
		File outFile = new File(outputDir, String.format("%010d.xmi", ++entityCnt));
		
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
			
			XmiCasSerializer serializer = new XmiCasSerializer(aCAS.getTypeSystem());
			XMLSerializer xmlSerializer = new XMLSerializer(osw, true);
			
			serializer.serialize(aCAS, xmlSerializer.getContentHandler());
			osw.close();
			
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new ResourceProcessException(e);
		} catch (SAXException e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new ResourceProcessException(e);
		}
	}
}
