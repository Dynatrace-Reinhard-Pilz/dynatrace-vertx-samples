package com.dynatrace.vertx.samples.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Streams {
	
	public static final OutputStream DEVNULL = new NullOutputStream();
	
	private static final int BUFFER_SIZE = 4 * 1024;
	
	private Streams() {
		// prevent instantiation
	}

	public static final long copy(InputStream in, OutputStream out)
			throws IOException
	{
		long len = 0;
		final byte buffer[] = new byte[BUFFER_SIZE];
		int read = in.read(buffer, 0, buffer.length);
		while (read > 0) {
			len = len + read;
			out.write(buffer, 0, read);
			read = in.read(buffer);
		}
		return len;
	}
	
	public static byte[] getBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}
	
	private static class NullOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			// do nothing
		}
		
	}
	
}
