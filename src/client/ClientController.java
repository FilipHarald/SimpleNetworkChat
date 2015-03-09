package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import other.ChatMessage;
import other.CommandMessage;
import other.Message;
import client.gui.*;

/**
 * 
 * @author Andreas
 *
 */
public class ClientController {
	
	private ClientGUI cgui;
	private Client client;

    private static final Pattern patternCommands = Pattern.compile("^\\/(\\w*) (\\w*)\\s?(.*)?");
	
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
				cgui.setInitialFocus();
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
                        if (message instanceof ChatMessage) {
                            ChatMessage cmsg = (ChatMessage)message;
                            if (cmsg.hasImage()) {
                                cgui.append(cmsg.toString(), cmsg.getImage());
                            } else {
                                cgui.append(cmsg.toString());
                            }
                        }

					}
					
				});
			}

			public void onDisconnected() {}
		});
	}
	
	public void sendMessage(String textMessage, ImageIcon image) {
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
						client.sendChatMessage(option, text, image);
						break;
                    case "whois":
                        client.sendCommandMessage(command, option);
                        break;
                    case "kick":
                    	client.sendCommandMessage(command, option);
                    	break;
					default:
						cgui.append("UNKNONWASDN CAMAMAD");
						break;
				}
			} else {
				client.sendChatMessage(cgui.getRecipients(), textMessage, image);
			}
			
			cgui.clearImage();
		}
		
	}
}
