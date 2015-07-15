package com.dynatrace.vertx.samples;

import java.net.URL;

import com.dynatrace.vertx.samples.handlers.RequestEndHandler;
import com.dynatrace.vertx.samples.utils.HttpClient;

public interface Base {
	
	public static final String UPLOAD_PATH = "/upload";

	public static final int PORT = 51234;
	
	public static final URL GET_URL = HttpClient.createURL(
			"http://localhost:" + PORT + RequestEndHandler.GET_PATH
	);
	
	public static final URL POST_URL = HttpClient.createURL(
			"http://localhost:" + PORT + RequestEndHandler.POST_PATH
	);
	public static final URL SHUTDOWN_URL = HttpClient.createURL(
			"http://localhost:" + PORT + RequestEndHandler.SHUTDOWN_PATH
	);
	
	public static final URL UPLOAD_URL = HttpClient.createURL(
			"http://localhost:" + PORT + UPLOAD_PATH
	);
	
	public static final URL SELF_URL = HttpClient.createURL(
			"http://localhost:" + PORT + RequestEndHandler.SELF_PATH
	);
	
}
