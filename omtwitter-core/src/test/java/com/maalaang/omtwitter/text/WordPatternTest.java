package com.maalaang.omtwitter.text;

import junit.framework.TestCase;

public class WordPatternTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReplaceRomanToArabic() {
		assertEquals("x3", WordPattern.replaceRomanToArabic("xiii"));
		assertEquals("s2", WordPattern.replaceRomanToArabic("sii"));
		assertEquals("super 4", WordPattern.replaceRomanToArabic("super iv"));
		assertEquals("galaxy s3", WordPattern.replaceRomanToArabic("galaxy siii"));
		assertEquals("galaxy s2", WordPattern.replaceRomanToArabic("galaxy s2"));
		assertEquals("i am 3433", WordPattern.replaceRomanToArabic("i am 3433"));
	}

}
