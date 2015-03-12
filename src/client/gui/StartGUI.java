package client.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import client.*;

/**
 * 
 * @author Andreas
 *
 */

public class StartGUI extends JPanel {
	
	private JTextField usernameTF = new JTextField();
	private JTextField hostnameTF = new JTextField();
	private JTextField portTF = new JTextField();
	private ClientController controller;

	private JButton connectBtn = new JButton("Connect");
	
	public StartGUI(ClientController controller) {
		this.controller = controller;

		createGUI();

		usernameTF.setText("User");
		hostnameTF.setText("127.0.0.1");
		portTF.setText("3520");

	}
	
	public void createGUI() {
		this.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(500, 250));
		
		// Create logo
		JLabel logoLabel = new JLabel("<html>SimpleNetworkChat<br><hr style='width: 340px;'></html>");
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		logoLabel.setFont(new Font("Consolas", Font.BOLD, 36));
		logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		this.add(logoLabel, BorderLayout.NORTH);
		
		// Create input fields
		JPanel data = new JPanel(new GridLayout(4, 1, 5, 5));
		usernameTF.setText("Andreas");
		usernameTF.setBorder(new TitledBorder("Username"));
		data.add(usernameTF);
		hostnameTF.setText("127.0.0.1");
		hostnameTF.setBorder(new TitledBorder("Hostname/IP:"));
		data.add(hostnameTF);
		portTF.setText("3520");
		portTF.setBorder(new TitledBorder("Port:"));
		data.add(portTF);
		connectBtn.addActionListener(new ConnectListener());
		data.add(connectBtn);
		
		// Add listeners
		EnterListener listener = new EnterListener();
		usernameTF.addKeyListener(listener);
		hostnameTF.addKeyListener(listener);
		portTF.addKeyListener(listener);
		connectBtn.addKeyListener(listener);
		
		this.add(data, BorderLayout.CENTER);
	}
	
	private void connect() {
		try {
			String username = usernameTF.getText();
			String hostname = hostnameTF.getText();
			int port = Integer.parseInt(portTF.getText());

			controller.connect(hostname, port, username);

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "You have entered incorrect values.");
		}
	}
	
	private class ConnectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			connect();
		}
	}
	
	private class EnterListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				connect();
			}
		}		
	}
}
