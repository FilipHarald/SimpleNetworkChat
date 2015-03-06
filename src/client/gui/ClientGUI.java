package client.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


/**
 * 
 * @author Andreas
 *
 */

public class ClientGUI extends JPanel {
	
	private JTextArea chatBox = new JTextArea();
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
		
		
		listModel.addElement("Användare 1");
		listModel.addElement("Användare 2");
		listModel.addElement("Användare 3");
		
		users = new JList<String>(listModel);
		
		users.setFont(new Font("Consolas", Font.PLAIN, 12));
		users.setPreferredSize(new Dimension(150, 600));
		users.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		users.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(users, BorderLayout.EAST);
		
		
		String testchat = 	"[14:00:12] <Andreas> Hej chatten!\n" +
							"[14:00:32] <Filip> Hej Andreas!\n" + 
							"[14:00:58] <Klein> *mumlar på danska*";
		
		chatBox.setText(testchat);
		JButton testbutton = new JButton("Test");
		testbutton.addActionListener(new NewMessageListener());
		add(testbutton, BorderLayout.SOUTH);
	}
	
	public void append(String entry) {
		chatBox.append("\n" + entry);
	}
	
	public void append(String[] entries) {
		for (int i = 0; i < entries.length; i++) {
			append(entries[i]);
		}
	}
	
	public void setUsers() {
		//Uppdatera listan med användare
	}
	
	//ej klar, registrerar när man väljer flera men blir fel när man markerar en
	private class NewMessageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] selectedIndices = users.getSelectedIndices();
			String[] recipients = new String[selectedIndices.length];
			
			for (int i = 0, len = recipients.length; i < len; i++) {
				recipients[i] = (String) users.getModel().getElementAt(i);
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
