/**
 * 
 */
package com.maalaang.omtwitter.io;

import junit.framework.TestCase;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 *
 */
public class OMTwitterCorpusFileReaderTest extends TestCase {

	private OMTwitterCorpusFileReader reader = null;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		int[] fields = new int[] { OMTwitterCorpusFile.FIELD_QUERY, OMTwitterCorpusFile.FIELD_AUTHOR, OMTwitterCorpusFile.FIELD_TEXT };
		reader = new OMTwitterCorpusFileReader(ClassLoader.getSystemResource("corpus/twitter_corpus_example.txt").getFile(), "\t", fields);
	}

	@Override
	protected void tearDown() throws Exception {
		reader.close();
		super.tearDown();
	}

	/**
	 * Test method for {@link com.maalaang.omtwitter.io.OMTwitterCorpusFileReader#next()}.
	 */
	public void testNext() {
		OMTweet tweet = null;
		
		tweet = reader.next();
		assertEquals(tweet.getId(), null);
		assertEquals(tweet.getAuthor(), "author");
		assertEquals(tweet.getDateString(), null);
		assertEquals(tweet.getPolarity(), OMTweet.POLARITY_NOT_SPECIFIED);
		assertEquals(tweet.getQuery(), "query");
		assertEquals(tweet.getText(), "this is an example tweet");
		
		tweet = reader.next();
		assertNotNull(tweet);
		
		assertFalse(reader.hasNext());
	}
}
