package client.gui.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

public class ChatPanel extends JPanel {

	private JTabbedPane tabPane = new JTabbedPane();
	private Map<Integer, ChatTab> tabs;
	private int currentTab;
	
	private ChatPanelListener listener;
	
	public ChatPanel() {		
		this.setLayout(new BorderLayout());
		
		tabs = new HashMap<Integer, ChatTab>();
		
		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				currentTab = tabPane.getSelectedIndex();
				fireOnChangedTab();
			}
		});
		
		this.add(tabPane, BorderLayout.CENTER);
	}
	
	public void addListener(ChatPanelListener listener) {
		this.listener = listener;
	}
	
	public void fireOnChangedTab() {
		if (listener != null) {
			listener.onChangedTab(tabPane.getTitleAt(currentTab));
		}
	}
	
	public int getCurrentTab() {
		return currentTab;
	}
	
	public void addTab(String name) {
		addTab(name, true);
	}
	
	public void addTab(String name, boolean showButton) {
		ChatTab newTab = new ChatTab();		
		tabPane.addTab(name, newTab);
		int index = tabPane.indexOfTab(name); 
		tabs.put(index, newTab);
		if (showButton)
			tabPane.setTabComponentAt(index, new TabButtonComponent(name));
	}
	
	public void appendLobbyTab(String text, ImageIcon image) {
		ChatTab tab = tabs.get(0);
		tab.append(text, image);
	}
	
	public void appendAllTabs(String text, ImageIcon image) {
		for (Entry<Integer, ChatTab> entry : tabs.entrySet()) {
			ChatTab tab = entry.getValue();
			tab.append(text, image);
		}
	}
	
	public void appendTab(int group, String text, ImageIcon image) {
		if (tabs.containsKey(group)) {
			ChatTab tab = tabs.get(group);
			tab.append(text, image);
		}
	}
	
	private class ChatTab extends JPanel {
		
		private JTextPane chatBox;
		private StyledDocument doc;
		
		public ChatTab() {
			doc = new DefaultStyledDocument();
			
			chatBox = new JTextPane(doc);
			chatBox.setAutoscrolls(true);
			chatBox.setEditable(false);
			chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
			chatBox.setBounds(0, 0, 650, 500);
			
			JScrollPane scroll = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(650, 500));
			scroll.setViewportView(chatBox);
			scroll.setBorder(new MatteBorder(0, 0, 1, 1, Color.BLACK));
			this.add(scroll, BorderLayout.CENTER);
		}
		
		public void append(String text, ImageIcon image) {
			//TODO: append image
			try {
				doc.insertString(doc.getLength(), text + "\n", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class TabButtonComponent extends JPanel {
		
		public TabButtonComponent(String name) {
			this.setOpaque(false);
	        this.add(new JLabel(name));
	        this.add(new TabButton());
		}
		
		private class TabButton extends JButton implements ActionListener {
			public TabButton() {
				this.setPreferredSize(new Dimension(16,16));
				this.addActionListener(this);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = tabPane.indexOfTabComponent(TabButtonComponent.this);
				if (i != -1) {
					tabPane.remove(i);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(new ChatPanel());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}
