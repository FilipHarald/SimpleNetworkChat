package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static Logger logger;
	private static FileHandler fileHandler;
	
	public static void init(String name) {
		logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL);
		
		try {
			fileHandler = new FileHandler("log.log");
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void write(String level, String text) {
		switch (level) {
			case "info":
				logger.info(text);
				break;
			case "warning":
				logger.warning(text);
				break;
			case "severe":
				logger.severe(text);
				break;
			case "config":
				logger.config(text);
				break;
			default:
				break;
		}
	}
		
}
