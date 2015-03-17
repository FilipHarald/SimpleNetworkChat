package server.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import server.*;

/**
 * This is the GUI-class for the server
 * @author Filip
 *
 */

public class ServerGUI extends JPanel {

	private ServerController controller;
	
	private JLabel ipAddress = new JLabel("", JLabel.CENTER);
	private JLabel port = new JLabel("Port: ");
	private JTextField txtPort = new JTextField("3520");
	private JButton btnStartStop = new JButton("START");
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> listUsers = new JList<String>(listModel);
	private JTextArea textAreaLog = new JTextArea();

	/**
	 * Contructor that takes a ServerController as parameter
	 * @param controller the controller 
	 */
	public ServerGUI(ServerController controller) {
		this.controller = controller;
		createGUI();
	}

	/**
	 * Method that creates the GUI
	 */
	public void createGUI() {

		this.setLayout(new GridBagLayout());
		ipAddress.setText("<html>IP: <i>" + controller.getIP() + "</i></html>");
		txtPort.setPreferredSize(new Dimension(50, 20));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 100));
		ipAddress.setPreferredSize(new Dimension(150,25));

		panel.add(ipAddress);
		panel.add(port);
		panel.add(txtPort);
		panel.add(btnStartStop);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,0,5);
		this.add(panel, c);

		listUsers.setFont(new Font("Consolas", Font.PLAIN, 12));
		listUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane scrollList = new JScrollPane(listUsers, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollList.setPreferredSize(new Dimension(150, 400));
		scrollList.setViewportView(listUsers);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0,5,5,5);
		this.add(scrollList, c);

		DefaultCaret caret = (DefaultCaret)textAreaLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textAreaLog.setAutoscrolls(true);
		textAreaLog.setEditable(false);
		textAreaLog.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		JScrollPane scroll = new JScrollPane(textAreaLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(700, 500));
		scroll.setViewportView(textAreaLog);
		
		ClickListener listener = new ClickListener();
		btnStartStop.addActionListener(listener);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(5,5,5,5);
		this.add(scroll, c);
	}

	/**
	 * Method that takes a String as parameter and appends that String to the JTextArea
	 * @param string taxt to be appended
	 */
	public synchronized void appendText(String string) {
		textAreaLog.append(string);
	}
	
	/**
	 * Method that updates the clientlist on the serverGUI. Takes a String[] as parameter
	 * @param clientList String[] with clients
	 */
	public void updateClientList(String[] clientList) {
		listModel.clear();
		for (String client : clientList) {
			listModel.addElement(client);
		}
	}

	/**
	 * Method for changing state on start/stop button on GUI depending on if it is running or not
	 */
	private void toggleButton() {
		if (controller.isRunning()) {
			btnStartStop.setText("STOP");
		} else {
			btnStartStop.setText("START");
		}
	}

	/**
	 * Method for clearing userlist on GUI
	 */
	public void clearList() {
		listModel.clear();
	}
	
	/**
	 * Inner Class for listening on buttons in GUI
	 * @author Jimmy
	 *
	 */
	private class ClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnStartStop) {
				if (!controller.isRunning()) {
					try {
						int port = Integer.parseInt(txtPort.getText());
						controller.startServer(port);
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, "Port can only be a number");
					}
				} else {
					controller.stopServer();
					listModel.clear();
				}
				toggleButton();
			}
		}
	}
}
