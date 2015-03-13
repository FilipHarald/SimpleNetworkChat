package server.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * 
 *
 * @author Filip o jimmy
 *
 */
public class Log {
	
	private static final Logger logger = Logger.getLogger(Log.class.getName());
	
	public static final Level INFO = Level.INFO;
	public static final Level WARNING = Level.WARNING;
	public static final Level SEVERE = Level.SEVERE;

	private static FileHandler fileHandler;
	private static ConsoleHandler consoleHandler;
	private static LogFormatter logFormatter;
	
	private static LogListener serverController;
	
	public static void init() {
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);

		logFormatter = new LogFormatter();
		
		try {
			fileHandler = new FileHandler("server.log");
			fileHandler.setFormatter(logFormatter);
			logger.addHandler(fileHandler);
			
			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(logFormatter);
			
			logger.addHandler(consoleHandler);
			
			if (serverController != null) {
				serverController.onInit();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void close() {
		if (fileHandler != null) {
			logger.removeHandler(fileHandler);
			fileHandler.close();
		}
		if (consoleHandler != null) {
			logger.removeHandler(consoleHandler);
			consoleHandler.close();
		}
		if (serverController != null) {
			serverController.onClose();
		}
	}
	
	public static void write(Level level, String text) {
		LogRecord record = new LogRecord(level, text);
		if (logger != null && fileHandler != null) {
			logger.log(record);
			if (serverController != null) {
				serverController.onWrite(logFormatter.format(record));
			}
		} else {
			if (serverController != null) {
				serverController.onWrite("Logger is not initialized yet.\n");
			}
		}
		
	}
	
	public static void addListener(LogListener controller){
		serverController = controller;
	}
		
}
