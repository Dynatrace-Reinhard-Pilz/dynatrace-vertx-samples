package com.dynatrace.vertx.samples.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LengthAwareInputStream extends InputStream {

	private final int length;
	private final InputStream source;
	private int bytesRead = 0;
	
	public LengthAwareInputStream(InputStream source, int length) {
		this.source = source;
		this.length = length;
	}
	
	public LengthAwareInputStream(byte[] buffer) {
		this(new ByteArrayInputStream(buffer), buffer.length);
	}
	
	public int length() {
		return length;
	}

	@Override
	public int read() throws IOException {
		if (bytesRead == length) {
			return -1;
		}
		bytesRead++;
		return source.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (bytesRead == length) {
			return -1;
		}
		int bytesToRead = Math.min(len, length - bytesRead);
		bytesRead = bytesRead + bytesToRead;
		return source.read(b, off, bytesToRead);
	}
	
}
