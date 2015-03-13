package tests;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import client.*;
import exceptions.NameInUseException;

public class StressTest {
	public static void main(String[] args) {
		
		int y = 1;
		while (y < 30) {
			try {
				new Client("localhost", 3520, "StressTest" + y).start();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameInUseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			y++;
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		Client client;
		int x = 1;
		while (x < 10) {
			client = new Client("10.1.13.11", 3520, "Andreas" + x);
			System.out.println("Client " + x + " connected.");
			try {
				Thread.sleep(10000);
			} catch (Exception e) {}
			
			client.close();
			
			System.out.println("Client " + x + " disconnected.");
			x++;
		}*/
	}
}
