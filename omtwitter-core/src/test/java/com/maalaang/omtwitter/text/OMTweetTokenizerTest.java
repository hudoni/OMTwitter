/**
 * 
 */
package com.maalaang.omtwitter.text;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * @author Sangwon Park
 *
 */
public class OMTweetTokenizerTest extends TestCase {
	Logger logger = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		logger = LoggerFactory.getLogger(getClass());
	}

	/**
	 * Test method for {@link com.maalaang.omtwitter.text.OMTweetTokenizer#tokenize(java.lang.String)}.
	 */
	public void testTokenize() {
		String tweet = "@redhat I looooooove my #iphone4s http://exampleurl.com/pic.jpg";
		
		logger.info("tweet - " + tweet);
		
		List<OMTweetToken> list = OMTweetTokenizer.tokenize(tweet);
		Iterator<OMTweetToken> it = list.iterator();
		
		
		OMTweetToken tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_USER, tok.getType());
		assertEquals("@redhat", tok.getText());
		assertEquals(0, tok.getBegin());
		assertEquals(7, tok.getEnd());
		assertEquals(OMTweetToken.NORMALIZED_TEXT_USER, tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("I", tok.getText());
		assertEquals(8, tok.getBegin());
		assertEquals(9, tok.getEnd());
		assertEquals("i", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("looooooove", tok.getText());
		assertEquals(10, tok.getBegin());
		assertEquals(20, tok.getEnd());
		assertEquals("loove", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_NORMAL, tok.getType());
		assertEquals("my", tok.getText());
		assertEquals(21, tok.getBegin());
		assertEquals(23, tok.getEnd());
		assertEquals("my", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_HASHTAG, tok.getType());
		assertEquals("#iphone4s", tok.getText());
		assertEquals(24, tok.getBegin());
		assertEquals(33, tok.getEnd());
		assertEquals("#iphone4s", tok.getNormalizedText());
		logger.info(tok.toString());
		
		tok = it.next();
		assertEquals(OMTweetToken.TOKEN_TYPE_URL, tok.getType());
		assertEquals("http://exampleurl.com/pic.jpg", tok.getText());
		assertEquals(34, tok.getBegin());
		assertEquals(63, tok.getEnd());
		assertEquals(OMTweetToken.NORMALIZED_TEXT_URL, tok.getNormalizedText());
		logger.info(tok.toString());
	}

}
