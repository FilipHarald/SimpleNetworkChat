package client;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.ImageIcon;

import message.Message;

/**
 * 
 * @author Albert och Henrik
 *
 */
public class Client extends Thread {
	private String serverHost;
	private int serverPort;

	private String userName;

	private Thread messageListener;
	private ObjectOutputStream outputStream;

	private Set<ClientListener> listeners;
	
	public Client(String serverHost, int serverPort, String userName) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.userName = userName;
		this.listeners = new HashSet<ClientListener>();
	
		this.start();
	}

	public void addListener(ClientListener listener) {
		listeners.add(listener);
	}

	public void sendMessage(Message message) {
		if (outputStream != null) {
			try {
				outputStream.writeObject(message);
				outputStream.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void sendMessage(String[] recipients, String text, ImageIcon image) {
		if (outputStream != null) {
			try {
				outputStream.writeObject(new Message(userName, recipients, text, image));
				outputStream.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void run() {

		try (Socket socket = new Socket(serverHost, serverPort);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());) {

			outputStream = oos;

			new MessageListener(ois).start();

			fireConnected();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void fireConnected() {
		for (ClientListener listener : listeners) {
			listener.onConnected();
		}
	}

	private void fireDisconnected() {
		for (ClientListener listener : listeners) {
			listener.onDisconnected();
		}
	}

	private void fireClientsUpdated(String[] clients) {
		for (ClientListener listener : listeners) {
			listener.onClientsUpdated(clients);
		}
	}

	private void fireMessageReceived(Message message) {
		for (ClientListener listener : listeners) {
			listener.onMessageReceived(message);
		}
	}

	private class MessageListener extends Thread {

		private ObjectInputStream inputStream;

		public MessageListener(ObjectInputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public void run() {

			// Get handshake response

			fireConnected();

			// Get user list

			fireClientsUpdated(null);

			while (true) {

				// Get incoming messages

				fireMessageReceived(null);

			}
		}

	}
}
