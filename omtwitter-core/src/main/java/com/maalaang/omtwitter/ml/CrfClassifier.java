/**
 * 
 */
package com.maalaang.omtwitter.ml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.pipe.tsf.OffsetConjunctions;
import cc.mallet.pipe.tsf.SequencePrintingPipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * @author Sangwon Park
 *
 */
public class CrfClassifier {
	private	CRF crf = null;
	private Pipe pipe = null;
	
	public void loadModel(String modelFile) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(modelFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		crf = (CRF)ois.readObject();
		pipe = crf.getInputPipe();
		pipe.setTargetProcessing(false);
		ois.close();
		fis.close();
	}
	
	public Instance classify(String[][] data) {
		Instance instance = new Instance(data, null, null, null);
		return crf.label(instance);
	}
	
	public Instance classify(Instance instance) {
		return crf.label(instance);
	}

	public void train(String[] trainingFiles, String fieldDelim, int[] fields, String modelFile, String featureDumpFile, boolean writeFeatureFile) throws IOException, ClassNotFoundException {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();

		int[][] conjunctions = new int[4][];
		conjunctions[0] = new int[] { -2 };
		conjunctions[1] = new int[] { -1 };
		conjunctions[2] = new int[] { 1 };
		conjunctions[3] = new int[] { 2 };

		pipes.add(new TokenWithPosSequence(true));
		pipes.add(new TweetFeatures());
		pipes.add(new OffsetConjunctions(conjunctions));
		pipes.add(new TokenSequence2FeatureVectorSequence());
		
		/* for debugging feature generation */
		if (writeFeatureFile) {
			PrintWriter out = null;
			out = new PrintWriter(featureDumpFile);
			pipes.add(new SequencePrintingPipe(out));	
		}
		
		Pipe pipe = new SerialPipes(pipes);
		InstanceList trainingInstances = new InstanceList(pipe);

		for (String file : trainingFiles) {
			trainingInstances.addThruPipe(new TweetEntityCorpusLineIterator(file, fieldDelim, fields));
		}
		
		CRF crf = new CRF(pipe, null);
		crf.addStatesForLabelsConnectedAsIn(trainingInstances);
		
//		crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
//		crf.addStatesForHalfLabelsConnectedAsIn(trainingInstances);
		
		crf.addStartState();

		CRFTrainerByLabelLikelihood trainer = new CRFTrainerByLabelLikelihood(crf);
		trainer.setGaussianPriorVariance(10.0);
		
//		CRFTrainerByStochasticGradient trainer = new CRFTrainerByStochasticGradient(crf, 1.0);
//		CRFTrainerByL1LabelLikelihood trainer = new CRFTrainerByL1LabelLikelihood(crf, 0.75);

//		trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
//		trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "testing"));
		
		trainer.train(trainingInstances);
		
		if (!writeFeatureFile) {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));
			oos.writeObject(crf);
			oos.close();
		}
	}
}