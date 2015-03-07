package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
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
	private HashMap<String, ClientHandler> clientMap;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private HashMap<String, LinkedList<Message>> undeliveredMessageMap;

	public Server(int port) {
		// Initialize log
		Log.init(Server.class.getName());

		clientMap = new HashMap<String, ClientHandler>();
		undeliveredMessageMap = new HashMap<String, LinkedList<Message>>();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Log.write(Log.INFO,	String.format("Server running at port %d", serverSocket.getLocalPort()));
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
                ClientHandler handler = clientMap.get(message.getArguments());
                addMessage(new ServerMessage(new String[]{message.getSender()}, handler.getClientAddress()));
                break;
            case "kick":
                break;
            default:
                break;
        }
    }

	public void addMessage(Message message) {

        // Check if command message
        if (message instanceof CommandMessage) {
            handleCommand((CommandMessage)message);
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
        	
        	message.setTimeReceived(System.currentTimeMillis());
        	
            threadPool.execute(new MessageSender(message));
        }
	}

	public String[] getClients() {
		String[] array = new String[clientMap.size()];
		array = clientMap.keySet().toArray(array);
		return array;
	}
	
	public String getClientsAsString(){
		String temp = "";
		String[] clients = getClients();
		if (clients.length > 0) {
			for(String s : clients){
				temp += s + ",";
			}
			
			temp = temp.substring(0, temp.length()-1);
		}
		
		return temp;
	}

	public void addClientHandler(String clientName, ClientHandler clientHandler) {
		clientMap.put(clientName, clientHandler);
		Log.write(Log.INFO, String.format(
				"Added client %s (with ClientHandler %s)", clientName,
				clientHandler));
		String[] clients = getClients();
		String list = getClientsAsString();
		System.out.println(list);
		addMessage(new DataMessage(null, clients));
		if (undeliveredMessageMap.containsKey(clientName)) {
			for (Message m : undeliveredMessageMap.get(clientName)) {
				addMessage(m);
			}
		}
	}

	public void removeClientHandler(String clientName) {
		clientMap.remove(clientName);
		
		String clientList = getClientsAsString();
		if (clientList.length() > 0) {
			addMessage(new DataMessage(null, getClients()));
			addMessage(new ServerMessage(getClients(), (String.format("%s disconnected", clientName))));
		}
		
		Log.write(Log.INFO, String.format("Removed client %s", clientName));
	}
	
	private class MessageSender implements Runnable {
		private Message message;

		public MessageSender(Message message) {
			this.message = message;
		}

		@Override
		public void run() {
			String recipient = message.getRecipients()[0];
			if (clientExists(recipient)) {
				message.setTimeDelivered(System.currentTimeMillis());
				clientMap.get(recipient).sendToClient(message);
				Log.write(Log.INFO, String.format("Delivered message of type %s to %s from %s", message.getClass().getName(), recipient, message.getSender()));
			} else {
				if (!undeliveredMessageMap.containsKey(recipient)) {
					undeliveredMessageMap.put(recipient, new LinkedList<Message>());
				}
				undeliveredMessageMap.get(recipient).add(message);
				
				Log.write(Log.INFO, String.format("User %s not online. Added message to undelivered storage", recipient));
			}
		}
	}
}
