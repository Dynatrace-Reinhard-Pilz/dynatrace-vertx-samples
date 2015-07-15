package com.dynatrace.vertx.samples.handlers;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

import com.dynatrace.vertx.samples.Base;

public class RequestHandler implements Handler<HttpServerRequest> {

	private static final Logger LOGGER =
			Logger.getLogger(RequestHandler.class.getName());
	
	private final Vertx vertx;
	private final String internalMessageAddress;
	
	public RequestHandler(Vertx vertx, String internalMessageAddress) {
		Objects.requireNonNull(vertx);
		Objects.requireNonNull(internalMessageAddress);
		this.vertx = vertx;
		this.internalMessageAddress = internalMessageAddress;
	}
	
	@Override
	public void handle(HttpServerRequest request) {
		LOGGER.log(Level.INFO, request.uri() + " has been called");
		
		if (Base.UPLOAD_PATH.equals(request.uri())) {
			request.pause();
			
			final String filename = UUID.randomUUID().toString();
			
			AsyncFileHandler fileHandler =
					new AsyncFileHandler(filename, request);
			
			vertx.fileSystem().open(filename, fileHandler);
			return;
		}
		
		request.dataHandler(new RequestDataHandler());
		request.endHandler(new RequestEndHandler(
				request, vertx, internalMessageAddress
		));
		request.exceptionHandler(new RequestExceptionHandler());
	}		
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}