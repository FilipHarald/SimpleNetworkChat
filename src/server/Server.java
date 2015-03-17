package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import message.*;
import server.log.Log;

/**
 * The server class used by the clients to send messages with text and/or images to each other.
 * @author Filip & Jimmy
 *
 */
public class Server extends Thread {
	private ServerSocket serverSocket;
	private Map<String, ClientHandler> clientMap;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private Map<String, LinkedList<Message>> undeliveredMessageMap;
	private ServerListener serverController;
	private boolean isStopping = false;

	/**
	 * Constructor who takes an int as parameter and throws IOException
	 * @param port the port the server uses
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		// Initialize log
		Log.init();

		clientMap = new ConcurrentHashMap<String, ClientHandler>();
		undeliveredMessageMap = new ConcurrentHashMap<String, LinkedList<Message>>();
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ex) {
			Log.write(Log.SEVERE, ex.getMessage());
			Log.close();
			throw ex;
		} 

        // Register shutdown hook so that we can close log file
        // NOTE: This won't trigger when terminating the process from inside Eclipse
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	try {
					serverSocket.close();
				} catch (IOException e) {
					Log.write(Log.SEVERE, e.getMessage());
				}
            	Log.close();
            }
        });
	}

	public void addListener(ServerListener serverListener) {
		serverController = serverListener;
		
	}

	//The method closes the server
	public void stopServer() {
		try {
			Log.write(Log.INFO, "Shutting down server...");
			int numClients = clientMap.size();
			
			for (Entry<String, ClientHandler> entry : clientMap.entrySet()) {
				ClientHandler handler = entry.getValue();
				handler.disconnect();
			}
			
			int wait = (100 * numClients) > 5000 ? 5000 : (100 * numClients);
			
			threadPool.shutdown();
			threadPool.awaitTermination(wait, TimeUnit.MILLISECONDS);
			
			serverSocket.close();
			isStopping = true;
			
		} catch (InterruptedException | IOException ex) {
			Log.write(Log.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void run() {
		Log.write(Log.INFO,	String.format("Server running on port %s", serverSocket.getLocalPort()));
		while (!Thread.interrupted() && !isStopping) {
			try {
				Socket socket = serverSocket.accept();
				Log.write(Log.INFO, String.format("Client %s connected", socket.getRemoteSocketAddress().toString()));
				new ClientHandler(socket, this).start();
				Log.write(Log.INFO, "ClientHandler created");
			} catch (IOException e) {
				Log.write(Log.SEVERE, e.getMessage());
			}
		}

		// Make sure we close log
		Log.close();
	}

	//The method checks if a client exists
	public boolean clientExists(String clientName) {
		return clientMap.containsKey(clientName);
	}

    private void handleCommand(CommandMessage message) {
    	System.out.println(String.format("Handling command %s", message.toString()));
        switch (message.getCommand()) {
            case "whois":
            	if (clientMap.containsKey(message.getArguments())) {
	                ClientHandler handler = clientMap.get(message.getArguments());
	                addMessage(new ServerMessage(new String[]{message.getSender()}, handler.getClientAddress()));
            	} else {
            		addMessage(new ServerMessage(new String[]{message.getSender()}, 
            				String.format("No user by the name %s is currently connected", message.getArguments())));
            	}
                break;
            case "kick":
            	if (clientMap.containsKey(message.getArguments())) {
        			addMessage(new ServerMessage(null, String.format("%s kicked %s from the chat!", message.getSender(), message.getArguments())));
        			addMessage(new DataMessage(new String[] {message.getArguments()}, null));
            	} else {
            		addMessage(new ServerMessage(new String[] {message.getSender()}, "The user you're trying to kick is not online"));
            	}
                break;
            default:
                break;
        }
    }

    /**
     * Method for checking what kind of message and how many recipients there are. Takes a Message as parameter
     * @param message the Message that is to be sent
     */
	public void addMessage(Message message) {

        // Check if command message
        if (message instanceof CommandMessage) {
            handleCommand((CommandMessage)message);
            return;
        }

        String[] recipients = message.getRecipients();

        // If recipients is null, the message should go to all clients
        if (recipients == null) {
            addMessage(message.copy(getClients()));
        }
        // If recipients length is 0, no clients are currently connected
        else if (recipients.length == 0) {
        	return;
        } 
        // If recipients is more than one, break up into individual messages
        else if (recipients.length > 1) {
            for (String recipient : recipients) {
                addMessage(message.copy(new String[]{recipient}));
            }
        // If we get here, then we have a message with a single recipient, send it!
        } else {
        	
        	// This is so that undelivered messages retain their original received time
        	if (message.getTimeReceived() <= 0) {
        		message.setTimeReceived(System.currentTimeMillis());
        	}

        	try {
        		threadPool.execute(new MessageSender(message));
        	} catch (RejectedExecutionException ex) {
        		Log.write(Log.WARNING, "Server shutting down, unable to send message");
        	}
        }
	}

