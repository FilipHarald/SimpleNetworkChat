package server.gui;

import server.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * 
 * @author Filip
 *
 */

public class ServerGUI extends JPanel {

	private ServerController controller;

	private JTextField txtPort = new JTextField();
	private JButton btnStart = new JButton("START");
	private JButton btnStop = new JButton("STOP");
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> listUsers = new JList<String>(listModel);
	private JTextArea textAreaLog = new JTextArea();

	public ServerGUI() {
		this.controller = new ServerController(this);
		createGUI();
	}

	public void createGUI() {

		this.setLayout(new GridBagLayout());

		txtPort.setPreferredSize(new Dimension(50, 25));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 75));
		panel.add(txtPort);
		panel.add(btnStart);
		panel.add(btnStop);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,5,5);
		this.add(panel, c);

		listUsers.setFont(new Font("Consolas", Font.PLAIN, 12));
		listUsers.setPreferredSize(new Dimension(100, 300));
		listUsers.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
		listUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5);
		this.add(listUsers, c);

		textAreaLog.setAutoscrolls(true);
		textAreaLog.setEditable(false);
		textAreaLog.setFont(new Font("Consolas", Font.PLAIN, 12));
		textAreaLog.setBounds(0, 0, 400, 500);
		
		JScrollPane scroll = new JScrollPane(textAreaLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(400, 500));
		scroll.setViewportView(textAreaLog);
		scroll.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
		
		ClickListener listener = new ClickListener();
		btnStart.addActionListener(listener);
		btnStop.addActionListener(listener);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(5,5,5,5);
		this.add(scroll, c);

	}



	public void appendText(String string) {
		textAreaLog.append(string);
	}
	
	
	public void updateClientList(String[] clientList) {
		listModel.clear();
		for (String client : clientList) {
			listModel.addElement(client);
		}
	}
	
	private class ClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnStart) {
				int port = 0;
				try {
					port = Integer.parseInt(txtPort.getText());
					controller.startServer(port);
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Ange endast siffror som port");
				}
			} else if (e.getSource() == btnStop) {
				controller.stopServer();
			}
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(new ServerGUI());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}

}
