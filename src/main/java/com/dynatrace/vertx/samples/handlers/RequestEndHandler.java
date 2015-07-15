package com.dynatrace.vertx.samples.handlers;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import com.dynatrace.vertx.samples.Base;
import com.dynatrace.vertx.samples.utils.AsynchronousShutdown;

public class RequestEndHandler implements Handler<Void> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RequestEndHandler.class.getName());

	public static final String SHUTDOWN_PATH = "/shutdown";
	public static final String SELF_PATH = "/self";
	public static final String POST_PATH = "/post";
	public static final String GET_PATH = "/get";
	
	private static final int NUM_INTERNAL_MESSAGES = 1;
	
	private static final String LOCALHOST = "localhost";
	
	private final HttpServerRequest request;
	private final Vertx vertx;
	private final String internalMessageAddress;
	
	public RequestEndHandler(
			HttpServerRequest request,
			Vertx vertx,
			String internalMessageAddress
	) {
		Objects.requireNonNull(request);
		Objects.requireNonNull(vertx);
		Objects.requireNonNull(internalMessageAddress);
		this.request = request;
		this.vertx = vertx;
		this.internalMessageAddress = internalMessageAddress;
	}
	
	@Override
	public void handle(Void event) {
		HttpServerResponse response = request.response();
		response.setStatusCode(HttpURLConnection.HTTP_OK);
		String uri = request.uri();
		response.putHeader("Content-Length", String.valueOf(uri.getBytes(UTF_8).length));
		response.write(uri, UTF_8.name());
		response.end();
		
		if (SHUTDOWN_PATH.equals(uri)) {
			AsynchronousShutdown.shutdown();
			return;
		}
		if (GET_PATH.equals(uri)) {
			HttpClient httpClient = vertx.createHttpClient();
			httpClient.setHost(LOCALHOST);
			httpClient.setPort(Base.PORT);
			LOGGER.info(LOCALHOST);
			HttpClientRequest request = httpClient.get(SELF_PATH, new HttpResponseHandler());
			request.end();
			httpClient.close();
			for (int i = 1; i <= NUM_INTERNAL_MESSAGES; i++) {
				vertx.eventBus().send(internalMessageAddress, String.valueOf(i), new InternalMessageResponseHandler());
			}
		}
	}		
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}