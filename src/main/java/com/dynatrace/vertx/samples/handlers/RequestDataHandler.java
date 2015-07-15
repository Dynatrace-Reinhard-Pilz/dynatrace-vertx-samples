package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;

public class RequestDataHandler implements Handler<Buffer> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RequestDataHandler.class.getName());
	
	@Override
	public void handle(Buffer buffer) {
		
		LOGGER.log(Level.INFO, buffer.length() + " bytes received");
	}
	
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
