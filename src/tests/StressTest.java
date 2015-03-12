package tests;

import client.gui.*;
import client.*;

public class StressTest {
	public static void main(String[] args) {
		
		int y = 1;
		while (y < 1000) {
			new Client("10.1.13.11", 3520, "StressTest" + y);
			y++;
		}
		
		Client client;
		int x = 5;
		while (true) {
			client = new Client("10.1.13.11", 3520, "Andreas" + x);
			System.out.println("Client " + x + " connected.");
			try {
				Thread.sleep(10000);
			} catch (Exception e) {}
			
			client.close();
			
			System.out.println("Client " + x + " disconnected.");
			x++;
		}
	}
}
