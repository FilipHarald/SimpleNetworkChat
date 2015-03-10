package client.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import client.*;

/**
 * 
 * @author Andreas
 */

public class ClientGUI extends JPanel {
	
	private JTextPane chatBox;
	private StyledDocument doc;
	private Style textStyle;
	private Style imgStyle;
	private Style serverStyle;
	private Style pmStyle;
	
	private JTextField chatTF = new JTextField();
	private JButton sendBtn = new JButton("Send");
	private JButton addImageBtn = new JButton("Attach image");
	private ImageIcon imageToSend = null;

	private ClientController cc;
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> users;
		
	public ClientGUI(ClientController cc) {
		this.cc = cc;
		
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
				
		StyleContext context = new StyleContext();
		doc = new DefaultStyledDocument(context);
		textStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(textStyle, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(textStyle, 12);
		StyleConstants.setSpaceAbove(textStyle, 4);
		StyleConstants.setSpaceBelow(textStyle, 4);
		StyleConstants.setFontFamily(textStyle, "Consolas");
		
		serverStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(serverStyle, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(serverStyle, 16);
		StyleConstants.setSpaceAbove(serverStyle, 4);
		StyleConstants.setSpaceBelow(serverStyle, 4);
		StyleConstants.setFontFamily(serverStyle, "Consolas");
		StyleConstants.setBold(serverStyle, true);
		
		pmStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(pmStyle, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(pmStyle, 14);
		StyleConstants.setSpaceAbove(pmStyle, 4);
		StyleConstants.setSpaceBelow(pmStyle, 4);
		StyleConstants.setFontFamily(pmStyle, "Consolas");
		StyleConstants.setItalic(pmStyle, true);
		
		imgStyle = doc.addStyle("Image style", null);
		
		
		chatBox = new JTextPane(doc);
		chatBox.setAutoscrolls(true);
		chatBox.setEditable(false);
		chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
		chatBox.setBounds(0, 0, 650, 500);
		
		JScrollPane scroll = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(650, 500));
		scroll.setViewportView(chatBox);
		scroll.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
		add(scroll, BorderLayout.CENTER);
		
		users = new JList<String>(listModel);
		
		users.setFont(new Font("Consolas", Font.PLAIN, 12));
		users.setPreferredSize(new Dimension(150, 600));
		users.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		users.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(users, BorderLayout.EAST);
		
		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(800, 32));
		add(southPanel, BorderLayout.SOUTH);
		
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
					
		this.setFocusTraversalPolicy(new FocusPolicy(chatTF, sendBtn, addImageBtn));
		this.setFocusTraversalPolicyProvider(true);
		
	}
	
	public void setInitialFocus() {
		chatTF.requestFocusInWindow();
	}
	
	public void append(String entry) {
		try {
			doc.insertString(doc.getLength(), entry + "\n", textStyle);
		} catch (Exception e) {}
//		chatBox.setText(chatBox.getText() + "\n" + entry);
	}
	
	public void appendServerMessage(String entry) {
		try {
			doc.insertString(doc.getLength(), entry + "\n", serverStyle);
		} catch (Exception e) {}
	}
	
	public void appendPrivateMessage(String entry) {
		try {
			doc.insertString(doc.getLength(), entry + "\n", pmStyle);
		} catch (Exception e) {}
	}
	
	public void append(String[] entries) {
		for (int i = 0, len = entries.length; i < len; i++) {
			append(entries[i]);
		}
	}
	
	public void append(ArrayList<String> entries) {
		for (int i = 0, len = entries.size(); i < len; i++) {
			append(entries.get(i));
		}
	}
	
	public void append(Object obj) {
		append(obj.toString());
	}
	
	public void append(Object obj, ImageIcon icon) {
		append(obj.toString() + "\n");
		try {
			StyleConstants.setIcon(imgStyle, icon);
			doc.insertString(doc.getLength(), "ignored", imgStyle);
			doc.insertString(doc.getLength(), "\n", textStyle);
			
		} catch (Exception e) {}
	}
	
	public boolean hasImage() {
		if (imageToSend == null) {
			return false;
		}
		return true;
	}
	
	public void clearImage() {
		imageToSend = null;
	}
	
	public String[] getRecipients() {
		ArrayList<String> recipients;
		try {
			recipients = (ArrayList<String>) users.getSelectedValuesList();
			String[] arrayRecipients = new String[recipients.size()];
			arrayRecipients = recipients.toArray(arrayRecipients);
			return arrayRecipients;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public void setUsers(String[] userList) {
		listModel.clear();
		for (String user : userList) {
			listModel.addElement(user);
		}
	}
	
	private class SendMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String message = chatTF.getText();
			cc.sendMessage(message, imageToSend);
			chatTF.setText("");
			addImageBtn.setEnabled(true);
		}
	}
	
	private class AddImage implements ActionListener {
		private JFileChooser chooser = new JFileChooser();
		private FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"'.jpg', '.png', '.gif'", "jpg", "png", "gif");
		
		public void actionPerformed(ActionEvent e) {
			chooser.setFileFilter(filter);
			int returnValue = chooser.showOpenDialog(null);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				System.out.println(filePath);
				imageToSend = new ImageIcon(filePath);
				switch(imageToSend.getImageLoadStatus()) {
					case MediaTracker.ABORTED:
						System.out.println("ABORTED");
						break;
					case MediaTracker.COMPLETE:
						System.out.println("COMPLETE");
						break;
					case MediaTracker.ERRORED:
						System.out.println("ERRORED");
						break;
					case MediaTracker.LOADING:
						System.out.println("LOADING");
						break;
				}
				addImageBtn.setEnabled(false);
			}
		}
	}
	
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
			System.out.println("Get after component");
			System.out.println(order.indexOf(component));
			System.out.println(order.size());
			int index = (order.indexOf(component) + 1) % order.size();
			System.out.println("New component index = " + index);
			return order.get(index);
		}

		@Override
		public Component getComponentBefore(Container container,
				Component component) {
			System.out.println("Get before component");
			int index = order.indexOf(component);
			if (index == 0) {
				return getLastComponent(container);
			} else {
				return order.get(index - 1);
			}
		}

		@Override
		public Component getDefaultComponent(Container container) {
			System.out.println("Get default component");
			return order.get(0);
		}

		@Override
		public Component getFirstComponent(Container container) {
			System.out.println("Get first component");
			return order.get(0);
		}

		@Override
		public Component getLastComponent(Container container) {
			System.out.println("Get last component");
			return order.get(order.size() - 1);
		}
		
	}
	
}
