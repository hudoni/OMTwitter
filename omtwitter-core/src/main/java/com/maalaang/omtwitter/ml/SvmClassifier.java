package com.maalaang.omtwitter.ml;

import java.io.File;
import java.net.MalformedURLException;
import java.text.ParseException;


import jnisvmlight.FeatureVector;
import jnisvmlight.SVMLightModel;

public class SvmClassifier {
	private SVMLightModel model = null;
	
	public SvmClassifier(File modelFile) throws MalformedURLException, ParseException {
		model = SVMLightModel.readSVMLightModelFromURL(modelFile.toURI().toURL());
	}
	
	public double classify(SvmFeatureVector fv) {
		int size = fv.size();
		
		int[] features = new int[size];
		double[] values = new double[size];
		
		fv.toArrays(features, values);

		return model.classify(new FeatureVector(features, values));
	}
}
