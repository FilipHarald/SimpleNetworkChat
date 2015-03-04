package server;

import java.net.Socket;

public class ClientHandler extends Thread{
	private Socket socketIn;
	
	
	public ClientHandler(Socket socketIn) {
		this.socketIn = socketIn;
	}

	@Override
	public void run() {
		//skapa ClientHandlerInput 
	}
	
	private class ClientHandlerInput extends Thread{
		//ska lyssna på socketIn
	}
	

}
