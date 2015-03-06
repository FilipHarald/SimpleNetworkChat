package server;

import java.io.*;
import java.net.Socket;
import java.sql.Date;

import other.*;

public class ClientHandler extends Thread {
	private String clientName;
	private Socket socket;
	private Server server;
	ObjectOutputStream oos;

	public ClientHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			Message message;
			Object obj;
			
			// Get user handshake
			obj = ois.readObject();
			if (obj instanceof Message) {
				message = (Message) obj;
				clientName = message.getSender();
				
				if (server.clientExists(clientName)) {
					Log.write(Log.WARNING, String.format("Client %s is already connected to the server", clientName));
					oos.close();
					return;
				}
				
				server.addClientHandler(clientName, this);
			} else {
				System.out.println("Handshake object is not of class Message");
			}
			
			// Send current user list
			sendToClient(new Message(null, server.getClientList(), null, null));
			
			new ClientHandlerInput(server, ois).start();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	 public void sendToClient(Message message) {
		 try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }

	public String getClientName() {
		return clientName;
	}

	// ---------------------------------------------------------------------
	private class ClientHandlerInput extends Thread {
		private Server server;
		private ObjectInputStream inputStream;

		public ClientHandlerInput(Server server, ObjectInputStream inputStream) {
			this.server = server;
			this.inputStream = inputStream;
		}

		public void run() {
			
			Message message;
			Object obj;
			try {
				while (!Thread.interrupted()) {
					obj = inputStream.readObject();
					if (obj instanceof Message) {
						message = (Message) obj;
						message.setTimeReceived(System.currentTimeMillis());
						server.addMessage(message);
					} else {
						System.out.println("Object received is not of class Message");
					}
				}
			} catch (IOException ex) {
				Log.write(Log.INFO, String.format("Client %s disconnected", clientName));
				server.removeClientHandler(clientName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
