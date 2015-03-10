package tests;

import client.ClientController;

public class TestClientGUI {
	
	public static void main(String[] args) {
		ClientController controller = new ClientController("localhost", 3520, "A");
	}

}
