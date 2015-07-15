package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;

public class HttpResponseDataHandler implements Handler<Buffer> {

	private static final Logger LOGGER =
			Logger.getLogger(HttpResponseDataHandler.class.getName());
	
	@Override
	public void handle(Buffer buffer) {
		LOGGER.log(Level.INFO, "Received data from HTTP Server: " + new String(buffer.getBytes()));
	}

}
