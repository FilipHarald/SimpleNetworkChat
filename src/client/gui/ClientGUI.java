package client.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import client.*;
import client.gui.models.Group;
import client.gui.panels.*;

/**
 * Main GUI panel for Client. It combines all panels into a cohesive
 * interface and talks to a ClientController.
 * 
 * @author Andreas
 */

public class ClientGUI extends JPanel {
	
	private JTextField chatTF = new JTextField();
	private JButton sendBtn = new JButton("Send");
	private JButton addImageBtn = new JButton("Attach image");
	private ImageIcon imageToSend = null;
	
	private ChatPanel chatPanel = new ChatPanel();
	private UsersPanel usersPanel = new UsersPanel();
	
	private Map<String, Group> groups;
	private String currentGroup;

	private String defaultTabName = "Lobby";
	
	private ClientController cc;
		
	public ClientGUI(ClientController cc) {
		this.cc = cc;
		this.groups = new HashMap<String, Group>();
		
		// Create default tab 
		this.addGroup(defaultTabName, null, false);
		this.currentGroup = defaultTabName;
		
		// Create GUI
		this.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout(5, 0));
		
		usersPanel.setBorder(new EmptyBorder(35, 0, 0, 5));
		chatPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
		
		this.add(chatPanel, BorderLayout.CENTER);
		this.add(usersPanel, BorderLayout.EAST);		
		
		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(800, 32));
		this.add(southPanel, BorderLayout.SOUTH);
		
		chatTF.setPreferredSize(new Dimension(580, 24));
		chatTF.setFont(new Font("Consolas", Font.PLAIN, 12));
		chatTF.addActionListener(new SendMessage());
		southPanel.add(chatTF);
		
		sendBtn.setPreferredSize(new Dimension(70, 24));
		sendBtn.setFont(new Font("Consolas", Font.BOLD, 12));
		sendBtn.addActionListener(new SendMessage());
		southPanel.add(sendBtn);
		
		addImageBtn.setPreferredSize(new Dimension(130, 24));
		addImageBtn.setFont(new Font("Consolas", Font.BOLD, 12));
		addImageBtn.addActionListener(new AddImage());
		southPanel.add(addImageBtn);
		
		// Restrict focus to text input field, send button, and attach image button
		this.setFocusTraversalPolicy(new FocusPolicy(chatTF, sendBtn, addImageBtn));
		this.setFocusTraversalPolicyProvider(true);
		
		registerListeners();
		
	}
	
	private void registerListeners() {
		chatPanel.addListener(new ChatPanelListener() {
			@Override
			public void onChangedTab(String name) {
				if (groups.containsKey(name)) {
					currentGroup = name;
					usersPanel.updateList(groups.get(name).getUsers());
				}
			}
		});
		
		usersPanel.addListener(new UsersPanelListener() {
			@Override
			public void onCreateGroup(String name, String[] users) {
				addGroup(name, users, true);
			}
		});
	}
	
	private void addGroup(String name, String[] users, boolean showButton) {
		groups.put(name, new Group(name, users));
		chatPanel.addTab(name, showButton);
	}
	
	public void setInitialFocus() {
		chatTF.requestFocusInWindow();
	}
	
	public void appendPublicMessage(String text, ImageIcon image) {
		chatPanel.appendTab(0, text, image, ChatPanel.NORMAL);
	}
	
	public void appendServerMessage(String text) {
		chatPanel.appendAllTabs(text, null, ChatPanel.BOLD);
	}
	
	public void appendPrivateMessage(String text, ImageIcon image, int group) {
		chatPanel.appendTab(group, text, image, ChatPanel.ITALIC);
	}
	
	public void clearImage() {
		imageToSend = null;
	}
	
	public String[] getRecipients() {
		return groups.get(currentGroup).getUsers();
	}
	
	public void setUsers(String[] userList) {
		Group lobby = groups.get(defaultTabName);
		lobby.setUsers(userList);
		
		if (lobby.getName().equals(currentGroup)) {
			usersPanel.updateList(userList);
		}
	}
	
	private class SendMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String message = chatTF.getText();
			cc.sendMessage(message, imageToSend, chatPanel.getCurrentTab());
			chatTF.setText("");
			addImageBtn.setEnabled(true);
		}
	}
	
	private class AddImage implements ActionListener {
		private JFileChooser chooser = new JFileChooser();

		public AddImage() {
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					String name = file.getName().toLowerCase();
					return name.endsWith(".png") &&
							name.endsWith(".jpg") &&
							name.endsWith(".gif") &&
							name.endsWith(".jpg") &&
							file.length()  < 1 * (1024 * 1024);
				}

				@Override
				public String getDescription() {
					return "Images (MAX 3MB)";
				}
			});
		}
		
		public void actionPerformed(ActionEvent e) {
			int returnValue = chooser.showOpenDialog(null);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				imageToSend = new ImageIcon(filePath);
				addImageBtn.setEnabled(false);
			}
		}
	}
	
	/**
	 * Inner class used to cycle through a specified set of focusable components
	 * @author Albert
	 *
	 */
	private class FocusPolicy extends FocusTraversalPolicy {
		
		ArrayList<Component> order;
		
		public FocusPolicy(Component... components) {
			order = new ArrayList<Component>();
			for (Component component : components) {
				order.add(component);
			}
		}

		@Override
		public Component getComponentAfter(Container container,
				Component component) {
			int index = (order.indexOf(component) + 1) % order.size();
			return order.get(index);
		}

		@Override
		public Component getComponentBefore(Container container,
				Component component) {
			int index = order.indexOf(component);
			if (index == 0) {
				return getLastComponent(container);
			} else {
				return order.get(index - 1);
			}
		}

		@Override
		public Component getDefaultComponent(Container container) {
			return order.get(0);
		}

		@Override
		public Component getFirstComponent(Container container) {
			return order.get(0);
		}

		@Override
		public Component getLastComponent(Container container) {
			return order.get(order.size() - 1);
		}
		
	}
	
}
