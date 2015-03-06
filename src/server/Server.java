package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import other.Message;

/**
 * 
 * @author Filip & Jimmy
 *
 */
public class Server extends Observable implements Runnable {
	private ServerSocket serverSocket;
	private HashMap<String, ClientHandler> clientHashMap;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private Buffer<Message> undeliveredMessageBuffer;

	// private Log log;

	public Server(int port, Log log) {
		// this.log = log;
		// För tillfället så är log observer till servern och därför behöver
		// inte servern ha referens till loggen.
		// addObserver(log);

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// notifyObservers("Server is running");
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Client connected");
				new ClientHandler(socket, this).start();
				System.out.println("ClientHandler created");
			} catch (IOException e) {
				// notifyObservers(e);
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
		return (String[]) clientHashMap.keySet().toArray();
	}

	public void addClientHandler(String clientName, ClientHandler clientHandler) {
		clientHashMap.put(clientName, clientHandler);
	}

	public void removeClientHandler(String clientName) {
		clientHashMap.remove(clientName);
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
