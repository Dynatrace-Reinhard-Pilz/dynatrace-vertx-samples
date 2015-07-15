package com.dynatrace.vertx.samples.utils;

import static com.dynatrace.vertx.samples.utils.Streams.copy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class HttpClient {
	
	private static final Logger LOGGER = Logger.getLogger(HttpClient.class.getName());
	
	public static final int ANY_RESPONSE_CODE = -1;
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	
	public interface ContentType {
		public static final String URL_ENCODED = "application/x-www-form-urlencoded";
		public static final String FORM_DATA = "multipart/form-data";
	}
	
	public static enum Method {
		GET, POST
	}

	public static final class Result {
		public final String content;
		public final int responseCode;
		
		private Result(String content, int responseCode) {
			this.content = content;
			this.responseCode = responseCode;
		}
	}
	
	private void drainQuietly(HttpURLConnection connection) {
		Objects.requireNonNull(connection);
		try (InputStream in = connection.getInputStream()) {
			copy(in, Streams.DEVNULL);
		} catch (IOException e) {
			// ignore
		}
	}
	
	public Result send(URL url, Method method) throws IOException {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		return send(url, method, ANY_RESPONSE_CODE, (LengthAwareInputStream) null, null);
	}
	
	public Result send(URL url, Method method, int expectedResponseCode) throws IOException {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		return send(url, method, expectedResponseCode, (LengthAwareInputStream) null, null);
	}
	
	public Result send(URL url, Method method, int expectedResponseCode, byte[] postData, Charset charSet) throws IOException {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		Objects.requireNonNull(charSet);
		if (postData == null) {
			return send(url, method, expectedResponseCode, (LengthAwareInputStream) null, charSet);
		}
		return send(url, method, expectedResponseCode, new LengthAwareInputStream(postData), charSet);
	}
	
	public Result send(URL url, Method method, int expectedResponseCode, LengthAwareInputStream postData, Charset charSet) throws IOException {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
//		LOGGER.log(Level.INFO, method + " " + url.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method.name());
		
		if ((method == Method.POST) && (postData != null)) {
			connection.setDoOutput(true);
			String contentType = ContentType.FORM_DATA;
			if (charSet != null) {
				contentType = contentType + "; charset=" + charSet.name();
			}
			connection.setRequestProperty(CONTENT_TYPE, contentType); 
			connection.setRequestProperty(CONTENT_LENGTH, String.valueOf(postData.length()));
			connection.setUseCaches(false);
			try (OutputStream out = connection.getOutputStream()) {
				copy(postData, out);
			}
		}
		
		int responseCode = connection.getResponseCode();
		if ((expectedResponseCode != ANY_RESPONSE_CODE) && (responseCode != expectedResponseCode)) {
			drainQuietly(connection);
			connection.disconnect();
			throw new IOException("Unexpected HTTP response code " +
					responseCode + " (expected: " + expectedResponseCode + ")");
		}
		try (
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		) {
			copy(in, out);
			out.close();
			String response = new String(out.toByteArray());
			LOGGER.log(Level.INFO, "  HTTP " + responseCode + " [" + url.toString() + "]");
			return new Result(response, responseCode);
		}
	}

	public String get(URL url) throws IOException {
		return send(url, Method.GET, ANY_RESPONSE_CODE).content;
	}
	
	public boolean expectOk(URL url) {
		return expectOk(url, Method.GET);
	}
	
	public boolean expectOk(URL url, Method method) {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		return expect(url, HttpURLConnection.HTTP_OK);
	}
	
	public boolean expect(URL url, int responseCode) {
		return expect(url, Method.GET, responseCode);
	}
	
	public boolean expect(URL url, Method method, int responseCode) {
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		try {
			send(url, method, responseCode);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static URL createURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
}
