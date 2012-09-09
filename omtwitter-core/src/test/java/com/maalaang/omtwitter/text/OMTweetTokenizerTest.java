/**
 * 
 */
package com.maalaang.omtwitter.text;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sangwon Park
 *
 */
public class OMTweetTokenizerTest extends TestCase {
	private Logger logger = null;
	private OMTweetTokenizer tweetTokenizer = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		logger = LoggerFactory.getLogger(getClass());
		tweetTokenizer = new OMTweetTokenizer();
	}

	/**
	 * Test method for {@link com.maalaang.omtwitter.text.OMTweetTokenizer#tokenize(java.lang.String)}.
	 */
	public void testTokenize() {
		String tweet = "@redhat I looooooove my #iphone4s 100$ http://exampleurl.com/pic.jpg";
		
		logger.info("tweet - " + tweet);
		
		OMTweetToken[] list = tweetTokenizer.tokenize(tweet);
		int idx = 0;
		
		OMTweetToken tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_USER, tok.getType());
		assertEquals("@redhat", tok.getText());
		assertEquals(0, tok.getBegin());
		assertEquals(7, tok.getEnd());
		assertEquals(OMTweetToken.NORMALIZED_TEXT_USER, tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("I", tok.getText());
		assertEquals(8, tok.getBegin());
		assertEquals(9, tok.getEnd());
		assertEquals("i", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("looooooove", tok.getText());
		assertEquals(10, tok.getBegin());
		assertEquals(20, tok.getEnd());
		assertEquals("loove", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("my", tok.getText());
		assertEquals(21, tok.getBegin());
		assertEquals(23, tok.getEnd());
		assertEquals("my", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_HASHTAG, tok.getType());
		assertEquals("#iphone4s", tok.getText());
		assertEquals(24, tok.getBegin());
		assertEquals(33, tok.getEnd());
		assertEquals("#iphone4s", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("100$", tok.getText());
		assertEquals(34, tok.getBegin());
		assertEquals(38, tok.getEnd());
		assertEquals("100$", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = list[idx++];
		assertEquals(OMTweetToken.TOKEN_TYPE_URL, tok.getType());
		assertEquals("http://exampleurl.com/pic.jpg", tok.getText());
		assertEquals(39, tok.getBegin());
		assertEquals(68, tok.getEnd());
		assertEquals(OMTweetToken.NORMALIZED_TEXT_URL, tok.getNormalizedText());
		logger.info(tok.toString());
	}

}
