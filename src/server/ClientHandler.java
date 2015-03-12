package server;

import java.io.*;
import java.net.Socket;

import message.*;
import server.log.Log;

public class ClientHandler extends Thread {
	private String clientName;
	private Socket socket;
	private Server server;
	private ObjectOutputStream oos;

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
					// We have to send directly to client since we haven't added it to server map yet
					sendMessage(new Message(null, null));
					// Make sure we close the socket, and return so that we exit the thread loop
					socket.close();
					return;
				} else {
					// We have to send directly to client since we haven't added it to server map yet
					sendMessage(new Message(clientName, null));
				}
				
				// Send client connected message to all current clients
				server.addMessage(new ServerMessage(server.getClients(), (String.format("%s connected", clientName))));

                // Register ClientHandle on server
				server.addClientHandler(clientName, this);
				
				// Spawn InputHandler for client
				new ClientInputHandler(server, ois).start();
				
			} else {
				System.out.println("Handshake object is not of class Message");
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	public synchronized void sendMessage(Message message) {
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

    public String getClientAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

	// ---------------------------------------------------------------------
	private class ClientInputHandler extends Thread {
		private Server server;
		private ObjectInputStream inputStream;

		public ClientInputHandler(Server server, ObjectInputStream inputStream) {
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
						if (message instanceof PrivateMessage) {
							System.out.println("Private message received, copying to send back");
							server.addMessage(message.copy(new String[] {clientName}));
						}
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
