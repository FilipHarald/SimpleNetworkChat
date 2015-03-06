package client.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import client.*;

/**
 * 
 * @author Andreas
 */

public class ClientGUI extends JPanel {
	
	private JTextPane chatBox = new JTextPane();
	private JTextField chatTF = new JTextField();
	private JButton sendBtn = new JButton("Send");
	private JButton addImageBtn = new JButton("Send image...");
	private ImageIcon imageToSend;

	private ClientController cc;
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> users;
		
	public ClientGUI(ClientController cc) {
		this.cc = cc;
		
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		chatBox.setAutoscrolls(true);
		chatBox.setEditable(false);
		chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
		chatBox.setBounds(0, 0, 650, 500);
		
		JScrollPane scroll = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(650, 500));
		scroll.setViewportView(chatBox);
		scroll.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
		add(scroll, BorderLayout.CENTER);
		
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
		
		String testchat = 	"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!\n" + 
							"[14:00:58] <Klein> *mumlar på danska*\n" + 
							"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!\n" + 
							"[14:00:58] <Klein> *mumlar på danska*\n" + 
							"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!\n" + 
							"[14:00:58] <Klein> *mumlar på danska*\n" + 
							"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!";
		chatBox.setText(testchat);
		
	}
	
	public void append(String entry) {
		chatBox.setText(chatBox.getText() + "\n" + entry);
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
	
	public ImageIcon getImageToSend() throws NullPointerException {
		return imageToSend;
	}
	
	public String[] getRecipients() throws ClassCastException {
		ArrayList<String> recipients;
		recipients = (ArrayList<String>) users.getSelectedValuesList();
		String[] stringReps = (String[]) recipients.toArray();
		return stringReps;
	}
	
	public void setUsers(String[] users) {
		listModel.clear();
		for (int i = 0, len = users.length; i < len; i++) {
			listModel.addElement(users[i]);
		}
		this.users = new JList<String>(listModel);
	}
	
	private class SendMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cc.sendMessage();
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
				frame.add(new ClientGUI(null));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}
