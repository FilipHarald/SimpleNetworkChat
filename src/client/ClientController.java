package client;

import javax.swing.ImageIcon;

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
	
	public ClientController() {
		cgui = new ClientGUI(this);
	}
	
	public void setClient(Client client) {
		this.client = client;
		this.client.addListener(new ClientListener() {
			public void onConnected() {}

			public void onClientsUpdated(String[] clients) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						cgui.setUsers(clients);
					}
				});
			}

			public void onMessageReceived(Message message) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						System.out.println(message);
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
	
	public void sendMessage(String textMessage) {
		if (cgui.hasImage()) {
			client.sendMessage(cgui.getRecipients(), textMessage, cgui.getImageToSend());
		} else {
			client.sendMessage(cgui.getRecipients(), textMessage, null);
		}
	}
}
