package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;

public class HttpResponseHandler implements Handler<HttpClientResponse> {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpResponseHandler.class.getName());

	@Override
	public void handle(HttpClientResponse response) {
		LOGGER.log(Level.INFO, "Response received - Status Code: " + response.statusCode());
		response.dataHandler(new HttpResponseDataHandler());
	}
	
}
