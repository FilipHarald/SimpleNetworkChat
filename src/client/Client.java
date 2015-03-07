package client;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;

import other.ChatMessage;
import other.CommandMessage;
import other.DataMessage;
import other.Message;

/**
 * 
 * @author Albert och Henrik
 *
 */
public class Client extends Thread {
	private String serverHost;
	private int serverPort;
	private String userName;
	
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

	public void sendChatMessage(String[] recipients, String text, ImageIcon image) {
		if (outputStream != null) {
			try {
				outputStream.writeObject(new ChatMessage(userName, recipients, text, image));
				outputStream.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void sendChatMessage(String recipient, String text, ImageIcon image) {
		if (recipient.equals(userName)) {
			sendChatMessage(new String[]{recipient}, text, image);
		} else {
			sendChatMessage(new String[]{recipient, userName}, text, image);
		}
	}

    public void sendCommandMessage(String command, String arguments) {
        if (outputStream != null) {
            try {
                outputStream.writeObject(new CommandMessage(userName, command, arguments));
                outputStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

	@Override
	public void run() {

		try {
			Socket socket = new Socket(serverHost, serverPort);
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
		
		public Message getMessage() throws IOException {
			
			try {
				Object obj = inputStream.readObject();
				if (obj instanceof Message) {
					return (Message)obj;
				} else {
					System.out.println("Received object is not of type Message");
				}
			} catch (ClassNotFoundException | IOException e) {
				
				e.printStackTrace();
				
				System.out.println("Klienten nedkopplad?");
				fireDisconnected();
				
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
                fireClientsUpdated(message.getRecipients());

                while (!Thread.interrupted()) {

                    // Get incoming messages
                    message = getMessage();

                    if (message instanceof DataMessage) {
                        fireClientsUpdated((String[])((DataMessage)message).getData());
                    } else {
                        fireMessageReceived(message);
                    }
                }

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}
