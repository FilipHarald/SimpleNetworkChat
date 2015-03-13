package tests;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import message.Message;
import client.Client;
import client.ClientListener;
import exceptions.NameInUseException;

public class TestClient {

	public static void main(String[] args) {
		
		Client client = new Client("localhost", 3520, "B");
		
		try {
			client.start();
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
		
		client.addListener(new ClientListener() {

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
