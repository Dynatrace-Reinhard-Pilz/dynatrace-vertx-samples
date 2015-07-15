package com.dynatrace.vertx.samples.handlers;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpServerRequest;

public class FileClosedHandler implements AsyncResultHandler<Void> {

	private static final Logger LOGGER =
			Logger.getLogger(FileClosedHandler.class.getName());
	
	private final String filename;
	private final HttpServerRequest request;
	
	public FileClosedHandler(String filename, HttpServerRequest request) {
		this.request = request;
		this.filename = filename;
	}
	
	@Override
	public void handle(AsyncResult<Void> ar) {
		LOGGER.log(Level.INFO, "FileClosedHandler called ");
		request.response().end();
		if (ar.succeeded()) {
			LOGGER.log(Level.INFO, "Uploaded of file " + filename + " succeeded");
		} else {
			LOGGER.log(Level.WARNING, "Upload did not succeed", ar.cause());
		}
		if (!new File(filename).delete()) {
			LOGGER.log(Level.WARNING, "Unable to delete uplaoded file " + filename);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
