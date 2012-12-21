package com.maalaang.omtwitter.ml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.maalaang.omtwitter.text.OMTweetToken;
import com.maalaang.omtwitter.text.OMTweetTokenizer;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * @author Sangwon Park
 */
public class TweetFeatures extends Pipe {
	private static final long serialVersionUID = 2396028381230L;
	private static final int SERIAL_VERSION = 58284;
	
	@Override
	public Instance pipe(Instance carrier) {
		TokenSequence ts = (TokenSequence) carrier.getData();
		
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			OMTweetToken omtToken = OMTweetTokenizer.omtToken(t.getText());
			String text = omtToken.getNormalizedText();
			
			switch(omtToken.getType()) {
			case OMTweetToken.TOKEN_TYPE_HASHTAG:
				t.setFeatureValue("HASHTAG", 1.0);
				t.setFeatureValue(text, 1.0);
				break;
			case OMTweetToken.TOKEN_TYPE_USER:
				t.setFeatureValue("USER", 1.0);
				break;
			case OMTweetToken.TOKEN_TYPE_URL:
				t.setFeatureValue("LINK", 1.0);
				break;
			case OMTweetToken.TOKEN_TYPE_NORMAL:
				t.setFeatureValue(text, 1.0);
				break;
			}
		}
		
		return carrier;
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