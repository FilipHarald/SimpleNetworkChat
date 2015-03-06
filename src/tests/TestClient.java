package tests;

import other.Message;
import client.Client;
import client.ClientListener;

public class TestClient {

	public static void main(String[] args) {
		
		Client client = new Client("10.1.13.11", 3520, "Anvandaren");
		
		client.addListener(new ClientListener() {

			@Override
			public void onConnected() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onClientsUpdated(String[] clients) {
				// TODO Auto-generated method stub
				System.out.println("Got clients: ");
				for (String client : clients) {
					System.out.println(client);
				}
			}

			@Override
			public void onMessageReceived(Message message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDisconnected() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	
}
