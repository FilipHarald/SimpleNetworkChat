package tests;

import java.io.IOException;

import server.Server;

public class TestServer {

	public static void main(String[] args) {
		Server server;
		try {
			server = new Server(3520);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
