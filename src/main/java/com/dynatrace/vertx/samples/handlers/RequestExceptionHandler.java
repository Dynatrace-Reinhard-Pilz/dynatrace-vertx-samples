package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;

public class RequestExceptionHandler implements Handler<Throwable> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RequestExceptionHandler.class.getName());

	@Override
	public void handle(Throwable throwable) {
		LOGGER.log(Level.WARNING,
				"An error occurred when handling a request",
				throwable
		);
	}
}
