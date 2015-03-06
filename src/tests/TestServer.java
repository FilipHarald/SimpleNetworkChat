package tests;

import server.Server;

public class TestServer {

	public static void main(String[] args) {
		Server server = new Server(3520);
		server.start();
	}
}
