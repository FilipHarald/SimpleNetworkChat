package tests;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import client.Client;
import exceptions.NameInUseException;

public class TestClientSendsMessage {

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 3520, "TestClientSendsMessage");
		
		try {
			client.start();
		} catch (ConnectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketTimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NameInUseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < 20; i++) {
			client.sendChatMessage(null, "Message" + i, null);
		}
	}
	
}
