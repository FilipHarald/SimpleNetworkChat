package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import other.*;

public class ClientHandler extends Thread {
	private Socket socketIn;
	private Socket socketOut;
	private String clientName;
	private Server server;

	public ClientHandler(Socket socketIn, Server server) {
		this.socketIn = socketIn;
		this.server = server;
		System.out.println("ClientHandler created");
	}

	@Override
	public void run() {
		// se till att få en socketOut först
		new ClientHandlerInput(socketIn, server).start();
	}

	// public boolean sendToClient(Message message) {
	// boolean sent = false;
	// //
	// // här ska meddelandet skickas med socketOut
	// //
	// return sent;
	// }
	private class ClientHandlerInput extends Thread {
		private Socket socketIn;
		private Server server;

		public ClientHandlerInput(Socket socketIn, Server server) {
			this.socketIn = socketIn;
			this.server = server;
		}

		public void run() {
			try (ObjectInputStream ois = new ObjectInputStream(
					socketIn.getInputStream())) {
				Message message;
				Object obj;
				while (!Thread.interrupted()) {
					obj = ois.readObject();
					if (obj instanceof Message) {
						message = (Message) obj;
						server.
					}else{
						System.out.println("Object received is not of class Message");
					}
					

				}
				
				
				
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					socketIn.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
	}

}
