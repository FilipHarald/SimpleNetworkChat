package client.gui.panels;

/**
 * Interface used by ChatPanel to fire events
 * @author Albert
 *
 */
public interface ChatPanelListener {
	/**
	 * Fired when a user switches to another tab in a ChatPanel
	 * @param name Name of the tab switched to
	 */
	void onChangedTab(String name);
}
