package client.gui.panels;

import java.util.*;
import java.util.Map.Entry;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * ChatPanel represent one or several tabs of chat windows.
 * 
 * Each tab is made up of the inner class ChatTab, and an optional
 * TabButtonComponent if the tab should be closeable.
 * 
 * @author Albert
 *
 */
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
	
	public void addTab(String name, boolean showButton) {
		ChatTab newTab = new ChatTab();		
		tabPane.addTab(name, newTab);
		int index = tabPane.indexOfTab(name); 
		tabs.put(index, newTab);
		tabPane.setTabComponentAt(index, new TabButtonComponent(name, showButton));
	}
	
	public void addTab(String name) {
		addTab(name, true);
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
	
	
	/**
	 * Inner class representing one tab in the ChatPanel.
	 * 
	 * @author Albert
	 *
	 */
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
			chatBox.setBounds(0, 35, 650, 500);
			chatBox.setFont(new Font("Consolas", Font.PLAIN, 12));
			
			
			JScrollPane scroll = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(650, 500));
			scroll.setViewportView(chatBox);
			scroll.setBounds(0, 35, 650, 500);
			
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
	
	/**
	 * Inner class representing a close button on the tab title
	 * 
	 * @author Albert
	 *
	 */
	private class TabButtonComponent extends JPanel {
		
		public TabButtonComponent(String name, boolean showButton) {
			this.setOpaque(false);
			JLabel label = new JLabel(name);
			label.setPreferredSize(new Dimension(name.length()*10, 15));
	        this.add(label);
	        if (showButton) {
	        	this.add(new TabButton());
	        }
		}
		
		private class TabButton extends JButton implements ActionListener {
			private int size = 12;

			public TabButton() {
				this.setPreferredSize(new Dimension(size, size));
				this.addActionListener(this);
				this.setBorderPainted(false);
			}

			@Override
			public void paint(Graphics g) {
				// We're painting the red cross instead of using an icon
				Graphics2D g2d = (Graphics2D)g.create();
				g2d.setStroke(new BasicStroke(4));
				g2d.setColor(Color.RED);
				g2d.draw(new Line2D.Double(0,0,size,size));
				g2d.draw(new Line2D.Double(0, size, size, 0));
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
}
