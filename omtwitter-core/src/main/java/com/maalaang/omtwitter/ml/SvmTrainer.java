package com.maalaang.omtwitter.ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException; 

import jnisvmlight.KernelParam;
import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.LearnParam;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;

public class SvmTrainer {
	
	public static int numOfExamples(String exampleFile) throws NumberFormatException, IOException {
		int cnt = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(exampleFile));
		while (br.readLine() != null) {
			cnt++;
		}
		br.close();
		
		return cnt;
	}
	
	public static void train(String exampleFile, String modelFile, int numOfDocs) throws IOException {
		SVMLightInterface trainer = new SVMLightInterface();
		SVMLightInterface.SORT_INPUT_VECTORS = false;
		
		LabeledFeatureVector[] trainData = new LabeledFeatureVector[numOfDocs];
		
		BufferedReader br = new BufferedReader(new FileReader(exampleFile));
		String line = null;
		int docCnt = 0;

		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("(:| |\t)+");
			
			int label = Integer.parseInt(tokens[0]);
			int nDims = (tokens.length - 1) / 2;
			
			int[] dims = new int[nDims];
			double[] values = new double[nDims];
			
			for (int i = 1, j = 0; j < nDims; i += 2, j++) {
				dims[j] = Integer.parseInt(tokens[i]);
				values[j] = Double.parseDouble(tokens[i+1]);
			}
			
			trainData[docCnt] = new LabeledFeatureVector(label, dims, values);
			docCnt++;
		}
		
		TrainingParameters tp = new TrainingParameters();
		
		LearnParam learnParam = tp.getLearningParameters();
		learnParam.verbosity = 1;
		learnParam.kernel_cache_size = 2048;
		learnParam.type = LearnParam.CLASSIFICATION;
		
		KernelParam kernelParam = tp.getKernelParameters();
		kernelParam.kernel_type = KernelParam.LINEAR;

		SVMLightModel model = trainer.trainModel(trainData, tp);
		model.writeModelToFile(modelFile);
		
		br.close();
	}
}
