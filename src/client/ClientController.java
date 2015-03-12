package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import message.*;
import client.gui.*;

/**
 * 
 * @author Andreas
 *
 */
public class ClientController {
	
	private ClientGUI cgui;
	private Client client;
	private JFrame frame;

    private static final Pattern patternCommands = Pattern.compile("^\\/(\\w*) (\\w*)\\s?(.*)?");
	
	public ClientController(String hostname, int port, String username, JFrame frameStart) {
		cgui = new ClientGUI(this);
		client = new Client(hostname, port, username);

		try {
			client.start();

			frameStart.setVisible(false);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame = new JFrame(String.format("SimpleNetworkChat - Connected to %s:%s", hostname, port));
					frame.add(cgui);
					frame.pack();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					cgui.setInitialFocus();
				}
			});

			setClient(client);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}

	}
	
	public void setClient(Client client) {
		this.client.addListener(new ClientListener() {
			public void onConnected(String host, int port) {
				// not used
			}

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
                        if (message instanceof ServerMessage) {
                        	cgui.appendServerMessage(message.toString());
                        } else if (message instanceof PrivateMessage) {
                        	PrivateMessage pmsg = (PrivateMessage)message;
                        	if (message.getSender().equals(client.getUserName())) {
                        		pmsg.setSenderCopy(true);
                        		cgui.appendPrivateMessage(message.toString(), pmsg.getImage(), pmsg.getGroup());
                        	} else {
                        		cgui.appendPrivateMessage(message.toString(), pmsg.getImage(), 0);
                        	}
                        } else if (message instanceof ChatMessage) {
                            ChatMessage cmsg = (ChatMessage)message;
                            cgui.appendPublicMessage(cmsg.toString(), cmsg.getImage());
                        }
					}
					
				});
			}

			public void onDisconnected() {}
		});
	}
	
	public void sendMessage(String textMessage, ImageIcon image, int group) {
		if (textMessage != null) {
			Matcher m = patternCommands.matcher(textMessage);
			
			if (m.find()) {
				System.out.println("Found match");
				String command, option, text;
				command = m.group(1);
				option = m.group(2);
				text = m.group(3);
				
				System.out.println(command);
				System.out.println(option);
				System.out.println(text);
				
				switch (command) {
					case "message":
					case "msg":
						client.sendPrivateMessage(option, text, image, group);
						break;
                    case "whois":
                        client.sendCommandMessage(command, option);
                        break;
                    case "kick":
                    	client.sendCommandMessage(command, option);
                    	break;
					default:
						cgui.appendPublicMessage("UNKNONWASDN CAMAMAD", null);
						break;
				}
			} else {
				if (group == 0) {
					client.sendChatMessage(cgui.getRecipients(), textMessage, image);
				} else {
					client.sendPrivateMessage(cgui.getRecipients(), textMessage, image, group);
				}
				
			}
			
			cgui.clearImage();
		}
		
	}
}
