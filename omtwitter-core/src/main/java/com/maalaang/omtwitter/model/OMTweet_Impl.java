/**
 * 
 */
package com.maalaang.omtwitter.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sangwon Park
 *
 */
public class OMTweet_Impl implements OMTweet {
	
	protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(OMTweet.DATE_FORMAT);
	
	protected String id;
	protected String author;
	protected Date date;
	protected String text;
	protected String query;
	protected int polarity;
	
	public OMTweet_Impl() {
		this(null, null, null, null, null, POLARITY_NOT_SPECIFIED);
	}
	
	public OMTweet_Impl(String author, String text) {
		this(null, author, null, text, null, POLARITY_NOT_SPECIFIED);
	}
	
	public OMTweet_Impl(String id, String author, String text) {
		this(id, author, null, text, null, POLARITY_NOT_SPECIFIED);
	}
	
	public OMTweet_Impl(String id, String author, Date date, String text) {
		this(id, author, date, text, null, POLARITY_NOT_SPECIFIED);
	}
	
	public OMTweet_Impl(String id, String author, String date, String text) {
		this(id, author, null, text, null, POLARITY_NOT_SPECIFIED);
		try {
			this.date = DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public OMTweet_Impl(String id, String author, Date date, String text, String query) {
		this(id, author, date, text, query, POLARITY_NOT_SPECIFIED);
	}
	
	public OMTweet_Impl(String id, String author, String date, String text, String query) {
		this(id, author, null, text, query, POLARITY_NOT_SPECIFIED);
		
		try {
			this.date = DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public OMTweet_Impl(String id, String author, Date date, String text, String query, int polarity) {
		this.id = id;
		this.author = author;
		this.date = date;
		this.text = text;
		this.query = query;
		this.polarity = polarity;
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author = author;

	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setText(java.lang.String)
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getText()
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setDate(java.lang.String)
	 */
	public void setDate(String date) {
		try {
			this.date = DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setDate(java.util.Date)
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getDateString()
	 */
	public String getDateString() {
		return DATE_FORMAT.format(date);
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setPolarity(int)
	 */
	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
	
	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setPolarity(String)
	 */
	public void setPolarity(String polarity) {
		if (POLARITY_STR_POSITIVE.equalsIgnoreCase(polarity)) {
			setPolarity(POLARITY_POSITIVE);
		} else if (POLARITY_STR_NEGATIVE.equalsIgnoreCase(polarity)) {
			setPolarity(POLARITY_NEGATIVE);
		} else if (POLARITY_STR_NEUTRAL.equalsIgnoreCase(polarity)) {
			setPolarity(POLARITY_NEUTRAL);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setPolarity(String)
	 */
	public String getPolarityString() {
		switch (polarity) {
		case POLARITY_POSITIVE:
			return POLARITY_STR_POSITIVE;
		case POLARITY_NEGATIVE:
			return POLARITY_STR_NEGATIVE;
		case POLARITY_NEUTRAL:
			return POLARITY_STR_NEUTRAL;
		case POLARITY_NOT_SPECIFIED:
			return POLARITY_STR_NOT_SPECIFIED;
		default:
			throw new IllegalStateException();
		}
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getPolarity()
	 */
	public int getPolarity() {
		return polarity;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#setQuery(java.lang.String)
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/* (non-Javadoc)
	 * @see com.maalaang.omtwitter.model.OMTweet#getQuery()
	 */
	public String getQuery() {
		return query;
	}

	@Override
	public String toString() {
		return "OMTweet [id=" + id + ", author=" + author + ", date="
				+ date + ", text=" + text + ", query=" + query + ", polarity="
				+ getPolarityString() + "]";
	}
	
}
