package client.gui.panels;

/**
 * Interface used by UsersPanel to fire events
 * @author Albert
 *
 */
public interface UsersPanelListener {
	/**
	 * Fired when user is trying to add a new Group
	 * @param name Name of the new Group
	 * @param users Users that should be in the Group
	 */
	void onCreateGroup(String name, String[] users);
}
