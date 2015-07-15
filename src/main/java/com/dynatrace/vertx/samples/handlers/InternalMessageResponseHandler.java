package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public class InternalMessageResponseHandler implements Handler<Message<Integer>> {
	
	private static final Logger LOGGER =
			Logger.getLogger(InternalMessageResponseHandler.class.getName());
	
	@Override
	public void handle(final Message<Integer> response) {
		LOGGER.log(Level.FINEST, "response: " + response.body());
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}