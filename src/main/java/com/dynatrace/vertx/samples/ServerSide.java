package com.dynatrace.vertx.samples;

import java.util.logging.Logger;

import org.vertx.java.platform.Verticle;

import com.dynatrace.vertx.samples.handlers.InternalMessageHandler;
import com.dynatrace.vertx.samples.handlers.RequestHandler;
import com.dynatrace.vertx.samples.utils.Logging;

public class ServerSide extends Verticle implements Base {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(ServerSide.class.getName());
	
	private final ClientSide client = new ClientSide();
	
	public ServerSide() {
		Logging.init();
	}
	
	@Override
	public void start() {
		InternalMessageHandler internalMessageHandler =
				new InternalMessageHandler();
		vertx.eventBus().registerHandler(
				internalMessageHandler.ADDRESS,
				internalMessageHandler
		);
		RequestHandler requestHandler = new RequestHandler(
				vertx,
				internalMessageHandler.ADDRESS
		);
		vertx.createHttpServer().requestHandler(requestHandler).listen(PORT);
		client.start();
	}
	
}
