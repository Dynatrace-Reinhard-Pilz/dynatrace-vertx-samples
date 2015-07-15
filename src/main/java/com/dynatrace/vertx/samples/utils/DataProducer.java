package com.dynatrace.vertx.samples.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class DataProducer extends InputStream {
	
	private static final byte VALUE = 23;

	@Override
	public int read() throws IOException {
		return VALUE;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		Objects.requireNonNull(b);
		for (int i = off; i < off + len; i++) {
			b[i] = VALUE;
		}
		return len;
	}

}
