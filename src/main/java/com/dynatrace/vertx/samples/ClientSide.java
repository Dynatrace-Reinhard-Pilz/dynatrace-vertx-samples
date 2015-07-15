package com.dynatrace.vertx.samples;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.vertx.samples.utils.AsynchronousShutdown;
import com.dynatrace.vertx.samples.utils.DataProducer;
import com.dynatrace.vertx.samples.utils.FileUploader;
import com.dynatrace.vertx.samples.utils.HttpClient;
import com.dynatrace.vertx.samples.utils.HttpClient.Method;
import com.dynatrace.vertx.samples.utils.LengthAwareInputStream;

public class ClientSide extends Thread implements Base {
	
	private static final Logger LOGGER = Logger.getLogger(ClientSide.class.getName());
	
	private static final HttpClient HTTP = new HttpClient();
	
	public ClientSide() {
		super(ClientSide.class.getSimpleName());
		setDaemon(true);
	}

	@Override
	public void run() {
		if (!HTTP.expectOk(GET_URL)) {
			AsynchronousShutdown.shutdown();
			return;
		}
		try (LengthAwareInputStream in = new LengthAwareInputStream(new DataProducer(), 1024)) {
			HTTP.send(POST_URL, Method.POST, 200, in, null);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to send HTTP request", e);
		}
		uploadFile();
		if (!HTTP.expectOk(SHUTDOWN_URL)) {
			AsynchronousShutdown.shutdown();
		}
	}
	
	private void uploadFile() {
		try (LengthAwareInputStream in = new LengthAwareInputStream(new DataProducer(), 16 * 1024)) {
			FileUploader fileUploader = new FileUploader(UPLOAD_URL);
			fileUploader.addFilePart(UUID.randomUUID().toString(), in);
			fileUploader.finish();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to upload file", e);
		}
	}
}
