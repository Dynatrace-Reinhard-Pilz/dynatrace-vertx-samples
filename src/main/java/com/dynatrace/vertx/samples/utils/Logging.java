package com.dynatrace.vertx.samples.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Functionality to streamline logging across all applications using this
 * library.<br />
 * <br />
 * All logging functionality is based on {@link Logger}s provided by J2SE.
 * 
 * @author Reinhard Pilz
 *
 */
public final class Logging extends Formatter {
	
	/**
	 * The default pattern for log files to produce.
	 */
	public static final String DEFAULT_LOGFILE_PATTERN =
			"sessionexport.%u.%g.log";
	
	/**
	 * A System Property for testing purposes, so the actual root {@link Logger}
	 * is not being changed.
	 */
	protected static final String PROPERTY_ROOT_LOGGER_NAME =
			Logging.class.getName() + ".rootlogger";
	
	/**
	 * The {@link Logger} which is hierarchically on top of all other
	 * {@link Logger}s
	 */
	private static final Logger ROOT_LOGGER =
			Logger.getLogger("");
	
	/**
	 * There is only one instance necessary for a {@link Formatter} of this
	 * type within the whole JVM.
	 */
	private static final Formatter INSTANCE = new Logging();
	
	/**
	 * There is only one instance necessary for the {@link ConsoleHandler}
	 * within the whole JVM.
	 */
	public static final Handler CONSOLE_HANDLER = createConsoleHandler();
	
	/**
	 * A reusable instance of the date object to prevent object churn
	 */
	protected final Date date = new Date();
	
	/**
	 * For formatting logging time stamps
	 */
	private final SimpleDateFormat sdf =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * A reusable instance for producing log entries
	 */
	private final StringBuilder sb = new StringBuilder();

    protected String lineSeparator = System.getProperty("line.separator");
    
    /**
     * Ensures that the functionality provided by this class is only
     * being called once.
     */
    private static volatile boolean initialized = false;

    /**
     * @return a predefined {@link ConsoleHandler}, moved out from the method
     * 		for initialization to increase code coverage.
     */
    private static final Handler createConsoleHandler() {
    	final Handler handler = new ConsoleHandler();
    	handler.setFormatter(INSTANCE);
    	handler.setLevel(Level.INFO);
    	return handler;
    }

    /**
     * Removes and closes all {@link Handler}s attached to the given
     * {@link Logger}
     * 
     * @param logger the logger to remove all the {@link Handler}s from
     */
    public static final void removeHandlers(final Logger logger) {
    	Objects.requireNonNull(logger);
		for (Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
			handler.close();
		}
    }

    /**
     * Factored functionality from the initialization routine, in order to
     * increase code coverage.
     * 
     * @param logger the {@link Logger} to consider as root logger
     * @param logFolder the folder to use for log files
     */
    protected static void configureRootLogger(final Logger logger) {
    	logger.setUseParentHandlers(false);
    	removeHandlers(logger);
		logger.addHandler(CONSOLE_HANDLER);
	}

    /**
     * Initializes the root logger and prints out an optional welcome message
     * immediately after initialization.
     * 
     * @param welcomeLogger the {@link Logger} to use for printing out the
     * 		welcome message
     * @param welcome the lines of the welcome message to print out
     */
	public static synchronized void init() {
		if (initialized) {
			return;
		}
		configureRootLogger(Logger.getLogger(
				System.getProperty(
						PROPERTY_ROOT_LOGGER_NAME,
						ROOT_LOGGER.getName()
				)
		));
		
		Logger.getLogger("com.dynatrace.optional.mom").setLevel(Level.INFO);
		
		initialized = true;
	}
    
	/**
	 * {@inheritDoc}
	 */
    @Override
    public synchronized String format(final LogRecord record) {
    	sb.setLength(0);
    	date.setTime(record.getMillis());
    	sb.append(sdf.format(date));
    	sb.append(" ");
    	sb.append(record.getLevel().getName());
    	sb.append(" [");
    	final String loggerName = record.getLoggerName();
    	final int lastdot = loggerName.lastIndexOf('.');
    	if (lastdot > 0 && (lastdot + 4 < loggerName.length())) {
    		sb.append(loggerName, lastdot + 1, loggerName.length());
    	} else {
    		sb.append(loggerName);
    	}
    	sb.append("] ");
    	String message = formatMessage(record);
    	sb.append(message);
    	
    	final Throwable throwable = record.getThrown();
    	if (throwable != null) {
    		sb.append(": ");
    		if (throwable.getMessage() != null) {
    			sb.append(throwable.getMessage());
    		} else {
    			sb.append(throwable.getClass().getName());
    		}
    		sb.append(lineSeparator);
    		sb.append(toString(throwable));
    	}
    	sb.append(lineSeparator);
    	return sb.toString();
    }
    
    private static final String toString(final Throwable t) {
    	if (t == null) {
    		return "";
    	}
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	t.printStackTrace(pw);
    	pw.close();
    	return sw.getBuffer().toString();
    }
    
}