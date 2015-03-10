package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import other.*;

/**
 * 
 * @author Filip & Jimmy
 *
 */
public class Server extends Thread {
	private ServerSocket serverSocket;
	private Map<String, ClientHandler> clientMap;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private Map<String, LinkedList<Message>> undeliveredMessageMap;

	public Server(int port) {
		// Initialize log
		Log.init(Server.class.getName());

		clientMap = new ConcurrentHashMap<String, ClientHandler>();
		undeliveredMessageMap = new ConcurrentHashMap<String, LinkedList<Message>>();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

        // Register shutdown hook so that we can close log file
        // NOTE: This won't trigger when terminating the process from inside Eclipse
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Log.close();
            }
        });
	}

	@Override
	public void run() {
		Log.write(Log.INFO,	String.format("Server running on port %s", serverSocket.getLocalPort()));
		while (!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
				Log.write(Log.INFO, "Client connected");
				new ClientHandler(socket, this).start();
				Log.write(Log.INFO, "ClientHandler created");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

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

	public void addMessage(Message message) {

        // Check if command message
        if (message instanceof CommandMessage) {
            handleCommand((CommandMessage)message);
            return;
        }

        String[] recipients = message.getRecipients();

        if (recipients == null) {
            addMessage(message.copy(getClients()));
        } else if (recipients.length == 0) {
        	return;
        } else if (recipients.length > 1) {
            for (String recipient : recipients) {
                addMessage(message.copy(new String[]{recipient}));
            }
        } else {
        	
        	if (message.getTimeReceived() <= 0) {
        		message.setTimeReceived(System.currentTimeMillis());
        	}

            threadPool.execute(new MessageSender(message));
        }
	}

	public String[] getClients() {
		String[] array = new String[clientMap.size()];
		array = clientMap.keySet().toArray(array);
		return array;
	}
	
	public void addClientHandler(String clientName, ClientHandler clientHandler) {
		// Add ClientHandler to our map of clients
		clientMap.put(clientName, clientHandler);
		Log.write(Log.INFO, String.format("Added client %s (with ClientHandler %s)", clientName, clientHandler));
		
		// Send DataMessage to all clients with updated userlist
		addMessage(new DataMessage(null, getClients()));
		
		// Add any undelivered messages to the message queue
		if (undeliveredMessageMap.containsKey(clientName)) {
			threadPool.execute(new MessageSender(undeliveredMessageMap.get(clientName)));
		}
	}

	public void removeClientHandler(String clientName) {
		// Remove ClientHandler from the map of clients
		clientMap.remove(clientName);
		Log.write(Log.INFO, String.format("Removed client %s", clientName));
		
		// If there are any clients left, send updated client list and disconnect message
		if (clientMap.size() > 0) {
			addMessage(new DataMessage(null, getClients()));
			addMessage(new ServerMessage(getClients(), (String.format("%s disconnected", clientName))));
		}
	}
	
	private class MessageSender implements Runnable {
		private LinkedList<Message> messages;

		public MessageSender(Message message) {
			messages = new LinkedList<Message>();
			messages.add(message);
		}

		public MessageSender(LinkedList<Message> messages) {
			this.messages = messages;
		}

		@Override
		public void run() {
			for (Message message : messages) {
				String recipient = message.getRecipients()[0];
				if (clientExists(recipient)) {
					message.setTimeDelivered(System.currentTimeMillis());
					clientMap.get(recipient).sendToClient(message);
					Log.write(Log.INFO, String.format("Delivered message of type %s to %s from %s", message.getClass().getName(), recipient, message.getSender()));
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
