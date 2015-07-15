package com.dynatrace.vertx.samples.handlers;

import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerRequest;

public class UploadDoneHandler extends VoidHandler {
	
	private static final Logger LOGGER =
			Logger.getLogger(UploadDoneHandler.class.getName());
	
	private final String filename;
	private final HttpServerRequest request;
	private final AsyncFile file;
	
	public UploadDoneHandler(String filename, AsyncFile file, HttpServerRequest request) {
		this.filename = filename;
		this.request = request;
		this.file = file;
	}

	@Override
	public void handle() {
		LOGGER.log(Level.INFO, "Upload Done: " + filename);
		request.response().setStatusCode(HttpURLConnection.HTTP_CREATED);
		FileClosedHandler fileClosedHandler = new FileClosedHandler(filename, request);
		try {
			file.exceptionHandler(new Handler<Throwable>() {

				@Override
				public void handle(Throwable event) {
					event.printStackTrace();
				}
				
			});
			file.close(fileClosedHandler);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
