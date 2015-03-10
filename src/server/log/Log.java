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
	
	private static LogListener guiListener;
	
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
			guiListener.onInit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void close() {
		fileHandler.close();
		consoleHandler.close();
		guiListener.onClose();
	}
	
	public static void write(int level, String text) {
		if (logger != null && fileHandler != null) {
			switch (level) {
				case INFO:
					logger.info(text);
					break;
				case WARNING:
					logger.warning(text);
					break;
				case SEVERE:
					logger.severe(text);
					break;
				default:
					break;
			}
			guiListener.onWrite(level + text);
		} else {
			System.out.println("Logger not found or not initiated yet");
			guiListener.onWrite(level + text);
		}
	}
	
	public static void addListener(LogListener l){
		guiListener = l;
	}
		
}