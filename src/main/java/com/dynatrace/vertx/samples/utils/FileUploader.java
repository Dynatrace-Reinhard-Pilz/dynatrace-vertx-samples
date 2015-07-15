package com.dynatrace.vertx.samples.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileUploader {
	
	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpURLConnection conn;
	private OutputStream outputStream;
	private PrintWriter writer;

	public FileUploader(URL url) throws IOException {
		boundary = "---------------------------" + System.currentTimeMillis();

		conn = (HttpURLConnection) url.openConnection();
		
		conn.setUseCaches(false);
		conn.setDoOutput(true); // indicates POST method
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		outputStream = conn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, Charset.defaultCharset().name()),
				true);
	}
	
	private void println(final String s) {
		writer.append(s).append(LINE_FEED);
	}
	
	private void println() {
		writer.append(LINE_FEED);
	}
	
	private void flush() {
		writer.flush();
	}
	
	private void close() {
		writer.close();
	}

	public void addFilePart(final String fileName, InputStream in)
			throws IOException {
		println("--" + boundary);
		println("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"");
		println("Content-Type: application/octet-stream");
		println("Content-Transfer-Encoding: binary");
		println();
		flush();

		Streams.copy(in, outputStream);
		outputStream.flush();

		println();
		flush();
	}

	public List<String> finish() throws IOException {
		List<String> response = new ArrayList<String>();

		println();
		flush();
		println("--" + boundary + "--");
		close();

		// checks server's status code first
		int status = conn.getResponseCode();
		if (status == HttpURLConnection.HTTP_CREATED) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			reader.close();
			conn.disconnect();
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}

		return response;
	}

}