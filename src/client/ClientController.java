package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public void sendMessage(String textMessage) {
		if (textMessage != null) {
			Pattern p = Pattern.compile("\\/(\\w*) (\\w*) (.*)");
			Matcher m = p.matcher(textMessage);
			
			if (m.find()) {
				String command, option, text;
				command = m.group(1);
				option = m.group(2);
				text = m.group(3);
				
				switch (command) {
				case "/message":
				case "/msg":
					client.sendMessage(option, text, cgui.getImageToSend());
					break;
				default:
					cgui.append("UNKNONWASDN CAMAMAD");
					break;
				}
			} else {
				client.sendMessage(cgui.getRecipients(), textMessage, cgui.getImageToSend());
			}
		}
		
	}
}
