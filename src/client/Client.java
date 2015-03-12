package client;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;

import message.*;

/**
 * 
 * @author Albert och Henrik
 *
 */
public class Client extends Thread {
	private String serverHost;
	private int serverPort;
	private String userName;
	private Socket socket;
	
	private ObjectOutputStream outputStream;
	private Set<ClientListener> listeners;
	
	public Client(String serverHost, int serverPort, String userName) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.userName = userName;
		this.listeners = new HashSet<ClientListener>();
	
		this.start();
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {}
		}
	}
	
	public void addListener(ClientListener listener) {
		listeners.add(listener);
	}
	
	public String getUserName() {
		return userName;
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

	public void sendChatMessage(String[] recipients, String text, ImageIcon image) {
		sendMessage(new ChatMessage(userName, recipients, text, image));
	}

	public void sendPrivateMessage(String[] recipients, String text, ImageIcon image, int group) {
		sendMessage(new PrivateMessage(userName, recipients, text, image, group));
	}

	public void sendPrivateMessage(String recipient, String text, ImageIcon image, int group) {
		sendPrivateMessage(new String[] {recipient}, text, image, group);
	}

    public void sendCommandMessage(String command, String arguments) {
    	sendMessage(new CommandMessage(userName, command, arguments));
    }

	@Override
	public void run() {

		try {
			socket = new Socket(serverHost, serverPort);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); 

			new MessageListener(ois).start();

			fireConnected();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void fireConnected() {
		for (ClientListener listener : listeners) {
			listener.onConnected(serverHost, serverPort);
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
		
		public Message getMessage() throws IOException {
			
			try {
				Object obj = inputStream.readObject();
				if (obj instanceof Message) {
					return (Message)obj;
				} else {
					System.out.println("Received object is not of type Message");
				}
			} catch (ClassNotFoundException ex) {
				throw new IOException();
			}
			
			return null;
		}

		@Override
		public void run() {
			
			try {
                Message message;

                // Send handshake response
                sendMessage(new Message(userName, null));

                // Get user list
                message = getMessage();
                if (message instanceof DataMessage) {
                	fireClientsUpdated((String[])((DataMessage)message).getData());
                }

                while (!Thread.interrupted()) {

                    // Get incoming messages
                    message = getMessage();

                    if (message instanceof DataMessage) {
           				Object data = ((DataMessage)message).getData();
        				if (data == null) {
        					inputStream.close();
        				} else {
        					fireClientsUpdated((String[])((DataMessage)message).getData());
        				}
                    } else {
                        fireMessageReceived(message);
                    }
                }

			} catch (IOException ex) {
				
				fireDisconnected();
				
				System.out.println("Klient nedkopplad");
				
//				ex.printStackTrace();
			}
		}

	}
}
