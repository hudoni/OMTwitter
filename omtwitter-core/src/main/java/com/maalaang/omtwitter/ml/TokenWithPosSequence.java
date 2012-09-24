package com.maalaang.omtwitter.ml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * @author Sangwon Park
 */
public class TokenWithPosSequence extends Pipe {
	private static final long serialVersionUID = 13483853094L;
	private static final int SERIAL_VERSION = 9879283;
	
	public TokenWithPosSequence(boolean targetProcessing) throws IOException, ClassNotFoundException {
		super (null, new LabelAlphabet());
		this.setTargetProcessing(targetProcessing);
	}
	
	public Instance pipe(Instance instance) {
		LabelSequence target = null;
		
		TokenSequence ts = new TokenSequence ();
		
		String[][] data = ((String[][]) instance.getData());
		String[] tokens = data[0];
		String[] posTags = data[1];
		
		if (isTargetProcessing()) {
			String[] labels = data[2];
			
			target = new LabelSequence((LabelAlphabet)getTargetAlphabet(), tokens.length);
			
			for (int i = 0; i < tokens.length; i++) {
				Token token = new Token(tokens[i]);
				token.setFeatureValue("POS-" + posTags[i], 1.0);
				ts.add(token);
				target.add(labels[i]);
			}
			
		} else {
			for (int i = 0; i < tokens.length; i++) {
				Token token = new Token(tokens[i]);
				token.setFeatureValue("POS-" + posTags[i], 1.0);
				ts.add(token);
			}
		}
		
		instance.setData(ts);

		if (isTargetProcessing()) {
			instance.setTarget(target);
		}
		
		return instance;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(SERIAL_VERSION);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		in.readInt();
	}
}