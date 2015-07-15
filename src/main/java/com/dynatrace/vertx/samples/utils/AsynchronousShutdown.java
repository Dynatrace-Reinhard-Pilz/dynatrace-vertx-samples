package com.dynatrace.vertx.samples.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AsynchronousShutdown extends Thread {
	
	private static final Logger LOGGER = Logger.getLogger(AsynchronousShutdown.class.getName());
	
	private AsynchronousShutdown() {
		super(AsynchronousShutdown.class.getSimpleName());
		setDaemon(true);
	}

	@Override
	public void run() {
		LOGGER.log(Level.INFO, "shutting down");
		System.exit(0);
	}
	
	public static final void shutdown() {
		new AsynchronousShutdown().start();
	}
}
