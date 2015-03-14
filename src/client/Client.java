package client;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import message.*;
import exceptions.*;

/**
 * Client class used to talk to Server. Uses an inner class MessageListener
 * to continually listen for incoming messages from the server.
 * 
 * @author Albert och Henrik
 *
 */
public class Client {
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
				// Something has gone wrong with the socket
				System.out.println(ex.getMessage());
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

	public void start() throws ConnectException, SocketTimeoutException, IOException, NameInUseException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(serverHost, serverPort), 2000);
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

		// Send handshake response
        sendMessage(new Message(userName, null));

        // Get user list
        try {
	        Message message = (Message)ois.readObject();
			if (message.getSender() == null) {
				throw new NameInUseException(String.format("Username %s is already connected", userName));
			}
        } catch (ClassNotFoundException ex) {
        	ex.printStackTrace();
        }

        // Start a MessageListener thread to listen for incoming messages
		new MessageListener(ois).start();
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

	/**
	 * Thread that listens for incoming messages from the server
	 * 
	 * @author Albert & Henrik
	 *
	 */
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
				System.out.println("Received object is not available on client");
			}
			
			return null;
		}

		@Override
		public void run() {
			
			try {
                Message message;

                // Get initial userlist
				message = getMessage();
                if (message instanceof DataMessage) {
	        		fireClientsUpdated(((DataMessage)message).getData());
	        	}

                while (!Thread.interrupted()) {

                    // Get incoming messages
                    message = getMessage();

                    /* If we get a DataMessage it's either a forced disconnect (data is null)
                     * or an updated client list. If it's any other type of message, notify listeners 
                     */
                    if (message instanceof DataMessage) {
           				Object data = ((DataMessage)message).getData();
        				if (data == null) {
        					inputStream.close();
        				} else {
        					fireClientsUpdated(((DataMessage)message).getData());
        				}
                    } else {
                        fireMessageReceived(message);
                    }
                }

			} catch (IOException ex) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fireDisconnected();
			}
		}

	}
}
