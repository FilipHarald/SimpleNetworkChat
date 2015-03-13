package client.gui.panels;

import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;

public class UsersPanel extends JPanel {

	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> usersList = new JList<String>(listModel);
	
	private UsersPanelListener listener;
	
	public UsersPanel() {
		this.setLayout(new BorderLayout());
		
		usersList.setFont(new Font("Consolas", Font.PLAIN, 12));
		usersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		usersList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					new ContextMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
		});

		JScrollPane scrollList = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollList.setPreferredSize(new Dimension(150, 600));
		scrollList.setViewportView(usersList);
		
		this.add(scrollList, BorderLayout.CENTER);
	}
	
	public void addListener(UsersPanelListener listener) {
		this.listener = listener;
	}
	
	private void fireOnCreateGroup(String name, String[] users) {
		listener.onCreateGroup(name, users);
	}
	
	public void updateList(String[] users) {
		listModel.clear();
		for (String user : users) {
			listModel.addElement(user);
		}
	}
	
	private class ContextMenu extends JPopupMenu {
		
		public ContextMenu() {
			JMenuItem createGroup = new JMenuItem("Create Group");
			
			if (usersList.isSelectionEmpty()) {
				createGroup.setEnabled(false);
			}
			
			createGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> selectedUsers = usersList.getSelectedValuesList();
					if (selectedUsers.size() > 0) {
						String name = JOptionPane.showInputDialog("Name of the new group:");
						String[] users = new String[selectedUsers.size()];
						users = selectedUsers.toArray(users);
						fireOnCreateGroup(name, users);
					}
				}
			});
			
			this.add(createGroup);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(new UsersPanel());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}
