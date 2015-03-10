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

public class StartClientGUI extends JPanel {
	
	private JTextField usernameTF = new JTextField();
	private JTextField hostnameTF = new JTextField();
	private JTextField portTF = new JTextField();
	private JFrame frame = new JFrame("SimpleNetworkChat");
	
	private JButton connectBtn = new JButton("Connect");
	
	public StartClientGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.add(setUpGUI());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
		
	}
	
	public JPanel setUpGUI() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(500, 250));
		
		JLabel logoLabel = new JLabel("<html>SimpleNetworkChat<br><hr style='width: 340px;'></html>");
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		logoLabel.setFont(new Font("Consolas", Font.BOLD, 36));
		logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel.add(logoLabel, BorderLayout.NORTH);
		
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
		
		EnterListener listener = new EnterListener();
		usernameTF.addKeyListener(listener);
		hostnameTF.addKeyListener(listener);
		portTF.addKeyListener(listener);
		connectBtn.addKeyListener(listener);
		
		panel.add(data, BorderLayout.CENTER);
	
		return panel;
	}
	
	private void connect() {
		try {
			String username = usernameTF.getText();
			String hostname = hostnameTF.getText();
			int port = Integer.parseInt(portTF.getText());
			
			new ClientController(hostname, port, username);
			
			frame.setVisible(false);
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

	public static void main(String[] args) {
		new StartClientGUI();
	}
	
}
