package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import other.Message;

/**
 * 
 * @author Filip & Jimmy
 *
 */
public class Server extends Thread {
	private ServerSocket serverSocket;
	private HashMap<String, ClientHandler> clientHashMap;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private Buffer<Message> undeliveredMessageBuffer;

	public Server(int port) {
		// Initiera loggen
		Log.init(Server.class.getName());
		
		clientHashMap = new HashMap<String, ClientHandler>();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Log.write(Log.INFO, String.format("Server running at port %d", serverSocket.getLocalPort()));
		while (true) {
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

	public void addMessage(Message message) {
		String[] recipients = message.getRecipients();
		
		if (recipients == null || recipients.length > 1) {
			String sender = message.getSender();
			String textMessage = message.getTextMessage();
			ImageIcon image = message.getImage();
			
			if (recipients != null) {
				for (String client : recipients) {
					threadPool.execute(new MessageSender(new Message(sender,
							client, textMessage, image)));
				}
			} else {
				for (String client : getClientList()) {
					threadPool.execute(new MessageSender(new Message(sender, 
							client, textMessage, image)));
				}
			}
		} else {
			threadPool.execute(new MessageSender(message));
		}
		
	}

	public String[] getClientList() {
		String[] array = new String[clientHashMap.size()];
		array = clientHashMap.keySet().toArray(array);
		return array;
	}

	public void addClientHandler(String clientName, ClientHandler clientHandler) {
		clientHashMap.put(clientName, clientHandler);
		Log.write(Log.INFO, String.format("Added client %s (with ClientHandler %s)", clientName, clientHandler));
	}

	public void removeClientHandler(String clientName) {
		clientHashMap.remove(clientName);
		Log.write(Log.INFO, String.format("Removed client %s", clientName));
	}

	// --------------------------------------------------------------------------
	private class MessageSender implements Runnable {
		private Message message;

		public MessageSender(Message message) {
			this.message = message;
		}

		@Override
		public void run() {
			clientHashMap.get(message.getRecipients()[0]).sendToClient(message);
		}

	}

}
