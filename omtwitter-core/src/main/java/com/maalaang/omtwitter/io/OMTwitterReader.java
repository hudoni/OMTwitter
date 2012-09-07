/**
 * 
 */
package com.maalaang.omtwitter.io;

import com.maalaang.omtwitter.model.OMTweet;

/**
 * @author Sangwon Park
 */
public interface OMTwitterReader {
	/**
	 * Tell whether it has more element to read.
	 * @return True
	 */
	public boolean hasNext();
	
	/**
	 * Returns the next <code>OMTweet</code> object.
	 * @return next <code>OMTweet</code> object.
	 */
	public OMTweet next();
	
	/**
	 * Close the reader.
	 */
	public void close();
}