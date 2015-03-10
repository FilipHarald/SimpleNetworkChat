package server.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 *
 * @author Filip o jimmy
 *
 */
public class Log {
	
	public static final int INFO = 1;
	public static final int WARNING = 2;
	public static final int SEVERE = 3;

	private static Logger logger;
	private static FileHandler fileHandler;
	private static ConsoleHandler consoleHandler;
	
	private static LogListener serverController;
	
	public static void init(String name) {
		logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		
		
		
		try {
			fileHandler = new FileHandler("server.log");
			fileHandler.setFormatter(new LogFormatter());
			logger.addHandler(fileHandler);
			
			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new LogFormatter());
			
			logger.addHandler(consoleHandler);
			
			serverController.onInit();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void close() {
		fileHandler.close();
		consoleHandler.close();
		serverController.onClose();
	}
	
	public static void write(int level, String text) {
		String toGUI = "";
		if (logger != null && fileHandler != null) {
			switch (level) {
				case INFO:
					logger.info(text);
					toGUI = "[INFO] " + text;
					break;
				case WARNING:
					logger.warning(text);
					toGUI = "[WARNING] " + text;
					break;
				case SEVERE:
					logger.severe(text);
					toGUI = "[SEVERE] " + text;
					break;
				default:
					break;
			}
		} else {
			toGUI = "Logger not found or not initiated yet";
			System.out.println(toGUI);
		}
		serverController.onWrite(toGUI);
	}
	
	public static void addListener(LogListener controller){
		serverController = controller;
	}
		
}