	/**
	 * Method that returns a String array with the clientnames
	 * @return string array with clientnames
	 */
	public String[] getClients() {
		String[] array = new String[clientMap.size()];
		array = clientMap.keySet().toArray(array);
		return array;
	}
	
	/**
	 * Method for adding clienthandler för new client, taking a String and a ClientHandler as parameters.
	 * @param clientName String with clients name
	 * @param clientHandler	new Clients ClientHandler
	 */
	public void addClientHandler(String clientName, ClientHandler clientHandler) {
		// Add ClientHandler to our map of clients
		clientMap.put(clientName, clientHandler);
		Log.write(Log.INFO, String.format("Added client %s (with ClientHandler %s)", clientName, clientHandler));
		
		sendNewClientList();

		// Add any undelivered messages to the message queue, then remove them from the undelivered map
		if (undeliveredMessageMap.containsKey(clientName)) {
			threadPool.execute(new MessageSender(undeliveredMessageMap.get(clientName)));
			undeliveredMessageMap.remove(clientName);
		}
	}

	/**
	 * Method for removing disconnected clients ClientHandler that takes a String as parameter
	 * @param clientName String with ClientName
	 */
	public void removeClientHandler(String clientName) {
		// Remove ClientHandler from the map of clients
		clientMap.remove(clientName);
		Log.write(Log.INFO, String.format("Removed client %s", clientName));
		
		// If there are any clients left, send updated client list and disconnect message
		if (clientMap.size() > 0) {
			addMessage(new ServerMessage(getClients(), (String.format("%s disconnected", clientName))));
		}
		
		// Send client list updates to remaining clients
		sendNewClientList();
	}
	
	/**
	 * Method that sends the updated clientlist to serverGUI
	 */
	public void sendNewClientList(){
		String[] clients = getClients();
		
		// Send updated client list if there are any remaining clients
		if (clients.length > 0) {
			addMessage(new DataMessage(null, clients));
		}
		
		// Always update GUI
		fireOnClientListUpdated(clients);
	}
	
	private void fireOnClientListUpdated(String[] clients) {
		if (serverController != null) {
			serverController.onClientListUpdated(clients);
		}
	}
	 /**
	  * This class sends the messages from the server to the clients
	  */
	private class MessageSender implements Runnable {
		private LinkedList<Message> messages;

		/**
		 * Constructor that takes a Message as parameter
		 * @param message Message that is to be sent
		 */
		public MessageSender(Message message) {
			messages = new LinkedList<Message>();
			messages.add(message);
		}
		
		/**
		 * Constructor that takes a LinkedList with Messages as parameter
		 * @param message LinkedList with Messages that is to be sent
		 */
		public MessageSender(LinkedList<Message> messages) {
			this.messages = messages;
		}

		/**
		 * This method sends the messages.
		 */
		public void run() {
			for (Message message : messages) {
				String recipient = message.getRecipients()[0];
				if (clientExists(recipient)) {
					message.setTimeDelivered(System.currentTimeMillis());
					clientMap.get(recipient).sendMessage(message);
					Log.write(Log.INFO, String.format("Delivered %s to %s from %s", message.getClass().getName(), recipient, message.getSender()));
				} else if (message instanceof ChatMessage) {
					if (!undeliveredMessageMap.containsKey(recipient)) {
						undeliveredMessageMap.put(recipient, new LinkedList<Message>());
					}
					undeliveredMessageMap.get(recipient).add(message);
					
					Log.write(Log.INFO, String.format("User %s not online. Added message to undelivered storage", recipient));
				}
			}
		}
	}

}
