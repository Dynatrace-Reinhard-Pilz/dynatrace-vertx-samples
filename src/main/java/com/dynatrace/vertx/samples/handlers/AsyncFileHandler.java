package com.dynatrace.vertx.samples.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

public class AsyncFileHandler implements AsyncResultHandler<AsyncFile> {

	private static final Logger LOGGER =
			Logger.getLogger(AsyncFileHandler.class.getName());
	
	private final String filename;
	private final HttpServerRequest request;
	
	public AsyncFileHandler(String filename, HttpServerRequest request) {
		this.filename = filename;
		this.request = request;
	}
	
	@Override
	public void handle(AsyncResult<AsyncFile> ar) {
		LOGGER.log(Level.INFO, "Opening file " + filename);
		if (ar.failed()) {
			LOGGER.log(Level.WARNING, "Opening file " + filename + " failed", ar.cause());
			return;
		}
		
		AsyncFile file = ar.result();
		Pump pump = Pump.createPump(request, file);
		UploadDoneHandler endHandler = new UploadDoneHandler(filename, file, request);
		request.endHandler(endHandler);
		pump.start();
		request.resume();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
