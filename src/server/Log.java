package server;

import java.util.Observable;
import java.util.Observer;

/**
 * Just nu har jag försökt mig på att göra loggning med en Observer/Observable
 * implementation. Men det kanske är bättre att göra Log synchronized och låta
 * andra skriva på den istället?
 * 
 *
 * @author Filip
 *
 */
public class Log implements Observer {

	@Override
	public void update(Observable o, Object arg) {
		// Här ska loggning ske men nu använder jag SYSO
		System.out.println((String) arg);
	}

}
