/**
 * 
 */
package com.maalaang.omtwitter.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Sangwon Park
 *
 */
public class LogSystemStream {
	public static void redirectOutToLog(Level level) {
		System.setOut(new PrintStream(new LogRedirectedStream(Logger.getLogger("system.out"), level, System.out)));
	}

	public static void redirectErrToLog(Level level) {
		System.setErr(new PrintStream(new LogRedirectedStream(Logger.getLogger("system.err"), level, System.err)));
	}
}

final class LogRedirectedStream extends OutputStream {
	private final Logger logger;
	private final Level logLevel;

	public LogRedirectedStream(Logger logger, Level logLevel, OutputStream outputStream) {
		super();
		this.logger = logger;
		this.logLevel = logLevel;
	}

	public void write(byte[] b) throws IOException {
		if (logger.isEnabledFor(logLevel)) {
			logger.log(logLevel, new String(b).replaceAll("\\s+", " "));
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if (logger.isEnabledFor(logLevel)) {
			logger.log(logLevel, new String(b, off, len).replaceAll("\\s+", " "));
		}
	}

	public void write(int b) throws IOException {
		if (logger.isEnabledFor(logLevel)) {
			logger.log(logLevel, String.valueOf((char) b));
		}
	}
}