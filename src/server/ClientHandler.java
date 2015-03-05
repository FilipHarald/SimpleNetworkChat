package server;

import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket socketIn;
	private Socket socketOut;
	private String clientName;

	public ClientHandler(Socket socketIn) {
		this.socketIn = socketIn;
	}

	@Override
	public void run() {
		// skapa ClientHandlerInput
	}

//	public boolean sendToClient(Message message) {
//		boolean sent = false;
//		// 
//		// här ska meddelandet skickas med socketOut
//		//
//		return sent;
//	}

	private class ClientHandlerInput extends Thread {
		// ska lyssna på socketIn
	}

}
