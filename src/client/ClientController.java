package client;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import other.Message;
import client.gui.*;

import javax.swing.*;

/**
 * 
 * @author Andreas
 *
 */
public class ClientController {
	
	private ClientGUI cgui;
	private Client client;
	
	public ClientController(String hostname, int port, String username) {
		cgui = new ClientGUI(this);
		client = new Client(hostname, port, username);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("SimpleNetworkChat");
				frame.add(cgui);
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
		
		setClient(client);
		
	}
	
	public void setClient(Client client) {
		this.client.addListener(new ClientListener() {
			public void onConnected() {}

			public void onClientsUpdated(String[] clients) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						cgui.setUsers(clients);	
					}
				});
			}

			public void onMessageReceived(Message message) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (message.hasImage()) {
							cgui.append(message, message.getImage());
						} else {
							cgui.append(message);
						}
					}
					
				});
			}

			public void onDisconnected() {}
		});
	}
	
	public void showGUI(String hostname, int port, String username) {
		JFrame clientFrame = new JFrame("SimpleNetworkChat");
		clientFrame.add(new ClientGUI(this));
		clientFrame.pack();
		clientFrame.setLocationRelativeTo(null);
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.setVisible(true);
		
		
		setClient(new Client(hostname, port, username));
	}
	
	public void sendMessage(String textMessage) {
		if (cgui.hasImage()) {
			client.sendMessage(cgui.getRecipients(), textMessage, cgui.getImageToSend());
		} else {
			client.sendMessage(cgui.getRecipients(), textMessage, null);
		}
	}
}
