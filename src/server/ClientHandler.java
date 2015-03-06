package server;

import java.io.*;
import java.net.Socket;

import other.*;

public class ClientHandler implements Runnable {
	private String clientName;
	private Socket socket;
	private Server server;
	ObjectOutputStream oos;

	public ClientHandler(Socket socketIn, Server server) {
		this.socket = socketIn;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			oos = new ObjectOutputStream(
					socket.getOutputStream());
			Message message;
			Object obj;
			while (!Thread.interrupted()) {
				obj = ois.readObject();
				if (obj instanceof Message) {
					message = (Message) obj;
					clientName = message.getSender();
					sendToClient(new Message(null, server.getClientList(), null, null));
				} else {
					System.out
							.println("Handshake object is not of class Message");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		new ClientHandlerInput(socket, server).start();
	}

	 public void sendToClient(Message message) {
		 try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }

	public String getClientname() {
		return clientName;
	}

	// ---------------------------------------------------------------------
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
						server.addMessage(message);
					} else {
						System.out
								.println("Object received is not of class Message");
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
