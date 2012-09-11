/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public class EmoticonProcessor {
	private final static String SMILEY_EMOTICON_REGEX = "(\\s|^)(\\>\\:\\]|\\:\\-\\)|\\:\\)|\\:o\\)|\\:\\]|\\:3|\\:c\\)|\\:\\>|\\=\\]|8\\)|\\=\\)|\\:\\}|\\:\\^\\)|\\:っ\\)|\\>\\:D|\\:\\-D|\\:D|8\\-D|8D|x\\-D|xD|X\\-D|XD|\\=\\-D|\\=D|\\=\\-3|\\=3|8\\-\\)|\\:っD|\\:\\-\\)\\)|\\>;\\]|;\\-\\)|;\\)|\\*\\-\\)|\\*\\)|;\\-\\]|;\\]|;D|;\\^\\)|O\\:\\-\\)|0\\:\\-3|0\\:3|O\\:\\-\\)|O\\:\\)|0;\\^\\)|o\\/\\\\o|\\^5|\\>_\\>\\^|\\^\\<_\\<|\\*\\\\0\\/\\*|\\:\\'\\-\\)|\\:\\'\\)|\\}\\:\\-\\)|\\}\\:\\)|#\\-\\)|\\%\\-\\)|\\%\\))(\\s|$)";
	private final static String FROWNY_EMOTICON_REGEX = "(\\s|^)(\\>\\:\\[|\\:\\-\\(|\\:\\(|\\:\\-c|\\:c|\\:\\-\\<|\\:っC|\\:\\<|\\:\\-\\[|\\:\\[|\\:\\{|\\>\\.\\>|\\<\\.\\<|\\>\\.\\<|\\:\\-\\|\\||\\:\\@|D\\:\\<|D\\:|D8|D;|D\\=|DX|v\\.v|D\\-\\'\\:|\\>\\:\\\\|\\>\\:\\/|\\:\\-\\/|\\:\\-\\.|\\:\\/|\\:\\\\|\\=\\/|\\=\\\\|\\:S|\\:\\'\\-\\(|\\:\\'\\(|QQ|\\<\\:\\-\\||\\:\\||\\:\\-\\||\\:\\-###\\.\\.|\\:###\\.\\.|\\>\\:X|\\:\\-X|\\:X|\\:\\-#|\\:#|\\:\\$)(\\s|$)";
	
	private Pattern smileyPattern = null;
	private Pattern frownyPattern = null;
	
	public EmoticonProcessor() {
		this.smileyPattern = Pattern.compile(SMILEY_EMOTICON_REGEX);
		this.frownyPattern = Pattern.compile(FROWNY_EMOTICON_REGEX);
	}

	public void updatePolarityByEmoticon(OMTweet tweet) {
		tweet.setPolarity(polarityByEmoticon(tweet.getText()));
	}
	
	public int polarityByEmoticon(String text) {
		Matcher smileyMatcher = smileyPattern.matcher(text);
		if (smileyMatcher.find()) {
			return OMTweet.POLARITY_POSITIVE;
		}
		
		Matcher frownyMatcher = frownyPattern.matcher(text);
		if (frownyMatcher.find()) {
			return OMTweet.POLARITY_NEGATIVE;
		}
		
		return OMTweet.POLARITY_NOT_SPECIFIED;
	}
	
	public String removeEmoticon(String text) {
		Matcher smileyMatcher = smileyPattern.matcher(text);
		text = smileyMatcher.replaceAll(" ");
		
		Matcher frownyMatcher = frownyPattern.matcher(text);
		text = frownyMatcher.replaceAll(" ");
		
		return text.trim();
	}
	
	public void removeEmoticon(OMTweet tweet) {
		tweet.setText(removeEmoticon(tweet.getText()));
	}
}
