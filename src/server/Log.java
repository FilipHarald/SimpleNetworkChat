package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Just nu har jag försökt mig på att göra loggning med en Observer/Observable
 * implementation. Men det kanske är bättre att göra Log synchronized och låta
 * andra skriva på den istället?
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
	
	public static void init(String name) {
		logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL);
		
		try {
			fileHandler = new FileHandler("log.log");
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		} else {
			System.out.println("Loggern finns inte eller är inte initierad");
		}
	}
		
}
