package tests;

import client.Client;
import server.Server;

public class TestKickClient {

	public static void main(String[] args) {
		
		Server s = new Server(3520);
		s.start();
		
		Client a = new Client("localhost", 3520, "A");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Client b = new Client("localhost", 3520, "B");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		a.sendCommandMessage("kick", "B");
		
	}
	
}
