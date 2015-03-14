package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.WindowEvent;
import javax.swing.*;

import message.*;
import client.gui.*;

/**
 * Controller class that binds together Client and GUI components
 * 
 * @author Andreas
 *
 */
public class ClientController {
	
	private ClientGUI cgui;
	private StartGUI sgui;
	private Client client;
	private JFrame frameStart;
	private JFrame frameGUI;
	private String hostname;
	private int port;
	private String username;

    private static final Pattern patternCommands = Pattern.compile("^\\/(\\w*) (\\w*)\\s?(.*)?");
	
	public ClientController() {
		cgui = new ClientGUI(this);
		sgui = new StartGUI(this);

		// Start by showing connect window
		showStartGUI();
	}

	private void showStartGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frameStart = new JFrame("SimpleNetworkChat - Connect");
				frameStart.add(sgui);
				frameStart.pack();
				frameStart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frameStart.setLocationRelativeTo(null);
				frameStart.setVisible(true);
			}
		});
	}

	public void connect(String hostname, int port, String username) {
		try {
			this.hostname = hostname;
			this.port = port;
			this.username = username;

			client = new Client(hostname, port, username);

			// Make sure we register listeners before we start the client
			registerListeners();
			client.start();

			// Hide connect window
			frameStart.setVisible(false);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frameGUI = new JFrame(String.format("SimpleNetworkChat - Connected to %s:%d", hostname, port));
					frameGUI.add(cgui);
					frameGUI.pack();
					frameGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frameGUI.setLocationRelativeTo(null);
					frameGUI.setResizable(false);
					frameGUI.setVisible(true);
					cgui.setInitialFocus();
				}
			});

		} catch (Exception ex) {
			// If there are any problems connecting to server, show error message
			JOptionPane.showMessageDialog(null, ex.getMessage());
			if (!frameStart.isVisible()) {
				if (frameGUI != null) {
					frameGUI.setVisible(false);
				}
				frameStart.setVisible(true);
			}
		}
	}
	
	public void registerListeners() {
		client.addListener(new ClientListener() {

			@Override
			public void onClientsUpdated(String[] clients) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						cgui.setUsers(clients);	
					}
				});
			}

			@Override
			public void onMessageReceived(Message message) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
                        if (message instanceof ServerMessage) {
                        	cgui.appendServerMessage(message.toString());
                        } else if (message instanceof PrivateMessage) {
                        	PrivateMessage pmsg = (PrivateMessage)message;
                        	// We need this check to make sure PM user sent is shown in the correct tab
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

			@Override
			public void onDisconnected() {
				// Show option to reconnect if disconnected from server
				int answer = JOptionPane.showConfirmDialog(null, "Do you want to reconnect?", "Client disconnected!", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					connect(hostname, port, username);
				} else {
					frameStart.dispatchEvent(new WindowEvent(frameStart, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}
	
	public void sendMessage(String textMessage, ImageIcon image, int group) {
		if (textMessage != null) {
			
			// Check for any commands
			Matcher m = patternCommands.matcher(textMessage);

			if (m.find()) {
				String command, option, text;
				command = m.group(1);
				option = m.group(2);
				text = m.group(3);
				
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
				// If not a command, simply send appropriate message
				if (group == 0) {
					client.sendChatMessage(cgui.getRecipients(), textMessage, image);
				} else {
					client.sendPrivateMessage(cgui.getRecipients(), textMessage, image, group);
				}
				
			}
			
			cgui.clearImage();
		}
		
	}

	public static void main(String[] args) {
		new ClientController();
	}
}
