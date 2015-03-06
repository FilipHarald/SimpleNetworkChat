package client.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import client.*;
/**
 * 
 * @author Andreas
 *
 */

public class ClientGUI extends JPanel {
	
	private JTextArea chatBox = new JTextArea();
	private JTextField chatTF = new JTextField();
	private JButton sendBtn = new JButton("Send");
	private JButton addImageBtn = new JButton("Send image...");
	private ImageIcon imageToSend;
	private ClientController cc;
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> users;
		
	public ClientGUI() {
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		chatBox.setPreferredSize(new Dimension(650, 500));
		chatBox.setAutoscrolls(true);
		chatBox.setLineWrap(true);
		chatBox.setEditable(false);
		chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
		chatBox.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
		add(chatBox, BorderLayout.CENTER);
		
		
		listModel.addElement("Jimmy");
		listModel.addElement("Filip");
		listModel.addElement("Klein");
		listModel.addElement("Albert");
		listModel.addElement("Andreas");
		
		users = new JList<String>(listModel);
		
		users.setFont(new Font("Consolas", Font.PLAIN, 12));
		users.setPreferredSize(new Dimension(150, 600));
		users.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		users.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(users, BorderLayout.EAST);
		
		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(800, 32));
		add(southPanel, BorderLayout.SOUTH);
		
		chatTF.setPreferredSize(new Dimension(595, 24));
		chatTF.setFont(new Font("Consolas", Font.PLAIN, 12));
		southPanel.add(chatTF);
		
		sendBtn.setPreferredSize(new Dimension(70, 24));
		sendBtn.setFont(new Font("Consolas", Font.BOLD, 12));
		southPanel.add(sendBtn);
		
		addImageBtn.setPreferredSize(new Dimension(130, 24));
		addImageBtn.setFont(new Font("Consolas", Font.BOLD, 12));
		addImageBtn.addActionListener(new AddImage());
		southPanel.add(addImageBtn);
		
		String testchat = 	"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!\n" + 
							"[14:00:58] <Klein> *mumlar på danska*";
		
		chatBox.setText(testchat);
		
	}
	
	public void append(String entry) {
		chatBox.append("\n" + entry);
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
	
	public ImageIcon getImageToSend() throws NullPointerException {
		return imageToSend;
	}
	
	public ArrayList<String> getRecipients() throws NullPointerException {
		ArrayList<String> recipients;
		recipients = (ArrayList<String>) users.getSelectedValuesList();
		return recipients;
	}
	
	public void setUsers() {
		//Uppdatera listan med användare
	}
	
	private class SendMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
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
				imageToSend = new ImageIcon(chooser.getSelectedFile().getName());
				addImageBtn.setEnabled(false);
			}
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("SimpleNetworkChat");
				frame.add(new ClientGUI());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}
