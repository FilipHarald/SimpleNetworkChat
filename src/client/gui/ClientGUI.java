package client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author Andreas
 *
 */

public class ClientGUI extends JPanel {
	
	private JTextArea chatBox = new JTextArea();
	private JList<String> users;
		
	public ClientGUI() {
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		chatBox.setPreferredSize(new Dimension(650, 500));
		chatBox.setAutoscrolls(true);
		chatBox.setLineWrap(true);
		chatBox.setEditable(false);
		add(chatBox, BorderLayout.CENTER);
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("Användare 1");
		listModel.addElement("Användare 2");
		listModel.addElement("Användare 3");
		
		users = new JList<String>(listModel);
		
		users.setPreferredSize(new Dimension(150, 600));
		add(users, BorderLayout.EAST);
		
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
