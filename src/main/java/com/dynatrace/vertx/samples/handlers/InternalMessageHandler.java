package com.dynatrace.vertx.samples.handlers;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public class InternalMessageHandler implements Handler<Message<String>> {
	
	private static final Logger LOGGER =
			Logger.getLogger(InternalMessageHandler.class.getName());
	
	public final String ADDRESS = UUID.randomUUID().toString();

	@Override
	public void handle(final Message<String> message) {
		LOGGER.log(Level.INFO, message.body());
		message.reply(Integer.parseInt(message.body()) + 1000);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}