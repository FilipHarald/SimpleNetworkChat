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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatPanel extends JPanel {
	
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;

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
		tabPane.setTabComponentAt(index, new TabButtonComponent(name, showButton));
	}
	
	public void appendLobbyTab(String text, ImageIcon image, int style) {
		ChatTab tab = tabs.get(0);
		tab.append(text, image, style);
	}
	
	public void appendAllTabs(String text, ImageIcon image, int style) {
		for (Entry<Integer, ChatTab> entry : tabs.entrySet()) {
			ChatTab tab = entry.getValue();
			tab.append(text, image, style);
		}
	}
	
	public void appendTab(int group, String text, ImageIcon image, int style) {
		if (tabs.containsKey(group)) {
			ChatTab tab = tabs.get(group);
			tab.append(text, image, style);
		}
	}
	
	private class ChatTab extends JPanel {
		
		private JTextPane chatBox;
		private StyledDocument doc;
		private Style imgStyle;
		private SimpleAttributeSet boldStyle;
		private SimpleAttributeSet italicStyle;
		
		public ChatTab() {
			this.setLayout(new BorderLayout());
			
			doc = new DefaultStyledDocument();
			
			imgStyle = doc.addStyle("imgStyle", null);
			boldStyle = new SimpleAttributeSet();
			boldStyle.addAttribute(StyleConstants.CharacterConstants.Bold, true);
			italicStyle = new SimpleAttributeSet();
			italicStyle.addAttribute(StyleConstants.CharacterConstants.Italic, true);
			
			chatBox = new JTextPane(doc);
			chatBox.setAutoscrolls(true);
			chatBox.setEditable(false);
			chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
			
			JScrollPane scroll = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(650, 500));
			scroll.setViewportView(chatBox);
			
			this.add(scroll, BorderLayout.CENTER);
		}
		
		public void append(String text, ImageIcon image, int style) {
			SimpleAttributeSet attr;
			switch (style) {
				case NORMAL:
					attr = null;
					break;
				case BOLD:
					attr = boldStyle;
					break;
				case ITALIC:
					attr = italicStyle;
					break;
				default:
					attr = null;
					break;
			}
			try {
				doc.insertString(doc.getLength(), text + "\n", attr);
				if (image != null) {
					StyleConstants.setIcon(imgStyle, image);
					doc.insertString(doc.getLength(), "ignored", imgStyle);
					doc.insertString(doc.getLength(), "\n", null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private class TabButtonComponent extends JPanel {
		
		public TabButtonComponent(String name, boolean showButton) {
			this.setOpaque(false);
			JLabel label = new JLabel(name);
			label.setPreferredSize(new Dimension(name.length()*10, 15));
	        this.add(label);
	        if (showButton)
	        	this.add(new TabButton());
		}
		
		private class TabButton extends JButton implements ActionListener {
			public TabButton() {
				this.setPreferredSize(new Dimension(16, 16));
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
