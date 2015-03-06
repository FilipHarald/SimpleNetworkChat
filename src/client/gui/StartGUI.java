package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import client.*;

/**
 * 
 * @author Andreas
 *
 */

public class StartGUI extends JPanel {
	
	private JTextField usernameTF = new JTextField();
	private JButton connectBtn = new JButton("Connect");
	private ClientController cc = new ClientController();
	
	public StartGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("SimpleNetworkChat");
				frame.add(setUpGUI());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
		
	}
	
	public JPanel setUpGUI() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 120));
		panel.add(new JLabel("Username:"));
		usernameTF.setPreferredSize(new Dimension(200, 30));
		panel.add(usernameTF);
		connectBtn.addActionListener(new ConnectListener());
		panel.add(connectBtn);
	
		return panel;
	}
	
	private class ConnectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameTF.getText();
			cc.setClient(new Client("127.0.0.1", 3520, username));
		}
	}
	
	public static void main(String[] args) {
		new StartGUI();
	}
	
}
